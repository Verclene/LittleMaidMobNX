package littleMaidMobX;

import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
}
