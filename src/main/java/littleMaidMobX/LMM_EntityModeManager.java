package littleMaidMobX;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import mmmlibx.lib.FileManager;
import mmmlibx.lib.MMM_ManagerBase;

public class LMM_EntityModeManager extends MMM_ManagerBase {
	// TODO issue #9 merge from LittleMaidMobAX(https://github.com/asiekierka/littleMaidMobX/commit/c226adf6c2fbe888eb45613d16a95460fb4ebba4)
	
	public static class EntityPriorityComparator implements Comparator<LMM_EntityModeBase> {
		@Override
		public int compare(LMM_EntityModeBase o1, LMM_EntityModeBase o2) {
			return o1.priority() - o2.priority();
		}
	}

	public static final String prefix = "EntityMode";
	public static final List<LMM_EntityModeBase> maidModeList = new ArrayList<LMM_EntityModeBase>();


	public static void init() {
		// 特定名称をプリフィックスに持つmodファイをを獲得
		FileManager.getModFile("EntityMode", prefix);
	}
	
	public static void loadEntityMode() {
		(new LMM_EntityModeManager()).load();
	}

	@Override
	protected String getPreFix() {
		return prefix;
	}

	@Override
	protected boolean append(Class pclass) {
		// プライオリティー順に追加
		// ソーター使う？
		if (!LMM_EntityModeBase.class.isAssignableFrom(pclass)) {
			return false;
		}
		
		try {
			LMM_EntityModeBase lemb = null;
			lemb = (LMM_EntityModeBase)pclass.getConstructor(LMM_EntityLittleMaid.class).newInstance((LMM_EntityLittleMaid)null);
			lemb.init();
			/*
			if (maidModeList.isEmpty() || lemb.priority() >= maidModeList.get(maidModeList.size() - 1).priority()) {
				maidModeList.add(lemb);
			} else {
				for (int li = 0; li < maidModeList.size(); li++) {
					if (lemb.priority() < maidModeList.get(li).priority()) {
						maidModeList.add(li, lemb);
						break;
					}
				}
			}*/
			maidModeList.add(lemb);
			return true;
		} catch (Exception e) {
			LMM_LittleMaidMobNX.Debug("Failed to load Entity Mode class %s!" + pclass.getName());
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * AI追加用のリストを獲得。 
	 */
	public static List<LMM_EntityModeBase> getModeList(LMM_EntityLittleMaid pentity) {
		List<LMM_EntityModeBase> llist = new ArrayList<LMM_EntityModeBase>();
		for (LMM_EntityModeBase lmode : maidModeList) {
			try {
				llist.add(lmode.getClass().getConstructor(LMM_EntityLittleMaid.class).newInstance(pentity));
			} catch (Exception e) {
			} catch (Error e) {
			}
		}
		
		return llist;
	}

	/**
	 * ロードされているモードリストを表示する。
	 */
	public static void showLoadedModes() {
		LMM_LittleMaidMobNX.Debug("Loaded Mode lists(%d)", maidModeList.size());
		for (LMM_EntityModeBase lem : maidModeList) {
			LMM_LittleMaidMobNX.Debug("%04d : %s", lem.priority(), lem.getClass().getSimpleName());
		}
	}

}
