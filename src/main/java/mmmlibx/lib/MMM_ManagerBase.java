package mmmlibx.lib;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import littleMaidMobX.LMM_LittleMaidMobNX;
import net.blacklab.lib.classutil.FileClassUtil;
import net.blacklab.lmmnx.util.LMMNX_DevMode;

public abstract class MMM_ManagerBase {

	protected abstract String getPreFix();
	/**
	 * 追加処理の本体
	 */
	protected abstract boolean append(Class pclass);


	protected void load() {
		// ロード
		
		// 開発用
		Package lpackage = MMMLib.class.getPackage();
		String ls = "";
		if (lpackage != null) {
			ls = MMMLib.class.getPackage().getName().replace('.', File.separatorChar);
		}

		if(LMMNX_DevMode.DEVMODE != LMMNX_DevMode.NOT_IN_DEV){
			startSearch(FileManager.dirDevClasses, true);
			if(LMMNX_DevMode.DEVMODE == LMMNX_DevMode.DEVMODE_ECLIPSE){
				for(File f:FileManager.dirDevIncludeClasses){
					startSearch(f, true);
				}
			}
		}

		startSearch(FileManager.dirMods, false);
	}
	
	private void startSearch(File root, boolean dev){
		if(dev){
			if (root.isDirectory()) {
				// ディレクトリの解析
				decodeDirectory(root, root);
			} else {
				// Zipの解析
				decodeZip(root);
			}
			return;
		}
		
		// mods
		for (File lf : root.listFiles()) {
			if (lf.isDirectory()) {
				// ディレクトリの解析
				startSearch(lf, false);
				if (root.equals(FileManager.dirMods)) {
					decodeDirectory(lf, root);
				}
			} else {
				// Zipの解析
				decodeZip(lf);
			}
		}
	}

	private void decodeDirectory(File pfile, File pRoot) {
		// ディレクトリ内のクラスを検索
		for (File lf : pfile.listFiles()) {
			if (lf.isFile()) {
				String lname = lf.getName();
				if (lname.indexOf(getPreFix()) >= 0 && lname.endsWith(".class")) {
					// 対象クラスファイルなのでロード
					//ディレクトリはパスを自動で治してくれないので、手動で。
					loadClass(FileClassUtil.getClassName(
							FileClassUtil.getLinuxAntiDotName(lf.getAbsolutePath()), 
							FileClassUtil.getLinuxAntiDotName(pRoot.getAbsolutePath())));
				}
			}else{
				//ディレクトリの場合は中身も捜索
				decodeDirectory(lf, pRoot);
			}
		}
	}

	private void decodeZip(File pfile) {
		// zipファイルを解析
		try {
			FileInputStream fileinputstream = new FileInputStream(pfile);
			ZipInputStream zipinputstream = new ZipInputStream(fileinputstream);
			ZipEntry zipentry;
			
			do {
				zipentry = zipinputstream.getNextEntry();
				if(zipentry == null) {
					break;
				}
				if (!zipentry.isDirectory()) {
					String lname = zipentry.getName();
					if (lname.indexOf(getPreFix()) >= 0 && lname.endsWith(".class")) {
						loadClass(zipentry.getName());
					}
				}
			} while(true);
			
			zipinputstream.close();
			fileinputstream.close();
		}
		catch (Exception exception) {
			MMMLib.Debug("add%sZip-Exception.", getPreFix());
		}
		
	}

	private void loadClass(String pname) {
		String lclassname = "";
		// 対象ファイルをクラスとしてロード
		try {
			Package lpackage = MMMLib.class.getPackage();
			lclassname = pname.endsWith(".class") ? pname.substring(0, pname.lastIndexOf(".class")) : pname;
			Class lclass;
			if(lpackage != null) {
	// TODO ★	lclassname = (new StringBuilder(String.valueOf(lpackage.getName()))).append(".").append(lclassname).toString();
				lclassname = lclassname.replace("/", ".");
// LMM_EntityModeManager でしか使ってないので暫定
				lclass = FileManager.COMMON_CLASS_LOADER.loadClass(lclassname);
			} else {
				lclass = Class.forName(lclassname);
			}
			if (Modifier.isAbstract(lclass.getModifiers())) {
				return;
			}
			if (append(lclass)) {
				MMMLib.Debug("get%sClass-done: %s", getPreFix(), lclassname);
			} else {
				MMMLib.Debug("get%sClass-fail: %s", getPreFix(), lclassname);
			}
			/*
            if (!(MMM_ModelStabilizerBase.class).isAssignableFrom(lclass) || Modifier.isAbstract(lclass.getModifiers())) {
            	MMMLib.Debug(String.format(String.format("get%sClass-fail: %s", pprefix, lclassname)));
                return;
            }
            
            MMM_ModelStabilizerBase lms = (MMM_ModelStabilizerBase)lclass.newInstance();
            pmap.put(lms.getName(), lms);
            MMMLib.Debug(String.format("get%sClass-done: %s[%s]", pprefix, lclassname, lms.getName()));
            */
		}
		catch (Exception exception) {
			MMMLib.Debug("get%sClass-Exception.(%s)", getPreFix(), lclassname);
			if(LMMNX_DevMode.DEVELOPMENT_DEBUG_MODE && LMM_LittleMaidMobNX.cfg_PrintDebugMessage) exception.printStackTrace();
		}
		catch (Error error) {
			MMMLib.Debug("get%sClass-Error: %s", getPreFix(), lclassname);
			if(LMMNX_DevMode.DEVELOPMENT_DEBUG_MODE && LMM_LittleMaidMobNX.cfg_PrintDebugMessage) error.printStackTrace();
		}
		
	}
	
	
}
