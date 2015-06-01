package mmmlibx.lib.multiModel.texture;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import mmmlibx.lib.multiModel.model.AbstractModelBase;
import net.minecraft.util.ResourceLocation;

/**
 * 個別のマルチテクスチャ・モデルを管理する。
 *
 */
public class MultiModelContainer {

	/**
	 * 標準のパーツ表示状況を設定する
	 */
	public int[] defaultVisivles = new int[16];
	
	/**
	 * バインドされているモデルクラス
	 */
	protected AbstractModelBase[] defaultModel;
	protected Map<Integer, AbstractModelBase[]> models;
	/**
	 * バインドされているテクスチャ
	 */
	protected Map<Integer, ResourceLocation> textures;
	protected boolean isDecodeJSON;


	public MultiModelContainer() {
		models = new HashMap<Integer, AbstractModelBase[]>();
		textures = new HashMap<Integer, ResourceLocation>();
		isDecodeJSON = false;
	}

	/**
	 * 渡されたストリームをJSONとして読み込む
	 * @param pStream
	 * @return
	 */
	public boolean loadJSON(FileInputStream pStream) {
		isDecodeJSON = true;
		return false;
	}

	public void addTexture(int pIndex, ResourceLocation pResource) {
		textures.put(pIndex, pResource);
	}

	/**
	 * インデックスに応じたテクスチャを返す
	 * @param pIndex
	 * @return
	 */
	public ResourceLocation getTexture(int pIndex) {
		return textures.get(pIndex);
	}

	public AbstractModelBase[] getModelClass(int pIndex) {
		if (models.containsKey(pIndex)) {
			return models.get(pIndex);
		}
		return defaultModel;
	}
	public AbstractModelBase[] getModelClass() {
		return defaultModel;
	}

	public MultiModelEntry getMultiModel() {
		return new MultiModelEntry();
	}

	public int getTextureCount() {
		return textures.size();
	}

}
