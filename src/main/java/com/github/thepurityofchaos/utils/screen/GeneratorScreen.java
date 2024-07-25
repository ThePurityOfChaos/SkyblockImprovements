package com.github.thepurityofchaos.utils.screen;

import java.util.ArrayList;
import java.util.List;

import com.github.thepurityofchaos.utils.NbtUtils;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.gui.MenuScreen;
import com.github.thepurityofchaos.utils.gui.MultilineTextFieldElement;
import com.github.thepurityofchaos.utils.gui.TextFieldElement;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

public class GeneratorScreen extends MenuScreen {
    public static final String[] rarities = {"NONE","COMMON","UNCOMMON","RARE","EPIC","LEGENDARY","MYTHIC","DIVINE","ULTIMATE","SPECIAL","VERY SPECIAL"};
    private final char[] colors = {'0','f','a','9','5','6','d','b','4','c','c'};
    private int current = 0;
    private boolean recombed = false;
    private TextFieldElement copiableCharacters = new TextFieldElement(0, 32, 256, 16, null);
    private TextFieldElement name = new TextFieldElement(0, 48, 256, 16, Text.of(""));
    private TextFieldElement generator = new MultilineTextFieldElement(0, 64, 256, 384, Text.of(Utils.getColorString('f')+""));
    private GUIElement rarity = new GUIElement(0, 448, 128, 16, button -> {cycleRarity();}, button -> {cycleBack();});
    private GUIElement recomb = new GUIElement(0, 464, 256, 16, button -> {recombed = !recombed;});
    private GUIElement generate = new GUIElement(0, 16, 256, 16, button -> {parseGenerator();});
    private TextFieldElement type = new TextFieldElement(128, 448, 128, 16, Text.of("&f&f"));
    private static GeneratorScreen screenInstance = new GeneratorScreen();

    public GeneratorScreen() {
        super();
        setTooltipsAndMessages();
        allElements.put("chars",copiableCharacters);
        allElements.put("name",name);
        allElements.put("generator",generator);
        allElements.put("type",type);
        allElements.put("rarity",rarity);
        allElements.put("generate",generate);
        allElements.put("recomb",recomb);
    }
    public static boolean parseItemToGenerator(ItemStack item){
        if(item==null) return false;
        //parse
        NbtCompound itemData = item.getNbt();
        try{
            //set name
            screenInstance.name.setText(NbtUtils.getStringFromName(itemData.asString()));
            //get rarity and type information (recomb included as rarity + rarity.length)
            Pair<Integer,String> rarityAndType = NbtUtils.getRarityAndTypeFromLore(itemData.asString());
            //set rarity
            screenInstance.setRarity(rarityAndType.getLeft()%rarities.length);
            //remove extraneous text from type
            if(rarityAndType.getLeft()!=rarityAndType.getLeft()%rarities.length){
                screenInstance.recombed = true;
                rarityAndType.setRight(rarityAndType.getRight().substring(1,rarityAndType.getRight().length()-1));
            }else{screenInstance.recombed = false;}
            //set type
            if(rarityAndType.getLeft()%rarities.length!=0) screenInstance.type.setText(rarityAndType.getRight().strip());
            //set generator body
            screenInstance.generator.setText(Utils.getColorString('f')+NbtUtils.getStringFromLore(itemData.asString(), rarityAndType.getLeft()==0)+" ");
            return true;
        }catch(Exception e){
            return false;
        }
    }
    private void parseGenerator(){
        allElements.remove("generate");
        Tooltip t = null;
        copiableCharacters.setTooltip(t);
        name.setTooltip(t);
        generator.setTooltip(t);
        rarity.setTooltip(t);
        generate.setTooltip(t);
        type.setTooltip(t);
        recomb.setTooltip(t);


        ScreenEvents.afterRender(this).register((currentScreen, drawContext, mouseX, mouseY, delta) -> {
        List<Text> generatedText = new ArrayList<>();
        generatedText.add(Text.of(name.getText().replace("&","§")));
        //body of the generator
        for(String str : generator.getText().replace("&","§").split("\\\\n"))
            generatedText.add(Text.of(str));

        generatedText.add(
            Text.of((recombed?Utils.getColorString(colors[current])+"§ka ":"") + 
            (rarity.getMessage().getString().contains("NONE")?"":rarity.getMessage().getString()+" ")+
            type.getText().replace("&","§")+
            (recombed?Utils.getColorString(colors[current])+" §ka":""))
        );

        ScreenUtils.draw(drawContext, generatedText, null, 262, 35, -1, -1, 1, 10, -1, -1, -1, 3, false);
        });
        ScreenEvents.remove(this).register((currentScreen) -> {
            setTooltipsAndMessages();
            allElements.put("generate",generate);
        });
    }
    private void setTooltipsAndMessages(){
        copiableCharacters.setText(Utils.getColorString('f')+" ⸕ ✧ ☘ ✎ ❈ ❤ ❂ ❁ α ☠ ");
        copiableCharacters.setEditable(false);
        copiableCharacters.setTooltip(Text.of("Special Characters"));
        name.setTooltip(Text.of("Name"));
        generator.setTooltip(Text.of("The body of the generator. \nUse \\n for a new line in the item.\nUse &[a-f,0-9] for different colors."));
        type.setTooltip(Text.of("The item's Type."));
        rarity.setTooltip(Text.of("The item's Rarity. Click to cycle, and right-click to go back!"));
        generate.setTooltip(Text.of("Remove all tooltips and start generating!"));
        recomb.setTooltip(Text.of("Do I really need to explain this?"));
        generate.setMessage(Text.of(Utils.getColorString('4')+Utils.getColorString('l')+"START GENERATING"));
        rarity.setMessage(MutableText.of(Text.of(Utils.getColorString(colors[current])+Utils.getColorString('l')+rarities[current]).getContent()));
        recomb.setMessage(Text.of(Utils.getColorString(colors[current])+"Toggle Recombobulation"));
    }
    private void cycleRarity() {
        current++;
        if(current>=rarities.length) current = 0;
        setRarity(current);
    }
    private void cycleBack(){
        current-=1;
        if(current<0) current = rarities.length-1;
        setRarity(current);
    }
    public void setRarity(int n){
        current = n;
        if(name.getText().contains("&")&&name.getText().length()>=2&&current!=0) name.setText(name.getText().substring(2));
        if(current !=0) name.setText("&"+colors[current]+name.getText());
        rarity.setMessage(MutableText.of(Text.of(Utils.getColorString(colors[current])+Utils.getColorString('l')+rarities[current]).getContent()));
        recomb.setMessage(Text.of(Utils.getColorString(colors[current])+"Toggle Recombobulation"));
    }
    public static GeneratorScreen getInstance(){
        return screenInstance;
    }
}
