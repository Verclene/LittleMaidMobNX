package littleMaidMobX;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import mmmlibx.lib.MMM_Helper;
import mmmlibx.lib.MMM_TextureManager;
import net.blacklab.lib.ConfigList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import network.W_Network;

@Mod(	modid   = LMM_LittleMaidMobNX.DOMAIN,
		name    = "LittleMaidMobNX",
		version = LMM_LittleMaidMobNX.VERSION)
public class LMM_LittleMaidMobNX {

	public static final String DOMAIN = "lmmx";
	public static final String VERSION = "NX1B52-1.8-F1450";
	public static final int VERSION_CODE = 3;
	
	public static String[] cfg_comment = {
		"spawnWeight = Relative spawn weight. The lower the less common. 10=pigs. 0=off",
		"spawnLimit = Maximum spawn count in the World.",
		"minGroupSize = Minimum spawn group count.",
		"maxGroupSize = Maximum spawn group count.",
		"canDespawn = It will despawn, if it lets things go. ",
		"checkOwnerName = At local, make sure the name of the owner. ",
		"antiDoppelganger = Not to survive the doppelganger. ",
		"enableSpawnEgg = Enable LMM SpawnEgg Recipe. ",
		"VoiceDistortion = LittleMaid Voice distortion.",
		"defaultTexture = Default selected Texture Packege. Null is Random",
		"DebugMessage = Print Debug Massages.",
		"DeathMessage = Print Death Massages.",
		"Dominant = Spawn Anywhere.",
		"Aggressive = true: Will be hostile, false: Is a pacifist",
		"IgnoreItemList = aaa, bbb, ccc: Items little maid to ignore",
//		"AchievementID = used Achievement index.(0 = Disable)",
//		"UniqueEntityId = UniqueEntityId(0 is AutoAssigned. max 255)"
	};

	public static ConfigList cfg;
//	@MLProp(info="Relative spawn weight. The lower the less common. 10=pigs. 0=off")
	public static int cfg_spawnWeight = 5;
//	@MLProp(info="Maximum spawn count in the World.")
	public static int cfg_spawnLimit = 20;
//	@MLProp(info="Minimum spawn group count.")
	public static int cfg_minGroupSize = 1;
//	@MLProp(info="Maximum spawn group count.")
	public static int cfg_maxGroupSize = 3;
//	@MLProp(info="It will despawn, if it lets things go. ")
	public static boolean cfg_canDespawn = false;
//	@MLProp(info="At local, make sure the name of the owner. ")
	public static boolean cfg_checkOwnerName = false;
//	@MLProp(info="Not to survive the doppelganger. ")
	public static boolean cfg_antiDoppelganger = true;
//	@MLProp(info="Enable LMM SpawnEgg Recipe. ")
	public static boolean cfg_enableSpawnEgg = true;


//	@MLProp(info="LittleMaid Voice distortion.")
	public static boolean cfg_VoiceDistortion = true;

//	@MLProp(info="Default selected Texture Packege. Null is Random")
	public static String cfg_defaultTexture = "";
//	@MLProp(info="Print Debug Massages.")
	public static boolean cfg_PrintDebugMessage = false;
//	@MLProp(info="Print Death Massages.")
	public static boolean cfg_DeathMessage = true;
//	@MLProp(info="Spawn Anywhere.")
	public static boolean cfg_Dominant = false;
	//アルファブレンド
	public static boolean cfg_isModelAlphaBlend = false;

//	@MLProp(info="true: AlphaBlend(request power), false: AlphaTest(more fast)")
//	public static boolean AlphaBlend = true;
//	@MLProp(info="true: Will be hostile, false: Is a pacifist")
	public static boolean cfg_Aggressive = true;
	public static String cfg_IgnoreItemList = "arsmagica2";
	//サウンド試験調整
	public static boolean cfg_ignoreForceSound = false;
	public static int cfg_soundPlayChance = 2;
	
	public static boolean cfg_forceLivingSound = false;
	public static int cfg_coolTimePlaySound = 20;

	//1.8後回し
	public static Achievement ac_Contract;

	@SidedProxy(
			clientSide = "littleMaidMobX.LMM_ProxyClient",
			serverSide = "littleMaidMobX.LMM_ProxyCommon")
	public static LMM_ProxyCommon proxy;

	@Instance(DOMAIN)
	public static LMM_LittleMaidMobNX instance;

	public static LMM_ItemSpawnEgg spawnEgg;

