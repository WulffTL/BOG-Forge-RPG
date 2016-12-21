package entities;

import models.TexturedModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.TerrainGrid;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Travis on 2/6/2016.
 *
 */
public class NPC extends Entity {

    private static final float NPC_SPEED = 20;
    private Vector2f evadeVector;
    private Vector2f pursueVector;
    private Vector2f moveVector = new Vector2f(1,0);
    private ArrayList<Entity> evadeList;
    private ArrayList<Entity> pursueList;
    private Vector2f initialPosition;

    public NPC(TexturedModel model, Vector2f position, Vector3f rotations, float scale) {
        super(model, position, rotations, scale);
        this.evadeList = new ArrayList<>();
        this.pursueList = new ArrayList<>();
        initialPosition = new Vector2f(position.getX(),position.getY());
    }

    private Vector2f getEvadeVector(){
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
            return evadeSumVector.normalise(evadeSumVector);
        }else{
            Random random = new Random(System.currentTimeMillis());
            Vector2f randomMove = new Vector2f(random.nextFloat(),random.nextFloat());
            return randomMove.normalise(randomMove);
        }
        }

    private Vector2f getPursueVector(){
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
            return pursueSumVector.normalise(pursueSumVector);
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

    public void move() {
        moveToTarget(new Vector2f(this.initialPosition.getX() + 100,this.initialPosition.getY() + 100));
//        float time = DisplayManager.getFrameTimeSeconds();
//        float distance = NPC_SPEED * time;
//        float dx = (distance * (getPursueVector().getX() + getEvadeVector().getX()));
//        float dz = (distance * (getPursueVector().getY() + getEvadeVector().getX()));
//        if(TerrainGrid.getTerrainByPosition(this.getPosition().x + dx, this.getPosition().z + dz) != null) {
//            super.increasePosition(dx,0,dz);
//            super.setEntityHeight(TerrainGrid.getCurrentTerrainHeight(this.getPosition().getX(),this.getPosition().getZ()));
//        }
    }

    public void moveToTarget(Vector2f targetPosition) {
        Vector2f currentPosition = new Vector2f(this.getPosition().getX(), this.getPosition().getZ());
        if(Maths.distanceBetween(currentPosition, targetPosition) > 1) {
            float distance = NPC_SPEED * DisplayManager.getFrameTimeSeconds();
            float slope = Maths.findSlope(targetPosition,currentPosition);
            float dx,dz;
            if(slope < 1) {
                dx = slope * distance;
                dz = distance;
            } else {
                dz = distance;
                dx = distance/slope;
            }
            super.increasePosition(dx,0,dz);
            super.setEntityHeight(TerrainGrid.getCurrentTerrainHeight(this.getPosition().getX(),this.getPosition().getZ()));
        }
    }

    public void moveInCircle(int radius, Vector2f center) {
        Vector2f angleOrigin = new Vector2f(center.getX() + radius, center.getY());
        Vector2f currentPosition = new Vector2f(this.getPosition().getX(), this.getPosition().getZ());
        float distanceBtwnPoints = Maths.distanceBetween(currentPosition,angleOrigin);
        float currentAngle = (float) Math.acos((Math.pow(radius,2) - distanceBtwnPoints)/(2*Math.pow(radius,2)));
        //arc length = radius * angle --> s = r*a
        float currentArcLength = radius * currentAngle;
        float timeSinceLastFrame = DisplayManager.getFrameTimeSeconds();
        float distance = timeSinceLastFrame * NPC_SPEED;
        //s = r*a --> a = s/r
        float addedAngle = (distance) / radius;
        float x = center.getX() + radius * (float) Math.sin(addedAngle + currentAngle);
        float z = center.getY() + radius * (float) Math.cos(addedAngle + currentAngle);
        System.out.println("Current Angle: " + currentAngle);
        System.out.println("Added Angle: " + addedAngle);
        this.setPosition(new Vector3f(x,TerrainGrid.getCurrentTerrainHeight(x,z),z));
    }
}
