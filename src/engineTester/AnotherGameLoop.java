package engineTester;

import entities.*;
import fontRendering.TextMaster;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.TerrainGrid;
import terrains.TerrainSquare;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Timer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wulfftl on 12/20/16.
 */
public class AnotherGameLoop {
    public static final int DAY_LENGTH = 2400;
    public static final int MIDDAY = DAY_LENGTH /2;

    public static void main(String[] args) {
        Timer.setDaylength(DAY_LENGTH);
        Timer.setTime(MIDDAY);

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        TextMaster.init(loader);

        /****************************************TERRAINS****************************************/
        //We place the four textures into a texture pack for the terrain to read
        TerrainTexturePack texturePack = new TerrainTexturePack(loader, "/terrainTextures/red", "/terrainTextures/blue", "/terrainTextures/green", "/terrainTextures/black");
        //We load up a blendmap which will tell the terrain which texture to use at what time
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("/blendMaps/blendMap"));

        for(int i = 0; i < TerrainGrid.DIMENSIONS; i++) {
            for(int j = 0; j < TerrainGrid.DIMENSIONS; j++) {
                TerrainGrid.addTerrainSquare(new TerrainSquare(i,j,loader,texturePack,blendMap));
            }
        }

        /****************************************PLAYER****************************************/
        //Our player model
        RawModel cubePlayer = OBJLoader.loadObjModel("person",loader);
        TexturedModel playerTexture = new TexturedModel(cubePlayer, new ModelTexture(loader.loadTexture("/objectTextures/playerTexture")));
        Player player = new Player(playerTexture,TerrainGrid.getPosition(TerrainGrid.DIMENSIONS/2,TerrainGrid.DIMENSIONS/2,0.5f,0.5f),new Vector3f(0,0,0),1);

        /***************************************NPCS*******************************************/
        NPC npc1 = new NPC(playerTexture,new Vector2f(player.getPosition().getX(),player.getPosition().getZ()),new Vector3f(0,0,0),1);
        NPC npc2 = new NPC(playerTexture,new Vector2f(player.getPosition().getX() + 50,player.getPosition().getZ() + 50),new Vector3f(0,0,0),1);
        NPC npc3 = new NPC(playerTexture,new Vector2f(player.getPosition().getX() + 90,player.getPosition().getZ() - 80),new Vector3f(0,0,0),1);
        NPC npc4 = new NPC(playerTexture,new Vector2f(player.getPosition().getX() - 90,player.getPosition().getZ() - 100),new Vector3f(0,0,0),1);
        //Evade lists
        //npc1.addEvade(npc2);
        npc1.addEvade(npc4);
        //npc2.addEvade(npc3);
        npc2.addEvade(npc4);
        //npc3.addEvade(npc4);
        npc3.addEvade(npc4);
        //npc4.addEvade(npc1);
        npc4.addEvade(npc2);
        //Pursue lists
        npc1.addPursue(player);
        npc2.addPursue(player);
        npc3.addPursue(player);
        npc4.addPursue(player);


        /****************************************CAMERA****************************************/
        Camera camera = new Camera(player);

        /****************************************RENDERERS****************************************/

        MasterRenderer renderer = new MasterRenderer(loader, camera);

        /****************************************MODELS****************************************/
        List<Entity> entities = new ArrayList<>();

        entities.add(player);
        entities.add(npc1);
        //entities.add(npc2);
        //entities.add(npc3);
        //entities.add(npc4);

        /****************************************LIGHTS****************************************/
        List<Light> lights = new ArrayList<>();

        Light sun = new Light(new Vector2f(100000,-100000),85000, new Vector3f(1f,1f,1f));
        lights.add(sun);

        /****************************************MAIN GAME LOOP****************************************/

        //Calling right before while loop resets delta to 0 so we start right at 0
        DisplayManager.getFrameTimeSeconds();

        float timeInSeconds;

        while(!Display.isCloseRequested()){
            Timer.update();
            timeInSeconds = Timer.getTime();
            //player.move();
            for(Entity e : entities) {
                e.move();
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

            renderer.renderScene(entities,lights,camera, new Vector4f(0,-1,0,1500));

            DisplayManager.updateDisplay();
        }

        /****************************************CLEAN UP****************************************/

        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
