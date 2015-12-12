package net.blacklab.lmmnx.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import littleMaidMobX.LMM_EnumSound;
import mmmlibx.lib.FileManager;
import net.blacklab.lib.classutil.FileClassUtil;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

/**
 * 新サウンドローディング(from 4.3)
 *
 */
public class LMMNX_SoundLoader {
	
	protected static LMMNX_SoundLoader instance = new LMMNX_SoundLoader();
	private boolean found = false;
	private boolean sound = false;
	
	private List<String> pathStore;
	
	public LMMNX_SoundLoader() {
		pathStore = new ArrayList<String>();
	}
	
	public static boolean isFoundSoundpack() {
		return instance.found;
	}
	
	public static boolean isSoundLoaded() {
		return instance.sound;
	}
	
	public static void load() {
		// 読み込みを開始するstaticメソッド．
		// 処理用のメソッドは全てインスタンス内に．
		instance.searchDir(FileManager.dirMods);
		instance.appendPath();
	}
	
	private void searchDir(File f) {
		if (!f.isDirectory()) {
			throw new IllegalStateException(f.getName()+" is not a directory!");
		}
		for (File t: f.listFiles()) {
			if (t.isDirectory()) {
				searchDir(t);
			}
			if (t.getName().endsWith(".zip")) {
				searchZip(t);
			}
			if (t.getName().endsWith(".ogg")) {
				String c1 = FileClassUtil.getLinuxAntiDotName(t.getAbsolutePath());
				String c2 = FileClassUtil.getLinuxAntiDotName(FileManager.dirMods.getAbsolutePath());
				String p = c1.substring(c2.length());
				if (p.startsWith("/")) {
					p = p.substring(1);
				}
				pathStore.add(p);
			}
			if ("littleMaidMob.cfg".equals(t.getName())) {
				try {
					decodeConfig(new FileInputStream(t));
				} catch (FileNotFoundException e) {
				}
			}
		}
	}
	
	private void searchZip(File f) {
		try {
			FileInputStream inputStream = new FileInputStream(f);
			ZipInputStream zipInputStream = new ZipInputStream(inputStream);
			ZipEntry entry;
			while ((entry = zipInputStream.getNextEntry()) != null) {
				if ("littleMaidMob.cfg".equals(FileClassUtil.getFileName(entry.getName()))) {
					ZipFile zipFile = new ZipFile(f);
					decodeConfig(zipFile.getInputStream((ZipArchiveEntry) entry));
					zipFile.close();
				}
				if (".ogg".equals(entry.getName())) {
					String fString = entry.getName().substring(entry.getName().startsWith("/") ? 1 : 0);
					pathStore.add(fString);
				}
			}
			zipInputStream.close();
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void decodeConfig(InputStream inputStream) {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		try {
			String buf;
			while ((buf = bufferedReader.readLine()) != null) {
				int a = buf.indexOf("=");
				if (buf.startsWith("se_") && a != -1) {
					LMM_EnumSound sound = LMM_EnumSound.valueOf(buf.substring(3, a));
					String enmString = buf.substring(++a);

					for (String s : enmString.split(",")) {
						String[] vlStrings = s.split(";");
						Integer col = -1;
						String name = vlStrings[vlStrings.length - 1];
						switch (vlStrings.length) {
						case 3:
						case 2:
							try {
								col = Integer.valueOf(vlStrings[vlStrings.length - 2]);
								if (col > 15) col = 15;
								if (col < -1) col = -1;
							} catch (NumberFormatException e) {
							}
						case 1:
							LMMNX_SoundRegistry.registerSoundName(sound, LMMNX_SoundRegistry.DEFAULT_TEXTURE_REGISTRATION_KEY, col, name);
							break;
						default:
							break;
						}
					}
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		found = true;
	}
	
	private void appendPath() {
		for (String path : pathStore) {
			// サーチ用に末尾の数値と拡張子を切り落とす
			String p1 = "";
			Pattern pattern = Pattern.compile("(.+)[0-9]*\\.ogg");
			Matcher matcher = pattern.matcher(path);
			if (matcher.find()) {
				p1 = matcher.group();
			} else {
				continue;
			}
			
			// サウンドネーム式に置換
			String p2 = p1.replace('/', '.');

			// サウンドネームとパスの比較を行う．
			// 最低限，サウンドファイルの直上のディレクトリ名が一致していれば登録する．
			// 基本的にネームは「littleMaidMob.〜」と登録されているはずである．
			for (int i=0; i!=-1 && i!=p2.lastIndexOf("."); i=p2.indexOf(".", i+1)) {
				String p3 = "littleMaidMob." + p2.substring(i+1);
				if (LMMNX_SoundRegistry.getRegisteredNamesList().contains(p3)) {
					LMMNX_SoundRegistry.registerSoundPath(p3, path);
				}
			}
		}
	}

}
