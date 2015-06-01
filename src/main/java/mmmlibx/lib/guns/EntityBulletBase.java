package mmmlibx.lib.guns;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBulletBase extends Entity implements IProjectile, IEntityAdditionalSpawnData {

	protected EntityLivingBase thrower;
	protected String throwerName;
	protected Block inBlock;
	protected int inX = -1;
	protected int inY = -1;
	protected int inZ = -1;
	
	protected int ticksInGround = 1200;
	protected int ticksInAir = 0;
	protected boolean inGround;
	
	protected List<Entity> exclusionEntity = new ArrayList<Entity>();
	
	public ItemStack bullet;
	public ItemStack gun;
	public float speed;
	



	public EntityBulletBase(World pWorld) {
		super(pWorld);
		setSize(0.25F, 0.25F);
		//yOffset = 0.0F;
		renderDistanceWeight = 10.0D;
	}
	public EntityBulletBase(World pWorld, double par2, double par4, double par6) {
		this(pWorld);
		this.setPosition(par2, par4, par6);
	}

	/**
	 * 独自仕様の生成処理
	 * @param pWorld
	 * @param pEntity
	 * @param pBullet
	 * @param pSpeed
	 * @param pf
	 */
	public EntityBulletBase(World pWorld, EntityLivingBase pEntity,
			ItemStack pGun, ItemStack pBullet, float pSpeed, float pf) {
		this(pWorld);
		thrower = pEntity;
		gun = pGun;
		bullet = pBullet;
		setSize(0.25F, 0.25F);
		setLocationAndAngles(pEntity.posX, pEntity.posY + (double)pEntity.getEyeHeight(), pEntity.posZ, pEntity.rotationYaw, pEntity.rotationPitch);
		posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		posY -= 0.10000000149011612D;
		posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		setPosition(this.posX, this.posY, this.posZ);
		float f = 0.4F;
		motionX = (double)(-MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * f);
		motionZ = (double)(MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * f);
		motionY = (double)(-MathHelper.sin((rotationPitch + appendAngle()) / 180.0F * (float)Math.PI) * f);
		setThrowableHeading(motionX, motionY, motionZ, pSpeed, pf);
	}

	protected float appendAngle() {
		return 0.0F;
	}

	protected float getGravityVelocity() {
		return 0.03F;
	}

	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		return 0.0F;
	}

	@Override
	protected void entityInit() {
		// 初期化コード無し
	}

	public void setThrowableHeading(double pMotionX, double pMotionY, double pMotionZ,
			float pSpeed, float pReaction) {
		float lf = MathHelper.sqrt_double(pMotionX * pMotionX + pMotionY * pMotionY + pMotionZ * pMotionZ);
		pMotionX /= (double) lf;
		pMotionY /= (double) lf;
		pMotionZ /= (double) lf;
		pMotionX += rand.nextGaussian() * 0.007499999832361937D * (double) pReaction;
		pMotionY += rand.nextGaussian() * 0.007499999832361937D * (double) pReaction;
		pMotionZ += rand.nextGaussian() * 0.007499999832361937D * (double) pReaction;
		pMotionX *= (double) pSpeed;
		pMotionY *= (double) pSpeed;
		pMotionZ *= (double) pSpeed;
		motionX = pMotionX;
		motionY = pMotionY;
		motionZ = pMotionZ;
		float f3 = MathHelper.sqrt_double(pMotionX * pMotionX + pMotionZ * pMotionZ);
		prevRotationYaw = rotationYaw = (float)(Math.atan2(pMotionX, pMotionZ) * 180.0D / Math.PI);
		prevRotationPitch = rotationPitch = (float)(Math.atan2(pMotionY, (double) f3) * 180.0D / Math.PI);
	}

	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double par1) {
		double d1 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
		d1 *= 64.0D;
		return par1 < d1 * d1;
	}

	@Override
	public void setPositionAndRotation(double par1, double par3, double par5,
			float par7, float par8) {
		// 着弾後に変な移動が起こるのを抑制
		if (!inGround) {
			setPosition(par1, par3, par5);
			setRotation(par7, par8);
		}
	}

	@SideOnly(Side.CLIENT)
	public void setVelocity(double par1, double par3, double par5) {
		motionX = par1;
		motionY = par3;
		motionZ = par5;
		/*
		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
			prevRotationYaw = rotationYaw = (float)(Math.atan2(par1, par5) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float)(Math.atan2(par3, (double) f) * 180.0D / Math.PI);
//			setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
		}
		*/
	}

	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setInteger("xTile", inX);
		par1NBTTagCompound.setInteger("yTile", inY);
		par1NBTTagCompound.setInteger("zTile", inZ);
		par1NBTTagCompound.setInteger("inTile", Block.getIdFromBlock(inBlock));
		
		par1NBTTagCompound.setByte("inGround", (byte) (inGround ? 1 : 0));
		
		if ((this.throwerName == null || this.throwerName.length() == 0)
				&& this.thrower != null && this.thrower instanceof EntityPlayer) {
			this.throwerName = this.thrower.getCommandSenderEntity().getName();
		}
		
		par1NBTTagCompound.setString("ownerName", throwerName == null ? "" : throwerName);
		
		if (bullet != null) {
			NBTTagCompound lbullet = new NBTTagCompound();
			bullet.writeToNBT(lbullet);
			par1NBTTagCompound.setTag("bullet", lbullet);
		}
	}

	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		inX = par1NBTTagCompound.getInteger("xTile");
		inY = par1NBTTagCompound.getInteger("yTile");
		inZ = par1NBTTagCompound.getInteger("zTile");
		inBlock = Block.getBlockById(par1NBTTagCompound.getInteger("inTile"));
		inGround = par1NBTTagCompound.getByte("inGround") == 1;
		throwerName = par1NBTTagCompound.getString("ownerName");
		
		if (throwerName != null && throwerName.length() == 0) {
			throwerName = null;
		}
		
		if (par1NBTTagCompound.hasKey("bullet")) {
			NBTTagCompound lbullet = par1NBTTagCompound.getCompoundTag("bullet");
			bullet = ItemStack.loadItemStackFromNBT(lbullet);
		}
	}

	public EntityLivingBase getThrower() {
		// 投射主名が設定されている場合の獲得
		if (thrower == null && throwerName != null && throwerName.length() > 0) {
			thrower = worldObj.getPlayerEntityByName(throwerName);
		}
		
		return thrower;
	}

	/**
	 * 当たり判定から除外するEntityを追加する。<br>
	 * 貫通処理などで使う用。
	 * @param pEntity
	 */
	public void addExclusion(Entity pEntity) {
		exclusionEntity.add(pEntity);
	}

	public void onUpdate() {
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
		super.onUpdate();
		
		// 着弾後の生存時間
		if (inGround) {
			speed = 0F;
			if (worldObj.getBlockState(new BlockPos(inX, inY, inZ)).getBlock() != inBlock || --ticksInGround <= 0) {
				// 一定時間を超えた
				// 着弾していたブロックが消えた
				setDead();
			}
			return;
		} else {
			// 滞空時間カウンタ
			++ticksInAir;
		}
		
		// 接触判定
		MovingObjectPosition movingobjectposition;
		Vec3 lvo = new Vec3(posX, posY, posZ);
		Vec3 lvt = new Vec3(
				posX + motionX, posY + motionY, posZ + motionZ);
		while (true) {
			// Block用
//			movingobjectposition = worldObj.rayTraceBlocks(lvo, lvt, true);
			movingobjectposition = worldObj.rayTraceBlocks(lvo, lvt, false, true, false);
			// Entity用
			lvo = new Vec3(posX, posY, posZ);
			lvt = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
			
			if (movingobjectposition != null) {
				lvt = new Vec3(
						movingobjectposition.hitVec.xCoord,
						movingobjectposition.hitVec.yCoord,
						movingobjectposition.hitVec.zCoord);
			}
			
			if (!worldObj.isRemote) {
				Entity entity = null;
				@SuppressWarnings("rawtypes")
				List list = worldObj.getEntitiesWithinAABBExcludingEntity(this,
						getEntityBoundingBox().addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
				double d0 = 0.0D;
				EntityLivingBase entitylivingbase = getThrower();
				
				for (int j = 0; j < list.size(); ++j) {
					Entity entity1 = (Entity) list.get(j);
					if (exclusionEntity.contains(entity1)) {
						// 排除リストに在るEntityは除外
						continue;
					}
					
					// 発射後5tickは射手に当たらない
					if (entity1.canBeCollidedWith()
							&& (entity1 != entitylivingbase || this.ticksInAir >= 5)) {
						float f = 0.3F;
						AxisAlignedBB axisalignedbb =
								entity1.getEntityBoundingBox().expand((double) f, (double) f, (double) f);
						MovingObjectPosition movingobjectposition1 =
								axisalignedbb.calculateIntercept(lvo, lvt);
						
						if (movingobjectposition1 != null) {
							double d1 = lvo.distanceTo(movingobjectposition1.hitVec);
							
							if (d1 < d0 || d0 == 0.0D) {
								entity = entity1;
								d0 = d1;
							}
						}
					}
				}
				
				if (entity != null) {
					movingobjectposition = new MovingObjectPosition(entity);
				}
			}
			
			speed = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
			if (movingobjectposition != null) {
				if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
						&& worldObj.getBlockState(new BlockPos(movingobjectposition.hitVec.xCoord,
								movingobjectposition.hitVec.yCoord,
								movingobjectposition.hitVec.zCoord)).getBlock() == Blocks.portal) {
					// ポータルに突入
					setInPortal();
					break;
				} else {
					if (onImpact(movingobjectposition)) {
						GunsBase.Debug("hit %f, %f, %f", posX, posY, posZ);
						return;
					}
				}
			} else {
				// 接触対象なし
//				posX += motionX;
//				posY += motionY;
//				posZ += motionZ;
				break;
			}
		}
		
		// 移動量計算
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		float f1 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
		rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
		
		for (rotationPitch = (float) (Math.atan2(motionY, (double) f1) * 180.0D / Math.PI);
				rotationPitch - prevRotationPitch < -180.0F;
				prevRotationPitch -= 360.0F) {
			;
		}
		
		// 角度補正
		while (rotationPitch - prevRotationPitch >= 180.0F) {
			prevRotationPitch += 360.0F;
		}
		while (rotationYaw - prevRotationYaw < -180.0F) {
			prevRotationYaw -= 360.0F;
		}
		while (rotationYaw - prevRotationYaw >= 180.0F) {
			prevRotationYaw += 360.0F;
		}
		
		rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
		rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
		float f2 = 0.99F;
		float f3 = getGravityVelocity();
		
		if (isInWater()) {
			for (int i = 0; i < 4; ++i) {
				float f4 = 0.25F;
				//1.8後回し
				/*
				worldObj.spawnParticle("bubble",
						posX - motionX * (double) f4,
						posY - motionY * (double) f4,
						posZ - motionZ * (double) f4,
						motionX, motionY, motionZ);
					*/
			}
			
			f2 = 0.8F;
		}
		
		motionX *= (double) f2;
		motionY *= (double) f2;
		motionZ *= (double) f2;
		motionY -= (double) f3;
		setPosition(posX, posY, posZ);
	}

	/**
	 * 命中時の判定。<br>
	 * trueを返さない限り貫通して処理を継続
	 * @param var1
	 * @return
	 */
	protected boolean onImpact(MovingObjectPosition var1) {
		if (var1.entityHit != null) {
			if (bullet != null) {
				return ((ItemBulletBase)bullet.getItem()).onHitEntity(var1, this, var1.entityHit);
			} else {
				var1.entityHit.attackEntityFrom(
						DamageSource.causeThrownDamage(this, getThrower()), 1.0F);
				if (!worldObj.isRemote) {
					setDead();
				}
				return true;
			}
		} else {
			Block lblock = worldObj.getBlockState(new BlockPos(var1.hitVec.xCoord, var1.hitVec.yCoord, var1.hitVec.zCoord)).getBlock();
			//int lmeta = worldObj.getBlockMetadata(var1.blockX, var1.blockY, var1.blockZ);
			//if (checkDestroyBlock(var1, var1.blockX, var1.blockY, var1.blockZ, lblock, lmeta)) {
			//	return onBreakBlock(var1, var1.blockX, var1.blockY, var1.blockZ, lblock, lmeta);
			//} else {
				// 貫通できなかった
				posX = var1.hitVec.xCoord;
				posY = var1.hitVec.yCoord;
				posZ = var1.hitVec.zCoord;
				motionX = 0;
				motionY = 0;
				motionZ = 0;
				inGround = true;
				inBlock = lblock;
				inX = (int) var1.hitVec.xCoord;
				inY = (int) var1.hitVec.yCoord;
				inZ = (int) var1.hitVec.zCoord;
				setPosition(posX, posY, posZ);
				// 着弾パーティクル
				for (int i = 0; i < 8; ++i) {
//					worldObj.spawnParticle("snowballpoof", this.posX, this.posY,
					/*
					worldObj.spawnParticle("smoke",
							var1.hitVec.xCoord, var1.hitVec.yCoord, var1.hitVec.zCoord,
							0.0D, 0.0D, 0.0D);
							*/
				}
				return true;
			//}
		}
	}

	/**
	 * 破壊対象であるかの確認
	 * @param var1
	 * @param pX
	 * @param pY
	 * @param pZ
	 * @param pBlock
	 * @param pMetadata
	 * @return
	 */
	public boolean checkDestroyBlock(MovingObjectPosition var1, int pX, int pY, int pZ, Block pBlock, int pMetadata) {
		if ((pBlock instanceof BlockPane && pBlock.getMaterial() == Material.glass)
				|| (pBlock instanceof BlockFlowerPot)
				|| (pBlock instanceof BlockTNT)) {
			return true;
		}
		return false;
	}

	/**
	 * 破壊動作
	 * @param var1
	 * @param pX
	 * @param pY
	 * @param pZ
	 * @param pBlock
	 * @param pMetadata
	 * @return
	 */
	public boolean onBreakBlock(MovingObjectPosition var1, int pX, int pY, int pZ, Block pBlock, int pMetadata) {
		GunsBase.Debug("destroy: %d, %d, %d", pX, pY, pZ);
		if (pBlock instanceof BlockTNT) {
			removeBlock(pX, pY, pZ, pBlock, pMetadata);
			pBlock.onBlockDestroyedByExplosion(worldObj, new BlockPos(pX, pY, pZ), new Explosion(worldObj, getThrower(), pX, pY, pZ, 0.0F, false, false));
			return true;
		} else {
			removeBlock(pX, pY, pZ, pBlock, pMetadata);
			pBlock.onBlockDestroyedByPlayer(worldObj, new BlockPos(pX, pY, pZ), worldObj.getBlockState(new BlockPos(pX, pY, pZ)));
			return false;
		}
	}

	/**
	 * ブロック破壊時の動作
	 * @param pX
	 * @param pY
	 * @param pZ
	 * @param pBlock
	 * @param pMetadata
	 */
	protected void removeBlock(int pX, int pY, int pZ, Block pBlock, int pMetadata) {
		worldObj.playAuxSFX(2001, new BlockPos(pX, pY, pZ), Block.getIdFromBlock(pBlock) + (pMetadata << 12));
		worldObj.setBlockToAir(new BlockPos(pX, pY, pZ));	
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		// 高加速体に対応するための処置
		PacketBuffer lpbuf = new PacketBuffer(buffer);
		lpbuf.writeInt(getEntityId());
		lpbuf.writeBoolean(inGround);
		if (inGround) {
			lpbuf.writeInt(inX);
			lpbuf.writeInt(inY);
			lpbuf.writeInt(inZ);
			lpbuf.writeInt(Block.getIdFromBlock(inBlock));
		} else {
			lpbuf.writeInt(Float.floatToIntBits((float)motionX));
			lpbuf.writeInt(Float.floatToIntBits((float)motionY));
			lpbuf.writeInt(Float.floatToIntBits((float)motionZ));
		}
		try {
			lpbuf.writeItemStackToBuffer(bullet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		// 高加速体に対応するための処置
		PacketBuffer lpbuf = new PacketBuffer(additionalData);
		Entity lentity = worldObj.getEntityByID(lpbuf.readInt());
		if (lentity instanceof EntityLivingBase) {
			thrower = (EntityLivingBase)lentity;
		}
		inGround = lpbuf.readBoolean();
		if (inGround) {
			inX = lpbuf.readInt();
			inY = lpbuf.readInt();
			inZ = lpbuf.readInt();
			inBlock = Block.getBlockById(lpbuf.readInt());
		} else {
			motionX = (double)Float.intBitsToFloat(lpbuf.readInt());
			motionY = (double)Float.intBitsToFloat(lpbuf.readInt());
			motionZ = (double)Float.intBitsToFloat(lpbuf.readInt());
			setVelocity(motionX, motionY, motionZ);
		}
		try {
			bullet = lpbuf.readItemStackFromBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 弾丸の色
	 * 0xRRGGBB
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public int getBulletColor() {
		if (bullet != null) {
			return ((ItemBulletBase)bullet.getItem()).getBulletColor(bullet);
		}
		return 0x804000;
	}

}
