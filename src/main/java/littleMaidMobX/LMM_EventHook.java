package littleMaidMobX;

import net.blacklab.lib.ItemUtil;
import net.blacklab.lib.Version;
import net.blacklab.lmmnx.api.event.LMMNX_Event;
import net.blacklab.lmmnx.api.item.LMMNX_API_Item;
import net.blacklab.lmmnx.api.mode.LMMNX_API_Farmer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class LMM_EventHook
{
	@SubscribeEvent
	public void onEntityItemPickupEvent(EntityItemPickupEvent event)
	{
		if(event.entityPlayer instanceof LMM_EntityLittleMaidAvatar)
		{
			if(event.item!=null)
			{
				event.setCanceled(true);
			}
		}
	}
	
	public class RunThread extends Thread{
		public PlayerEvent.PlayerLoggedInEvent e;
		
		public RunThread(PlayerEvent.PlayerLoggedInEvent ev){
			e = ev;
		}
		
		public void run(){
			Version.VersionData v = Version.getLatestVersion("http://mc.el-blacklab.net/lmmnxversion.txt");
			if(LMM_LittleMaidMobNX.VERSION_CODE < v.code){
				//バージョンが古い
				// TODO これメイドのAvatarキャッチしない？
				try{
					//別スレッドから使えるんかい
					e.player.addChatMessage(new ChatComponentText("[LittleMaidMobNX]New Version Avaliable : "+v.name));
					e.player.addChatMessage(new ChatComponentText("[LittleMaidMobNX]Go to : http://el-blacklab.net/"));
				}catch(Exception e){}
			}
		}
	}
	@SubscribeEvent
	public void onEntitySpawn(LivingSpawnEvent.CheckSpawn event){
		if(event.entityLiving instanceof LMM_EntityLittleMaid){
			LMM_EntityLittleMaid maid = (LMM_EntityLittleMaid) event.entityLiving;
			if(maid.isContract()||maid.isWildSaved) return;
//			NBTTagCompound t = new NBTTagCompound();
//			maid.writeEntityToNBT(t);
//			maid.readEntityFromNBT(t);
			maid.onSpawnWithEgg();
			int c = maid.getTextureBox()[0].getWildColorBits();
			if(c<=0) maid.setColor(12); else for(int i=15;i>=0;i--){
				int x = (int) Math.pow(2, i);
				if((c&x)==x) maid.setColor(i);
			}
			maid.setTextureNames();
			maid.isWildSaved = true;
//			event.setResult(Result.ALLOW);
		}
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
		new RunThread(event).start();
	}
	
	// TODO issue #9 merge from LittleMaidMobAX(https://github.com/asiekierka/littleMaidMobX/commit/92b2850b1bc4a70b69629cfc84c92748174c8bc6)
	@SubscribeEvent
	public void onEntitySpawned(EntityJoinWorldEvent event){
		if (event.entity instanceof EntityArrow) {
				EntityArrow arrow = (EntityArrow) event.entity;
				if (arrow.shootingEntity instanceof LMM_IEntityLittleMaidAvatar) {
					LMM_IEntityLittleMaidAvatar avatar = (LMM_IEntityLittleMaidAvatar) arrow.shootingEntity;
					/* if (arrow.isDead) {
						for (Object obj : arrow.worldObj.loadedEntityList) {
							if (obj instanceof EntityCreature && !(obj instanceof LMM_EntityLittleMaid)) {
								EntityCreature ecr = (EntityCreature)obj;
								if (ecr.getEntityToAttack() == avatar) {
									ecr.setTarget(avatar.getMaid());
								}
							}
						}
					} */
					arrow.shootingEntity = avatar.getMaid();
					LMM_LittleMaidMobNX.Debug("Set "+event.entity.getClass()+" field shootingEntity from avator to maid");
			}
		}
	}
	
	@SubscribeEvent
	public void onItemPutChest(LMMNX_Event.LMMNX_ItemPutChestEvent event){
		LMM_EntityLittleMaid maid = event.maid;
//		IInventory target = event.target;
		ItemStack stack = event.stack;
		if(LMMNX_API_Item.isSugar(stack.getItem())|| stack.getItem() == Items.clock){
			event.setCanceled(true);
		}
		if(maid.getMaidModeInt()==LMM_EntityMode_Basic.mmode_FarmPorter){
			if(LMMNX_API_Farmer.isSeed(stack.getItem())||LMMNX_API_Farmer.isHoe(maid, stack)){
				event.setCanceled(true);
			}
			if(event.maidStackIndex>13){
				event.setCanceled(false);
			}
		}
		if(event.maidStackIndex==17&&ItemUtil.isHelm(stack)){
			event.setCanceled(true);
		}
	}
}
