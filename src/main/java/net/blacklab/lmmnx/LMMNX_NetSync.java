package net.blacklab.lmmnx;

import java.util.Iterator;

import org.apache.commons.lang3.math.Fraction;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_LittleMaidMobNX;
import mmmlibx.lib.MMM_Helper;
import mmmlibx.lib.MMM_TextureBox;
import mmmlibx.lib.MMM_TextureManager;

public class LMMNX_NetSync {
	
	public static final byte LMMNX_Sync = (byte) 0x84;
	
	public static final byte LMMNX_Sync_UB_Armor   = (byte) 0x00;
	public static final byte LMMNX_Sync_UB_Swim    = (byte) 0x01;
	public static final byte LMMNX_Sync_UB_Freedom = (byte) 0x02;
	//クライアントのみ
	public static final byte LMMNX_Sync_UB_RequestParamRecall = (byte) 0x03;
	
	// サーバがテクスチャ設定を受信
	public static final byte LMMNX_Sync_String_MT_SaveToServer   = (byte) 0x10;
	public static final byte LMMNX_Sync_String_AT_SaveToServer   = (byte) 0x11;
	// サーバから保存されたテクスチャ情報を返す
	public static final byte LMMNX_Sync_String_MT_Return = (byte) 0x12;
	public static final byte LMMNX_Sync_String_AT_Return = (byte) 0x13;
	
	public static void onPayLoad(LMM_EntityLittleMaid pMaid, byte[] pData){
		if(pData==null) return;
		if((pData[5] & 0x10) == 0x10){
			onPayLoad(pMaid, pData[5], MMM_Helper.getStr(pData, 6));
			return;
		}
		if((pData[5] & 0x00)==0x00){
			// byte
			if(pData.length!=7) throw new IndexOutOfBoundsException("Data has wrong size");
			onPayLoad(pMaid, pData[5], pData[6]);
		}
	}
	
	public static void onPayLoad(LMM_EntityLittleMaid pMaid, byte pMode, byte pData){
		switch (pMode) {
		case LMMNX_Sync_UB_Armor:
			LMM_LittleMaidMobNX.Debug("SYNC ARMOR");
			pMaid.setMaidArmorVisible(pData);
			break;
		case LMMNX_Sync_UB_Swim:
			pMaid.setSwimming(pData==(byte)1);
			break;
		case LMMNX_Sync_UB_Freedom:
			pMaid.setFreedom(pData==(byte)1);
			break;
		case LMMNX_Sync_UB_RequestParamRecall :
			pMaid.syncMaidArmorVisible();
			pMaid.recallParamToClient();
			break;
		}
	}
	
	public static void onPayLoad(LMM_EntityLittleMaid pMaid, byte pMode, String pString){
		switch(pMode){
		case LMMNX_Sync_String_MT_SaveToServer:
			pMaid.textureModelNameForClient = pString;
			break;
		case LMMNX_Sync_String_AT_SaveToServer:
			pMaid.textureArmorNameForClient = pString;
			break;
		case LMMNX_Sync_String_MT_Return:
			LMM_LittleMaidMobNX.Debug("SET %s", pString);
			pMaid.setTextureIndex(new int[]{referTextureIndex(pString),pMaid.textureData.textureIndex[1]});
			pMaid.setTextureNames();
			break;
		case LMMNX_Sync_String_AT_Return:
			LMM_LittleMaidMobNX.Debug("SET %s", pString);
			pMaid.setTextureIndex(new int[]{pMaid.textureData.textureIndex[0],referTextureArmorIndex(pString)});
			pMaid.setTextureNames();
			break;
		}
	}

	public static int referTextureIndex(String string) {
		int i=0;
		for (Iterator iterator = MMM_TextureManager.getTextureList().iterator(); iterator.hasNext();) {
			MMM_TextureBox lBox = (MMM_TextureBox) iterator.next();
			if(lBox.textureName.equals(string)){
				return i;
			}
			i++;
		}
		return 0;
	}
	
	public static int referTextureArmorIndex(String string){
		int i=0;
		for (Iterator iterator = MMM_TextureManager.getTextureList().iterator(); iterator.hasNext();) {
			MMM_TextureBox lBox = (MMM_TextureBox) iterator.next();
			if(lBox.hasArmor()&&lBox.textureName.equals(string)){
				return i;
			}
			i++;
		}
		return 0;
	}

}
