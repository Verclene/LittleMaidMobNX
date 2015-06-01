package littleMaidMobX;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import net.minecraft.util.ResourceLocation;
import mmmlibx.lib.FileManager;
import mmmlibx.lib.MMMLib;

public class LMM_SoundManager {
	
	/** mods\littleMaidMobX を保持する */
	private static File soundDir = null;
	/** サウンドパックのフォルダまたはZipを保持する。nullの場合はサウンドをロードしない */
	private static File soundPackDir = null;
	private static Map<String, InputStream> soundStreamMap = new HashMap<String, InputStream>();

	public static final String SoundConfigName = "littleMaidMob.cfg";

	// soundindex, value
	public static Map<Integer, String> soundsDefault = new HashMap<Integer, String>();
	// soundIndex, texturePack, color, value
	public static Map<Integer, Map<String, Map<Integer, String>>> soundsTexture = new HashMap<Integer, Map<String,Map<Integer,String>>>();
	public static float soundRateDefault;
	public static Map<String, Map<Integer, Float>> soundRateTexture = new HashMap<String,Map<Integer,Float>>();

	
	public static void init() {
		// 初期設定
		soundDir = new File(FileManager.dirMods, "/littleMaidMobX/");
		if (!getSoundDir().exists() || !getSoundDir().isDirectory()) {
			getSoundDir().mkdirs();
			LMM_LittleMaidMobX.Debug("Create SoundDir: %s", getSoundDir().toString());
		} else {
			LMM_LittleMaidMobX.Debug("SoundDir: %s", getSoundDir().toString());
		}
	}
	
	public static File getSoundDir()
	{
		return soundDir;
	}
	
	public static InputStream getSoundJson()
	{
		// 起動時に自動生成される mods/littleMaidMobX/sounds/sounds.json を読み出す
		try
		{
			return new FileInputStream(new File(getSoundDir(), "sounds.json"));
		}
		catch (FileNotFoundException e) {}

		return null;
	}
	
	public static InputStream getResourceStream(ResourceLocation resource)
	{
		String path = resource.getResourcePath().toLowerCase();
		
		// よく分からんが .mcmeta はいらないのと思うので消す
		if(path.endsWith(".mcmeta"))
		{
			path = path.substring(0, path.length()-7);
		}

		if(path.equalsIgnoreCase("sounds.json"))
		{
			return LMM_SoundManager.getSoundJson();
		}
		
		String fileName = path;
		int c = fileName.lastIndexOf('/');
		if(c >= 0)
		{
			fileName = fileName.substring(c+1);
		}
		
		if(soundStreamMap.size() > 0 && fileName.endsWith(".ogg"))
		{
			return soundStreamMap.get(fileName);
		}

		return null;
	}

	public static void setSoundRate(int soundindex, String value, String target) {
		// 文字列を解析して値を設定
		String arg[] = value.split(",");
		String tvalue;
		Map<Integer, Float> mif;
		if (target == null) {
			target = "";
		} else {
			target = target.trim();
		}
		
		for (String s : arg) {
			if (s.indexOf(';') == -1) {
				// テクスチャ指定詞が無い
				s = s.trim();
				float lf = s.isEmpty() ? 1.0F : Float.valueOf(s);
				if (target.isEmpty()) {
					soundRateDefault = lf;
				} else {
					mif = soundRateTexture.get(target);
					if (mif == null) {
						mif = new HashMap<Integer, Float>();
						soundRateTexture.put(target.trim(), mif);
					}
					mif.put(-1, lf);
				}
			} else {
				// テクスチャ指定詞解析
				String ss[] = s.trim().split(";");
				String ls[];
				if (ss.length < 2) continue;
				if (target.isEmpty()) {
					if (ss.length > 2) {
						ss[0] = ss[0].trim();
						ls = new String[] { ss[0].isEmpty() ? ";" : ss[0], ss[1].trim(), ss[2].trim()};
					} else {
						ls = new String[] { ";", ss[0].trim(), ss[1].trim()};
					}
				} else {
					if (ss.length > 2) {
						ls = new String[] { target, ss[1].trim(), ss[2].trim()};
					} else {
						ls = new String[] { target, ss[0].trim(), ss[1].trim()};
					}
				}
					
				int li = ls[1].isEmpty() ? -1 : Integer.valueOf(ls[1]);
				float lf = ls[2].isEmpty() ? 1.0F : Float.valueOf(ls[2]);
				mif = soundRateTexture.get(ls[0]);
				if (mif == null) {
					mif = new HashMap<Integer, Float>();
					soundRateTexture.put(ls[0], mif);
				}
				mif.put(li, lf);
			}
		}
	}

