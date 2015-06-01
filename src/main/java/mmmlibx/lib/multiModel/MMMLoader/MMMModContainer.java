package mmmlibx.lib.multiModel.MMMLoader;

import java.util.Arrays;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

import com.google.common.eventbus.EventBus;

public class MMMModContainer extends DummyModContainer {

	public MMMModContainer() {
		super(new ModMetadata());
		ModMetadata lmeta = getMetadata();
		
		lmeta.modId		= "OldModelLoader";
		lmeta.name		= "OldModelLoader";
		lmeta.version	= "1.0";
		lmeta.authorList	= Arrays.asList("MMM");
		lmeta.description	= "The MultiModel before 1.6.2 is read.";
		lmeta.url			= "";
		lmeta.credits		= "";
		setEnabledState(true);
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		// これ付けないとDisableで判定されちゃう。
		return true;
	}

	@Override
	public Class<?> getCustomResourcePackClass() {
		// 古いリソースを読み込むためのリソースパック
		return MMMResourcePack.class;
	}

}
