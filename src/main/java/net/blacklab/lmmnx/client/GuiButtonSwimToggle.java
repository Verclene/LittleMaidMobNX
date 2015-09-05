package net.blacklab.lmmnx.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonSwimToggle extends GuiButtonArmorToggle {

	public GuiButtonSwimToggle(int buttonId, int x, int y, String buttonText,
			boolean ison) {
		super(buttonId, x, y, buttonText, ison);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		// TODO 自動生成されたメソッド・スタブ
		if(!visible) return;
		GlStateManager.pushMatrix();
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		mc.getTextureManager().bindTexture(new ResourceLocation("lmmx:textures/gui/container/buttons/topbuttons.png"));
		if(hovered){
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}else{
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
		}
//		int k = this.getHoverState(this.hovered);
//		GlStateManager.enableBlend();
//		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
//		GlStateManager.blendFunc(770, 771);
//		this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + k * 20, this.width / 2, this.height);
//		this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
//		this.mouseDragged(mc, mouseX, mouseY);
		drawTexturedModalRect(xPosition, yPosition, 48, toggle?16:0, 16, 16);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

}
