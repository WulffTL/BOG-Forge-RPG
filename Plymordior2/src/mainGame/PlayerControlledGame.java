package mainGame;

import entities.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.TerrainSquare;

import java.util.List;

/**
 * Created by Travis on 5/5/2016.
 */

public class PlayerControlledGame {

    private Loader loader;
    private List<Entity> entities;
    private List<Entity> immovableEntities;
    private List<Light> lights;
    private TerrainSquare[][] terrainArray;
    private List<TerrainSquare> terrains;
    private MasterRenderer renderer;
    private Player player;
    private Camera camera;
    private Vector4f clipPlane = new Vector4f(0,-1,0,15000);


    public PlayerControlledGame(Loader loader, List<Entity> entities, List<Entity> immovableEntities, List<Light> lights,
                                TerrainSquare[][] terrainArray, List<TerrainSquare> terrains, MasterRenderer renderer, Player player
                         ){
        this.loader = loader;
        this.entities = entities;
        this.immovableEntities = immovableEntities;
        this.lights = lights;
        this.terrainArray = terrainArray;
        this.terrains = terrains;
        this.renderer = renderer;
        this.player = player;
        this.camera = new Camera(player);
    }

    public void grantPlayerControl(){
        /****************************************MAIN GAME LOOP****************************************/

        entities.add(player);
        while(!Display.isCloseRequested()){
            player.move();
            camera.move();
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            renderer.renderScene(entities,immovableEntities,lights,camera,clipPlane);
            DisplayManager.updateDisplay();
        }

        /****************************************CLEAN UP****************************************/

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }



}
