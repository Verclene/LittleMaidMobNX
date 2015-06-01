package mmmlibx.lib.guns;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * 初期化処理とかを書く
 *
 */
public class GunsBase {

	public static ArrayList<EntityPlayer> playerList = new ArrayList<EntityPlayer>();
	public static boolean isDebugMessage = true;

	protected static List<ItemGunsBase> appendList = new ArrayList<ItemGunsBase>();


	public static void Debug(String pText, Object... pData) {
		// デバッグメッセージ
		if (isDebugMessage) {
			System.out.println(String.format("GunsBase-" + pText, pData));
		}
	}

	public static void init() {
		// イベントハンドラの登録
		GunsBase lgunsbase = new GunsBase();
		FMLCommonHandler.instance().bus().register(lgunsbase);
		MinecraftForge.EVENT_BUS.register(lgunsbase);
	}

	public static void setholdSight(EntityPlayer pPlayer) {
		playerList.add(pPlayer);
	}
/*
	@SubscribeEvent
	public boolean onTick(TickEvent.ClientTickEvent pEvent) {
		// 登録されたプレーヤに対し残心を指示
		if (pEvent.side == Side.CLIENT) {
			for (EntityPlayer lplayer : playerList) {
				ItemStack lis = lplayer.getCurrentEquippedItem();
				if (lis.getItem() instanceof ItemGunsBase) {
					ItemGunsBase lgun = (ItemGunsBase)lis.getItem();
					lplayer.setItemInUse(lis, lgun.getHoldTime(lis));
					Debug("%d: %s, %s", lplayer.getItemInUseCount(), lplayer.getItemInUse().toString(), lgun.toString());
				}
			}
			playerList.clear();
		}
		return true;
	}
*/
	/**
	 * アイテムスタックの変動によるアイテムの再使用を抑制する
	 */
	@SuppressWarnings("unchecked")
	public static void setUncheckedItemStack(ItemStack pGun, EntityPlayer pPlayer) {
		try {
			Class<?> lclass = ReflectionHelper.getClass(pGun.getClass().getClassLoader(), "net.minecraft.entity.EntityLivingBase");
			Field lfield = ReflectionHelper.findField(lclass, "previousEquipment", "field_82180_bT");
			ItemStack[] lequipments = (ItemStack[])lfield.get(pPlayer);
			lequipments[0] = pGun.copy();
			lfield.set(pPlayer, lequipments);
//			ReflectionHelper.setPrivateValue(lclass, pPlayer, lequipments, "previousEquipment");
			if (pPlayer instanceof EntityPlayer) {
				Container lctr = pPlayer.openContainer;
				for (int li = 0; li < lctr.inventorySlots.size(); li++) {
					ItemStack lis = lctr.getSlot(li).getStack(); 
					if (lis == pGun) {
						lctr.inventoryItemStacks.set(li, pGun.copy());
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


/*
	// DebugMessage
	@SubscribeEvent
	public void onplayerUseEventStart(PlayerUseItemEvent.Start pEvent) {
		Debug("EventStart: %s - %s",
				pEvent.entityPlayer instanceof EntityPlayerMP ? "MP" : "SP",
				pEvent.item.toString());
	}

	@SubscribeEvent
	public void onplayerUseEventStop(PlayerUseItemEvent.Stop pEvent) {
		Debug("EventStop: %s - %s",
				pEvent.entityPlayer instanceof EntityPlayerMP ? "MP" : "SP",
				pEvent.item.toString());
	}

	@SubscribeEvent
	public void onplayerUseEventFinish(PlayerUseItemEvent.Finish pEvent) {
		Debug("EventFinish: %s - %s",
				pEvent.entityPlayer instanceof EntityPlayerMP ? "MP" : "SP",
				pEvent.item.toString());
	}

	@SubscribeEvent
	public boolean onPlayerTick(TickEvent.PlayerTickEvent pEvent) {
		// 登録されたプレーヤに対し残心を指示
		if (pEvent.phase == Phase.START) {
			ItemStack lis1 = pEvent.player.getItemInUse();
			ItemStack lis2 = pEvent.player.inventory.getCurrentItem();
			Debug("EventTick: %s - %s : %s (%b)",
					pEvent.player instanceof EntityPlayerMP ? "MP" : "SP",
					lis1 == null ? "null" : lis1.toString(),
					lis2 == null ? "null" : lis2.toString(),
					lis1 == lis2);
		}
		return true;
	}
*/
	
	public static void appendItem(ItemGunsBase pItem) {
		appendList.add(pItem);
	}

	public static void initAppend() {
		for (ItemGunsBase li : appendList) {
			li.init();
		}
		appendList.clear();
	}

}
