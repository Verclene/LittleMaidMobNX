package net.blacklab.lmmnx.util;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityMode_Basic;
import littleMaidMobX.LMM_LittleMaidMobNX;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class LMMNX_EntityMode_Debug extends LMM_EntityMode_Basic {
	
	public static final int mmode_Debug = 0x00f0;

	public LMMNX_EntityMode_Debug(LMM_EntityLittleMaid pEntity) {
		super(pEntity);
		isAnytimeUpdate = true;
	}

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public void init() {
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		EntityAITasks[] ltasks = new EntityAITasks[2];
		ltasks[0] = pDefaultMove;
		ltasks[1] = pDefaultTargeting;
		
		if(LMMNX_DevMode.DEBUG_PRINT_SWITCH) owner.addMaidMode(ltasks, "Debug", mmode_Debug);
	}

	@Override
	public boolean changeMode(EntityPlayer pentityplayer) {
		if(!LMMNX_DevMode.DEBUG_PRINT_SWITCH) return false; 
		ItemStack litemstackl0 = owner.maidInventory.getStackInSlot(17);
		ItemStack litemstackl1 = owner.maidInventory.getStackInSlot(16);
		if (litemstackl0 != null && litemstackl1 != null) {
			if (litemstackl0.getItem() == LMM_LittleMaidMobNX.spawnEgg && litemstackl1.getItem() == Item.getItemFromBlock(Blocks.barrier)) {
				owner.setMaidMode("Debug");
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean setMode(int pMode) {
		if(!LMMNX_DevMode.DEBUG_PRINT_SWITCH) return false; 
		switch (pMode) {
		case mmode_Debug :
			owner.setBloodsuck(false);
			owner.aiAttack.setEnable(false);
			owner.aiShooting.setEnable(false);
			return true;
		}
		
		return false;
	}

	@Override
	public void updateAITick(int pMode) {
		super.updateAITick(pMode);
		if (pMode == mmode_Debug && owner.isRemainsContract()) {
			int a = ReflectionHelper.getPrivateValue(LMM_EntityLittleMaid.class, owner, "maidContractLimit");
			ReflectionHelper.setPrivateValue(LMM_EntityLittleMaid.class, owner, Math.max(a-500, 0), "maidContractLimit");
		}
	}

}
