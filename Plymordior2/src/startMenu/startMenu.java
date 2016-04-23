package startMenu;

import entities.*;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.w3c.dom.Text;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Travis on 4/20/2016.
 */
public class StartMenu {

    public static void startGame() {
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        MasterRenderer renderer = new MasterRenderer(loader);

        List<Light> lights = new ArrayList<>();
        Light overheadLight = new Light(new Vector3f(0,1000,-7000),new Vector3f(1,1,1));
        lights.add(overheadLight);

        List<Terrain> terrains = new ArrayList<>();
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture snowTexture = new TerrainTexture(loader.loadTexture("snow"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexturePack snowPack = new TerrainTexturePack(snowTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                terrains.add(new Terrain(i,j,loader,snowPack,blendMap,"mountainHeightMap"));
            }
        }

        //Our player model
        List<Entity> entities = new ArrayList<>();
        List<Entity> immovableEntities = new ArrayList<>();
        List<TexturedModel> models = new ArrayList<>();
        RawModel tree = OBJLoader.loadObjModel("lowPolyTree",loader);
        RawModel cubePlayer = OBJLoader.loadObjModel("lastTry",loader);
        RawModel lamp = OBJLoader.loadObjModel("lamp",loader);
        TexturedModel lampModel = new TexturedModel(lamp, new ModelTexture(loader.loadTexture("lamp")));
        TexturedModel playerTexture = new TexturedModel(cubePlayer, new ModelTexture(loader.loadTexture("white")));
        models.add(playerTexture);
        TexturedModel testModel = new TexturedModel(tree, new ModelTexture(loader.loadTexture("lowPolyTree")));
        models.add(testModel);
        models.add(lampModel);
        StartMenuEntity startMenuEntity = new StartMenuEntity(playerTexture);
        entities.add(startMenuEntity);
        immovableEntities.add(startMenuEntity);

        Camera camera = new Camera();

        while(!Display.isCloseRequested()){

            if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD0)){
                startMenuEntity.switchModel(models,0);
            }else if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1)){
                startMenuEntity.switchModel(models,1);
            }else if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2)){
                startMenuEntity.switchModel(models,2);
            }

            camera.startMenuMove();
            startMenuEntity.rotate();
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            renderer.renderScene(entities,immovableEntities,terrains,lights,camera, new Vector4f(0,-1,0,15));
            DisplayManager.updateDisplay();
        }

        loader.cleanUp();
        DisplayManager.closeDisplay();

    }
}
