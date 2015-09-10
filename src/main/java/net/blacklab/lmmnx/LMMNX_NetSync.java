package net.blacklab.lmmnx;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_LittleMaidMobNX;

public class LMMNX_NetSync {
	
	public static final byte LMMNX_Sync_Under_Byte = (byte) 0x84;
	
	public static final byte LMMNX_Sync_UB_Armor   = (byte) 0x00;
	public static final byte LMMNX_Sync_UB_Swim    = (byte) 0x01;
	public static final byte LMMNX_Sync_UB_Freedom = (byte) 0x02;
	//クライアントのみ
	public static final byte LMMNX_Sync_UB_RequestArmorVisibleRecall = (byte) 0x03;
	
	public static void onPayLoad(LMM_EntityLittleMaid pMaid, byte[] pData){
		if(pData==null) return;
		if(pData[0]==LMMNX_Sync_Under_Byte){
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
		case LMMNX_Sync_UB_RequestArmorVisibleRecall :
			LMM_LittleMaidMobNX.Debug("REQUEST RECEIVED");
			pMaid.syncMaidArmorVisible();
			break;
		}
	}

}