	public static float getSoundRate(String texturename, int colorvalue){
		if (texturename == null || texturename.length() == 0) texturename = ";";
		Map<Integer, Float> mif = soundRateTexture.get(texturename);
		if (mif == null) {
			// 指定詞のものが無ければ無指定のものを検索
			mif = soundRateTexture.get(";");
			if (mif == null) {
				return soundRateDefault;
			}
		}
		Float lf = mif.get(colorvalue);
		if (lf == null) {
			lf = mif.get(-1);
			if (lf == null) {
				return soundRateDefault;
			}
		}
		return lf;
	}

	public static void setSoundValue(int soundindex, String value, String target) {
		// 文字列を解析して値を設定
		String arg[] = value.split(",");
		
		for (String s : arg) {
			String tvalue;
			if (s.indexOf(';') == -1) {
				// テクスチャ指定詞が無い
				if (target == null || target.isEmpty()) {
					tvalue = value;
				} else {
					tvalue = (new StringBuilder()).append(target).append(";-1;").append(value).toString();
				}
			} else {
				// テクスチャ指定詞解析
				String ss[] = s.trim().split(";");
				if (ss.length == 2) {
					tvalue = (new StringBuilder()).append(target).append(";").append(value).toString();
				} else {
					tvalue = value;
				}
			}
			setSoundValue(soundindex, tvalue);
		}
	}

	public static void setSoundValue(int soundindex, String value) {
		// 文字列を解析して値を設定
		String arg[] = value.split(",");
		
		for (String s : arg) {
			if (s.indexOf(';') == -1) {
				// テクスチャ指定詞が無い
				soundsDefault.put(soundindex, s.trim());
			} else {
				// テクスチャ指定詞解析
				Map<String, Map<Integer, String>> msi = soundsTexture.get(soundindex);
				if (msi == null) {
					msi = new HashMap<String, Map<Integer,String>>();
					soundsTexture.put(soundindex, msi);
				}
				String ss[] = s.trim().split(";");
				if (ss.length < 2) continue;
				if (ss[0].length() == 0) ss[0] = ";";
				Map<Integer, String> mst = msi.get(ss[0]);
				if (mst == null) {
					mst = new HashMap<Integer, String>();
					msi.put(ss[0], mst);
				}
				ss[1] = ss[1].trim();
				int i = ss[1].length() == 0 ? -1 : Integer.valueOf(ss[1]);
				if (ss.length < 3) {
					mst.put(i, "");
				} else {
					mst.put(i, ss[2].trim());
				}
			}
		}
	}

	public static String getSoundValue(LMM_EnumSound enumsound, String texturename, int colorvalue){
		if (enumsound == LMM_EnumSound.Null) return null;
		
		Map<String, Map<Integer, String>> msi = soundsTexture.get(enumsound.index);
		if (msi == null) {
			return soundsDefault.get(enumsound.index);
		}
		
		if (texturename == null || texturename.length() == 0) texturename = ";";
		Map<Integer, String> mst = msi.get(texturename);
		if (mst == null) {
			// 指定詞のものが無ければ無指定のものを検索
			mst = msi.get(";");
			if (mst == null) {
				return soundsDefault.get(enumsound.index);
			}
		}
		String s = mst.get(colorvalue);
		if (s == null) {
			s = mst.get(-1);
			if (s == null) {
				return soundsDefault.get(enumsound.index);
			}
		}
		return LMM_LittleMaidMobX.DOMAIN + ":" + s;
	}

