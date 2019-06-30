package io.github.d0048.common.gui;

import java.io.IOException;
import java.util.List;

import akka.event.Logging.Info;
import io.github.d0048.MCML;
import io.github.d0048.common.blocks.MLTensorDisplayTileEntity;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MLTensorDisplayGui extends GuiScreen {
	GuiButton button1;
	final int BUTTON1 = 0;

	MLTensorDisplayTileEntity display;

	public MLTensorDisplayGui(MLTensorDisplayTileEntity display) {
		this.display = display;
	}

	@Override
	public void initGui() {
		button1 = new GuiButton(BUTTON1, (width / 2) - 100 / 2, height - 40, 100, 20, "Remove");
		buttonList.add(button1 = new GuiButton(BUTTON1, (width / 2) - 100 / 2, height - 40, 100, 20, "Close"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		button1.drawButton(mc, mouseX, mouseY, partialTicks);

	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case BUTTON1:
			info(FMLCommonHandler.instance().getEffectiveSide().isServer()+" that this is server");
			mc.displayGuiScreen(null);
			if (display != null) {
				display.getWorld().setBlockState(display.getPos(), Blocks.AIR.getDefaultState());
			}
			break;
		}
	}

	@Override
	public void onGuiClosed() {
	}

	@Override
	public void updateScreen() {
	}

	public void drawTooltip(List<String> lines, int mouseX, int mouseY, int posX, int posY, int width, int height) {
		if (mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height) {
			drawHoveringText(lines, mouseX, mouseY);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}static void info(String s) {
		MCML.logger.info(s);
	}
}
