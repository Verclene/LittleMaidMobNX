package net.blacklab.lmmnx.entity.littlemaid.exp;

import java.util.UUID;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityMode_Basic;
import littleMaidMobX.LMM_LittleMaidMobNX;
import net.blacklab.lmmnx.api.item.LMMNX_API_Item;
import net.blacklab.lmmnx.entity.littlemaid.mode.EntityMode_DeathWait;
import net.blacklab.lmmnx.util.NXCommonUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;

public class ExperienceHandler {

	protected LMM_EntityLittleMaid theMaid;

	private static final String uuidString = "NX_EXP_HP_BOOSTER";
	public static final UUID NX_EXP_HP_BOOSTER = UUID.nameUUIDFromBytes(uuidString.getBytes());

	private boolean isWaitRevive = false;
	// 死亡までの猶予時間
	private int deathCount = 0;
	// 復帰までに最低限必要になる時間
	private int pauseCount = 0;
	private int requiredSugarToRevive = 0;

	public ExperienceHandler(LMM_EntityLittleMaid maid) {
		theMaid = maid;
	}

	public void onLevelUp(int level) {
		/*
		 * 報酬付与・固定アイテム
		 */
		if (level%20 == 0) {
			NXCommonUtil.giveItem(new ItemStack(Items.name_tag), theMaid);
		}
		if (level%50 == 0) {
			NXCommonUtil.giveItem(new ItemStack(Items.emerald, level/50), theMaid);
		}

		/*
		 * 最大HP上昇
		 */
		double modifyamount = 0;
		double prevamount = 0;
		if (level > 30) {
			modifyamount += (Math.min(level, 50)-30)/2;
		}
		if (level > 50) {
			modifyamount += MathHelper.floor_float((Math.min(level, 75)-50)/2.5f);
		}
		if (level > 75) {
			modifyamount += MathHelper.floor_float((Math.min(level, 150)-75)/7.5f);
		}
		if (level > 150) {
			modifyamount += (Math.min(level, 300)-150)/15;
		}
		IAttributeInstance maxHPattr = theMaid.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		AttributeModifier existedMod = maxHPattr.getModifier(NX_EXP_HP_BOOSTER);
		if (existedMod != null) {
			prevamount = existedMod.getAmount();
		}
		if (modifyamount != 0 || prevamount < modifyamount) {
			// Modifier書き換え
			float prevHP = theMaid.getHealth();
			AttributeModifier maxHPmod = new AttributeModifier(NX_EXP_HP_BOOSTER, uuidString, modifyamount, 0);
			if (existedMod != null) {
				maxHPattr.removeModifier(existedMod);
			}
			maxHPattr.applyModifier(maxHPmod);
			// たぶんremoveModifierした時に20を超える体力が削られちゃうので，再設定．
			theMaid.setHealth(prevHP);
		}
	}

	public boolean onDeath(DamageSource cause) {
		LMM_LittleMaidMobNX.Debug("HOOK CATCH");
		if (theMaid.getMaidLevel() >= 20 && !cause.getDamageType().equals("outOfWorld") && !cause.getDamageType().equals("lmmnx_timeover") && !isWaitRevive) {
			// 復帰に必要な砂糖はレベルが低いほど大きく，猶予は少なく
			LMM_LittleMaidMobNX.Debug("DISABLING Remote=%s", theMaid.worldObj.isRemote);
			theMaid.playSound("random.glass");
			deathCount = (int) Math.max(1200, 200 + Math.pow(theMaid.getMaidLevel()-20, 1.8));
			pauseCount = (int) Math.max(100, 600 - (theMaid.getMaidLevel()-20)*6.5);
			requiredSugarToRevive = Math.min(16, 64 - (int)((theMaid.getMaidLevel()-20)/100f*48f));
			isWaitRevive = true;
			LMM_LittleMaidMobNX.Debug("TURN ON COUNT=%d", deathCount);
			return true;
		} else if (cause.getDamageType().equals("lmmnx_timeover")) {
			theMaid.playSound("random.glass");
		}
		return false;
	}

	public void onLivingUpdate() {
		LMM_LittleMaidMobNX.Debug("COUNT %d", deathCount);
		if (isWaitRevive) {
			LMM_LittleMaidMobNX.Debug("HOOK UPDATE");

			// 死亡判定
			if (--deathCount <= 0 && !theMaid.isDead) {
				theMaid.attackEntityFrom(
						new DamageSource("lmmnx_timeover").setDamageBypassesArmor().setDamageAllowedInCreativeMode().setDamageIsAbsolute(),
						Float.MAX_VALUE);
			}

			// 行動不能
			if ((--pauseCount > 0 || deathCount > 0) && theMaid.getMaidModeInt() != EntityMode_DeathWait.mmode_DeathWait) {
				theMaid.setMaidWait(false);
				theMaid.setMaidMode(EntityMode_DeathWait.mmode_DeathWait);
			}

			// 砂糖を持っているか？
			int sugarCount = 0;
			for (int i=0; i<18 && sugarCount < requiredSugarToRevive; i++) {
				ItemStack stack = theMaid.maidInventory.mainInventory[i];
				if (stack!=null && LMMNX_API_Item.isSugar(stack.getItem())) {
					sugarCount += stack.stackSize;
				}
			}
			// 砂糖が規定数以上ある場合は死亡猶予
			if (sugarCount >= requiredSugarToRevive) {
				deathCount++;
				// 復帰
				if (deathCount > 0 && pauseCount <= 0) {
					isWaitRevive = false;
					theMaid.heal(10f);
					for(int i=0; i<18 && requiredSugarToRevive > 0; i++) {
						ItemStack stack = theMaid.maidInventory.mainInventory[i];
						if (stack!=null && LMMNX_API_Item.isSugar(stack.getItem())) {
							int consumesize = Math.min(stack.stackSize, requiredSugarToRevive);
							stack.stackSize -= consumesize;
							if (stack.stackSize <= 0) {
								stack = null;
							}
							requiredSugarToRevive -= consumesize;
						}
					}
					theMaid.setMaidWait(false);
					theMaid.setMaidMode(LMM_EntityMode_Basic.mmode_Escorter);
				}
			}
		}
	}

	public boolean onDeathUpdate() {
		LMM_LittleMaidMobNX.Debug("HOOK ONDEATH");
		if (isWaitRevive && deathCount > 0) {
			LMM_LittleMaidMobNX.Debug("DISABLING");
			if (theMaid.worldObj.isRemote) {
				theMaid.showParticleFX(EnumParticleTypes.SUSPENDED_DEPTH, 0.5D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D);
			}
			return true;
		}
		return false;
	}

	public void readEntityFromNBT(NBTTagCompound tagCompound) {
		isWaitRevive = tagCompound.getBoolean("LMMNX_EXP_HANDLER_DEATH_WAIT");
		deathCount = tagCompound.getInteger("LMMNX_EXP_HANDLER_DEATH_DCNT");
		pauseCount = tagCompound.getInteger("LMMNX_EXP_HANDLER_DEATH_PCNT");
		requiredSugarToRevive = tagCompound.getInteger("LMMNX_EXP_HANDLER_DEATH_REQ");
	}

	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		tagCompound.setBoolean("LMMNX_EXP_HANDLER_DEATH_WAIT", isWaitRevive);
		tagCompound.setInteger("LMMNX_EXP_HANDLER_DEATH_DCNT", deathCount);
		tagCompound.setInteger("LMMNX_EXP_HANDLER_DEATH_PCNT", pauseCount);
		tagCompound.setInteger("LMMNX_EXP_HANDLER_DEATH_REQ", requiredSugarToRevive);
	}

}
