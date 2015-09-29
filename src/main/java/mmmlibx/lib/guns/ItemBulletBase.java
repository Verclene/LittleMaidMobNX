package mmmlibx.lib.guns;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBulletBase extends Item {

	protected final static String Tag_Speed		= "speed";
	protected final static String Tag_Reaction	= "reaction";
	protected final static String Tag_Power		= "power";
	
	/** 発射時の音 */
	public String soundFire;
	
	/** 基準発射速度 **/
	float speed;
	/** 発射時の反動 **/
	float reaction;
	/** ダメージ係数 **/
	float power;


	public ItemBulletBase() {
		setCreativeTab(CreativeTabs.tabCombat);
		// 500m/s
		speed = 25F;
		reaction = 1.0F;
		power = 0.2F;
	}

	public float getSpeed(ItemStack pBullet) {
		if (pBullet.hasTagCompound() && pBullet.getTagCompound().hasKey(Tag_Speed)) {
			return pBullet.getTagCompound().getFloat(Tag_Speed);
		}
		return speed;
	}

	public float getReaction(ItemStack pBullet) {
		if (pBullet.hasTagCompound() && pBullet.getTagCompound().hasKey(Tag_Reaction)) {
			return pBullet.getTagCompound().getFloat(Tag_Reaction);
		}
		return reaction;
	}

	public float getPower(ItemStack pBullet) {
		if (pBullet.hasTagCompound() && pBullet.getTagCompound().hasKey(Tag_Power)) {
			return pBullet.getTagCompound().getFloat(Tag_Power);
		}
		return power;
	}

	/**
	 * 弾薬に関連付けられたEntityを返す。
	 * @param pGun
	 * @param pBullet
	 * @param pWorld
	 * @param pPlayer
	 * @return
	 */
	public EntityBulletBase getBulletEntity(ItemStack pGun, ItemStack pBullet, World pWorld, EntityPlayer pPlayer, int pUseCount) {
		// 標準弾体
		ItemGunsBase lgun = ((ItemGunsBase)pGun.getItem());
		return new EntityBulletBase(pWorld, pPlayer, pGun, pBullet,
				getSpeed(pBullet) * lgun.getEfficiency(pGun, pPlayer, pUseCount),
				lgun.getAccuracy(pGun, pPlayer, pUseCount));
	}

	/**
	 * 発射音
	 * @param pWorld
	 * @param pPlayer
	 * @param pBullet
	 */
	public void playSoundFire(World pWorld, EntityPlayer pPlayer, ItemStack pGun, ItemStack pBullet) {
		if (soundFire != null && !soundFire.isEmpty()) {
			Item lgun = pGun.getItem();
			float lvol = 0.5F;
			if (lgun instanceof ItemGunsBase) {
				lvol = ((ItemGunsBase)lgun).volume;
			}
			pWorld.playSoundAtEntity(pPlayer, soundFire,
					lvol, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		}
	}

	/**
	 * 弾の色
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public int getBulletColor(ItemStack pBullet) {
		return 0x804000;
	}

	public float getHitDamage(EntityBulletBase pBullrtEntity,  Entity pTargetEntity, ItemStack pBullet) {
		float ldam = pBullrtEntity.speed * getPower(pBullet);//Math.ceil((double)lfd * damage * 0.1D * (isInfinity ? 0.5D : 1D));
		GunsBase.Debug("damage: %f", ldam);
		return ldam;
	}

	public boolean onHitEntity(MovingObjectPosition var1, EntityBulletBase pBullrtEntity,  Entity pTargetEntity) {
		pTargetEntity.hurtResistantTime = 0;
		pTargetEntity.attackEntityFrom(
				DamageSource.causeThrownDamage(pBullrtEntity, pBullrtEntity.getThrower()), getHitDamage(pBullrtEntity, pTargetEntity, pBullrtEntity.bullet));
		if (!pBullrtEntity.worldObj.isRemote) {
			pBullrtEntity.setDead();
		}
		return true;
	}

}
