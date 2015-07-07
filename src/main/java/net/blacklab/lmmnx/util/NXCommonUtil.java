package net.blacklab.lmmnx.util;

import littleMaidMobX.LMM_LittleMaidMobNX;

public class NXCommonUtil {
	
	/**
	 * ファイルパスをLinux区切りに変換し、間に挟まった"."を除去します。
	 * @param par1
	 * @return
	 */
	public static String getLinuxAntiDotName(String par1){
		return par1.replace("\\", "/").replace("/.", "");
	}
	
	/**
	 * ファイルからクラスを読み取る時用。root以下にあるpathについてクラス名に変換する。
	 * @param path
	 * @param root
	 * @return
	 */
	public static String getClassName(String path, String root){
		if(!path.endsWith(".class")) return null; 
		LMM_LittleMaidMobNX.Debug("CN %s - %s", path, root);
		
		if(root!=null){
			if(!root.isEmpty()&&path.startsWith(root)){
				path = path.substring(root.length());
			}
		}
		if(path.startsWith("/")) path = path.substring(1);
		if(path.endsWith(".class")) path = path.substring(0,path.lastIndexOf(".class"));
		LMM_LittleMaidMobNX.Debug("CN %s", path.replace("/", "."));
		return path.replace("/", ".");
	}

}
