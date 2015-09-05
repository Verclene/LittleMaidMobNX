package mmmlibx.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import org.lwjgl.opengl.GL11;

@SuppressWarnings("deprecation")
public class MMM_ItemRenderer extends ItemRenderer {

	// プライベート変数を使えるように
	public Minecraft mc;
	public ItemStack itemToRender;
	public float equippedProgress;
	public float prevEquippedProgress;
	protected static ResourceLocation texGlint;


	public MMM_ItemRenderer(Minecraft minecraft) {
		super(minecraft);
		
		mc = minecraft;
		try {
			// きらめきテクスチャの確保
			texGlint = (ResourceLocation)ObfuscationReflectionHelper.getPrivateValue(ItemRenderer.class, null, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Minecraft getMC() {
		return mc;
	}

	public ItemStack getItemToRender() {
		return itemToRender;
	}

	public float getEquippedProgress() {
		return equippedProgress;
	}

	public float getPrevEquippedProgress() {
		return prevEquippedProgress;
	}

	@Override
	public void renderItem(EntityLivingBase entityliving, ItemStack itemstack, ItemCameraTransforms.TransformType par3) {
		Item litem = itemstack.getItem();
		if (MMM_ItemRenderManager.isEXRender(litem)) {
			// 特殊レンダラ
			MMM_ItemRenderManager lii = MMM_ItemRenderManager.getEXRender(litem);
			Client.setTexture(lii.getRenderTexture(itemstack));
			GL11.glPushMatrix();
			boolean lflag = lii.renderItem(entityliving, itemstack, par3);
			GL11.glPopMatrix();
			if (lflag) {
				if (itemstack != null && itemstack.hasEffect() && par3 == TransformType.NONE) {
					GL11.glDepthFunc(GL11.GL_EQUAL);
					GL11.glDisable(GL11.GL_LIGHTING);
					Client.setTexture(texGlint);
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
					lii.renderItem(entityliving, itemstack, TransformType.NONE);
//					renderItemIn2D(var6, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
					GL11.glPopMatrix();
					
					GL11.glPushMatrix();
					GL11.glMatrixMode(GL11.GL_TEXTURE);
					GL11.glLoadIdentity();
					GL11.glScalef(var15, var15, var15);
					var16 = Minecraft.getSystemTime() % 4873L / 4873.0F * 8.0F;
					GL11.glTranslatef(-var16, 0.0F, 0.0F);
					GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
					GL11.glMatrixMode(GL11.GL_MODELVIEW);
					lii.renderItem(entityliving, itemstack, TransformType.NONE);
//					renderItemIn2D(var6, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
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
		super.renderItem(entityliving, itemstack, par3);
	}

	@Override
	public void renderItemInFirstPerson(float f) {
		itemToRender = null;
		equippedProgress = 0.0F;
		prevEquippedProgress = 0.0F;
		
		try {
			// ローカル変数を確保
			itemToRender = (ItemStack)ObfuscationReflectionHelper.getPrivateValue(ItemRenderer.class, this, 4);
			equippedProgress = (Float)ObfuscationReflectionHelper.getPrivateValue(ItemRenderer.class, this, 5);
			prevEquippedProgress = (Float)ObfuscationReflectionHelper.getPrivateValue(ItemRenderer.class, this, 6);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (itemToRender != null) {
			Item litem = itemToRender.getItem();
			if (MMM_ItemRenderManager.isEXRender(litem)) {
				if (MMM_ItemRenderManager.getEXRender(litem).renderItemInFirstPerson(MMM_Helper.mc.thePlayer, itemToRender, f)) {
					return;
				}
			}
		}
		
		super.renderItemInFirstPerson(f);
	}


}