	public static void rebuildSoundPack() {
		// 特殊文字を値に変換
		// Default
		Map<Integer, String> lmap = new HashMap<Integer, String>();
		lmap.putAll(soundsDefault);
		for (Entry<Integer, String> lt : soundsDefault.entrySet()) {
			int li = lt.getKey();
			if (lt.getValue().equals("^")) {
				String ls = lmap.get(li & -16);
				if (ls != null && (li & 0x0f) != 0 && !ls.equals("^")) {
					lmap.put(li, ls);
//					soundsDefault.put(li, ls);
					LMM_LittleMaidMobX.Debug(String.format("soundsDefault[%d] = [%d]", li, li & -16));
				} else {
//					soundsDefault.remove(li);
					LMM_LittleMaidMobX.Debug(String.format("soundsDefault[%d] removed.", li));
				}
			} else {
				lmap.put(li, lt.getValue());
			}
		}
		soundsDefault = lmap;
		
		// Texture
		for (Entry<Integer, Map<String, Map<Integer, String>>> mim : soundsTexture.entrySet()) {
			for (Entry<String, Map<Integer, String>> msm : mim.getValue().entrySet()) {
				
				for (Entry<Integer, String> mis : msm.getValue().entrySet()) {
					if (mis.getValue().equals("^")) {
						boolean lf = false;
						if ((mim.getKey() & 0x0f) != 0) {
							Map<String, Map<Integer, String>> lmsm = soundsTexture.get(mim.getKey() & -16);
							if (lmsm != null) {
								Map<Integer, String> lmis = lmsm.get(msm.getKey());
								if (lmis != null) {
									String ls = lmis.get(mis.getKey());
									if (ls != null && !ls.equals("^")) {
										msm.getValue().put(mis.getKey(), ls);
										lf = true;
										LMM_LittleMaidMobX.Debug(String.format("soundsTexture[%d, %s, %d] = [%d]", mim.getKey(), msm.getKey(), mis.getKey(), mim.getKey() & -16));
									}
								}
							}
						}
						if (!lf) {
							msm.getValue().remove(mis.getKey());
							LMM_LittleMaidMobX.Debug(String.format("soundsTexture[%d, %s, %d] removed.", mim.getKey(), msm.getKey(), mis.getKey()));
						}
					}
				}
			}
		}
	}

	public static void decodeSoundPack(String fileName, Reader reader, boolean iswrite, boolean isdefault) {
		// サウンドパックを解析して音声を設定
		try {
			List<LMM_EnumSound> list1 = new ArrayList<LMM_EnumSound>();
			list1.addAll(Arrays.asList(LMM_EnumSound.values()));
			list1.remove(LMM_EnumSound.Null);
			BufferedReader breader = new BufferedReader(reader);
			boolean loadsoundrate = false;
			String str;
			String packname = fileName;
			packname = packname.substring(0, packname.lastIndexOf("."));
			while ((str = breader.readLine()) != null) {
				str = str.trim();
				if (str.isEmpty() || str.startsWith("#")) continue;
				int i = str.indexOf('=');
				if (i > -1) {
					String name = str.substring(0, i).trim();
					String value = str.substring(i + 1).trim();
					
					int index = -1;
					if (name.startsWith("se_")) {

						// TODO ★ サウンドパックのファイル構成が正しいとは限らないため、ファイル構造を無視して読みだす
						int cd = value.lastIndexOf('.');
						if(cd >= 0) value = value.substring(cd+1);

						// TODO ★ 音声ファイルの指定文字列の末尾に数値が付いてしまっているパックがあるので削除
						value = value.replaceAll("\\d+$", ""); // ファイルの終わりの数値部分を削除
						
						String ss = name.substring(3);
						try {
							index = LMM_EnumSound.valueOf(ss).index;
							list1.remove(LMM_EnumSound.valueOf(ss));
						}
						catch (Exception exception) {
							LMM_LittleMaidMobX.Debug(String.format("unknown sound parameter:%s.cfg - %s", packname, ss));
						}
					} else if (name.equals("LivingVoiceRate")) {
						if (isdefault) {
							setSoundRate(index, value, null);
						} else {
							setSoundRate(index, value, packname);
						}
						loadsoundrate = true;
					}
					if (index > -1) {
						if (isdefault) {
							setSoundValue(index, value);
						} else {
							setSoundValue(index, value, packname);
						}
		    			LMM_LittleMaidMobX.Debug(String.format("%s(%d) = %s", name, index, value));
					}
				}
			}
			breader.close();
			
			// コンフィグファイルがフォルダ内の場合のみ書き込む(Zip内の場合は書き込まない)
			if(iswrite)
			{
				// 無かった項目をcfgへ追加
				if (!list1.isEmpty()) {
					BufferedWriter bwriter = new BufferedWriter(new FileWriter(fileName, true));
					for (int i = 0; i < list1.size(); i++) {
						writeBuffer(bwriter, list1.get(i));
					}
					bwriter.close();
				}
				if (!loadsoundrate) {
					BufferedWriter bwriter = new BufferedWriter(new FileWriter(fileName, true));
					writeBufferSoundRate(bwriter, 1.0F);
					bwriter.close();
				}
			}
		}
		catch (Exception exception) {
			LMM_LittleMaidMobX.Debug("decodeSound Exception.");
		}
	}

