package com.github.thepurityofchaos.utils.screen;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.thepurityofchaos.utils.ComponentUtils;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.*;

import com.github.thepurityofchaos.utils.math.ColorUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class GeneratorScreen extends MenuScreen {
    public static final String[] rarities = {"NONE","COMMON","UNCOMMON","RARE","EPIC","LEGENDARY","MYTHIC","DIVINE","ULTIMATE","SPECIAL","VERY SPECIAL"};
    private final char[] colors = {'0','f','a','9','5','6','d','b','4','c','c'};
    private int current = 0;
    private boolean recombed = false;
    private char colorCode = 'f';

    private TextFieldElement name = new TextFieldElement(0, 32, 256, 16, Text.of(""));
    private TextFieldElement generator = new MultilineTextFieldElement(0, 48, 256, 384, Text.of(Utils.getColorString(colorCode)+""));
    private GUIElement rarity = new GUIElement(0, 432, 128, 16, button -> {cycleRarity();}, button -> {cycleBack();});
    private GUIElement recomb = new MenuElement(0, 448, 256, 16, button -> {recombed = !recombed;}, null);
    private GUIElement generate = new GUIElement(0, 16, 256, 16, button -> {parseGenerator();}, null);
    private TextFieldElement type = new TextFieldElement(128, 432, 128, 16, Text.of("&"+colorCode));
    private Map<String,String> replaceables = new HashMap<>();
    private static GeneratorScreen screenInstance = new GeneratorScreen();

    @Override
    public void init(){
        defineElements();
        super.init();
    }

    private void defineElements() {
        if(!initialized){
            genSources();

            GUIElement copy = new GUIElement(0,16,40,16, button -> {copyToClipboard(false);},button -> {copyToClipboard(true);});
            copy.setMessage("§aCopy");
            copy.setTooltip(Text.of("§fLeft Click to copy the name, body, rarity, and type. (WIP) Right Click to paste the same."));
            ((MenuElement)recomb).addSubElement(copy);
            String infoTooltip = """
                    Use \\n for a new line in the item.
                    
                    §6Colors:
                    §0&0 §1&1 §2&2 §3&3 §4&4 §5&5 §6&6 §7&7
                    §8&8 §9&9 §a&a §b&b §c&c §d&d §e&e §f&f""";

            GUIElement specialColors = new GUIElement(0, 16, 40, 16, null, null);
            specialColors.setMessage(Text.of("§cI§en§af§9o"));
            specialColors.changeDefaultBehavior(false);
            specialColors.setTooltip(Text.of(infoTooltip));
            ((MenuElement)recomb).addSubElement(specialColors);

            Set<String> keys = replaceables.keySet();
            int n = 0;
            StringBuilder infoTooltip2 = new StringBuilder("§6 Special Characters:§7");
            for(String key : keys){
                if(n<7)
                    infoTooltip2.append("\n§7").append("\\").append(key).append(" -> ").append(replaceables.get(key));
                else{
                    infoTooltip2.append("\n§7").append("\\").append(key).append(" -> ").append(replaceables.get(key));
                    GUIElement specialCharacters = new GUIElement(0, 16, 40, 16, null, null);
                    specialCharacters.changeDefaultBehavior(false);
                    specialCharacters.setMessage(Text.of("§fInfo"+replaceables.get(key)));
                    specialCharacters.setTooltip(Text.of(infoTooltip2.toString()));
                    infoTooltip2 = new StringBuilder("§6Special Characters:§7");
                    ((MenuElement)recomb).addSubElement(specialCharacters);
                }
                n=(n+1)%8;
            }
            if(n!=0){
                GUIElement specialCharacters = new GUIElement(0, 16, 40, 16, null, null);
                specialCharacters.changeDefaultBehavior(false);
                specialCharacters.setMessage(Text.of("§fInfo"+infoTooltip2.charAt(infoTooltip2.length()-1)));
                specialCharacters.setTooltip(Text.of(infoTooltip2.toString()));
                ((MenuElement)recomb).addSubElement(specialCharacters);
            }

            ((MenuElement)recomb).redirectVertical(true);
            initialized=true;
        }
    }

    public GeneratorScreen() {
        super();
        setTooltipsAndMessages();
        this.addElement("name",name);
        this.addElement("generator",generator);
        this.addElement("type",type);
        this.addElement("rarity",rarity);
        this.addElement("generate",generate);
        this.addElement("recomb",recomb);

    }
    public static boolean parseItemToGenerator(ItemStack item){
        if(item==null) return false;
        //parse
        try{
            //set name
            screenInstance.name.setText(ComponentUtils.convertTextToString(item.getName()));
            //get rarity and type information (recomb included as rarity + rarity.length)
            Pair<Integer,String> rarityAndType = ComponentUtils.getRarityAndTypeFromLore(item.get(DataComponentTypes.LORE));
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
            screenInstance.generator.setText(Utils.getColorString(getInstance().colorCode)+ComponentUtils.getStringFromLore(item.get(DataComponentTypes.LORE), rarityAndType.getLeft()==0)+" ");
            return true;
        }catch(Exception e){
            return false;
        }
    }
    private void parseGenerator(){
        allElements.remove("generate");
        name.setTooltip((Tooltip) null);
        generator.setTooltip((Tooltip) null);
        rarity.setTooltip((Tooltip) null);
        generate.setTooltip((Tooltip) null);
        type.setTooltip((Tooltip) null);
        recomb.setTooltip((Tooltip) null);



        ScreenEvents.afterRender(this).register((currentScreen, drawContext, mouseX, mouseY, delta) -> {
            List<Text> generatedText = new ArrayList<>();
            generatedText.add(Text.of(replaceTerms(name.getText().replace("&","§"))));
            //body of the generator
            for(String str : replaceTerms(generator.getText()).replace("&","§").split("\\\\n"))
                generatedText.add(Text.of(str));

            generatedText.add(
                Text.of((recombed?Utils.getColorString(colors[current])+"§ka ":"") +
                (rarity.getMessage().getString().contains("NONE")?"":rarity.getMessage().getString()+" ")+
                replaceTerms(type.getText().replace("&","§"))+
                (recombed?Utils.getColorString(colors[current])+" §ka":""))
            );
        ScreenUtils.draw(drawContext, generatedText, null, 262, 35, -1, -1, 1, 10, -1, -1, -1, 3, false);
        });
        ScreenEvents.remove(this).register((currentScreen) -> {
            setTooltipsAndMessages();
            allElements.put("generate",generate);
        });
    }
    private String replaceTerms(String str) {
        //This could be a Pattern / Matcher regex, but I suspect that the overhead of a regex at this point is simply not going to make it more viable than this simplified version. Perhaps if the number of replacements exceeds 100 or so?
        if(str!=null) {
            Set<String> keys = replaceables.keySet();
            for(String key : keys){
                str = str.replace("\\"+key,replaceables.get(key));
            }
        }
        return str;
    }
    private void setTooltipsAndMessages(){
        name.setTooltip(Text.of("Name"));
        generator.setTooltip(Text.of("The body of the generator."));
        type.setTooltip(Text.of("The item's Type."));
        rarity.setTooltip(Text.of("The item's Rarity. Click to cycle, and right-click to go back!"));
        generate.setTooltip(Text.of("Start generating!"));
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
    private void genSources(){
        try{
            Type replacementMap = new TypeToken<Map<String,String>>(){}.getType();
            Identifier allReplacements = Identifier.of("sbimp","info/generatorreplacements.json");
            ResourceManager source = MinecraftClient.getInstance().getResourceManager();
            if(source!=null){
                Gson gson = new Gson();
                InputStream stream = source.getResource(allReplacements).get().getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(stream));
                JsonObject defaultParser = JsonParser.parseReader(r).getAsJsonObject();
                replaceables = (gson.fromJson(defaultParser, replacementMap));
                r.close();

            }
        }catch(Exception x){
            x.printStackTrace();
        }
    }
    private void copyToClipboard(boolean isPaste){
        if(client!=null) {
            //pasting
            if(isPaste) {
                String toPaste = client.keyboard.getClipboard();
                //valid paste
                if(toPaste.contains("gen item")) {
                    //find the relative locations of each fragment (f)
                    int[][] f = {
                            {0,toPaste.indexOf("item_name:"),"item_name:".length()},
                            {1,toPaste.indexOf("rarity:"),"rarity:".length()},
                            {2,toPaste.indexOf("item_lore:"),"item_lore:".length()},
                            {3,toPaste.indexOf("type:"),"type:".length()}
                    };
                    //sort just in case the paste is out of order
                    Arrays.sort(f, Comparator.comparingInt(a -> a[1]));
                    //for results
                    String[] pastes = new String[f.length];
                    Arrays.fill(pastes,"");
                    //determine each substring and paste them into the array
                    int last = f.length-1;
                    for (int i = 0; i < last; i++) {
                        if (f[i][1] >= 0) {
                            pastes[f[i][0]] = toPaste.substring(f[i][1] + f[i][2], f[i+1][1]);
                        }
                    }
                    //last substring needs no cap
                    if (f[last][1] >= 0) {
                        pastes[f[last][0]] = toPaste.substring(f[last][1] + f[last][2]);
                    }
                    for(int i=0; i<pastes.length; i++)
                        pastes[i]=pastes[i].strip();
                    //set results
                    screenInstance.name.setText(pastes[0]);
                    screenInstance.setRarity(0);
                    for (int i = 0; i < rarities.length; i++) {
                        if (rarities[i].equals(pastes[1])) {
                            screenInstance.setRarity(i);
                            break;
                        }
                    }
                    screenInstance.generator.setText(pastes[2]);
                    screenInstance.type.setText(pastes[3]);
                }
            }
            //copy down what already exists
            else{
                String nameString = screenInstance.name.getText(), rarityString = screenInstance.rarity.getMessage().getString(), itemLoreString = replaceTerms(screenInstance.generator.getText()), typeString = screenInstance.type.getText();
                client.keyboard.setClipboard(
                        (!Objects.equals(nameString, "")&&nameString!=null?"/gen item item_name:":"")+nameString+
                                (!Objects.equals(rarityString, "")&&rarityString!=null?"\nrarity:":"")+rarityString.substring(4)+
                                (!Objects.equals(itemLoreString, "")&&itemLoreString!=null?"\nitem_lore:":"")+itemLoreString+
                                (!Objects.equals(typeString, "")&&typeString!=null?"\ntype:":"")+typeString);
            }

        }


    }

}
