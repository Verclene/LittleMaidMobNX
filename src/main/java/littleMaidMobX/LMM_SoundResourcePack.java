package littleMaidMobX;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.imageio.ImageIO;

import mmmlibx.lib.MMMLib;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;

import com.google.common.collect.ImmutableSet;

/**
 * サウンドパック用
 */
public class LMM_SoundResourcePack implements IResourcePack {

	public LMM_SoundResourcePack() {
	}

	@Override
	public InputStream getInputStream(ResourceLocation par1ResourceLocation) throws IOException {
		InputStream inputstream = getResourceStream(par1ResourceLocation);
//		if (inputstream != null) {
			return inputstream;
//		} else {
//			throw new FileNotFoundException(par1ResourceLocation.getResourcePath());
//		}
	}

	private InputStream getResourceStream(ResourceLocation resource) {
		String path = resource.getResourcePath();
		InputStream lis = null;
		if(resource.getResourceDomain().equalsIgnoreCase(LMM_LittleMaidMobNX.DOMAIN))
		{
			lis = LMM_SoundManager.getResourceStream(resource);
			LMM_LittleMaidMobNX.Debug("getResource:%s : %s", resource, lis);
		}
		return lis;
	}

	@Override
	public boolean resourceExists(ResourceLocation resource) {
		return LMM_SoundManager.getResourceExists(resource);
	}

	public static final Set lmmxResourceDomains = ImmutableSet.of(LMM_LittleMaidMobNX.DOMAIN);
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
		return "ModelAndSound";
	}

}