	public static void loadSoundPack() {
	/* TODO ★ デフォルトと同じファイルを読み込んでいる？必要？
		if (sounddir.exists() && sounddir.isDirectory()) {
			for (File file : sounddir.listFiles()) {
				if (file.getName().compareToIgnoreCase(SoundConfigName) == 0) {
					continue;
				}
				if (file.isFile() && file.canRead() && file.getName().endsWith(".cfg")) {
					// 音声定義ファイルと認識
					LMM_LittleMaidMobX.Debug("Load SoundPack:" + file.getName());
					decodeSoundPack(file, false);
				}
			}
		} else {
			LMM_LittleMaidMobX.Debug("no Sound Directory.");
		}
		
		rebuildSoundPack();
	*/
	}

	public static void loadDefaultSoundPack()
	{
		try
		{
			boolean loadCfg = loadSoundPackCfg();
		
			if(loadCfg == false)
			{
				File soundCfg = new File(getSoundDir(), "default_" + SoundConfigName);
				soundPackDir = null;
	
				if (soundCfg.exists() && soundCfg.isFile())
				{
					LMM_LittleMaidMobX.Debug(soundCfg.getName());

					Reader reader = new FileReader(soundCfg);
					decodeSoundPack(soundCfg.getName(), reader, true, true);
					reader.close();
				}
				else
				{
					LMM_LittleMaidMobX.Debug("no Default Sound cfg.");
					createDefaultSoundPack(soundCfg);
				}
			}
		}
		catch (Exception e)
		{
			LMM_LittleMaidMobX.Debug("Error: Create Sound cfg failed.");
			e.printStackTrace();
		}
		rebuildSoundPack();
	}
	
	/** mods 直下のディレクトリとZipを全て検索、ディレクトリ内のZipはチェックしない */
	public static boolean loadSoundPackCfg() throws IOException
	{
		for(File file : FileManager.dirMods.listFiles())
		{
			if(file.isDirectory())
			{
				if(searchSoundCfgDir(file))
				{
					soundPackDir = file;
					putAllSoundStream(file);
					createSoundJson(file);
					return true;
				}
			}
			else if(file.getName().toLowerCase().endsWith(".zip"))
			{
				if(searchSoundCfgZip(file))
				{
					soundPackDir = file;
					createSoundJson(file);
					return true;
				}
			}
		}
		return false;
	}

	public static void putAllSoundStream(File dir) throws IOException
	{
		for(File file : dir.listFiles())
		{
			String name = file.getName().toLowerCase();
			if(file.isDirectory())
			{
				putAllSoundStream(file);
			}
			else if(name.endsWith(".ogg"))
			{
				soundStreamMap.put(name, new FileInputStream(file));
			}
		}
	}
	
	// mods配下の全フォルダからコンフィグファイルを検索する
	// 最初に見つけた時点で終了する。2つ以上サウンドパックを入れた場合、どちらが使われるかは保証できない。
	public static boolean searchSoundCfgDir(File dir) throws IOException
	{
		for(File file : dir.listFiles())
		{
			if(file.isDirectory())
			{
				if(searchSoundCfgDir(file))
				{
					return true;
				}
			}
			else if(file.getName().equalsIgnoreCase(SoundConfigName))
			{
				Reader reader = new FileReader(file);
				
				decodeSoundPack(file.getName(), reader, false, true);
				
				reader.close();
				
				return true;
			}
		}
		return false;
	}
	
