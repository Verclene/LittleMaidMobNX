package mmmlibx.lib;

import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.NONE;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.EXTRescaleNormal;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("deprecation")
public class MMM_ItemRenderManager {

	protected static Map<Object, MMM_ItemRenderManager> classList = new HashMap<Object, MMM_ItemRenderManager>();
	protected static List<Object> checkList = new ArrayList<Object>();
	protected static MMM_ItemRendererForge forgeRender;
	protected Random random;
	
	protected Object fobject;
	protected Method frenderItem;
	protected Method frenderItemInFirstPerson;
	protected Method fgetRenderTexture;
	protected Method frenderItemWorld;
	protected MMM_IItemRenderManager exRender;



	private MMM_ItemRenderManager(Object pObject, Method prenderItem, Method prenderItemInFirstPerson,
			Method pgetRenderTexture, Method prenderItemWorld) {
		fobject = pObject;
		exRender = null;
		frenderItem = prenderItem;
		frenderItemInFirstPerson = prenderItemInFirstPerson;
		fgetRenderTexture = pgetRenderTexture;
		frenderItemWorld = prenderItemWorld;
		random = new Random();
	}

	private MMM_ItemRenderManager(Object pObject, MMM_IItemRenderManager pEXRender) {
		fobject = pObject;
		exRender = pEXRender;
//		frenderItem = pEXRender.getClass().getDeclaredMethod("renderItem", EntityLivingBase.class, ItemStack.class, int.class);
//		frenderItemInFirstPerson = prenderItemInFirstPerson;
//		fgetRenderTexture = pgetRenderTexture;
//		frenderItemWorld = prenderItemWorld;
		random = new Random();
		if (MMM_Helper.isForge) {
			registerForge((Item)pObject, pEXRender);
		}
	}

	public static void registerForge(Item pItem, MMM_IItemRenderManager pEXRender) {
		if (forgeRender == null) {
			forgeRender = new MMM_ItemRendererForge();
		}
		MinecraftForgeClient.registerItemRenderer(pItem, forgeRender);
		MMMLib.Debug("registerForge:%s", pItem.getClass().getSimpleName());
	}

	public static boolean setEXRender(Item pItem, MMM_IItemRenderManager pEXRender) {
		// アイテムの特殊描画機能を強制的に追加する
		if (pItem == null || pEXRender == null) return false;
		
		checkList.add(pItem);
		classList.put(pItem, new MMM_ItemRenderManager(pItem, pEXRender));
		return true;
	}

