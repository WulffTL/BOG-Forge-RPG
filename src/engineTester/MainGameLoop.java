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
import toolbox.Maths;
import toolbox.Timer;
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
 *
 */

public class MainGameLoop {

    public static final int DAY_LENGTH = 2400;
    public static final int MIDDAY = DAY_LENGTH /2;

    public static void main(String[] args) {

        Timer.setDaylength(DAY_LENGTH);
        Timer.setTime(0);

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        TextMaster.init(loader);

        /****************************************TERRAINS****************************************/
        //We place the four textures into a texture pack for the terrain to read
        TerrainTexturePack texturePack = new TerrainTexturePack(loader, "/terrainTextures/snow1", "/terrainTextures/mud", "/terrainTextures/grassy2", "/terrainTextures/brick");
        //We load up a blendmap which will tell the terrain which texture to use at what time
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("/blendMaps/blendMap"));
        TerrainTexture mountainBlendMap = new TerrainTexture(loader.loadTexture("/blendMaps/mountainBlendMap"));
        //load in texture pack, blend map, and height map to create the texture

        TerrainGrid.addTerrainSquare(new TerrainSquare(0,0,loader,texturePack,mountainBlendMap,"mountainHeightMap"));

        for(int i = 0; i < TerrainGrid.DIMENSIONS; i++) {
            for(int j = 0; j < TerrainGrid.DIMENSIONS; j++) {
                if(!(i == 0 && j == 0)){
                    TerrainGrid.addTerrainSquare(new TerrainSquare(i,j,loader,texturePack,blendMap));
                }
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
        Player player = new Player(playerTexture,TerrainGrid.getPosition(0,0,0.4150f,0.4309f),new Vector3f(0,0,0),1);

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
        //Particle Constants
        Vector2f middleOfStars = TerrainGrid.getPosition(0,0,0.4072f,0.3174f);
        float starParticleHeight = HeightsGenerator.AMPLITUDE * 1.1f;
        float starRadius = 120;
        float frequency = 30;
        //Particle Textures
        ParticleTexture planetTexture = new ParticleTexture(loader.loadTexture("/particleTextures/particleStar"), 1, true);
        ParticleTexture moonTexture = new ParticleTexture(loader.loadTexture("/particleTextures/cosmic"),4,true);
        ParticleTexture sunTexture = new ParticleTexture(loader.loadTexture("/particleTextures/fire"),8,true);

        ParticleMaster.init(loader,renderer.getProjectionMatrix());

        ParticleSystem planetParticleSystem = new ParticleSystem(planetTexture,100,10,0.01f,15f,15f);
        planetParticleSystem.setLifeError(0.5f);
        planetParticleSystem.setSpeedError(0.5f);
        planetParticleSystem.setScaleError(10f);

        ParticleSystem moonParticleSystem = new ParticleSystem(moonTexture,100,0.01f,0f,1f,15f);

        ParticleSystem outerSunParticleSystem = new ParticleSystem(sunTexture,50,4,0,5f,50f);
        ParticleSystem innerSunParticleSystem = new ParticleSystem(sunTexture,200,0.75f,0,10f,100f);
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

        //Lamp models
        // The locations are important and related to the lights below
        Vector2f lampOneLocation = TerrainGrid.getPosition(0,0,0.4482f,0.5322f);
        Vector2f lampTwoLocation = TerrainGrid.getPosition(0,0,0.3984f,0.5322f);

        TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp",loader),
                new ModelTexture(loader.loadTexture("/objectTextures/lamp")));
        lamp.getTexture().setUseFakeLighting(true);
        //Add lamps

        entities.add(new Entity(lamp, lampOneLocation));
        entities.add(new Entity(lamp, lampTwoLocation));

        //Create stall
        float stallPosX = 0.4541f;
        float stallPozZ = 0.4209f;
        Vector3f stallRotation = new Vector3f(0,135,0);
        float stallScale = 3;
        Entity stall2 = new Entity(loader,"stall","stallTexture");
        stall2.setPosition(TerrainGrid.get3fPosition(0,0,stallPosX,stallPozZ));
        stall2.setRotation(stallRotation);
        stall2.setScale(stallScale);
        entities.add(stall2);

        //Multiple Fern Models (using different textures with same model)
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("/objectTextures/fernAtlas"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern",loader), fernTextureAtlas);
        fern.getTexture().setHasTransparency(true);
        //Adding fern models randomly
        Random random = new Random(); //Some will be in random locations
        for(int i = 0; i < 5000; i++) {
            float xPos = Math.abs(random.nextFloat() * TerrainSquare.TERRAIN_SIZE*TerrainGrid.DIMENSIONS);
            float zPos = Math.abs(random.nextFloat() * TerrainSquare.TERRAIN_SIZE*TerrainGrid.DIMENSIONS);
            float scale = (float) Math.abs(random.nextGaussian() * random.nextInt() % 3);
            Entity entity = new Entity(fern, new Vector2f(xPos,zPos), new Vector3f(0,0,0),scale);
            if(Maths.isBetween(entity.getPosition().getY(),0,HeightsGenerator.AMPLITUDE*0.9f)) {
                entities.add(entity);
            }
        }

        //Pine Tree Model and texture
        RawModel model = OBJLoader.loadObjModel("pine", loader);
        TexturedModel tree = new TexturedModel(model, new ModelTexture(loader.loadTexture("/objectTextures/pine")));
        //Add trees randomly
        for(int i = 0; i < 500; i++) {
            float xPos = Math.abs(random.nextFloat() * TerrainSquare.TERRAIN_SIZE*TerrainGrid.DIMENSIONS);
            float zPos = Math.abs(random.nextFloat() * TerrainSquare.TERRAIN_SIZE*TerrainGrid.DIMENSIONS);
            float scale = (float) Math.abs(random.nextGaussian() * random.nextInt() % 4);
            Entity entity = new Entity(tree, new Vector2f(xPos,zPos), new Vector3f(0,0,0),scale);
            if(Maths.isBetween(entity.getPosition().getY(),0,HeightsGenerator.AMPLITUDE*0.9f)) {
                entities.add(entity);
            }
        }

        /****************************************LIGHTS****************************************/

        List<Light> lights = new ArrayList<>();

        Light sun = new Light(new Vector2f(100000,-100000),85000, new Vector3f(1f,1f,1f));
        lights.add(sun);
        //TODO: Add method to entities to set lights on them
        //red lamp
        lights.add(new Light(lampOneLocation,15,new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
        //green lamp
        lights.add(new Light(lampTwoLocation,15,new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
        //sun light
        lights.add(new Light(middleOfStars, 25, new Vector3f(1,1,1), new Vector3f(0.1f,0.001f,0.001f)));

        /****************************************GUIS**************************************************/

        List<GuiTexture> guiTextures = new ArrayList<>();
        GuiTexture backgroundStaminaBar = new GuiTexture(loader.loadTexture("/guis/backgroundBar"), new Vector2f(-0.6f, 0.9f), new Vector2f(0.25f, 0.05f));
        GuiTexture staminaBar = new GuiTexture(loader.loadTexture("/guis/staminaBar"), new Vector2f(-0.6f, 0.9f), new Vector2f(0.25f, 0.05f));
        guiTextures.add(backgroundStaminaBar);
        guiTextures.add(staminaBar);
        //TODO: Create Character HP, Mana GUI

        /****************************************MAIN GAME LOOP****************************************/

        //Calling right before while loop resets delta to 0 so we start right at 0
        DisplayManager.getFrameTimeSeconds();

        float timeInSeconds;
        float anglePlanetAroundSun;

        while(!Display.isCloseRequested()){
            Timer.update();
            timeInSeconds = Timer.getTime();
            player.move();

            //Planetarium Particles

            anglePlanetAroundSun = (float) (frequency*2*Math.PI*(timeInSeconds / DAY_LENGTH));

            //BEGIN PLANET PARTICLES
            float planetXComponent = starRadius * (float)Math.sin(anglePlanetAroundSun);
            float planetYComponent = starRadius * (float)Math.cos(anglePlanetAroundSun);

            //Set particle position
            planetParticleSystem.generateParticles(
                    new Vector3f(
                            middleOfStars.x + (planetXComponent),
                            starParticleHeight,
                            middleOfStars.y + (planetYComponent)
                    )
            );

            //set direction particle will travel
            planetParticleSystem.setDirection(
                    //Derivative of position (normalized)
                    new Vector3f(
                            -planetYComponent/starRadius,
                            0,
                            planetXComponent/starRadius
                    ),
                    0.1f
            );

            //BEGIN MOON PARTICLES
            float moonRadius = starRadius/20f;
            float moonXComponent = (float) (((starRadius) * Math.sin(anglePlanetAroundSun)) - ((moonRadius) * Math.sin((starRadius/moonRadius)*anglePlanetAroundSun)));
            float moonYComponent = (float) (((starRadius) * Math.cos(anglePlanetAroundSun)) - ((moonRadius) * Math.cos((starRadius/moonRadius)*anglePlanetAroundSun)));

            moonParticleSystem.generateParticles(
                    new Vector3f(
                            middleOfStars.x + moonXComponent,
                            (float) (starParticleHeight + (moonRadius * (Math.sin((starRadius/moonRadius)*anglePlanetAroundSun)))),
                            middleOfStars.y + moonYComponent
                    )
            );

            moonParticleSystem.setDirection(
                    //Found by guess and check since derivatives weren't working as planned
                    new Vector3f(
                            -1f * (float) Math.cos(anglePlanetAroundSun + Math.PI/2),
                            (float) Math.cos((starRadius/moonRadius)*anglePlanetAroundSun),
                            (float) Math.sin(anglePlanetAroundSun + Math.PI/2)
                    ),
                    0.5f
            );

            outerSunParticleSystem.generateParticles(
                    new Vector3f(
                            middleOfStars.x,
                            starParticleHeight,
                            middleOfStars.y
                    )
            );

            innerSunParticleSystem.generateParticles(
                    new Vector3f(
                            middleOfStars.x,
                            starParticleHeight,
                            middleOfStars.y
                    )
            );

            ParticleMaster.update(camera);
            AudioMaster.setListenerData(player.getPosition().getX(),player.getPosition().getY(),player.getPosition().getZ());

            lakeSource.setVolume(Math.max(0,(1 - (player.getRoundedDistanceToElevation(WaterTile.HEIGHT)/noiseDistanceThreshold))/4));

            if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
                player.printCurrentLocation();
            }

            //Changing time
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                Timer.setTime(Timer.getTime() + 1f);
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                Timer.setTime(Timer.getTime() - 1f);
            }

            camera.move();

            renderer.renderShadowMap(entities,sun);

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            //change sun brightness
            if(Timer.getTime() < MIDDAY) {
                sun.setColor(new Vector3f(2* timeInSeconds /MIDDAY,2* timeInSeconds /MIDDAY,2* timeInSeconds /MIDDAY));
            } else {
                sun.setColor(new Vector3f((2*(DAY_LENGTH - timeInSeconds))/MIDDAY,2*(DAY_LENGTH - timeInSeconds)/MIDDAY,2*(DAY_LENGTH - timeInSeconds)/MIDDAY));
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

}
