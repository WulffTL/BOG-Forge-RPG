package engineTester;

import entities.*;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.*;
import models.RawModel;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Time;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Travis on 10/25/2015.
 */

public class MainGameLoop {

    private static final int GRID_SIZE_TERRAINS = 9;

    public static int getGridSizeTerrains(){
        return GRID_SIZE_TERRAINS;
    }

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        TextMaster.init(loader);

        /****************************************FONT STUFF****************************************/
        FontType font = new FontType(loader.loadTexture("tahoma"), new File("./Plymordior2/res/tahoma.fnt"));
        GUIText text = new GUIText("This is a test text!", 1, font, new Vector2f(0, 0), 1f, true);

        /****************************************RENDERERS****************************************/

        GuiRenderer guiRenderer = new GuiRenderer(loader);
        MasterRenderer renderer = new MasterRenderer(loader);

        /****************************************TERRAINS****************************************/
        //Create an array for our terrains to go into
        List<Terrain> terrains = new ArrayList<>();
        //First we get the four textures for the blend map
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture snowTexture = new TerrainTexture(loader.loadTexture("steel_floor"));
        //We place the four textures into a texture pack for the terrain to read
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexturePack snowPack = new TerrainTexturePack(snowTexture, rTexture, gTexture, bTexture);
        //We load up a blendmap which will tell the terrain which texture to use at what time
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        //load in texture pack, blend map, and height map to create the texture
        Terrain terrain0000 = new Terrain(0,0,loader,snowPack,blendMap,"heightMap1");
        Terrain terrain0001 = new Terrain(0,1, loader, texturePack, blendMap, "heightMap2");
        Terrain terrain0002 = new Terrain(0,2,loader,texturePack,blendMap,"heightMap3");
        Terrain terrain0100 = new Terrain(1,0, loader, texturePack, blendMap, "heightMap0100");
        Terrain terrain0101 = new Terrain(1,1, loader, texturePack, blendMap, "heightMap0101");
        Terrain terrain0102 = new Terrain(1,2, loader, texturePack, blendMap, "heightMap2");
        Terrain terrain0200 = new Terrain(2,0, loader, texturePack, blendMap, "heightMap2");
        Terrain terrain0201 = new Terrain(2,1, loader, texturePack, blendMap, "heightMap2");
        Terrain terrain0202 = new Terrain(2,2, loader, texturePack, blendMap, "heightMap2");

        terrains.add(terrain0000);
        terrains.add(terrain0001);
        terrains.add(terrain0002);
        terrains.add(terrain0100);
        terrains.add(terrain0101);
        terrains.add(terrain0102);
        terrains.add(terrain0200);
        terrains.add(terrain0201);
        terrains.add(terrain0202);

        Terrain[][] terrainArray = new Terrain[3][3];
        terrainArray[0][0] = terrain0000;
        terrainArray[0][1] = terrain0001;
        terrainArray[0][2] = terrain0002;
        terrainArray[1][0] = terrain0100;
        terrainArray[1][1] = terrain0101;
        terrainArray[1][2] = terrain0102;
        terrainArray[2][0] = terrain0200;
        terrainArray[2][1] = terrain0201;
        terrainArray[2][2] = terrain0202;


        /****************************************WATER****************************************/

        WaterShader waterShader = new WaterShader();
        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
        List<WaterTile> waters = new ArrayList<>();
        WaterTile water = new WaterTile(2122, 1816, Terrain.getCurrentTerrain(terrainArray,2122,1816).getHeightOfTerrain(2122,1816) + 0.8f);
        waters.add(water);

        /****************************************MODELS****************************************/
        List<Entity> entities = new ArrayList<>();
        List<Entity> immovableEntities = new ArrayList<>();

        //Our player model
        RawModel cubePlayer = OBJLoader.loadObjModel("person",loader);
        TexturedModel playerTexture = new TexturedModel(cubePlayer, new ModelTexture(loader.loadTexture("white")));
        Player player = new Player(playerTexture, new Vector3f(87,Terrain.getCurrentTerrain(terrainArray,2100,1800).getHeightOfTerrain(2100,1800),1800),0,180,0,1);
        immovableEntities.add(player);

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
        Entity theOneLamp = new Entity(lamp, new Vector3f(186,Terrain.getCurrentTerrain(terrainArray,186,294).getHeightOfTerrain(186,294), 294), 0, 0, 0, 1, 0);

