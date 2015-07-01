package littleMaidMobX;

import net.blacklab.lmmnx.util.Version;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class LMMNX_LoggedInEvent {
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

}
