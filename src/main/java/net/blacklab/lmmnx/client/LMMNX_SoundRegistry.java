package net.blacklab.lmmnx.client;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import littleMaidMobX.LMM_EnumSound;
import mmmlibx.lib.FileManager;
import net.blacklab.lib.obj.Pair;
import net.blacklab.lib.obj.SinglePair;

public class LMMNX_SoundRegistry {
	
	public static final String DEFAULT_TEXTURE_REGISTRATION_KEY = "!#DEFAULT#!";

	// Sound→((テクスチャ名+色)+パス)の順．
	private Map<LMM_EnumSound, Map<Pair<String, Integer>, String>> registerMap;
	// 実際の参照パス
	private Map<String, List<String>> pathMap;
	
	private static LMMNX_SoundRegistry instR = new LMMNX_SoundRegistry();
	
	private LMMNX_SoundRegistry() {
	}

	public static void registerSoundName(LMM_EnumSound enumSound, String texture, Integer color, String name) {
		// サウンド・ネームの登録
		Map<Pair<String, Integer>, String> map = instR.registerMap.get(enumSound);
		if (map == null) {
			map = new HashMap<Pair<String,Integer>, String>();
		}else if (map.containsKey(new SinglePair(texture, color))) {
			return;
		}
		map.put(new SinglePair<String, Integer>(texture, color), name);
	}
	
	public static List<String> getRegisteredNamesList() {
		List<String> retmap = new ArrayList<String>();
		for (Map<Pair<String, Integer>, String> v: instR.registerMap.values()) {
			for (String f: v.values()) {
				retmap.add(f);
			}
		}
		return retmap;
	}
	
	public static void registerSoundPath(String name, String path) {
		// サウンドの種類を増やす
		List<String> g = instR.pathMap.get(name);
		if (g == null) {
			g = new ArrayList<String>();
		}
		g.add(path);
	}
	
	public static String getSoundRegisteredName(LMM_EnumSound sound, String texture, Integer color) {
		Map<Pair<String, Integer>, String> tMap = instR.registerMap.get(sound);
		if (tMap != null) {
			String value = tMap.get(new SinglePair(texture, color));
			if (value != null) {
				return value;
			}
			value = tMap.get(new SinglePair(texture, -1));
			if (value != null) {
				return value;
			}
			value = tMap.get(new SinglePair(DEFAULT_TEXTURE_REGISTRATION_KEY, color));
			if (value != null) {
				return value;
			}
			value = tMap.get(new SinglePair(DEFAULT_TEXTURE_REGISTRATION_KEY, -1));
			if (value != null) {
				return value;
			}
		}
		return null;
	}
	
	public static String getPathFromRegisteredName(String name){
		List<String> g = instR.pathMap.get(name);
		if (g == null) return null;
		String ret = g.get((int)(Math.random() * g.size()));
		return ret;
	}
	
	public static InputStream getSoundStream(String name) {
		return FileManager.COMMON_CLASS_LOADER.getResourceAsStream(getPathFromRegisteredName(name));
	}
	
	public static InputStream getSoundStream(LMM_EnumSound sound, String texture, Integer color) {
		return getSoundStream(getSoundRegisteredName(sound, texture, color));
	}
	
}
