package net.blacklab.lmmnx.api.event;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class LMMNX_Event extends Event {
	
	@Cancelable
	public static class LMMNX_ItemPutChestEvent extends LMMNX_Event{
		
		public LMM_EntityLittleMaid maid;
		public IInventory target;
		public ItemStack stack;
		public int maidStackIndex;
		
		public LMMNX_ItemPutChestEvent(LMM_EntityLittleMaid maid, IInventory target, ItemStack stack, int maidStackIndex){
			this.maid = maid;
			this.target = target;
			this.stack = stack;
			this.maidStackIndex = maidStackIndex;
			this.setCanceled(false);
		}
		
	}

}
