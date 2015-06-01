package mmmlibx.lib.multiModel.texture;

import mmmlibx.lib.multiModel.model.AbstractModelBase;
import net.minecraft.util.ResourceLocation;

/**
 * マルチモデルに含まれる個別のモデル、テクスチャを保持
 *
 */
public class MultiModelEntry {

	protected String name;
	protected int color;
	protected AbstractModelBase model;
	protected ResourceLocation texture;


	public AbstractModelBase getModel() {
		return model;
	}

	public int getColor() {
		return color;
	}

}
