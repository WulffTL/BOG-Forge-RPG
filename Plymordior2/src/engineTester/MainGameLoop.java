package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import models.RawModel;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Travis on 10/25/2015.
 */

public class MainGameLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        /****************************************TERRAINS****************************************/
        //Create an array for our terrains to go into
        List<Terrain> terrains = new ArrayList<>();
        //First we get the four textures for the blend map
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mud"));
        //We place the four textures into a texture pack for the terrain to read
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        //We load up a blendmap which will tell the terrain which texture to use at what time
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap2"));
        //load in texture pack, blend map, and height map to create the texture
        Terrain terrain = new Terrain(0,-1, loader, texturePack, blendMap, "heightMap");
        terrains.add(terrain);

        /****************************************MODELS****************************************/
        List<Entity> entities = new ArrayList<>();

        //Our player model
        RawModel person = OBJLoader.loadObjModel("person",loader);
        TexturedModel man = new TexturedModel(person, new ModelTexture(loader.loadTexture("playerTexture")));
        Player player = new Player(man, new Vector3f(100,0,-50),0,0,0,1);
        entities.add(player);

        //Pine Tree Model
        RawModel model = OBJLoader.loadObjModel("pine", loader);
        TexturedModel tree = new TexturedModel(model, new ModelTexture(loader.loadTexture("pine")));

        //Multiple Fern Models (using different textures with same model)
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fernAtlas"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern",loader), fernTextureAtlas);
        fern.getTexture().setHasTransparency(true);

        //Lamp models (the locations are important and related to the lights below)
        TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp",loader),
                new ModelTexture(loader.loadTexture("lamp")));
        lamp.getTexture().setUseFakeLighting(true);

        //Adding all models to the list
        Random random = new Random(); //Some will be in random locations
        entities.add(new Entity(lamp, new Vector3f(185,terrain.getHeightOfTerrain(185,-293), -293),0,0,0,1,10));
        entities.add(new Entity(lamp, new Vector3f(370,terrain.getHeightOfTerrain(370,-300), -300),0,0,0,1,10));
        entities.add(new Entity(lamp, new Vector3f(293,terrain.getHeightOfTerrain(293,-305), -305),0,0,0,1,10));

        //Adding 500 random ferns
        for (int i = 0; i < 500; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * -600;
            float y = terrain.getHeightOfTerrain(x,z);
            entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z),0,0,0,0.6f,1));
        }

        //Adding 50 random trees
        for (int i = 0; i < 50; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * -600;
            float y = terrain.getHeightOfTerrain(x,z);
            entities.add(new Entity(tree, new Vector3f(x, y, z),0,0,0,3,1));
        }

        /****************************************LIGHTS****************************************/

        List<Light> lights = new ArrayList<>();

        Light sun = new Light(new Vector3f(0,1000,-7000),new Vector3f(1,1,1));
        Light lavaLight = new Light(new Vector3f(293,terrain.getHeightOfTerrain(293,-305)+13,-305), new Vector3f(2,2,0),
                new Vector3f(1,0.01f,0.002f));
        lights.add(lavaLight);

        lights.add(sun);
        lights.add(new Light(new Vector3f(185,terrain.getHeightOfTerrain(185,-293)+13,-293),
                new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
        lights.add(new Light(new Vector3f(370,terrain.getHeightOfTerrain(370,-300)+13,-300),
                new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));


        /****************************************CAMERA****************************************/

        Camera camera = new Camera(player);

        /****************************************GUIS****************************************/

        List<GuiTexture> guis = new ArrayList<>();
        GuiTexture backgroundHealthBar = new GuiTexture(loader.loadTexture("backgroundBar"),
                new Vector2f(-0.6f, -0.8f), new Vector2f(0.25f, 0.05f));
        GuiTexture healthBar = new GuiTexture(loader.loadTexture("healthBar"),
                new Vector2f(-0.6f, -0.8f), new Vector2f(0.25f, 0.05f));
        guis.add(backgroundHealthBar);
        guis.add(healthBar);

        /****************************************RENDERERS****************************************/

        GuiRenderer guiRenderer = new GuiRenderer(loader);
        MasterRenderer renderer = new MasterRenderer(loader);

        /****************************************MOUSE PICKER****************************************/

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

        /****************************************MAIN GAME LOOP****************************************/

        while(!Display.isCloseRequested()){
            player.move(terrain);
            camera.move();

            picker.update();
            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
            if(terrainPoint != null){
                lavaLight.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 1, terrainPoint.z));
            }

            for(Entity entity : entities) {
                picker.update();
                Vector3f terrainPoint2 = picker.getCurrentTerrainPoint();
                if (picker.isIntersectingSphere(terrainPoint2, entity)){
                    entity.increasePosition(1,0,1);
                }
            }
            renderer.renderScene(entities, terrains, lights, camera);

            float playersCurrentStamina = player.getCurrentStamina();
            GuiTexture backgroundStaminaBar = new GuiTexture(loader.loadTexture("backgroundBar"),
                    new Vector2f(-0.6f, -0.9f), new Vector2f(0.25f, 0.05f));
            GuiTexture staminaBar = new GuiTexture(loader.loadTexture("staminaBar"),
                    new Vector2f(-0.6f - 0.25f + (0.25f * (playersCurrentStamina/100)), -0.9f),
                    new Vector2f(0.25f * (playersCurrentStamina/100), 0.05f));
            guis.add(backgroundStaminaBar);
            guis.add(staminaBar);
            guiRenderer.render(guis);

            DisplayManager.updateDisplay();
        }
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
