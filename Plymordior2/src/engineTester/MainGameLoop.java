package engineTester;

import entities.*;
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
import startMenu.StartMenu;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import toolbox.Time;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

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

       StartMenu.startGame();

//
//        DisplayManager.createDisplay();
//        Loader loader = new Loader();
//
//        /****************************************RENDERERS****************************************/
//
//        GuiRenderer guiRenderer = new GuiRenderer(loader);
//        MasterRenderer renderer = new MasterRenderer(loader);
//
//        /****************************************TERRAINS****************************************/
//        //Create an array for our terrains to go into
//        List<Terrain> terrains = new ArrayList<>();
//        //First we get the four textures for the blend map
//        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
//        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("grass"));
//        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
//        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mud"));
//        TerrainTexture snowTexture = new TerrainTexture(loader.loadTexture("snow"));
//        //We place the four textures into a texture pack for the terrain to read
//        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
//        TerrainTexturePack snowPack = new TerrainTexturePack(snowTexture, rTexture, gTexture, bTexture);
//        //We load up a blendmap which will tell the terrain which texture to use at what time
//        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
//        //load in texture pack, blend map, and height map to create the texture
//        Terrain snowTerrain = new Terrain(0,0,loader,snowPack,blendMap,"mountainHeightMap");
//        Terrain terrain = new Terrain(0,1, loader, texturePack, blendMap, "mountainHeightMap");
//        Terrain terrain2 = new Terrain(1,0, loader, texturePack, blendMap, "mountainHeightMap");
//        Terrain terrain3 = new Terrain(1,1, loader, texturePack, blendMap, "mountainHeightMap");
//        Terrain terrain4 = new Terrain(1,2, loader, texturePack, blendMap, "mountainHeightMap");
//        Terrain terrain5 = new Terrain(2,1,loader,snowPack,blendMap,"mountainHeightMap");
//        Terrain terrain6 = new Terrain(2,2,loader,snowPack,blendMap,"mountainHeightMap");
//        Terrain terrain7 = new Terrain(2,0,loader,snowPack,blendMap,"mountainHeightMap");
//        Terrain terrain8 = new Terrain(0,2,loader,snowPack,blendMap,"mountainHeightMap");
//
//
//        terrains.add(terrain);
//        terrains.add(snowTerrain);
//        terrains.add(terrain2);
//        terrains.add(terrain3);
//        terrains.add(terrain4);
//        terrains.add(terrain5);
//        terrains.add(terrain6);
//        terrains.add(terrain7);
//        terrains.add(terrain8);
//
//
//        terrainArray[0][0] = snowTerrain;
//        terrainArray[0][1] = terrain;
//        terrainArray[1][0] = terrain2;
//        terrainArray[1][1] = terrain3;
//        terrainArray[1][2] = terrain4;
//        terrainArray[2][1] = terrain5;
//        terrainArray[2][2] = terrain6;
//        terrainArray[2][0] = terrain7;
//        terrainArray[0][2] = terrain8;
//
//
//        /****************************************WATER****************************************/
//
//        WaterShader waterShader = new WaterShader();
//        WaterFrameBuffers buffers = new WaterFrameBuffers();
//        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
//        List<WaterTile> waters = new ArrayList<>();
//        WaterTile water = new WaterTile(150, 150, Terrain.getCurrentTerrain(terrainArray,100,100).getHeightOfTerrain(100,100) - 0.3f);
//        waters.add(water);
//
//        /****************************************MODELS****************************************/
//        List<Entity> entities = new ArrayList<>();
//        List<Entity> immovableEntities = new ArrayList<>();
//
//        //Our player model
//        RawModel cubePlayer = OBJLoader.loadObjModel("lastTry",loader);
//        TexturedModel playerTexture = new TexturedModel(cubePlayer, new ModelTexture(loader.loadTexture("white")));
//        Player player = new Player(playerTexture, new Vector3f(700,Terrain.getCurrentTerrain(terrainArray,700,1000).getHeightOfTerrain(700,1000),1000),0,180,0,1);
//        immovableEntities.add(player);
//
//        //Pine Tree Model
//        RawModel model = OBJLoader.loadObjModel("pine", loader);
//        TexturedModel tree = new TexturedModel(model, new ModelTexture(loader.loadTexture("pine")));
//
//        //Multiple Fern Models (using different textures with same model)
//        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fernAtlas"));
//        fernTextureAtlas.setNumberOfRows(2);
//        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern",loader), fernTextureAtlas);
//        fern.getTexture().setHasTransparency(true);
//
//        //Lamp models (the locations are important and related to the lights below)
//        TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp",loader),
//                new ModelTexture(loader.loadTexture("lamp")));
//        lamp.getTexture().setUseFakeLighting(true);
//        Entity theOneLamp = new Entity(lamp, new Vector3f(186,Terrain.getCurrentTerrain(terrainArray,186,294).getHeightOfTerrain(186,294), 294), 0, 0, 0, 1, 0);
//
//        //Adding all models to the list
//        Random random = new Random(); //Some will be in random locations
//        entities.add(new Entity(lamp, new Vector3f(185,Terrain.getCurrentTerrain(terrainArray,185,293).getHeightOfTerrain(185,293), 293),0,0,0,1,0));
//        entities.add(new Entity(lamp, new Vector3f(370,Terrain.getCurrentTerrain(terrainArray,370,300).getHeightOfTerrain(370,300), 300),0,0,0,1,0));
//        entities.add(new Entity(lamp, new Vector3f(293,Terrain.getCurrentTerrain(terrainArray,293,305).getHeightOfTerrain(293,305), 305),0,0,0,1,0));
//
//        //Adding 5000 random ferns
//        for (int i = 0; i < 5000; i++) {
//            float x = random.nextFloat() * 2400;
//            float z = random.nextFloat() * 1800;
//            float y = Terrain.getCurrentTerrain(terrainArray,x,z).getHeightOfTerrain(x,z);
//            entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z),0,0,0,0.6f,6));
//        }
//
//        //Adding 50 random trees
//        for (int i = 0; i < 500; i++) {
//            float x = random.nextFloat() * 2400;
//            float z = random.nextFloat() * 1800;
//            float y = Terrain.getCurrentTerrain(terrainArray,x,z).getHeightOfTerrain(x,z);
//            entities.add(new Entity(tree, new Vector3f(x, y, z),0,0,0,3,0));
//        }
//
//        //Adding NPCs
//        ArrayList<NPC> NPCs = new ArrayList<>();
//        NPC npc1 = new NPC(playerTexture, new Vector3f(393,Terrain.getCurrentTerrain(terrainArray,393,1305).getHeightOfTerrain(393,1305), 1305),
//                0,0,0,1,10,new ArrayList<>(),new ArrayList<>());
//        NPCs.add(npc1);
//
//        NPC npc2 = new NPC(playerTexture, new Vector3f(893,Terrain.getCurrentTerrain(terrainArray,893,105).getHeightOfTerrain(893,105), 105),
//                0,0,0,1,10, new ArrayList<>(), new ArrayList<>());
//        NPCs.add(npc2);
//
//        NPC npc3 = new NPC(playerTexture, new Vector3f(1493,Terrain.getCurrentTerrain(terrainArray,1493,305).getHeightOfTerrain(1493,305), 305),
//                0,0,0,1,10, new ArrayList<>(), new ArrayList<>());
//        NPCs.add(npc3);
//
//        npc1.addEvade(player);
//        npc1.addPursue(theOneLamp);
//        npc2.addEvade(player);
//        npc2.addPursue(theOneLamp);
//        npc3.addEvade(player);
//        npc3.addPursue(theOneLamp);
//        entities.addAll(NPCs);
//
//        /****************************************LIGHTS****************************************/
//
//        List<Light> lights = new ArrayList<>();
//
//        Light sun = new Light(new Vector3f(0,1000,-7000),new Vector3f(1,1,1));
//        lights.add(sun);
//
//        List<Light> allLights = new ArrayList<>();
//
//        allLights.addAll(lights);
//
//        /****************************************CAMERA****************************************/
//
//        Camera camera = new Camera(player);
//
//        /****************************************GUIS****************************************/
//
//        List<GuiTexture> guis = new ArrayList<>();
//        GuiTexture backgroundHealthBar = new GuiTexture(loader.loadTexture("backgroundBar"),
//                new Vector2f(-0.6f, -0.8f), new Vector2f(0.25f, 0.05f));
//        GuiTexture healthBar = new GuiTexture(loader.loadTexture("healthBar"),
//                new Vector2f(-0.6f, -0.8f), new Vector2f(0.25f, 0.05f));
//        guis.add(backgroundHealthBar);
//        guis.add(healthBar);
//
//        /****************************************MOUSE PICKER****************************************/
//
//        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
//
//        /****************************************MAIN GAME LOOP****************************************/
//
//        long secondsPassed = System.currentTimeMillis();
//
//        while(!Display.isCloseRequested()){
//            player.move(Terrain.getCurrentTerrain(terrainArray, player.getPosition().x, player.getPosition().z));
//
//            for (NPC npc : NPCs) {
//                npc.move(Terrain.getCurrentTerrain(terrainArray, npc.getPosition().x, npc.getPosition().z),secondsPassed);
//            }
//
//            if(Time.isTopOfSecond(secondsPassed)){
//                secondsPassed++;
//            }
//
//            camera.move();
//
//            picker.update();
//
//            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
//
//            //RENDER REFLECTION TEXTURE
//            buffers.bindReflectionFrameBuffer();
//            float distance = 2 * (camera.getPosition().y - water.getHeight());
//            camera.getPosition().y -= distance;
//            camera.invertPitch();
//            renderer.renderScene(entities,immovableEntities,terrains,lights,camera,new Vector4f(0,1,0,-water.getHeight()+0.1f));
//            camera.getPosition().y += distance;
//            camera.invertPitch();
//
//            //RENDER REFRACTION TEXTURE
//            buffers.bindRefractionFrameBuffer();
//            renderer.renderScene(entities,immovableEntities,terrains,lights,camera,new Vector4f(0,-1,0,water.getHeight()));
//
//            //RENDER TO SCREEN
//            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
//            buffers.unbindCurrentFrameBuffer();
//            renderer.renderScene(entities,immovableEntities,terrains,lights,camera, new Vector4f(0,-1,0,15));
//            waterRenderer.render(waters,camera,sun);
//
//            float playersCurrentStamina = player.getCurrentStamina();
//            GuiTexture backgroundStaminaBar = new GuiTexture(loader.loadTexture("backgroundBar"),
//                    new Vector2f(-0.6f, -0.9f), new Vector2f(0.25f, 0.05f));
//            GuiTexture staminaBar = new GuiTexture(loader.loadTexture("staminaBar"),
//                    new Vector2f(-0.6f - 0.25f + (0.25f * (playersCurrentStamina/100)), -0.9f),
//                    new Vector2f(0.25f * (playersCurrentStamina/100), 0.05f));
//            guis.add(backgroundStaminaBar);
//            guis.add(staminaBar);
//            guiRenderer.render(guis);
//
//            DisplayManager.updateDisplay();
//        }
//
//        /****************************************CLEAN UP****************************************/
//
//        buffers.cleanUp();
//        guiRenderer.cleanUp();
//        renderer.cleanUp();
//        loader.cleanUp();
//        waterShader.cleanUp();
//        DisplayManager.closeDisplay();
    }
}
