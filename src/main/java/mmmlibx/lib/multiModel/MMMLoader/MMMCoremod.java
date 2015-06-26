package mmmlibx.lib.multiModel.MMMLoader;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

//@TransformerExclusions({"mmmlibx.lib.multiModel.MMMLoader"})
public class MMMCoremod implements IFMLLoadingPlugin{

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {"mmmlibx.lib.multiModel.MMMLoader.MMMTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return "mmmlibx.lib.multiModel.MMMLoader.MMMModContainer";
	}

	@Override
	public String getSetupClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAccessTransformerClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