	public static void Debug(String pText, Object... pVals) {
		// デバッグメッセージ
		if (cfg_PrintDebugMessage) {
			System.out.println(String.format("littleMaidMob-" + pText, pVals));
		}
	}
	
	public String getName() {
		return "littleMaidMobNX";
	}

	public String getPriorities() {
		// MMMLibを要求
		return "required-after:mod_MMM_MMMLib";
	}

	public String getVersion() {
		return "1.8";
	}
	
	public static Random randomSoundChance;

	@EventHandler
	public void PreInit(FMLPreInitializationEvent evt)
	{
		//FileManager.setSrcPath(evt.getSourceFile());
		//MMM_Config.init();

		// MMMLibのRevisionチェック
//		MMM_Helper.checkRevision("6");
		//MMM_Config.checkConfig(this.getClass());
		
		randomSoundChance = new Random();

		//Config
		cfg = new ConfigList();
		try {
			cfg.loadConfig(getName(), evt);
		} catch (IOException e) {
			e.printStackTrace();
		}
		cfg_Aggressive = cfg.getBoolean("Aggressive", true);
		cfg_antiDoppelganger = cfg.getBoolean("antiDoppelganger", true);
		cfg_canDespawn = cfg.getBoolean("canDespawn", false);
		cfg_checkOwnerName = cfg.getBoolean("checkOwnerName", true);
		cfg_DeathMessage = cfg.getBoolean("DeathMessage", true);
		cfg_defaultTexture = cfg.getString("defaultTexture", "default_Origin");
		cfg_Dominant = cfg.getBoolean("Dominant", false);
		cfg_enableSpawnEgg = cfg.getBoolean("enableSpawnEgg", true);
		cfg_IgnoreItemList = cfg.getString("IgnoreItemList", "");
		cfg_maxGroupSize = cfg.getInt("maxGroupSize", 3);
		cfg_minGroupSize = cfg.getInt("minGroupSize", 1);
		cfg_PrintDebugMessage = cfg.getBoolean("PrintDebugMessage", false);
		cfg_spawnLimit = cfg.getInt("spawnLimit", 20);
		cfg_spawnWeight = cfg.getInt("spawnWeight", 5);
		cfg_isModelAlphaBlend = cfg.getBoolean("isModelAlphaBlend", true);
		
		cfg_ignoreForceSound = cfg.getBoolean("ignoreForceSound", false);
		cfg_soundPlayChance = Math.max(1,cfg.getInt("soundPlayChance", 2));
		cfg_forceLivingSound = cfg.getBoolean("forceLivingSound", false);
		cfg_coolTimePlaySound = Math.max(cfg.getInt("coolTimePlaySound", 5),20);
		
		try {
			cfg.saveConfig(getName(), evt);
		} catch (IOException e) {
			e.printStackTrace();
		}

		cfg_defaultTexture = cfg_defaultTexture.trim();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new LMM_GuiCommonHandler());

		MMM_TextureManager.instance.init();

		EntityRegistry.registerModEntity(LMM_EntityLittleMaid.class, "LittleMaidX", 0, instance, 80, 3, true);

		/* langファイルに移動
		ModLoader.addLocalization("entity.LittleMaidX.name", "LittleMaidX");
		ModLoader.addLocalization("entity.LittleMaidX.name", "ja_JP", "リトルメイド");
		*/
		// アイテム自体は登録しておき、レシピを隠して無効化
		spawnEgg = new LMM_ItemSpawnEgg();
		spawnEgg.setUnlocalizedName(DOMAIN + ":spawn_lmmx_egg");
		//spawnEgg.setTextureName(DOMAIN + ":spawn_lmmx_egg");
		GameRegistry.registerItem(spawnEgg, "spawn_lmmx_egg");
		if (cfg_enableSpawnEgg) {
			// 招喚用レシピを追加
			GameRegistry.addRecipe(new ItemStack(spawnEgg, 1), new Object[] {
				"scs",
				"sbs",
				" e ",
				Character.valueOf('s'), Items.sugar,
				Character.valueOf('c'), new ItemStack(Items.dye, 1, 3),
				Character.valueOf('b'), Items.slime_ball,
				Character.valueOf('e'), Items.egg,
			});
		}

		ac_Contract = (Achievement) new Achievement("achievement.contract", "contract", 0, 0, Items.cake, null).initIndependentStat().registerStat();
		Achievement[] achievements = new Achievement[] { ac_Contract };
		AchievementPage.registerAchievementPage(new AchievementPage("LittleMaidNX", achievements));

