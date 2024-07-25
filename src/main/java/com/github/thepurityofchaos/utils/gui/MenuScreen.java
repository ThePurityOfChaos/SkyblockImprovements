package com.github.thepurityofchaos.utils.gui;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;

public class MenuScreen extends GUIScreen {
    protected Map<String,MenuElement> menuElements = new HashMap<String,MenuElement>();
    private boolean centerMenus = false;
    protected <T extends Element & Drawable & Selectable> void addDrawableChildren(T drawableElement){
        if(drawableElement instanceof MenuElement){
            MenuElement mElement = (MenuElement) drawableElement;
            if(centerMenus){
                mElement.redirectHorizontal(mElement.getCenteredX()>this.width/2);
                mElement.redirectVertical(mElement.getCenteredY()>this.height/2);
            }
            if(!mElement.recorded()&&mElement.renderMenu()){
                for(GUIElement element : mElement.getSubElements()){
                    addDrawableChild(element);
                }
                mElement.record(true);
            }
            else if(!mElement.renderMenu()){
                for(GUIElement element : mElement.getSubElements()){
                    remove(element); 
                }
                mElement.record(false);
            }
        }
    }
    @Override
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement){
        T t = super.addDrawableChild(drawableElement);
        if(drawableElement instanceof MenuElement){
            super.addDrawableChild(((MenuElement)drawableElement).getToggler());
        }
        return t;
            
        
    }

    @Override
    public void close(){
        for(GUIElement element : allElements.values()){
            if(element instanceof MenuElement){
                ((MenuElement)element).disableMenu();
            }
        }
        super.close();
    }
    @Override
    public boolean addElement(String str, GUIElement e){
        if(e instanceof MenuElement){
            menuElements.put(str,(MenuElement)e);
        }
        return super.addElement(str, e);
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta){
        menuElements.forEach((key,element)->{
            addDrawableChildren(element);
        });
        super.render(context, mouseX, mouseY, delta);
    }
    public void center(boolean b){
        centerMenus = b;
    }
}
