package net.blacklab.lmmnx.client;

import java.util.HashMap;
import java.util.Map;

import littleMaidMobX.LMM_EnumSound;

public class LMMNX_SoundRegistry {
	
	public static final String DEFAULT_TEXTURE_REGISTRATION_KEY = "!#DEFAULT#!";

	private Map<String, Map<LMM_EnumSound, String>> registerMap;
	
	private static LMMNX_SoundRegistry instR = new LMMNX_SoundRegistry();
	
	private LMMNX_SoundRegistry() {
		registerMap = new HashMap<String, Map<LMM_EnumSound, String>>();
		registerMap.put(DEFAULT_TEXTURE_REGISTRATION_KEY, new HashMap<LMM_EnumSound, String>());
	}

	public static void registerSoundHash(String texture, LMM_EnumSound enumSound, String path) {
		// サウンド・パスの登録
		if (texture==null || texture.isEmpty()) texture = DEFAULT_TEXTURE_REGISTRATION_KEY;

		if (instR.registerMap.get(texture) == null) {
			instR.registerMap.put(texture, new HashMap<LMM_EnumSound, String>());
		}
		instR.registerMap.get(texture).put(enumSound, path);
	}
	
	public static String getRegisteredPath(String texture, LMM_EnumSound enumSound) {
		if (texture==null || texture.isEmpty()) texture = DEFAULT_TEXTURE_REGISTRATION_KEY;
		
		Map<LMM_EnumSound, String> r = instR.registerMap.get(texture);
		if (r != null) {
			String s = r.get(enumSound);
			if (!texture.equals(DEFAULT_TEXTURE_REGISTRATION_KEY) && s == null) {
				s = getRegisteredPath(DEFAULT_TEXTURE_REGISTRATION_KEY, enumSound);
			}
			return s;
		}
		return getRegisteredPath(DEFAULT_TEXTURE_REGISTRATION_KEY, enumSound);
	}
	
}