	// zip配下からコンフィグファイルを検索する
	// 最初に見つけた時点で終了する。2つ以上サウンドパックを入れたら保証できない。
	public static boolean searchSoundCfgZip(File file)
	{
		boolean foundCfg = false;
		try
		{
			FileInputStream fileinputstream = new FileInputStream(file);
			ZipInputStream zipinputstream = new ZipInputStream(fileinputstream);
			ZipEntry zipentry;
			do
			{
				zipentry = zipinputstream.getNextEntry();
				if(zipentry == null)
				{
					break;
				}
				if (!zipentry.isDirectory())
				{
					String name = zipentry.getName();
					int c = name.lastIndexOf("/");
					if(c >= 0)
					{
						name = name.substring(c+1);
					}
					name = name.toLowerCase();
					if (foundCfg==false && name.equalsIgnoreCase(SoundConfigName))
					{
						ZipFile zipFile = new ZipFile(file);
							InputStream inputStream = zipFile.getInputStream(zipentry);
								Reader reader = new InputStreamReader(inputStream);
									decodeSoundPack(name, reader, false, true);
								reader.close();
							inputStream.close();
						zipFile.close();

						foundCfg = true;
						break;
					}
				}
			}
			while(true);
			
			zipinputstream.close();
			fileinputstream.close();
			
			// .cfgを見つけたら、サウンドパックと判断し、oggを全て読み出す
			if(foundCfg)
			{
				fileinputstream = new FileInputStream(file);
				zipinputstream = new ZipInputStream(fileinputstream);
				do
				{
					zipentry = zipinputstream.getNextEntry();
					if(zipentry == null)
					{
						break;
					}
					if (!zipentry.isDirectory())
					{
						String name = zipentry.getName();
						int c = name.lastIndexOf("/");
						if(c >= 0)
						{
							name = name.substring(c+1);
						}
						name = name.toLowerCase();
						if (name.endsWith(".ogg"))
						{
							soundStreamMap.put(name, (new ZipFile(file)).getInputStream(zipentry));
						}
					}
				}
				while(true);
				
				zipinputstream.close();
				fileinputstream.close();
			}
		}
		catch (Exception exception)
		{
			MMMLib.Debug("Load Sound pack Zip-Exception.");
		}
		return foundCfg;
	}

