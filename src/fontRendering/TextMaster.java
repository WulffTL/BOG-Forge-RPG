package fontRendering;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontMeshCreator.TextMeshData;
import renderEngine.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Travis on 10/9/2016.
 *
 */
public class TextMaster {

    private static Loader loader;
    private static Map<FontType, List<GUIText>> texts = new HashMap<>();
    private static FontRenderer renderer;

    public static void init(Loader theLoader) {
        renderer = new FontRenderer();
        loader = theLoader;
    }

    public static void render() {
        renderer.render(texts);
    }

    public static void loadText(GUIText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = texts.get(font);
        if(textBatch == null) {
            textBatch = new ArrayList<>();
            texts.put(font, textBatch);
        }
        if(!textBatch.contains(text)) {
            textBatch.add(text);
        }
    }

    public static void removeText(GUIText text) {
        List<GUIText> textBatch = texts.get(text.getFont());
        if(textBatch != null && textBatch.contains(text)) {
            textBatch.remove(text);
        }
        if(textBatch != null && textBatch.isEmpty()) {
            texts.remove(text.getFont());
        }
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }

}
