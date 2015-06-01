package zabuton;

import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(	modid = VZN_Zabuton.DOMAIN,
		name  = VZN_Zabuton.DOMAIN,
		dependencies="required-after:Forge@[10.12.2.1121,)")
public class VZN_Zabuton
{
//	@MLProp
	public static boolean isDebugMessage = true;
	
	public static Item zabuton;
	public static final String DOMAIN = "zabuton";
	@SidedProxy(
			clientSide = "zabuton.VZN_ProxyClient",
			serverSide = "zabuton.VZN_ProxyCommon")
	public static VZN_ProxyCommon proxy;


	public static void Debug(String pText, Object... pData) {
		// デバッグメッセージ
		if (isDebugMessage) {
			System.out.println(String.format("Zabuton-" + pText, pData));
		}
	}

	public String getPriorities() {
		return "required-after:mod_MMM_MMMLib";
	}

	public String getVersion() {
		return "1.7.2";
	}

	@EventHandler	// 1.6.2
	public void PreInit(FMLPreInitializationEvent evt)
	{
		// MMMLibのRevisionチェック
//		MMM_Helper.checkRevision("3");
		
		zabuton = new VZN_ItemZabuton();
		zabuton.setUnlocalizedName(DOMAIN+":zabuton");
		//zabuton.setTextureName(DOMAIN+":zabuton");
		zabuton.setCreativeTab(CreativeTabs.tabTransport);
		GameRegistry.registerItem(zabuton, "zabuton");
		for (int i = 0; i < 16; i++) {
			/* langファイルに移動
			ModLoader.addLocalization(
					(new StringBuilder()).append(zabuton.getUnlocalizedName()).append(".").append(ItemDye.dyeColorNames[15 - i]).append(".name").toString(),
					(new StringBuilder()).append("Zabuton ").append(ItemDye.field_150923_a[15 - i]).toString()
				);
			ModLoader.addLocalization(
					(new StringBuilder()).append(zabuton.getUnlocalizedName()).append(".").append(ItemDye.dyeColorNames[15 - i]).append(".name").toString(),
					"ja_JP",
					(new StringBuilder()).append("座布団 ").append(VZN_ItemZabuton.colorNamesJP[15 - i]).toString()
				);
			*/
			GameRegistry.addRecipe(new ItemStack(zabuton, 1, 15 - i), new Object[] {
				"s ", "##", 
				Character.valueOf('s'), Items.string,
				Character.valueOf('#'), new ItemStack(Blocks.wool, 1, i)
			});
		}

		EntityRegistry.registerModEntity(VZN_EntityZabuton.class, "zabuton", 0, this, 80, 3, true);
		proxy.RegistRenderer();
		
		BlockDispenser.dispenseBehaviorRegistry.putObject(zabuton, new VZN_BehaviorZabutonDispense());
	}
}