        //Adding all models to the list
        Random random = new Random(); //Some will be in random locations
        entities.add(new Entity(lamp, new Vector3f(185,Terrain.getCurrentTerrain(terrainArray,185,293).getHeightOfTerrain(185,293), 293),0,0,0,1,0));
        entities.add(new Entity(lamp, new Vector3f(370,Terrain.getCurrentTerrain(terrainArray,370,300).getHeightOfTerrain(370,300), 300),0,0,0,1,0));
        entities.add(new Entity(lamp, new Vector3f(293,Terrain.getCurrentTerrain(terrainArray,293,305).getHeightOfTerrain(293,305), 305),0,0,0,1,0));

        for(int i = 0; i < 500; i++) {
            float xPos = Math.abs(random.nextInt() % 800);
            float zPos = Math.abs(random.nextInt() % 2400);
            float yPos = Terrain.getCurrentTerrain(terrainArray,xPos,zPos).getHeightOfTerrain(xPos,zPos);
            float scale = Math.abs(random.nextFloat() * random.nextInt() % 3);
            immovableEntities.add(new Entity(fern, new Vector3f(xPos,yPos,zPos),0,0,0,scale,0));
        }

        for(int i = 0; i < 50; i++) {
            float xPos = Math.abs(random.nextInt() % 800);
            float zPos = Math.abs(random.nextInt() % 2400);
            float yPos = Terrain.getCurrentTerrain(terrainArray,xPos,zPos).getHeightOfTerrain(xPos,zPos);
            float scale = Math.abs(random.nextFloat() * random.nextInt() % 10);
            immovableEntities.add(new Entity(tree, new Vector3f(xPos,yPos,zPos),0,0,0,scale,0));
        }

        /****************************************LIGHTS****************************************/

        List<Light> lights = new ArrayList<>();

        Light sun = new Light(new Vector3f(400,400,100),new Vector3f(1,1,1));
        lights.add(sun);

        List<Light> allLights = new ArrayList<>();

        allLights.addAll(lights);

        /****************************************CAMERA****************************************/

        Camera camera = new Camera(player);


        /****************************************MAIN GAME LOOP****************************************/

        long secondsPassed = System.currentTimeMillis();

        while(!Display.isCloseRequested()){
            player.move(Terrain.getCurrentTerrain(terrainArray, player.getPosition().x, player.getPosition().z));

            if(Time.isTopOfSecond(secondsPassed)){
                secondsPassed++;
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
                player.printCurrentLocation();
            }

            camera.move();

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            //RENDER REFLECTION TEXTURE
            buffers.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - water.getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(entities,immovableEntities,terrains,lights,camera,new Vector4f(0,1,0,-water.getHeight()+0.1f));
            camera.getPosition().y += distance;
            camera.invertPitch();

            //RENDER REFRACTION TEXTURE
            buffers.bindRefractionFrameBuffer();
            renderer.renderScene(entities,immovableEntities,terrains,lights,camera,new Vector4f(0,-1,0,water.getHeight()));

            //RENDER TO SCREEN
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            buffers.unbindCurrentFrameBuffer();
            renderer.renderScene(entities,immovableEntities,terrains,lights,camera, new Vector4f(0,-1,0,15));
            waterRenderer.render(waters,camera,sun);

            float playersCurrentStamina = player.getCurrentStamina();
            GuiTexture backgroundStaminaBar = new GuiTexture(loader.loadTexture("backgroundBar"),
                    new Vector2f(-0.6f, -0.9f), new Vector2f(0.25f, 0.05f));
            GuiTexture staminaBar = new GuiTexture(loader.loadTexture("staminaBar"),
                    new Vector2f(-0.6f - 0.25f + (0.25f * (playersCurrentStamina/100)), -0.9f),
                    new Vector2f(0.25f * (playersCurrentStamina/100), 0.05f));

            TextMaster.render();

            DisplayManager.updateDisplay();
        }

        /****************************************CLEAN UP****************************************/

        TextMaster.cleanUp();
        buffers.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        waterShader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
