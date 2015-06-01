package mmmlibx.lib.multiModel.texture;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mmmlibx.lib.FileLoaderBase;
import mmmlibx.lib.MMMLib;
import mmmlibx.lib.multiModel.model.AbstractModelBase;
import mmmlibx.lib.multiModel.model.mc162.ModelLittleMaid_Orign;
import net.minecraft.util.ResourceLocation;

public class MultiModelManager extends FileLoaderBase {

	/**
	 * 古いテクスチャパックに対応するためのテーブル
	 */
	protected static final String defNames[] = {
		"mob_littlemaid0.png", "mob_littlemaid1.png", "mob_littlemaid2.png", "mob_littlemaid3.png",
		"mob_littlemaid4.png", "mob_littlemaid5.png", "mob_littlemaid6.png", "mob_littlemaid7.png",
		"mob_littlemaid8.png", "mob_littlemaid9.png", "mob_littlemaida.png", "mob_littlemaidb.png",
		"mob_littlemaidc.png", "mob_littlemaidd.png", "mob_littlemaide.png", "mob_littlemaidf.png",
		"mob_littlemaidw.png",
		"mob_littlemaid_a00.png", "mob_littlemaid_a01.png"
	};
	
	public static MultiModelManager instance = new MultiModelManager();
	
	protected AbstractModelBase[] defaultModel;
	protected Map<String, AbstractModelBase[]> models;
	protected Map<String, String> modelNames;
	protected Map<String, MultiModelContainer> textures;
	protected List<String> preFixs;


	public MultiModelManager() {
		models = new HashMap<String, AbstractModelBase[]>();
		modelNames = new HashMap<String, String>();
		defaultModel = getModelBase(ModelLittleMaid_Orign.class);
		preFixs = new ArrayList<String>();
		preFixs.add("/mob/littleMaid/");
		preFixs.add("/textures/entity/littleMaid/");
		preFixs.add("/multiModel/");
		textures  = new HashMap<String, MultiModelContainer>();
	}

	@Override
	public boolean isZipLoad() {
		return true;
	}

	@Override
	public void execute() {
		// ファイルを解析してテクスチャパックとモデルのリストを作成する
		super.execute();
		// テクスチャパックにモデルを関連付ける
		setModels();
	}

	@Override
	public void decodeZip(File pFile) {
		// TODO Auto-generated method stub
		super.decodeZip(pFile);
	}

	@Override
	public boolean load(File pFile, String pFileName, InputStream pInputStream) {
//		MMMLib.Debug("loadTexture:%s, %s", pFile.toString(), pFileName);
		if(MMMLib.proxy.isClient())
		{
			if(addModelClass(pFileName))
			{
				return true;
			}
		}
		return addTexture(pFile, pFileName);
	}

	/**
	 * マルチモデルクラスの追加
	 * @param pFileName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean addModelClass(String pFileName) {
		if (pFileName.endsWith(".class") && pFileName.indexOf("$") == -1) {
			String lcname = pFileName.substring(0, pFileName.length() - 6);
			if (lcname.startsWith("/")) {
				lcname = lcname.substring(1);
			}
			lcname = lcname.replace("/", ".");
//			MMMLib.Debug("try MultiModelClass: %s", lcname);
			try {
				ClassLoader lcl = getClass().getClassLoader();
				Class<?> lc = lcl.loadClass(lcname);
				if (AbstractModelBase.class.isAssignableFrom(lc) && !Modifier.isAbstract(lc.getModifiers())) {
					Class<? extends AbstractModelBase> lca = (Class<? extends AbstractModelBase>)lc;
					int lindex = lcname.lastIndexOf('_');
					if (lindex > -1) {
						String lname = lcname.substring(lindex + 1, lcname.length());
						models.put(lcname, getModelBase(lca));
						modelNames.put(lname, lcname);
						MMMLib.Debug("get MultiModelClass: %s(%s)", lname, lcname);
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	protected boolean addTexture(File pFile, String pFileName) {
		if (pFileName.endsWith(".png")) {
			if (!pFileName.startsWith("/")) {
				pFileName = (new StringBuilder()).append("/").append(pFileName).toString();
			}
			for (String lfix : preFixs) {
				int lindex = pFileName.indexOf(lfix);
				if (lindex > -1) {
					String lname = pFileName.substring(pFileName.lastIndexOf('/'));
					int lcol = getIndex(lname);
					if (lcol > -1) {
						lname = pFileName.substring(lindex + lfix.length(), pFileName.lastIndexOf('/'));
						lname.replace('/', '.');
						MultiModelContainer lcon = textures.get(lname);
						if (lcon == null) {
							lcon = new MultiModelContainer();
							textures.put(lname, lcon);
						}
//						MMMLib.Debug("addTexturePack: %s - %d: %s", lname, lcol, pFileName);
						lcon.addTexture(lcol, new ResourceLocation(pFileName));
						return true;
					}
				}
			}
		}
		return false;
	}

	protected void setModels() {
		MMMLib.Debug("setModels execute.");
		for (Entry<String, String> le : modelNames.entrySet()) {
			MMMLib.Debug("models: %s - %s", le.getKey(), le.getValue());
		}
		// body
		for (Entry<String, MultiModelContainer> le : textures.entrySet()) {
			String lmname = le.getKey();
			MultiModelContainer lcont = le.getValue();
			if (lcont.defaultModel == null) {
				MMMLib.Debug("checkModel: %s(%d)", lmname, lcont.getTextureCount());
				int lindex = lmname.lastIndexOf('_');
				if (lindex > 0) {
					lmname = lmname.substring(lindex + 1,lmname.length());
					lcont.defaultModel = getModelFromName(lmname);
				} else {
					lcont.defaultModel = defaultModel;
				}
			}
		}
	}

	protected AbstractModelBase[] getModelFromName(String pName) {
		String ls = modelNames.get(pName);
		return ls == null ? defaultModel : models.get(ls);
	}

	protected AbstractModelBase[] getModelBase(Class<? extends AbstractModelBase> pClass) {
		try {
			Object lo = pClass.getConstructor().newInstance();
			if (lo instanceof AbstractModelBase) {
				AbstractModelBase lmodel = (AbstractModelBase)lo;
				float[] lsize = lmodel.getArmorModelsSize();
				AbstractModelBase[] lamb;
				if (lsize != null && lsize.length > 0) {
					lamb = new AbstractModelBase[1 + lsize.length];
					int li = 1;
					Constructor<?> lc = pClass.getConstructor(float.class);
					for (float lf : lsize) {
						lamb[li++] = (AbstractModelBase)lc.newInstance(lf);
					}
				} else {
					lamb = new AbstractModelBase[1];
				}
				lamb[0] = lmodel;
				return lamb;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected int getIndex(String name) {
		// 文字列からインデックスを取り出す
		for (int i = 0; i < defNames.length; i++) {
			if (name.endsWith(defNames[i])) {
				return i;
			}
		}
		Pattern p = Pattern.compile("_([0-9a-f]+).png");
		Matcher m = p.matcher(name);
		if (m.find()) {
			return Integer.decode("0x" + m.group(1));
		}
		return -1;
	}

	public MultiModelContainer getMultiModel(String pName) {
		MultiModelContainer lcont = textures.get(pName);
		if (lcont == null) {
			lcont = textures.get("default");
		}
		return lcont;
	}

}