		// AIリストの追加
		LMM_EntityModeManager.init();

		// アイテムスロット更新用のパケット
		W_Network.init(DOMAIN);

		//Model
		if(evt.getSide()==Side.CLIENT) ModelLoader.setCustomModelResourceLocation(LMM_LittleMaidMobNX.spawnEgg, 0, new ModelResourceLocation("lmmx:spawn_lmmx_egg","inventory"));

//		Debug("GUID-sneak: %s", LMM_EntityLittleMaid.maidUUIDSneak.toString());
		proxy.loadSounds();
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		if (MMM_Helper.isClient) {
			// 名称変換テーブル
			/* langファイルに移動
			ModLoader.addLocalization("littleMaidMob.text.Health", "Health");
			ModLoader.addLocalization("littleMaidMob.text.Health", "ja_JP", "メイド強度");
			ModLoader.addLocalization("littleMaidMob.text.AP", "AP");
			ModLoader.addLocalization("littleMaidMob.text.AP", "ja_JP", "メイド装甲");
			ModLoader.addLocalization("littleMaidMob.text.STATUS", "Status");
			ModLoader.addLocalization("littleMaidMob.text.STATUS", "ja_JP", "メイド状態");
			*/
			List<IResourcePack> defaultResourcePacks = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks", "field_110449_ao");
			defaultResourcePacks.add(new LMM_SoundResourcePack());
			defaultResourcePacks.add(new LMMNX_OldZipTexturesLoader());

			// デフォルトモデルの設定
			proxy.init();
		}

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt)
	{
		// カンマ区切りのアイテム名のリストを配列にして設定
		// "aaa, bbb,ccc  " -> "aaa" "bbb" "ccc"
		ignoreItemList = cfg_IgnoreItemList.trim().split("\\s*,\\s*");

		MinecraftForge.EVENT_BUS.register(new LMM_EventHook());

		// デフォルトモデルの設定
		MMM_TextureManager.instance.setDefaultTexture(LMM_EntityLittleMaid.class, MMM_TextureManager.instance.getTextureBox("default_Orign"));

		// Dominant
		BiomeGenBase[] biomeList = null;
		if(cfg_spawnWeight > 0) {
			if (cfg_Dominant)
			{
				biomeList = BiomeGenBase.getBiomeGenArray();
			}
			else
			{
				// 通常スポーン設定バイオームは適当
				biomeList = new BiomeGenBase[]{
						BiomeGenBase.desert,
						BiomeGenBase.plains,
						BiomeGenBase.savanna,
						BiomeGenBase.mushroomIsland,
						BiomeGenBase.forest,
						BiomeGenBase.birchForest,
						BiomeGenBase.swampland,
						BiomeGenBase.taiga,
				};
			}
			for(BiomeGenBase biome : biomeList)
			{
				if(biome!=null)
				{
					EntityRegistry.addSpawn(LMM_EntityLittleMaid.class,
							cfg_spawnWeight, cfg_minGroupSize, cfg_maxGroupSize, EnumCreatureType.CREATURE, biome);
				}
			}
		}

		// モードリストを構築
		LMM_EntityModeManager.loadEntityMode();
		LMM_EntityModeManager.showLoadedModes();

		// サウンドのロード
// TODO ★		proxy.loadSounds();

		// IFFのロード
		LMM_IFF.loadIFFs();
		
		if(evt.getSide()==Side.CLIENT){
			((LMM_ProxyClient)LMM_LittleMaidMobNX.proxy).countingThread = new LMM_ProxyClient.SoundTickCountingThread();
			((LMM_ProxyClient)LMM_LittleMaidMobNX.proxy).countingThread.start();
		}
	}


	// TODO ここから下はとりあえずいらんと思う
	
	private static String ignoreItemList[] = new String[]{};

	public static boolean isMaidIgnoreItem(ItemStack item)
	{
		return item!=null && item.getItem()!=null && isMaidIgnoreItem(item.getItem());
	}
	public static boolean isMaidIgnoreItem(Item item)
	{
		/*
		if(item!=null)
		{
			String name = (String) Item.itemRegistry.getNameForObject(item);
			for(String ignoreItemName : ignoreItemList)
			{
				if(name.indexOf(ignoreItemName) != -1)
				{
					return true;
				}
			}
		}
		*/
		return false;

	}
}
