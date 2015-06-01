package littleMaidMobX;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class LMM_EntityAIJumpToMaster extends EntityAIBase implements LMM_IEntityAI {

	protected LMM_EntityLittleMaid theMaid;
	protected EntityLivingBase theOwner;
	protected World theWorld;
	protected boolean isEnable;
	private boolean jumpTarget;
	protected AxisAlignedBB boundingBox;

	public LMM_EntityAIJumpToMaster(LMM_EntityLittleMaid pEntityLittleMaid) {
		super();
		
		theMaid = pEntityLittleMaid;
		theWorld = pEntityLittleMaid.worldObj;
		isEnable = true;
		boundingBox = AxisAlignedBB.fromBounds(0, 0, 0, 0, 0, 0);
	}

	@Override
	public boolean shouldExecute() {
		if (!isEnable || !theMaid.isContractEX() || theMaid.isMaidWaitEx()) {
			// 契約個体のみが跳ぶ
			return false;
		}
		if (theMaid.getLeashed()) {
			// 括られているなら跳ばない
			return false;
		}
		if (theMaid.isFreedom()) {
			// 自由行動の子は基点へジャンプ
			if (theMaid.homeWorld != theMaid.dimension) {
				LMM_LittleMaidMobX.Debug(String.format("ID:%d, %d -> %d, Change HomeWorld. reset HomePosition.",
						theMaid.getEntityId(),theMaid.homeWorld, theMaid.worldObj.provider.getDimensionId()));
//				theMaid.func_110171_b(
				theMaid.func_175449_a(new BlockPos(
						MathHelper.floor_double(theMaid.posX),
						MathHelper.floor_double(theMaid.posY),
						MathHelper.floor_double(theMaid.posZ)), 16);
				return false;
			}
			
			//1.8後回し
			
			if (theMaid.func_180486_cf().distanceSqToCenter(
					MathHelper.floor_double(theMaid.posX),
					MathHelper.floor_double(theMaid.posY),
					MathHelper.floor_double(theMaid.posZ)) > 400D) {
				jumpTarget = false;
				LMM_LittleMaidMobX.Debug(String.format(
						"ID:%d(%s) Jump To Home.", theMaid.getEntityId(),
						theMaid.worldObj.isRemote ? "C" : "W"));
				return true;
			}
			
		} else {
			jumpTarget = true;
			theOwner = theMaid.getMaidMasterEntity();
			if (theMaid.getAttackTarget() == null) {
				if (theMaid.mstatMasterDistanceSq < 144D) {
					return false;
				}
				//theMaid.setPosition(theMaid.getMaidMasterEntity().posX, theMaid.getMaidMasterEntity().posY, theMaid.getMaidMasterEntity().posZ);
			} else {
				// ターゲティング中は距離が伸びる
				if (theMaid.mstatMasterDistanceSq < (theMaid.isBloodsuck() ? 1024D : 256D)) {
					return false;
				}
			}
			LMM_LittleMaidMobX.Debug(
					"ID:%d(%s) Jump To Master.",
					theMaid.getEntityId(), theMaid.worldObj.isRemote ? "C" : "W");
			return true;
		}
		return false;
	}

	@Override
	public void startExecuting() {
		if (jumpTarget) {
			int i = MathHelper.floor_double(theOwner.posX) - 2;
			int j = MathHelper.floor_double(theOwner.posZ) - 2;
			int k = MathHelper.floor_double(theOwner.getEntityBoundingBox().minY);
			
			for (int l = 0; l <= 4; l++) {
				for (int i1 = 0; i1 <= 4; i1++) {
					if ((l < 1 || i1 < 1 || l > 3 || i1 > 3)
							&& theWorld.getBlockState(new BlockPos(i + l, k - 1, j + i1)).getBlock().isNormalCube()
							&& !theWorld.getBlockState(new BlockPos(i + l, k, j + i1)).getBlock().isNormalCube()
							&& !theWorld.getBlockState(new BlockPos(i + l, k + 1, j + i1)).getBlock().isNormalCube()) {
						// 主の前に跳ばない
						double dd = theOwner.getDistanceSq(
								(double) (i + l) + 0.5D + MathHelper.sin(theOwner.rotationYaw * 0.01745329252F) * 2.0D,
								(double) k,
								(double) (j + i1) - MathHelper.cos(theOwner.rotationYaw * 0.01745329252F) * 2.0D);
						if (dd > 8D) {
//							theMaid.setTarget(null);
//							theMaid.setRevengeTarget(null);
//							theMaid.setAttackTarget(null);
//							theMaid.getNavigator().clearPathEntity();
							theMaid.setLocationAndAngles(
									(float) (i + l) + 0.5F, k, (float) (j + i1) + 0.5F,
									theMaid.rotationYaw, theMaid.rotationPitch);
							return;
						}
					}
				}
			}
		} else {
			// ホームポジションエリア外で転移
			int lx = theMaid.func_180486_cf().getX();
			int ly = theMaid.func_180486_cf().getY();
			int lz = theMaid.func_180486_cf().getZ();
			if (!(isCanJump(lx, ly, lz))) {
				// ホームポジション消失
				LMM_LittleMaidMobX.Debug(String.format(
						"ID:%d(%s) home lost.",
						theMaid.getEntityId(), theMaid.worldObj.isRemote ? "C" : "W"));
				int a;
				int b;
				// int c;
				boolean f = false;
				// ｙ座標で地面を検出
				for (a = 1; a < 6 && !f; a++) {
					if (isCanJump(lx, ly + a, lz)) {
						f = true;
						ly += a;
						break;
					}
				}
				for (a = -1; a > -6 && !f; a--) {
					if (isCanJump(lx, ly + a, lz)) {
						f = true;
						ly += a;
						break;
					}
				}

				// CW方向に検索領域を広げる
				loop_search: for (a = 2; a < 18 && !f; a += 2) {
					lx--;
					lz--;
					for (int c = 0; c < 4; c++) {
						for (b = 0; b <= a; b++) {
							// N
							if (isCanJump(lx, ly + a, lz)) {
								f = true;
								break loop_search;
							}
							if (c == 0)
								lx++;
							else if (c == 1)
								lz++;
							else if (c == 2)
								lx--;
							else if (c == 3)
								lz--;
						}
					}
				}
				if (f) {
//					theMaid.func_110171_b(lx, ly, lz, (int) theMaid.func_110174_bM());
					theMaid.func_175449_a(new BlockPos(lx, ly, lz), (int) theMaid.getMaximumHomeDistance());
					LMM_LittleMaidMobX.Debug(String.format(
							"Find new position:%d, %d, %d.", lx, ly, lz));
				} else {
					if (isCanJump(lx, ly - 6, lz)) {
						ly -= 6;
					}
					LMM_LittleMaidMobX.Debug(String.format(
							"loss new position:%d, %d, %d.", lx, ly, lz));
				}
			} else {
				LMM_LittleMaidMobX.Debug(String.format(
						"ID:%d(%s) home solid.",
						theMaid.getEntityId(), theMaid.worldObj.isRemote ? "C" : "W"));
			}
			
//			theMaid.setTarget(null);
//			theMaid.setAttackTarget(null);
//			theMaid.getNavigator().clearPathEntity();
			theMaid.setLocationAndAngles((double) lx + 05D, (double) ly, (double) lz + 0.5D,
					theMaid.rotationYaw, theMaid.rotationPitch);
			
		}
		
		//theMaid.setTarget(null);
		theMaid.setAttackTarget(null);
		theMaid.setRevengeTarget(null);
		theMaid.getNavigator().clearPathEntity();
		LMM_LittleMaidMobX.Debug(String.format("ID:%d(%s) Jump Fail.",
				theMaid.getEntityId(), theMaid.worldObj.isRemote ? "C" : "W"));
	}

	/**
	 * 転移先のチェック
	 */
	protected boolean isCanJump(int px, int py, int pz) {
		//1.8後回し
		/*
		double lw = (double) theMaid.width / 2D;
		double ly = (double) py - (double) (theMaid.yOffset + theMaid.height);
		boundingBox.setBounds((double) px - lw, ly, (double) pz - lw,
				(double) px + lw, ly + (double) theMaid.height, (double) pz + lw);
		*/
		return theWorld.getBlockState(new BlockPos(px, py - 1, pz)).getBlock().getMaterial().isSolid()
				&& theWorld.func_147461_a(boundingBox).isEmpty();
	}

	@Override
	public boolean continueExecuting() {
		return false;
	}

	@Override
	public void setEnable(boolean pFlag) {
		isEnable = pFlag;
	}

	@Override
	public boolean getEnable() {
		return isEnable;
	}

	@Override
	public boolean isInterruptible() {
		return true;
	}

}