	public static boolean createDefaultSoundPack(File file1) {
		// サウンドのデフォルト値を設定
		for (LMM_EnumSound eslm : LMM_EnumSound.values()) {
			if (eslm == LMM_EnumSound.Null) continue;
			setSoundValue(eslm.index, eslm.DefaultValue);
		}
		
		// デフォルトサウンドパックを作成
		if (file1.exists()) {
			return false;
		}
		try {
			if (file1.createNewFile()) {
				BufferedWriter bwriter = new BufferedWriter(new FileWriter(file1));
				
				for (LMM_EnumSound eslm : LMM_EnumSound.values()) {
					writeBuffer(bwriter, eslm);
				}
				// LivingVoiceRate
				writeBufferSoundRate(bwriter, 1.0F);
				
				bwriter.close();
				LMM_LittleMaidMobX.Debug("Success create Default Sound cfg.");
			}
		} catch (IOException e) {
			LMM_LittleMaidMobX.Debug("Failed create Default Sound cfg(%s).", file1.getAbsolutePath());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected static void writeBuffer(BufferedWriter buffer, LMM_EnumSound enumsound) throws IOException {
		// 渡されたWBufferへ書き込む
		if (enumsound == LMM_EnumSound.Null) return;
		
		buffer.write("# ");
		buffer.write(enumsound.info);
		buffer.newLine();
		
		buffer.write("se_");
		buffer.write(enumsound.name());
		buffer.write("=");
		buffer.write(enumsound.DefaultValue);
		buffer.newLine();
		buffer.newLine();
	}

	protected static void writeBufferSoundRate(BufferedWriter buffer, float prate) throws IOException {
		// 渡されたWBufferへ書き込む
		buffer.write("# Living Voice Rate. 1.0=100%, 0.5=50%, 0.0=0%");
		buffer.newLine();
		buffer.write("LivingVoiceRate=" + prate);
		buffer.newLine();
		buffer.newLine();
	}

	/** 引数には、サウンドが入ったフォルダか、zipを指定 */
	public static void createSoundJson(File dir)
	{
		if(!getSoundDir().exists() || !getSoundDir().isDirectory())
		{
			return;
		}
		
		File file1 = new File(getSoundDir(), "sounds.json");
		try {
			BufferedWriter bwriter = new BufferedWriter(new FileWriter(file1));
			
			String str = searchSoundAndWriteFile("", dir, "");
			bwriter.write("{\n" + str + "\n}\n");
			bwriter.newLine();
			
			bwriter.close();
			LMM_LittleMaidMobX.Debug("Success create Sounds.json(%s).", file1.getAbsolutePath());
		} catch (IOException e) {
			LMM_LittleMaidMobX.Debug("Failed create Sounds.json(%s).", file1.getAbsolutePath());
			e.printStackTrace();
		}
	}

	private static String searchSoundAndWriteFile(String string, File dir, String string2) throws IOException
	{
		if(dir.isDirectory())
		{
			return searchSoundAndWriteFileDir(string, dir, string2);
		}
		else
		{
			return searchSoundAndWriteFileZip(string, dir);
		}
	}

	// 再帰的にフォルダを捜査し、音声ファイルをファイル出力する
	/* 出力例
		{
		"akari":{"category":"master","sounds":["akari1","akari2","akari3"]},
		"attack":{"category":"master","sounds":["attack01","attack02","attack03"]}
		}
	*/
	public static String searchSoundAndWriteFileDir(String output, File dir, String path) throws IOException
	{
		for(File file : dir.listFiles())
		{
			if(file.isDirectory())
			{
				output = output + searchSoundAndWriteFileDir(output, file, path + file.getName() +".");
			}
		}

		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		for(File file : dir.listFiles())
		{
			if(file.isFile() && file.getName().endsWith(".ogg"))
			{
				final String fileName  = file.getName().substring(0, file.getName().length()-4); // 拡張子削除
				final String soundName = fileName.replaceAll("\\d+$", ""); // ファイルの終わりの数値部分を削除
				final String name = fileName.replace(".", "/");
				if(!map.containsKey(soundName))
				{
					map.put(soundName, new ArrayList<String>());
				}
				map.get(soundName).add(name);
			}
		}
		for(String key : map.keySet())
		{
			String s = "";
			for(String name : map.get(key))
			{
				if(s.isEmpty())
				{
					s = "\""+key+"\":{\"category\":\"master\",\"sounds\":[";
				}
				else
				{
					s = s + ",";
				}
				s = s + "\"" + name + "\"";
			}
			s = s + "]}";

			if(!output.isEmpty())
			{
				output = output + ",\n";
			}
			output = output + s;
		}
		
		return output;
	}
	public static String searchSoundAndWriteFileZip(String output, File dir) throws IOException
	{
		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		try
		{
			FileInputStream fileinputstream = new FileInputStream(dir);
			ZipInputStream zipinputstream = new ZipInputStream(fileinputstream);
			ZipEntry zipentry;
			do
			{
				zipentry = zipinputstream.getNextEntry();
				if(zipentry == null)
				{
					break;
				}
				String fileNameInZip = zipentry.getName();
				if (!zipentry.isDirectory() && fileNameInZip.endsWith(".ogg"))
				{
					String fileName  = fileNameInZip.substring(0, fileNameInZip.length()-4); // 拡張子削除
					int c = fileName.lastIndexOf('/');
					if(c >= 0)
					{
						fileName = fileName.substring(c+1);
					}
					
					final String soundName = fileName.replaceAll("\\d+$", ""); // ファイルの終わりの数値部分を削除
					final String name = fileName.replace(".", "/");
					if(!map.containsKey(soundName))
					{
						map.put(soundName, new ArrayList<String>());
					}
					map.get(soundName).add(name);
				}
			}
			while(true);

			zipinputstream.close();
			fileinputstream.close();

			for(String key : map.keySet())
			{
				String s = "";
				for(String name : map.get(key))
				{
					if(s.isEmpty())
					{
						s = "\""+key+"\":{\"category\":\"master\",\"sounds\":[";
					}
					else
					{
						s = s + ",";
					}
					s = s + "\"" + name + "\"";
				}
				s = s + "]}";

				if(!output.isEmpty())
				{
					output = output + ",\n";
				}
				output = output + s;
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
		
		return output;
	}
}
