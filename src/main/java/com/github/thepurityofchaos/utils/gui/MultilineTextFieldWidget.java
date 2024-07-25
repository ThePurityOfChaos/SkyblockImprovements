package com.github.thepurityofchaos.utils.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.github.thepurityofchaos.utils.Utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class MultilineTextFieldWidget extends TextFieldWidget {
    private List<String> lines = new ArrayList<>();
    
    private int lineHeight = 8;
    private int cursorPosition = 0;

    public MultilineTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text){
        super(textRenderer,x,y,width,height,text);
        this.lineHeight = textRenderer.fontHeight;
        this.setMaxLength(Integer.MAX_VALUE);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render background
        context.fill(getX(), getY(), getX() + width, getY() + height, 0xFF000000);
        MinecraftClient client = MinecraftClient.getInstance();
        // Render each line of text
        int yOffset = 0;
        for (String line : lines) {
            context.drawText(client.textRenderer, Utils.getColorString('f')+line, getX(), getY() + yOffset, 1, false);
            yOffset += lineHeight;
        }

        // Render the cursor
        if (this.isFocused()) {
            int cursorX = getX() + client.textRenderer.getWidth(getTextUpToCursor());
            int cursorY = getY() + (this.getCursorLine() * lineHeight);
            context.fill(cursorX, cursorY, cursorX + 1, cursorY + lineHeight, 0xFFFFFFFF);
        }
    }
    private int getCursorLine() {
        int position = cursorPosition;
        int lineIndex = 0;
        for (String line : lines) {
            if (position > line.length()) {
                position -= line.length();
                lineIndex++;
            } else {
                break;
            }
        }
        return lineIndex;
    }

    private String getTextUpToCursor() {
        int currentCursor = cursorPosition;
        StringBuilder textUpToCursor = new StringBuilder();
        for (String line : lines) {
            if (currentCursor > line.length()) {
                currentCursor -= line.length();
            } else {
                textUpToCursor.append(line, 0, currentCursor);
                break;
            }
        }
        return textUpToCursor.toString();
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        // Handle character typing, add to the correct line
        if (Character.isISOControl(chr)) {
            return false;
        }
        String currentText = this.getText();
        try{
        this.setText(currentText.substring(0, cursorPosition) + chr + currentText.substring(cursorPosition));
        }catch(Exception e){
            this.setText(currentText+chr);
        }
        cursorPosition++;
        this.updateLines();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle key press for navigation and deletion
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && cursorPosition > 0) {
            String currentText = this.getText();
            try{
            this.setText(currentText.substring(0, cursorPosition - 1) + currentText.substring(cursorPosition));
            }catch(Exception e){
                this.setText(currentText.substring(0,currentText.length()-1));
            }
            cursorPosition--;
            this.updateLines();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT && cursorPosition > 0) {
            cursorPosition--;
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT && cursorPosition < this.getText().length()) {
            cursorPosition++;
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            moveCursorUp();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            moveCursorDown();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isVisible() && this.isMouseOver(mouseX, mouseY)) {
            this.setFocused(true);
            int clickedLine = (int) ((mouseY - this.getY()) / this.lineHeight);
            int lineStart = 0;
            for (int i = 0; i < clickedLine && i < lines.size(); i++) {
                lineStart += lines.get(i).length();
            }
            String line = lines.get(clickedLine < lines.size() ? clickedLine : lines.size() - 1);
            int charPos = this.getTextRenderer().trimToWidth(line, (int) (mouseX - this.getX())).length();
            cursorPosition = lineStart + charPos;
            return true;
        } else {
            this.setFocused(false);
            return false;
        }
    }

    private void moveCursorUp() {
        int currentLineIndex = getCursorLine();
        if (currentLineIndex > 0) {
            int prevLineLength = lines.get(currentLineIndex - 1).length();
            cursorPosition -= prevLineLength;
        }
    }

    private void moveCursorDown() {
        int currentLineIndex = getCursorLine();
        if (currentLineIndex < lines.size() - 1) {
            int currentLineLength = lines.get(currentLineIndex).length();
            cursorPosition += currentLineLength;
        }
    }

    void updateLines() {
        String currentText = this.getText();
        lines.clear();
        int currentLineWidth = 0;
        StringBuilder currentLine = new StringBuilder();
        TextRenderer textRenderer = this.getTextRenderer();
        for (char chr : currentText.toCharArray()) {
            int charWidth = textRenderer.getWidth(String.valueOf(chr));
            if (chr == '\n' || currentLineWidth + charWidth > this.width) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                currentLineWidth=0;
                if (chr != '\n') {
                    currentLine.append(chr);
                }
            } else {
                currentLine.append(chr);
            }
            currentLineWidth+=charWidth;
        }
        lines.add(currentLine.toString());
    }
    private TextRenderer getTextRenderer() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.textRenderer;
    }
    
}

