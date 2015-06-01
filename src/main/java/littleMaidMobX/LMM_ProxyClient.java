package littleMaidMobX;

import static littleMaidMobX.LMM_Statics.LMN_Client_PlaySound;
import static littleMaidMobX.LMM_Statics.LMN_Client_SetIFFValue;
import static littleMaidMobX.LMM_Statics.LMN_Client_SwingArm;
import mmmlibx.lib.MMM_EntityDummy;
import mmmlibx.lib.MMM_EntitySelect;
import mmmlibx.lib.MMM_Helper;
import mmmlibx.lib.MMM_RenderDummy;
import mmmlibx.lib.multiModel.model.mc162.RenderModelMulti;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import network.W_Message;

/**
 * クライアント専用処理。
 * マルチ用に分離。
 * 分離しとかないとNoSuchMethodで落ちる。
 */
public class LMM_ProxyClient extends LMM_ProxyCommon
{

	public void init() {
		RenderingRegistry.registerEntityRenderingHandler(LMM_EntityLittleMaid.class,new LMM_RenderLittleMaid(Minecraft.getMinecraft().getRenderManager(),0.3F));
		RenderingRegistry.registerEntityRenderingHandler(MMM_EntitySelect.class,	new RenderModelMulti(Minecraft.getMinecraft().getRenderManager(), 0.0F));
		RenderingRegistry.registerEntityRenderingHandler(MMM_EntityDummy.class,		new MMM_RenderDummy());
// TODO ★		RenderingRegistry.registerEntityRenderingHandler(EntityItem.class,			new MMM_RenderItem());
	}

	/* 呼び出し箇所なし
	public GuiContainer getContainerGUI(EntityClientPlayerMP var1, int var2,
			int var3, int var4, int var5) {
		Entity lentity = var1.worldObj.getEntityByID(var3);
		if (lentity instanceof LMM_EntityLittleMaid) {
			LMM_GuiInventory lgui = new LMM_GuiInventory(var1, (LMM_EntityLittleMaid)lentity);
//			var1.openContainer = lgui.inventorySlots;
			return lgui;
		} else {
			return null;
		}
	}
	*/

// Avatarr
	
	public void onItemPickup(EntityPlayer pAvatar, Entity entity, int i) {
		// アイテム回収のエフェクト
		// TODO:こっちを使うか？
//        mc.effectRenderer.addEffect(new EntityPickupFX(mc.theWorld, entity, avatar, -0.5F));
		MMM_Helper.mc.effectRenderer.addEffect(new EntityPickupFX(MMM_Helper.mc.theWorld, entity, pAvatar, 0.1F));
	}

	public void onCriticalHit(EntityPlayer pAvatar, Entity par1Entity) {
		//1.8後回し
		//MMM_Helper.mc.effectRenderer.addEffect(new EntityCrit2FX(MMM_Helper.mc.theWorld, par1Entity));
	}

	public void onEnchantmentCritical(EntityPlayer pAvatar, Entity par1Entity) {
		//1.8後回し
		//EntityCrit2FX entitycrit2fx = new EntityCrit2FX(MMM_Helper.mc.theWorld, par1Entity, "magicCrit");
		//MMM_Helper.mc.effectRenderer.addEffect(entitycrit2fx);
	}

	
// Network

	public void clientCustomPayload(W_Message var2) {
		// クライアント側の特殊パケット受信動作
		byte lmode = var2.data[0];
		int leid = 0;
		LMM_EntityLittleMaid lemaid = null;
		if ((lmode & 0x80) != 0) {
			leid = MMM_Helper.getInt(var2.data, 1);
			lemaid =LMM_Net.getLittleMaid(var2.data, 1, MMM_Helper.mc.theWorld);
			if (lemaid == null) return;
		}
		LMM_LittleMaidMobNX.Debug(String.format("LMM|Upd Clt Call[%2x:%d].", lmode, leid));
		
		switch (lmode) {
		case LMN_Client_SwingArm : 
			// 腕振り
			byte larm = var2.data[5];
			LMM_EnumSound lsound = LMM_EnumSound.getEnumSound(MMM_Helper.getInt(var2.data, 6));
			lemaid.setSwinging(larm, lsound);
//			mod_LMM_littleMaidMob.Debug(String.format("SwingSound:%s", lsound.name()));
			break;
			
		case LMN_Client_SetIFFValue:
			// IFFの設定値を受信
			int lval = var2.data[1];
			int lindex = MMM_Helper.getInt(var2.data, 2);
			String lname = (String)LMM_IFF.DefaultIFF.keySet().toArray()[lindex];
			LMM_LittleMaidMobNX.Debug("setIFF-CL %s(%d)=%d", lname, lindex, lval);
			LMM_IFF.setIFFValue(null, lname, lval);
			break;
			
		case LMN_Client_PlaySound : 
			// 音声再生
			LMM_EnumSound lsound9 = LMM_EnumSound.getEnumSound(MMM_Helper.getInt(var2.data, 5));
			lemaid.playSound(lsound9, true);
			LMM_LittleMaidMobNX.Debug(String.format("playSound:%s", lsound9.name()));
			break;
			
		}
	}

	public EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().thePlayer;
	}

	/* 呼び出し箇所なし
	public static void setAchievement() {
// MinecraftクラスからstatFileWriterが消えてる
//		MMM_Helper.mc.statFileWriter.readStat(mod_LMM_littleMaidMob.ac_Contract, 1);
	}
	*/

	public void loadSounds()
	{
		// 音声の解析
		LMM_SoundManager.init();
		// サウンドパック
		LMM_SoundManager.loadDefaultSoundPack();
		LMM_SoundManager.loadSoundPack();
	}

	public boolean isSinglePlayer()
	{
		return Minecraft.getMinecraft().isSingleplayer();
	}
}
