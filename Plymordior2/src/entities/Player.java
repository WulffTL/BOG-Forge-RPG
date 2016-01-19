package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Travis on 1/12/2016.
 */
public class Player extends Entity {

    private static final float RUN_SPEED = 100;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = 20;
    private static final float STAMNIA_DRAIN = 1 ;

    private static final float TERRAIN_HEIGHT = 0;

    private float currentSpeed = 0;
    private float strafeSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardSpeed = 0;
    private float staminaLost = 0;
    private float currentStamina = 100;

    private boolean isInAir = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale, 1);
    }

    public void move(Terrain terrain){
        falling(terrain);
        checkInputs();
        currentSpeed *= (currentStamina/100);
        super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);

        float maxMove = RUN_SPEED * DisplayManager.getFrameTimeSeconds();
        float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
        float strafeDistance = strafeSpeed * DisplayManager.getFrameTimeSeconds();

        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        float strafex = (float) (strafeDistance * Math.sin(Math.toRadians(super.getRotY() + 90)));
        float strafez = (float) (strafeDistance * Math.cos(Math.toRadians(super.getRotY() + 90)));

        float moveX = Maths.betweenValues((strafex+dx),-maxMove, maxMove);
        float moveZ = Maths.betweenValues((strafez+dz),-maxMove, maxMove);

        if(terrain.getHeightOfTerrain(getPosition().x + moveX,getPosition().z + moveZ) <=
                getPosition().y + 2){
            super.increasePosition(moveX,0,moveZ);
        }

        upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x,super.getPosition().z);
        if(super.getPosition().y< terrainHeight){
            upwardSpeed = 0;
            isInAir = false;
            super.getPosition().y = terrainHeight;
        }
        currentStamina -= staminaLost * DisplayManager.getFrameTimeSeconds();
        currentStamina = Maths.betweenValues(currentStamina,0,100);
    }

    private  void jump(){
        if(!isInAir) {
            this.upwardSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    private void checkInputs(){
        if(Keyboard.isKeyDown(Keyboard.KEY_W) || (Mouse.isButtonDown(1) && Mouse.isButtonDown(0))){
            this.currentSpeed = RUN_SPEED;
            this.staminaLost = STAMNIA_DRAIN;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            this.currentSpeed = -0.5f*RUN_SPEED;
            this.staminaLost = 0.4f*STAMNIA_DRAIN;
        }else {
            this.currentSpeed = 0;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
            this.strafeSpeed = RUN_SPEED;
            this.staminaLost = STAMNIA_DRAIN;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_E)){
            this.strafeSpeed = -RUN_SPEED;
            this.staminaLost = STAMNIA_DRAIN;
        }else {
            this.strafeSpeed = 0;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            this.currentTurnSpeed = -TURN_SPEED;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            this.currentTurnSpeed = TURN_SPEED;
        }else {
            this.currentTurnSpeed = 0;
        }

        if(Mouse.isButtonDown(1)){
            this.currentTurnSpeed = Mouse.getDX()*100;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            jump();
            this.staminaLost = STAMNIA_DRAIN;
        }

        if(this.currentSpeed == 0){
            this.staminaLost = -20*STAMNIA_DRAIN;
        }

    }

    public float getCurrentStamina() {
        return currentStamina;
    }

    private void falling(Terrain terrain){
        int timesToCheck=2;
        float currentPlayerHeight = getPosition().y;
        List<Float> nearbyHeights = new ArrayList<>();
        for (int i = 0; i < timesToCheck; i++){
            nearbyHeights.add(terrain.getHeightOfTerrain(getPosition().x + (i+1)*0.1f, getPosition().z + (i+1)*0.1f));
            nearbyHeights.add(terrain.getHeightOfTerrain(getPosition().x + (i+1)*-0.1f, getPosition().z + (i+1)*0.1f));
            nearbyHeights.add(terrain.getHeightOfTerrain(getPosition().x + (i+1)*0.1f, getPosition().z + (i+1)*-0.1f));
            nearbyHeights.add(terrain.getHeightOfTerrain(getPosition().x + (i+1)*-0.1f, getPosition().z + (i+1)*-0.1f));
        }
        float minHeight = Maths.getMinValue(nearbyHeights);
        int remainder = nearbyHeights.indexOf(minHeight) % 4;
        int count = 0;
        for(int i = remainder; i < nearbyHeights.size(); i += 4){
            if(nearbyHeights.get(i) < currentPlayerHeight - 0.3f){
                count++;
            }
        }
        if(count == timesToCheck){
            switch (remainder) {
                case 0: for(int j = 0; j < timesToCheck; j++){
                    increasePosition(0.1f,0,0.1f);
                    upwardSpeed += 0.1f * GRAVITY * DisplayManager.getFrameTimeSeconds();
                    super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);
                    float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x,super.getPosition().z);
                    if(super.getPosition().y< terrainHeight){
                        upwardSpeed = 0;
                        isInAir = false;
                        super.getPosition().y = terrainHeight;
                    }
                }
                    break;
                case 1: for(int j = 0; j < timesToCheck; j++){
                    increasePosition(-0.1f,0,0.1f);
                    upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
                    super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);
                    float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x,super.getPosition().z);
                    if(super.getPosition().y< terrainHeight){
                        upwardSpeed = 0;
                        isInAir = false;
                        super.getPosition().y = terrainHeight;
                    }
                }
                    break;
                case 2: for(int j = 0; j < timesToCheck; j++){
                    increasePosition(0.1f,0,-0.1f);
                    upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
                    super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);
                    float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x,super.getPosition().z);
                    if(super.getPosition().y< terrainHeight){
                        upwardSpeed = 0;
                        isInAir = false;
                        super.getPosition().y = terrainHeight;
                    }
                }
                    break;
                case 3: for(int j = 0; j < timesToCheck; j++){
                    increasePosition(-0.1f,0,-0.1f);
                    upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
                    super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);
                    float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x,super.getPosition().z);
                    if(super.getPosition().y< terrainHeight){
                        upwardSpeed = 0;
                        isInAir = false;
                        super.getPosition().y = terrainHeight;
                    }
                }
                    break;
            }
        }

    }
}
