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
import terrains.TerrainGrid;
import terrains.TerrainSquare;
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
        List<TerrainSquare> terrains = new ArrayList<>();
        //We place the four textures into a texture pack for the terrain to read
        TerrainTexturePack texturePack = new TerrainTexturePack(loader, "grassy2", "grass", "grassFlowers", "mud");
        //We load up a blendmap which will tell the terrain which texture to use at what time
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        //load in texture pack, blend map, and height map to create the texture

        TerrainGrid.addTerrainSquare(new TerrainSquare(0,0,loader,texturePack,blendMap,"heightMap1"));
        TerrainGrid.addTerrainSquare(new TerrainSquare(0,1,loader,texturePack,blendMap,"heightMap2"));
        TerrainGrid.addTerrainSquare(new TerrainSquare(0,2,loader,texturePack,blendMap,"heightMap3"));
        TerrainGrid.addTerrainSquare(new TerrainSquare(1,0,loader,texturePack,blendMap,"heightMap0100"));
        TerrainGrid.addTerrainSquare(new TerrainSquare(1,1,loader,texturePack,blendMap,"heightMap0101"));
        TerrainGrid.addTerrainSquare(new TerrainSquare(1,2,loader,texturePack,blendMap,"heightMap1"));
        TerrainGrid.addTerrainSquare(new TerrainSquare(2,0,loader,texturePack,blendMap,"heightMap1"));
        TerrainGrid.addTerrainSquare(new TerrainSquare(2,1,loader,texturePack,blendMap,"heightMap1"));
        TerrainGrid.addTerrainSquare(new TerrainSquare(2,2,loader,texturePack,blendMap,"heightMap1"));


        /****************************************WATER****************************************/

        WaterShader waterShader = new WaterShader();
        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
        List<WaterTile> waters = new ArrayList<>();
        WaterTile water = new WaterTile(2122, 1816, 0.8f);
        waters.add(water);

        /****************************************MODELS****************************************/
        List<Entity> entities = new ArrayList<>();
        List<Entity> immovableEntities = new ArrayList<>();

        //Our player model
        RawModel cubePlayer = OBJLoader.loadObjModel("person",loader);
        TexturedModel playerTexture = new TexturedModel(cubePlayer, new ModelTexture(loader.loadTexture("white")));
        Player player = new Player(playerTexture,new Vector2f(100,100),new Vector3f(0,0,0),1);
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
        Entity theOneLamp = new Entity(lamp, new Vector2f(186, 294));

        //Adding all models to the list
        Random random = new Random(); //Some will be in random locations
        entities.add(new Entity(lamp, new Vector2f(185, 293)));
        entities.add(new Entity(lamp, new Vector2f(370, 300)));
        entities.add(new Entity(lamp, new Vector2f(293, 305)));

        for(int i = 0; i < 500; i++) {
            float xPos = Math.abs(random.nextInt() % 800);
            float zPos = Math.abs(random.nextInt() % 2400);
            float scale = Math.abs(random.nextFloat() * random.nextInt() % 3);
            immovableEntities.add(new Entity(fern, new Vector2f(xPos,zPos), new Vector3f(0,0,0),scale));
        }

        for(int i = 0; i < 50; i++) {
            float xPos = Math.abs(random.nextInt() % 800);
            float zPos = Math.abs(random.nextInt() % 2400);
            float scale = Math.abs(random.nextFloat() * random.nextInt() % 10);
            immovableEntities.add(new Entity(tree, new Vector2f(xPos,zPos), new Vector3f(0,0,0),scale));
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
            player.move(TerrainGrid.getTerrainByPosition(player.getPosition().getX(), player.getPosition().getZ()));

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
            camera.getPosition().y += distance;
            camera.invertPitch();

            //RENDER REFRACTION TEXTURE
            buffers.bindRefractionFrameBuffer();

            //RENDER TO SCREEN
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            buffers.unbindCurrentFrameBuffer();
            renderer.renderScene(entities,immovableEntities,lights,camera, new Vector4f(0,-1,0,15));
            waterRenderer.render(waters,camera,sun);

            //GUI Stuff
            float playersCurrentStamina = player.getCurrentStamina();
            GuiTexture backgroundStaminaBar = new GuiTexture(loader.loadTexture("backgroundBar"),
                    new Vector2f(-0.6f, -0.9f), new Vector2f(0.25f, 0.05f));
            GuiTexture staminaBar = new GuiTexture(loader.loadTexture("staminaBar"),
                    new Vector2f(-0.6f - 0.25f + (0.25f * (playersCurrentStamina/100)), -0.9f),
                    new Vector2f(0.25f * (playersCurrentStamina/100), 0.05f));
            ArrayList<GuiTexture> guis = new ArrayList<>();
            guis.add(backgroundStaminaBar);
            guis.add(staminaBar);
            guiRenderer.render(guis);
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
