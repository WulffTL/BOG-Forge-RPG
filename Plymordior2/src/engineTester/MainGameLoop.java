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
        FontType font = new FontType(loader.loadTexture("candara"), new File("./Plymordior2/res/candara.fnt"));
        GUIText text = new GUIText("A sample string of text!", 3, font, new Vector2f(0.0f, 0.4f), 1f, true);
        text.setColour(0.1f,0.1f,0.1f);

        /****************************************RENDERERS****************************************/

        GuiRenderer guiRenderer = new GuiRenderer(loader);
        MasterRenderer renderer = new MasterRenderer(loader);

        /****************************************TERRAINS****************************************/
        //We place the four textures into a texture pack for the terrain to read
        TerrainTexturePack texturePack = new TerrainTexturePack(loader, "grassy2", "grass", "grassFlowers", "mud");
        //We load up a blendmap which will tell the terrain which texture to use at what time
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        //load in texture pack, blend map, and height map to create the texture

        for(int i = 0; i < TerrainGrid.DIMENSIONS; i++) {
            for(int j = 0; j < TerrainGrid.DIMENSIONS; j++) {
                TerrainGrid.addTerrainSquare(new TerrainSquare(i,j,loader,texturePack,blendMap));
            }
        }

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
        Player player = new Player(playerTexture,new Vector2f(1948,1615),new Vector3f(0,0,0),1);
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

        //Adding all models to the list
        Random random = new Random(); //Some will be in random locations
        int lampOneX = 185, lampOneY = 293;
        int lampTwoX = 370, lampTwoY = 300;
        int lampThreeX = 293, lampThreeY = 305;
        entities.add(new Entity(lamp, new Vector2f(lampOneX, lampOneY)));
        entities.add(new Entity(lamp, new Vector2f(lampTwoX, lampTwoY)));
        entities.add(new Entity(lamp, new Vector2f(lampThreeX, lampThreeY)));

        for(int i = 0; i < 500; i++) {
            float xPos = Math.abs(random.nextInt() % TerrainSquare.TERRAIN_SIZE);
            float zPos = Math.abs(random.nextInt() % TerrainSquare.TERRAIN_SIZE);
            float scale = Math.abs(random.nextFloat() * random.nextInt() % 3);
            immovableEntities.add(new Entity(fern, new Vector2f(xPos,zPos), new Vector3f(0,0,0),scale));
        }

        for(int i = 0; i < 50; i++) {
            float xPos = Math.abs(random.nextInt() % TerrainSquare.TERRAIN_SIZE);
            float zPos = Math.abs(random.nextInt() % TerrainSquare.TERRAIN_SIZE);
            float scale = Math.abs(random.nextFloat() * random.nextInt() % 10);
            immovableEntities.add(new Entity(tree, new Vector2f(xPos,zPos), new Vector3f(0,0,0),scale));
        }

        /****************************************LIGHTS****************************************/

        List<Light> lights = new ArrayList<>();

        Light sun = new Light(new Vector2f(0,-7000),1000, new Vector3f(1f,1f,1f));
        lights.add(sun);
        //red lamp
        lights.add(new Light(new Vector2f(lampOneX,lampOneY),15,new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
        //green lamp
        lights.add(new Light(new Vector2f(lampTwoX,lampTwoY),15,new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
        //yellow lamp
        lights.add(new Light(new Vector2f(lampThreeX,lampThreeY),15,new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f)));

        /****************************************CAMERA****************************************/

        Camera camera = new Camera(player);


        /****************************************MAIN GAME LOOP****************************************/

        while(!Display.isCloseRequested()){
            player.move();

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
