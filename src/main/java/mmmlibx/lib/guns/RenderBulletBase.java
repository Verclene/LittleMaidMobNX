package mmmlibx.lib.guns;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderBulletBase extends Render {

	protected RenderBulletBase(RenderManager p_i46179_1_) {
		super(p_i46179_1_);
	}

	@Override
	public void doRender(Entity var1, double var2, double var4, double var6,
			float var8, float var9) {
		doRender((EntityBulletBase)var1, var2, var4, var6, var8, var9);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity var1) {
		return null;
	}

	/**
	 * 標準描画処理。<br>
	 * 色は固定。
	 * @param pEntity
	 * @param pX
	 * @param pY
	 * @param pZ
	 * @param var8
	 * @param var9
	 */
	public void doRender(EntityBulletBase pEntity, double pX, double pY, double pZ,
			float var8, float var9) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)pX, (float)pY, (float)pZ);
		GL11.glRotatef((pEntity.prevRotationYaw + (pEntity.rotationYaw - pEntity.prevRotationYaw) * var9) - 90F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(pEntity.prevRotationPitch + (pEntity.rotationPitch - pEntity.prevRotationPitch) * var9, 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.getInstance();
		float f10 = 0.05625F;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		// テクスチャ使わない
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glScalef(f10, f10, f10);
		// 描画色
		int lcolor = getColor(pEntity);
		
		GL11.glPushMatrix();
		GL11.glRotatef(45F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(-4.7F, 0.0F, 0.0F);
		GL11.glNormal3f(f10, 0.0F, 0.0F);
		//1.8後回し
		tessellator.getWorldRenderer().startDrawingQuads();
		//tessellator.setColorOpaque_I(lcolor);
		GlStateManager.colorLogicOp(lcolor);
//		tessellator.setColorRGBA_F(0.5F, 0.25F, 0.0F, 1.0F);
		tessellator.getWorldRenderer().addVertex(4.5D, -0.5D, 0.0D);
		tessellator.getWorldRenderer().addVertex(4.5D, 0.0D, -0.5D);
		tessellator.getWorldRenderer().addVertex(4.5D, 0.5D, 0.0D);
		tessellator.getWorldRenderer().addVertex(4.5D, 0.0D, 0.5D);
		tessellator.draw();
		GL11.glNormal3f(-f10, 0.0F, 0.0F);
		tessellator.getWorldRenderer().startDrawingQuads();
//		tessellator.setColorRGBA_F(0.4F, 0.25F, 0.0F, 1.0F);
		GlStateManager.color(0.4F, 0.25F, 0.0F, 1.0F);
		//tessellator.setColorOpaque_I(lcolor);
		GlStateManager.colorLogicOp(lcolor);
		tessellator.getWorldRenderer().addVertex(4.5D, 0.0D, 0.5D);
		tessellator.getWorldRenderer().addVertex(4.5D, 0.5D, 0.0D);
		tessellator.getWorldRenderer().addVertex(4.5D, 0.0D, -0.5D);
		tessellator.getWorldRenderer().addVertex(4.5D, -0.5D, 0.0D);
		tessellator.draw();
		for (int j = 0; j < 4; j++) {
			GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
			GL11.glNormal3f(0.0F, 0.0F, f10);
			tessellator.getWorldRenderer().startDrawingQuads();
			//tessellator.setColorOpaque_I(lcolor);
			GlStateManager.colorLogicOp(lcolor);
//			tessellator.setColorRGBA_F(0.5F, 0.25F, 0.0F, 1.0F);
			GlStateManager.color(0.5F, 0.25F, 0.0F, 1.0F);
			tessellator.getWorldRenderer().addVertex(4.5D, -0.5D, 0.0D);
			tessellator.getWorldRenderer().addVertex(6.5D, -0.5D, 0.0D);
			tessellator.getWorldRenderer().addVertex(6.5D, 0.5D, 0.0D);
			tessellator.getWorldRenderer().addVertex(4.5D, 0.5D, 0.0D);
			tessellator.draw();
		}
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		renderOptional(pEntity, pX, pY, pZ, var8, var9);
		GL11.glPopMatrix();
		
//		renderOffsetAABB(pEntity.boundingBox, pX - pEntity.lastTickPosX, pY - pEntity.lastTickPosY, pZ - pEntity.lastTickPosZ);
	}

	public int getColor(EntityBulletBase pEntity) {
		return pEntity.getBulletColor();
	}

	public void renderOptional(EntityBulletBase pEntity, double pX, double pY, double pZ,
			float var8, float var9) {
	}

}
