package mmmlibx.lib.multiModel.MMMLoader;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.imageio.ImageIO;

import littleMaidMobX.LMM_LittleMaidMobX;
import littleMaidMobX.LMM_SoundManager;
import mmmlibx.lib.MMMLib;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;

import com.google.common.collect.ImmutableSet;

/**
 * 旧リソースを扱えるようにするクラス。<br>
 * 基本的にクラスローダーでマップされたリソースにアクセスできるようにするだけなので、特殊なマップ方法でロードされている場合は対応できない。
 *
 */
public class MMMResourcePack implements IResourcePack {

	protected ModContainer ownerContainer;


	public MMMResourcePack(ModContainer pContainer) {
		ownerContainer = pContainer;
	}

	@Override
	public InputStream getInputStream(ResourceLocation par1ResourceLocation) throws IOException {
		InputStream inputstream = getResourceStream(par1ResourceLocation, true);
		
		if (inputstream != null) {
			return inputstream;
		} else {
			throw new FileNotFoundException(par1ResourceLocation.getResourcePath());
		}
	}

	private InputStream getResourceStream(ResourceLocation resource, boolean b) {
		String path = resource.getResourcePath();
		InputStream lis = MMMResourcePack.class.getResourceAsStream(path);
		if(resource.getResourceDomain().equalsIgnoreCase(LMM_LittleMaidMobX.DOMAIN))
		{
			if(lis==null)
			{
				lis = LMM_SoundManager.getResourceStream(resource);
			}

			MMMLib.Debug("getResource:"+b+":%s : %s", resource, lis);
		}
		return lis;
	}

	@Override
	public boolean resourceExists(ResourceLocation par1ResourceLocation) {
		InputStream is = getResourceStream(par1ResourceLocation, false);
		
		// TODO ★ このInputStream はクローズしなくていいの？
		
		return is != null;
	}

	public static final Set lmmxResourceDomains = ImmutableSet.of(LMM_LittleMaidMobX.DOMAIN);
	@Override
	@SuppressWarnings("rawtypes")
	public Set getResourceDomains() {
		return lmmxResourceDomains;
	}

	@Override
	public IMetadataSection getPackMetadata(IMetadataSerializer par1MetadataSerializer, String par2Str)
	{ //throws IOException {
		return null;
	}

	// 未使用
	@Override
	public BufferedImage getPackImage() {// throws IOException {
		try {
			return ImageIO.read(DefaultResourcePack.class.getResourceAsStream("/"
					+ (new ResourceLocation("pack.png")).getResourcePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getPackName() {
		return "Default";
	}

}
