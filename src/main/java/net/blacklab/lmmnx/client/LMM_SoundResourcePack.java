package net.blacklab.lmmnx.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.imageio.ImageIO;

import littleMaidMobX.LMM_LittleMaidMobNX;
import littleMaidMobX.LMM_SoundManager;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

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
		InputStream lis = null;
		String iString = resource.getResourcePath();
		if(iString.startsWith("/")) iString = iString.substring(1);
		if(resource.getResourceDomain().equalsIgnoreCase(LMM_LittleMaidMobNX.DOMAIN))
		{
			if(iString.endsWith(".ogg")){
				LMM_LittleMaidMobNX.Debug("SOUND path %s", iString);
//				return FileManager.COMMON_CLASS_LOADER.getResourceAsStream("/"+iString);
			}
			lis = LMM_SoundManager.instance.getResourceStream(resource);
//			LMM_LittleMaidMobNX.Debug("getResource:%s : %s", resource, lis);
		}
		return lis;
	}

	@Override
	public boolean resourceExists(ResourceLocation resource) {
		return LMM_SoundManager.instance.existsResource(resource);
	}

	@SuppressWarnings("rawtypes")
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
