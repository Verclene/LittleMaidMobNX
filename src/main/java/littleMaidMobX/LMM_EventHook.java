package littleMaidMobX;

import net.blacklab.lmmnx.util.Version;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

public class LMM_EventHook
{
	@SubscribeEvent
	public void onEntityItemPickupEvent(EntityItemPickupEvent event)
	{
		if(event.entityPlayer instanceof LMM_EntityLittleMaidAvatar)
		{
			if(event.item!=null && LMM_LittleMaidMobNX.isMaidIgnoreItem(event.item.getEntityItem()))
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
			Version.VersionData v = Version.getLatestVersion();
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
}
