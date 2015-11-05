package net.blacklab.lmmnx;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class LMMNX_Achievements {

	// 契約
	public static Achievement ac_Contract;
	
	// 各モード
	public static Achievement ac_Fencer;
	public static Achievement ac_Archer;
	public static Achievement ac_Cook;
	public static Achievement ac_Farmer;
	public static Achievement ac_Healer;
	public static Achievement ac_Pharmacist;
	public static Achievement ac_Shearer;
	public static Achievement ac_TorchLayer;
	
	// モード拡張
	public static Achievement ac_RandomKiller;
	public static Achievement ac_Buster;

	public static Achievement ac_BlazingStar;
	
	// アクション
	public static Achievement ac_Overprtct;
	
	public static void initAchievements() {
		ac_Contract		= (Achievement) new Achievement("achievement.contract"		, "contract"	, 0, 0, Items.cake				, null			).initIndependentStat().registerStat();
		ac_Fencer		= (Achievement) new Achievement("achievement.fencer"		, "fencer"		,-4,-2, Items.diamond_sword		, ac_Contract	).initIndependentStat().registerStat();
		ac_RandomKiller	= (Achievement) new Achievement("achievement.bloodsucker"	, "bloodsucker"	,-4,-4, Items.diamond_axe		, ac_Fencer		).initIndependentStat().registerStat();
		ac_Buster		= (Achievement) new Achievement("achievement.zombuster"		, "zombuster"	,-5,-4, Items.iron_shovel		, ac_Fencer		).initIndependentStat().registerStat();
		ac_Archer		= (Achievement) new Achievement("achievement.archer"		, "archer"		,-3,-2, Items.bow				, ac_Contract	).initIndependentStat().registerStat();
		ac_BlazingStar	= (Achievement) new Achievement("achievement.blazingstar"	, "blazingstar"	,-3,-4, Items.flint_and_steel	, ac_Archer		).initIndependentStat().registerStat();
		ac_Cook			= (Achievement) new Achievement("achievement.cooking"		, "cooking"		,-2,-2, Items.coal				, ac_Contract	).initIndependentStat().registerStat();
		ac_Farmer		= (Achievement) new Achievement("achievement.farmer"		, "farmer"		,-1,-2, Items.diamond_hoe		, ac_Contract	).initIndependentStat().registerStat();
		ac_Healer		= (Achievement) new Achievement("achievement.healer"		, "healer"		, 1,-2, Items.bread				, ac_Contract	).initIndependentStat().registerStat();
		ac_Pharmacist	= (Achievement) new Achievement("achievement.pharmacist"	, "pharmacist"	, 2,-2, Items.nether_wart		, ac_Contract	).initIndependentStat().registerStat();
		ac_Shearer		= (Achievement) new Achievement("achievement.ripper"		, "ripper"		, 3,-2, Items.shears			, ac_Contract	).initIndependentStat().registerStat();
		ac_TorchLayer	= (Achievement) new Achievement("achievement.torcher"		, "torcher"		, 4,-2, Blocks.torch			, ac_Contract	).initIndependentStat().registerStat();
		
		ac_Overprtct	= (Achievement) new Achievement("achievement.overprtct"		, "overprtct"	, 6,-4, Items.diamond_chestplate, ac_Contract	).initIndependentStat().registerStat();

		Achievement[] achievements = new Achievement[] {
				ac_Contract,
				ac_Fencer,
				ac_RandomKiller,
				ac_Buster,
				ac_Archer,
				ac_BlazingStar,
				ac_Cook,
				ac_Farmer,
				ac_Healer,
				ac_Pharmacist,
				ac_Shearer,
				ac_TorchLayer,
				ac_Overprtct
				};
		AchievementPage.registerAchievementPage(new AchievementPage("LittleMaidNX", achievements));
	}

}
