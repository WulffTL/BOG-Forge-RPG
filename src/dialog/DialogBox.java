package dialog;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiTexture;
import org.lwjgl.util.vector.Vector2f;
import renderEngine.Loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by wulfftl on 12/20/16.
 */
public class DialogBox {
    private String dialog;
    private FontType font;
    private GuiTexture background;
    private GUIText text;

    public DialogBox(String file, Loader loader) {
        StringBuilder dialogContent = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new FileReader("./res/dialog/" + file + ".txt"));
            String line;
            while((line = reader.readLine()) != null) {
                dialogContent.append(line).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
            dialogContent.append("");
        }
        this.dialog = dialogContent.toString();
        this.font = new FontType(loader.loadTexture("./fonts/candara"), new File("./res/textures//fonts/candara.fnt"));
        this.text = new GUIText(this.dialog, 0.7f, this.font, new Vector2f(0,0.8f), 1f, false);
        this.background = new GuiTexture(loader.loadTexture("/guis/backgroundBar"), new Vector2f(-1.0f, -1.0f), new Vector2f(2.0f, 1.0f));
    }

    public void show(List<GuiTexture> guiTextures) {
        if(!guiTextures.contains(this.background)) {
            guiTextures.add(this.background);
        }
        TextMaster.loadText(this.text);
        text.setColour(1,1,1);
    }

    public void hide(List<GuiTexture> guiTextures) {
        this.text.remove();
        guiTextures.remove(this.background);
    }

    public String getDialog() {
        return dialog;
    }

    public FontType getFont() {
        return font;
    }

    public GuiTexture getBackground() {
        return background;
    }
}
