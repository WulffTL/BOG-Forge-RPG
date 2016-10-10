package entities;

import engineTester.MainGameLoop;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import terrains.TerrainSquare;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Travis on 2/6/2016.
 */
public class NPC extends Entity {

    private static final float NPC_SPEED = 45;
    private static final float OUT_OF_BOUNDS = MainGameLoop.getGridSizeTerrains()* TerrainSquare.getSIZE();
    private static Vector2f evadeVector;
    private static Vector2f pursueVector;
    private static Vector2f moveVector = new Vector2f(1,0);
    ArrayList<Entity> evadeList;
    ArrayList<Entity> pursueList;

    public NPC(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, float boundingRadius,
               ArrayList<Entity> evadeList, ArrayList<Entity> pursueList) {
        super(model, position, rotX, rotY, rotZ, scale, boundingRadius);
        this.evadeList = evadeList;
        this.pursueList = pursueList;
    }

    public NPC(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ, float scale, float boundingRadius) {
        super(model, index, position, rotX, rotY, rotZ, scale, boundingRadius);
    }

    private Vector2f evadeVector(){
        Vector2f evadeSumVector = new Vector2f();
        if(this.evadeList != null) {
            for (Entity entity : this.evadeList) {
                float xComponent = entity.getPosition().x - this.getPosition().x;
                if(xComponent != 0) {
                    xComponent = 1 / xComponent;
                }
                float zComponent = entity.getPosition().z - this.getPosition().z;
                if (zComponent != 0) {
                    zComponent = 1 / zComponent;
                }
                Vector2f entityPositionVector2f = new Vector2f(xComponent, zComponent);
                evadeSumVector = Vector2f.add(evadeSumVector, entityPositionVector2f, evadeSumVector);
            }
            return evadeSumVector;
        }else{
            Random random = new Random(System.currentTimeMillis());
            Vector2f randomMove = new Vector2f(random.nextFloat(),random.nextFloat());
            return randomMove.normalise(randomMove);
        }
        }

    private Vector2f pursueVector(){
        Vector2f pursueSumVector = new Vector2f();
        if(this.pursueList != null) {
            for (Entity entity : this.pursueList) {
                float xComponent = entity.getPosition().x - this.getPosition().x;
                if(xComponent != 0) {
                    xComponent = 1 / xComponent;
                }
                float zComponent = entity.getPosition().z - this.getPosition().z;
                if(zComponent != 0) {
                    zComponent = 1 / zComponent;
                }
                Vector2f entityPositionVector2f = new Vector2f(xComponent, zComponent);
                pursueSumVector = Vector2f.add(pursueSumVector, entityPositionVector2f, pursueSumVector);
            }
            return pursueSumVector;
        }else {
            Random random = new Random(System.currentTimeMillis());
            Vector2f randomMove = new Vector2f(random.nextFloat(),random.nextFloat());
            return randomMove.normalise(randomMove);
        }
        }

    public void addEvade(Entity evadeEntity){
        this.evadeList.add(evadeEntity);
    }

    public void addPursue(Entity pursueEntity){
        this.pursueList.add(pursueEntity);
    }
}