	public static boolean isEXRender(Item pItem) {
		if (checkList.contains(pItem)) {
			return classList.containsKey(pItem);
		}
		checkList.add(pItem);
		Method lrenderItem = null;
		Method lrenderItemInFirstPerson = null;
		Method lgetRenderTexture = null;
		Method lrenderItemWorld = null;
		Class lc = pItem.getClass();
		
		try {
			lrenderItem = lc.getMethod("renderItem", EntityLivingBase.class, ItemStack.class, int.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			lrenderItemInFirstPerson = lc.getMethod("renderItemInFirstPerson", float.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			lgetRenderTexture = lc.getMethod("getRenderTexture");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			lrenderItemWorld = lc.getMethod("isRenderItemWorld");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (lrenderItem != null || lrenderItemInFirstPerson != null || lgetRenderTexture != null) {
			classList.put(pItem, new MMM_ItemRenderManager(pItem,
					lrenderItem, lrenderItemInFirstPerson,
					lgetRenderTexture, lrenderItemWorld));
			return true;
		}
		return false;
	}

	public static MMM_ItemRenderManager getEXRender(Item pItem) {
		return classList.get(pItem);
	}


	public void renderItemLocal(EntityLivingBase entityliving, ItemStack itemstack, ItemCameraTransforms.TransformType par3) {
		// 特殊レンダラ
		Client.setTexture(getRenderTexture(itemstack));
		GL11.glPushMatrix();
		boolean lflag = renderItem(entityliving, itemstack, par3);
		GL11.glPopMatrix();
		if (lflag) {
			if (itemstack != null && itemstack.hasEffect() && par3 == TransformType.NONE) {
				GL11.glDepthFunc(GL11.GL_EQUAL);
				GL11.glDisable(GL11.GL_LIGHTING);
				Client.setTexture(MMM_ItemRenderer.texGlint);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
				float var14 = 0.76F;
				GL11.glColor4f(0.5F * var14, 0.25F * var14, 0.8F * var14, 1.0F);
				float var15 = 0.125F;
				
				GL11.glPushMatrix();
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glLoadIdentity();
				GL11.glScalef(var15, var15, var15);
				float var16 = Minecraft.getSystemTime() % 3000L / 3000.0F * 8.0F;
				GL11.glTranslatef(var16, 0.0F, 0.0F);
				GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				renderItem(entityliving, itemstack, TransformType.NONE);
//				renderItemIn2D(var6, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
				GL11.glPopMatrix();
				
				GL11.glPushMatrix();
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glLoadIdentity();
				GL11.glScalef(var15, var15, var15);
				var16 = Minecraft.getSystemTime() % 4873L / 4873.0F * 8.0F;
				GL11.glTranslatef(-var16, 0.0F, 0.0F);
				GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				renderItem(entityliving, itemstack, TransformType.NONE);
//				renderItemIn2D(var6, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
				GL11.glPopMatrix();
				
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glLoadIdentity();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}
			return;
		}
	}

	public boolean renderItem(Entity pEntity, ItemStack pItemstack, TransformType par3) {
		if (exRender != null) {
			return exRender.renderItem(pEntity, pItemstack, par3);
		} else if (frenderItem != null) {
			try {
				return (Boolean)frenderItem.invoke(fobject, pEntity, pItemstack, par3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean renderItemInFirstPerson(Entity pEntity, ItemStack pItemStack, float pDeltaTimepRenderPhatialTick) {
//	public boolean renderItemInFirstPerson(float pDelta, MMM_ItemRenderer pItemRenderer) {
		if (exRender != null) {
			return exRender.renderItemInFirstPerson(MMM_Helper.mc.thePlayer, pItemStack, pDeltaTimepRenderPhatialTick);
//			return exRender.renderItemInFirstPerson(pDelta, pItemRenderer);
		} else if (frenderItemInFirstPerson != null) {
			try {
				return (Boolean)frenderItemInFirstPerson.invoke(fobject, MMM_Helper.mc.thePlayer, pItemStack, pDeltaTimepRenderPhatialTick);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean renderItemWorld(EntityItem entityitem, double d, double d1, double d2, float f, float f1) {
		ItemStack lis = entityitem.getEntityItem();
		MMM_ItemRenderManager lirm = MMM_ItemRenderManager.getEXRender(lis.getItem());
		if (!lirm.isRenderItemWorld(lis)) return false;
		
		// テクスチャ
		Client.setTexture(lirm.getRenderTexture(lis));
		// 描画
		random.setSeed(187L);
		GL11.glPushMatrix();
		//1.8検討
		float f2 = MathHelper.sin((entityitem.lifespan + f1) / 10F + entityitem.hoverStart) * 0.1F + 0.1F;
		float f3 = ((entityitem.lifespan + f1) / 20F + entityitem.hoverStart) * 57.29578F;
		byte byte0 = 1;
		if (lis.stackSize > 1) {
			byte0 = 2;
		}
		if (lis.stackSize > 5) {
			byte0 = 3;
		}
		if (lis.stackSize > 20) {
			byte0 = 4;
		}
		GL11.glTranslatef((float)d, (float)d1 + f2, (float)d2);
		GL11.glEnable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
		GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);
		float f4 = 1.0F; //0.25F;
		for (int j = 0; j < byte0; j++) {
			GL11.glPushMatrix();
			if (j > 0) {
				float f5 = ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / f4;
				float f7 = ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / f4;
				float f9 = ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / f4;
				GL11.glTranslatef(f5, f7, f9);
			}
			lirm.renderItem(null, lis, NONE);
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		return true;
	}



	public ResourceLocation getRenderTexture(ItemStack pItemStack) {
		if (exRender != null) {
			return exRender.getRenderTexture(pItemStack);
		} else if (fgetRenderTexture != null) {
			try {
				return (ResourceLocation)fgetRenderTexture.invoke(fobject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean isRenderItemWorld(ItemStack pItemStack) {
		if (exRender != null) {
			return exRender.isRenderItemWorld(pItemStack);
		} else if (frenderItemWorld != null) {
			try {
				return (Boolean)frenderItemWorld.invoke(fobject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean isRenderItemInFirstPerson(ItemStack pItemStack) {
		if (exRender != null) {
			return exRender.isRenderItemInFirstPerson(pItemStack);
		}
		return false;
	}

	public boolean isRenderItem(ItemStack pItemStack) {
		if (exRender != null) {
			return exRender.isRenderItem(pItemStack);
		}
		return false;
	}

}
