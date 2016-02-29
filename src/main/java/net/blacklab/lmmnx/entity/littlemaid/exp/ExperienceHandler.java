package net.blacklab.lmmnx.entity.littlemaid.exp;

import java.util.UUID;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.blacklab.lmmnx.util.NXCommonUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

public class ExperienceHandler {
	
	protected LMM_EntityLittleMaid theMaid;

	private static final String uuidString = "NX_EXP_HP_BOOSTER";
	public static final UUID NX_EXP_HP_BOOSTER = UUID.nameUUIDFromBytes(uuidString.getBytes());
	
	private int deathCounts = 0;
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
		// 気絶機能はLv20以上で利用可能
		if (theMaid.getMaidLevel() >= 20) {
			
			return true;
		}
		return false;
	}
	
	public void onUpdate() {
		
	}
	
	public boolean onDeathUpdate() {
		return false;
	}
	
	public void readEntityFromNBT(NBTTagCompound tagCompound) {
		
	}
	
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		
	}

}
