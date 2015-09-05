package net.blacklab.lmmnx.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonArmorToggle extends GuiButton {

	private String showText;

	/**
	 * 0: Inner
	 * 1: Outer
	 */
	private int toggleNode;

	/**
	 * 0: Normal
	 * 1: Light
	 */
	private int toggleLight;

	public boolean toggle = true;

	public GuiButtonArmorToggle(int buttonId, int x, int y, String buttonText, boolean ison) {
		super(buttonId, x, y, 16, 16, "");
		// TODO 自動生成されたコンストラクター・スタブ
		showText = buttonText;
		toggle = ison;
	}

	/**
	 * 0: Inner
	 * 1: Outer
	 */
	public GuiButtonArmorToggle setNode(int i){
		if(i>1) i = 1;
		if(i<0) i = 0;
		toggleNode = i;
		return this;
	}

	/**
	 * 0: Normal
	 * 1: Light
	 */
	public GuiButtonArmorToggle setLight(int i){
		if(i>1) i = 1;
		if(i<0) i = 0;
		toggleLight = i;
		return this;
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
		drawTexturedModalRect(xPosition, yPosition, toggle?16*(toggleNode+1):0, 16*toggleLight, 16, 16);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

}
