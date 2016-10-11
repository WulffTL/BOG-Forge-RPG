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
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.TerrainSquare;
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
        Light overheadLight = new Light(new Vector3f(4500,1000,4500),new Vector3f(1,1,1));
        Light leftLight = new Light(new Vector3f(400,500,-4000), new Vector3f(1,1,1));
        Light rightLight = new Light(new Vector3f(-4000,500,400), new Vector3f(1,1,1));
        lights.add(overheadLight);
        lights.add(leftLight);
        lights.add(rightLight);

        List<TerrainSquare> terrains = new ArrayList<>();
        generateTerrain(loader, terrains);

        //Our player model
        List<Entity> entities = new ArrayList<>();
        List<Entity> immovableEntities = new ArrayList<>();
        List<TexturedModel> models = new ArrayList<>();
        RawModel playerCylinder = OBJLoader.loadObjModel("person",loader);
        RawModel cubePlayer = OBJLoader.loadObjModel("person",loader);
        RawModel spherePlayer = OBJLoader.loadObjModel("person",loader);
        TexturedModel sphereModel = new TexturedModel(spherePlayer, new ModelTexture(loader.loadTexture("white")));
        TexturedModel cubeModel = new TexturedModel(cubePlayer, new ModelTexture(loader.loadTexture("white")));
        models.add(cubeModel);
        TexturedModel cylinderModel = new TexturedModel(playerCylinder, new ModelTexture(loader.loadTexture("white")));
        models.add(cylinderModel);
        models.add(sphereModel);
        StartMenuEntity startMenuEntity = new StartMenuEntity(cubeModel);
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

            startMenuEntity.rotate();
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            renderer.renderScene(entities,immovableEntities,lights,camera, new Vector4f(0,-1,0,15));
            DisplayManager.updateDisplay();
        }

        loader.cleanUp();
        DisplayManager.closeDisplay();

    }

    private static void generateTerrain(Loader loader, List<TerrainSquare> terrains){
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture snowTexture = new TerrainTexture(loader.loadTexture("white"));
        TerrainTexturePack snowPack = new TerrainTexturePack(snowTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                terrains.add(new TerrainSquare(i,j,loader,snowPack,blendMap,"white"));
            }
        }
    }
}
