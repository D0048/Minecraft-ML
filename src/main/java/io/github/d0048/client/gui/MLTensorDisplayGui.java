package io.github.d0048.client.gui;

import java.io.IOException;
import java.util.List;

import io.github.d0048.MCML;
import io.github.d0048.common.networking.MCMLNetworkingBus;
import io.github.d0048.common.networking.MLTensorDisplaySyncMessage;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Parser;
import net.minecraft.client.gui.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

//GuiScreenBook
@SideOnly(Side.CLIENT)
public class MLTensorDisplayGui extends GuiScreen {
    GuiTextField fieldIDInput;
    GuiButton btnRerender, btnRW;
    final int BTN_RERENDER = 0, FIELD_IDINPUT = 1, BTN_RW = 2;

    static final String loadingTxt = "Loading Display Status...";
    public static NBTTagCompound currentNBT = null;

    BlockPos displayPos;

    public MLTensorDisplayGui(BlockPos displayPos) {
        this.displayPos = displayPos;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.add(btnRerender = new GuiButton(
                BTN_RERENDER, (width / 2) - 100 / 2, height - 40, 100, 20, "Render"));
        buttonList.add(btnRW = new GuiButton(
                BTN_RW, 10, 40, fontRenderer.getStringWidth(loadingTxt), 20, loadingTxt));
        setAllBtnEnabled(false);
        fieldIDInput = new GuiTextField(FIELD_IDINPUT, fontRenderer, 0, 0, width, 30);
        fieldIDInput.setMaxStringLength(Integer.MAX_VALUE);
        fieldIDInput.setText(loadingTxt);
        fieldIDInput.setTextColor(0x999999);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        fieldIDInput.drawTextBox();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BTN_RERENDER:
                mc.displayGuiScreen(null);
                break;
            case BTN_RW:
                if (currentNBT != null) {
                    currentNBT.setBoolean("writable", !currentNBT.getBoolean("writable"));
                }
                break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.fieldIDInput.textboxKeyTyped(typedChar, keyCode);
        if (!(keyCode == Keyboard.KEY_E && this.fieldIDInput.isFocused())) super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        fieldIDInput.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        submit();
        currentNBT = null;
    }

    @Override
    public void updateScreen() {
        fieldIDInput.updateCursorCounter();
        String dataID = fieldIDInput.getText();
        if (currentNBT != null && dataID.equals(loadingTxt)) {
            setAllBtnEnabled(true);
            setBtnTxtDynamic(fontRenderer, btnRW,
                    currentNBT.getBoolean("writable") ? "Read/Write" : "Write Only",
                    currentNBT.getBoolean("writable") ? 0x11FF11 : 0xFF1111);
            fieldIDInput.setText(currentNBT.getString("dataID"));
            fieldIDInput.setCursorPositionZero();
            fieldIDInput.setFocused(true);
        } else if (!dataID.equals(loadingTxt)) {
            try {
                setBtnTxtDynamic(fontRenderer, btnRW,
                        currentNBT.getBoolean("writable") ? "Read/Write" : "Write Only",
                        currentNBT.getBoolean("writable") ? 0x11FF11 : 0xFF1111);
                Parser.parse(dataID);
                btnRerender.enabled = true;
                fieldIDInput.setTextColor(0x11FF11);
                if (currentNBT != null) currentNBT.setString("dataID", dataID.trim().replace("\n", " "));
            } catch (Throwable e) {
                fieldIDInput.setTextColor(0xFF1111);
                btnRerender.enabled = false;
            }
        }
    }

    static void setBtnTxtDynamic(FontRenderer fontRenderer, GuiButton btn, String txt, int color) {
        btn.displayString = txt;
        btn.width = fontRenderer.getStringWidth(txt) + 5;
        btn.packedFGColour = color;
    }

    private void submit() {
        if (currentNBT != null) {
            MCMLNetworkingBus.getWrapperInstance().sendToServer(new MLTensorDisplaySyncMessage(currentNBT));
            info("Display Sync request: " + currentNBT);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void setAllBtnEnabled(boolean status) {
        for (GuiButton btn : buttonList) {
            btn.enabled = status;
        }
    }

    static void info(String s) {
        MCML.logger.info(s);
    }
}
