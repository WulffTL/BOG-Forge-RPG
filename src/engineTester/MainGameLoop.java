package engineTester;

import audio.AudioMaster;
import audio.Source;
import entities.*;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.*;
import models.RawModel;
import terrains.HeightsGenerator;
import terrains.TerrainGrid;
import terrains.TerrainSquare;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
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

    public static final int MIDNIGHT = 2400;
    public static final int MIDDAY = MIDNIGHT/2;
    //Start at midday
    private static float timeInSeconds = 0;

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        TextMaster.init(loader);

        /****************************************TERRAINS****************************************/
        //We place the four textures into a texture pack for the terrain to read
        TerrainTexturePack texturePack = new TerrainTexturePack(loader, "/terrainTextures/snow1", "/terrainTextures/mud", "/terrainTextures/grassy2", "/terrainTextures/brick");
        //We load up a blendmap which will tell the terrain which texture to use at what time
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("/blendMaps/blendMap"));
        //load in texture pack, blend map, and height map to create the texture

        for(int i = 0; i < TerrainGrid.DIMENSIONS; i++) {
            for(int j = 0; j < TerrainGrid.DIMENSIONS; j++) {
                TerrainGrid.addTerrainSquare(new TerrainSquare(i,j,loader,texturePack,blendMap));
            }
        }

        /****************************************AUDIO*******************************************/
        AudioMaster.init();
        AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE_CLAMPED);
        //Bird Noises
        int birdChirp = AudioMaster.loadSound("audio/bird.wav");
        Source birdSource = new Source();
        birdSource.setLooping(true);
        birdSource.play(birdChirp);
        birdSource.setPosition(0,0,0);
        birdSource.setMaxDistance(0);
        birdSource.setVolume(.02f);
        //Lake Noises
        int lakeNoise = AudioMaster.loadSound("audio/lake.wav");
        Source lakeSource = new Source();
        lakeSource.setLooping(true);
        lakeSource.play(lakeNoise);
        lakeSource.setPosition(0,0,0);
        lakeSource.setMaxDistance(0);

        /****************************************PLAYER****************************************/
        //Our player model
        RawModel cubePlayer = OBJLoader.loadObjModel("person",loader);
        TexturedModel playerTexture = new TexturedModel(cubePlayer, new ModelTexture(loader.loadTexture("/objectTextures/playerTexture")));
        Player player = new Player(playerTexture,new Vector2f(245,341),new Vector3f(0,0,0),1);

        /****************************************CAMERA****************************************/

        Camera camera = new Camera(player);

        /****************************************FONT STUFF****************************************/
        FontType font = new FontType(loader.loadTexture("/fonts/candara"), new File("./res/textures//fonts/candara.fnt"));
        GUIText text = new GUIText("Stamina", 0.7f, font, new Vector2f(0.18f,0.04f), 1f, false);
        text.setColour(1,1,1);

        /****************************************RENDERERS****************************************/

        GuiRenderer guiRenderer = new GuiRenderer(loader);
        MasterRenderer renderer = new MasterRenderer(loader, camera);

        /***************************************PARTICLES*****************************************/
        ParticleTexture starTextureAdditive = new ParticleTexture(loader.loadTexture("/particleTextures/particleStar"), 1, true);

        ParticleMaster.init(loader,renderer.getProjectionMatrix());
        ParticleSystem starParticleSystemAdditive = new ParticleSystem(starTextureAdditive,150,10,0.1f,10,1.6f);
        starParticleSystemAdditive.setLifeError(0.2f);
        starParticleSystemAdditive.setSpeedError(0.5f);
        starParticleSystemAdditive.setScaleError(1f);

        /****************************************WATER****************************************/

        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
        List<WaterTile> waters = new ArrayList<>();
        WaterTile water = new WaterTile(TerrainGrid.DIMENSIONS*TerrainSquare.TERRAIN_SIZE/2, (TerrainGrid.DIMENSIONS*TerrainSquare.TERRAIN_SIZE)/2);
        waters.add(water);
        float noiseDistanceThreshold = 150;

        /****************************************MODELS****************************************/
        List<Entity> entities = new ArrayList<>();

        entities.add(player);

        //Pine Tree Model
        RawModel model = OBJLoader.loadObjModel("pine", loader);
        TexturedModel tree = new TexturedModel(model, new ModelTexture(loader.loadTexture("/objectTextures/pine")));

        //Multiple Fern Models (using different textures with same model)
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("/objectTextures/fernAtlas"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern",loader), fernTextureAtlas);
        fern.getTexture().setHasTransparency(true);

        //Lamp models (the locations are important and related to the lights below)
        TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp",loader),
                new ModelTexture(loader.loadTexture("/objectTextures/lamp")));
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
            float xPos = Math.abs(random.nextFloat() * TerrainSquare.TERRAIN_SIZE*TerrainGrid.DIMENSIONS);
            float zPos = Math.abs(random.nextFloat() * TerrainSquare.TERRAIN_SIZE*TerrainGrid.DIMENSIONS);
            float scale = (float) Math.abs(random.nextGaussian() * random.nextInt() % 3);
            Entity entity = new Entity(fern, new Vector2f(xPos,zPos), new Vector3f(0,0,0),scale);
            if(entity.getPosition().getY() > 0) {
                entities.add(entity);
            }
        }

        for(int i = 0; i < 500; i++) {
            float xPos = Math.abs(random.nextFloat() * TerrainSquare.TERRAIN_SIZE*TerrainGrid.DIMENSIONS);
            float zPos = Math.abs(random.nextFloat() * TerrainSquare.TERRAIN_SIZE*TerrainGrid.DIMENSIONS);
            float scale = (float) Math.abs(random.nextGaussian() * random.nextInt() % 4);
            Entity entity = new Entity(tree, new Vector2f(xPos,zPos), new Vector3f(0,0,0),scale);
            if(entity.getPosition().getY() > 0) {
                entities.add(entity);
            }
        }

        /****************************************LIGHTS****************************************/

        List<Light> lights = new ArrayList<>();

        Light sun = new Light(new Vector2f(100000,-100000),15000, new Vector3f(1f,1f,1f));
        lights.add(sun);
        //red lamp
        lights.add(new Light(new Vector2f(lampOneX,lampOneY),15,new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
        //green lamp
        lights.add(new Light(new Vector2f(lampTwoX,lampTwoY),15,new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
        //yellow lamp
        lights.add(new Light(new Vector2f(lampThreeX,lampThreeY),15,new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f)));

        /****************************************GUIS**************************************************/
        List<GuiTexture> guiTextures = new ArrayList<>();
        GuiTexture backgroundStaminaBar = new GuiTexture(loader.loadTexture("/guis/backgroundBar"), new Vector2f(-0.6f, 0.9f), new Vector2f(0.25f, 0.05f));
        GuiTexture staminaBar = new GuiTexture(loader.loadTexture("/guis/staminaBar"), new Vector2f(-0.6f, 0.9f), new Vector2f(0.25f, 0.05f));
        guiTextures.add(backgroundStaminaBar);
        guiTextures.add(staminaBar);
        //TODO: Create Character HP, Mana GUI

        /****************************************MAIN GAME LOOP****************************************/

        //Calling right before while loop resets delta to 0 so we start right at midday
        DisplayManager.getFrameTimeSeconds();
        while(!Display.isCloseRequested()){
            player.move();
            starParticleSystemAdditive.generateParticles(new Vector3f(lampThreeX+200, TerrainGrid.getCurrentTerrainHeight(lampThreeX+200,lampTwoY+100)+ 200, lampTwoY+100));
            ParticleMaster.update(camera);
            AudioMaster.setListenerData(player.getPosition().getX(),player.getPosition().getY(),player.getPosition().getZ());

            lakeSource.setVolume(Math.max(0,(1 - (player.getDistanceToWater()/noiseDistanceThreshold))/4));

            if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
                player.printCurrentLocation();
            }

            camera.move();

            renderer.renderShadowMap(entities,sun);

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            //change sun brightness
            timeInSeconds += DisplayManager.getFrameTimeSeconds();
            timeInSeconds %= MIDNIGHT;
            if(timeInSeconds < MIDDAY) {
                sun.setColor(new Vector3f(timeInSeconds/MIDDAY,timeInSeconds/MIDDAY,timeInSeconds/MIDDAY));
            } else {
                sun.setColor(new Vector3f((MIDNIGHT-timeInSeconds)/MIDDAY,(MIDNIGHT-timeInSeconds)/MIDDAY,(MIDNIGHT-timeInSeconds)/MIDDAY));
            }

            //RENDER REFLECTION TEXTURE
            buffers.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - WaterTile.HEIGHT);
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(entities,lights,camera,new Vector4f(0,1,0,-WaterTile.HEIGHT));
            camera.getPosition().y += distance;
            camera.invertPitch();

            //RENDER REFRACTION TEXTURE
            buffers.bindRefractionFrameBuffer();
            renderer.renderScene(entities,lights,camera,new Vector4f(0,-1,0,WaterTile.HEIGHT));

            //RENDER TO SCREEN
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            buffers.unbindCurrentFrameBuffer();
            renderer.renderScene(entities,lights,camera, new Vector4f(0,-1,0,1500));
            waterRenderer.render(waters,camera,sun);

            ParticleMaster.renderParticles(camera);
            System.out.println(player.getCurrentStamina());
            staminaBar.setPosition(new Vector2f(-0.6f - 0.25f + (0.25f * Math.min(100,(player.getCurrentStamina()/100f))), 0.9f));
            staminaBar.setScale(new Vector2f(0.25f * Math.min(100,(player.getCurrentStamina()/100f)), 0.05f));
            guiRenderer.render(guiTextures);
            TextMaster.render();
            DisplayManager.updateDisplay();
        }

        /****************************************CLEAN UP****************************************/

        ParticleMaster.cleanUp();
        AudioMaster.cleanUp();
        TextMaster.cleanUp();
        buffers.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        waterShader.cleanUp();
        DisplayManager.closeDisplay();
    }

    public static float getTimeInSeconds() {
        return timeInSeconds;
    }
}
