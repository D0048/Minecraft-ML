package io.github.d0048.common.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class MLTensorDisplayGui extends GuiScreen {
	GuiButton button1;

	public MLTensorDisplayGui() {
	}

	@Override
	public void initGui() {
		button1 = new GuiButton(1, (width / 2) - 100 / 2, height - 40, 100, 20, "Close");
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		button1.drawButton(mc, mouseX, mouseY, partialTicks);

	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {

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
}
