package littleMaidMobX;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class LMMNX_OldZipTexturesLoader implements IResourcePack {
	
	public static Map<String, String> keys = new HashMap<String, String>();

	@Override
	public InputStream getInputStream(ResourceLocation arg0) throws IOException {
		if(resourceExists(arg0)){
			String key = arg0.getResourcePath();
			if(key.startsWith("/")) key = key.substring(1);
			File file = new File(keys.get(key));
			ZipFile zip = new ZipFile(file);
			InputStream i = zip.getInputStream(zip.getEntry(key));
			return i;
		}
		return null;
	}

	@Override
	public BufferedImage getPackImage() throws IOException {
		return null;
	}

	@Override
	public IMetadataSection getPackMetadata(IMetadataSerializer arg0,
			String arg1) throws IOException {
		return null;
	}

	@Override
	public String getPackName() {
		return "OldTexturesLoader";
	}

	@Override
	public Set<String> getResourceDomains() {
		return ImmutableSet.of("minecraft");
	}

	@Override
	public boolean resourceExists(ResourceLocation arg0) {
		String key = arg0.getResourcePath();
		if(key.startsWith("/")) key = key.substring(1);
		return keys.containsKey(key);
	}

}
