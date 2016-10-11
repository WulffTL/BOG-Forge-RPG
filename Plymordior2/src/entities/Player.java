package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.TerrainGrid;
import terrains.TerrainSquare;
import toolbox.Maths;

/**
 * Created by Travis on 1/12/2016.
 */
public class Player extends Entity {

    private static final float RUN_SPEED = 100;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -80;
    private static final float JUMP_POWER = 30;
    private static final float STAMINA_DRAIN = 1 ;
    private static final float STAMINA_GAIN = 50;
    private static final float STAMINA_CAP = 100;

    private float currentTurnSpeed = 0;
    private float upwardSpeed = 0;
    private float staminaLost = 0;
    private float currentStamina = 100;

    private boolean isInAir = false;
    private boolean isMovingForward = false;
    private boolean isMovingBackward = false;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;

    private float distance;
    private float dx;
    private float dz;

    public Player(TexturedModel model, Vector2f position, Vector3f rotations, float scale) {
        super(model, position, rotations, scale);
    }

    /**
     * Moves the player based on user input as well as the current terrain
     */
    public void move(){
        //checkInputs() will set the values for movement
        checkInputs();

        //Turn character
        super.increaseHRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        //Calculate distance we will move
        distance = RUN_SPEED * DisplayManager.getFrameTimeSeconds();

        //Calculate direction of movement, components of movement, and execute movement
        if(isMovingForward) {
            if(isMovingRight) {
                isMovingLeft = false;
                dx = (float) (distance * Math.sin(Math.toRadians(super.gethRotY() - 45)));
                dz = (float) (distance * Math.cos(Math.toRadians(super.gethRotY() - 45)));
            } else if (isMovingLeft) {
                isMovingRight = false;
                dx = (float) (distance * Math.sin(Math.toRadians(super.gethRotY() + 45)));
                dz = (float) (distance * Math.cos(Math.toRadians(super.gethRotY() + 45)));
            } else {
                dx = (float) (distance * Math.sin(Math.toRadians(super.gethRotY())));
                dz = (float) (distance * Math.cos(Math.toRadians(super.gethRotY())));
            }
        } else if (isMovingBackward) {
            if(isMovingRight) {
                isMovingLeft = false;
                dx = (float) (distance * Math.sin(Math.toRadians(super.gethRotY() - 135)));
                dz = (float) (distance * Math.cos(Math.toRadians(super.gethRotY() - 135)));
            } else if(isMovingLeft) {
                isMovingRight = false;
                dx = (float) (distance * Math.sin(Math.toRadians(super.gethRotY() - 225)));
                dz = (float) (distance * Math.cos(Math.toRadians(super.gethRotY() - 225)));
            } else {
                dx = (float) (distance * Math.sin(Math.toRadians(super.gethRotY() + 180)));
                dz = (float) (distance * Math.cos(Math.toRadians(super.gethRotY() + 180)));
            }
        } else if (isMovingRight) {
            dx = (float) (distance * Math.sin(Math.toRadians(super.gethRotY() - 90)));
            dz = (float) (distance * Math.cos(Math.toRadians(super.gethRotY() - 90)));
        } else if (isMovingLeft) {
            dx = (float) (distance * Math.sin(Math.toRadians(super.gethRotY() + 90)));
            dz = (float) (distance * Math.cos(Math.toRadians(super.gethRotY() + 90)));
        } else {
            dx = 0;
            dz = 0;
        }

        upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(dx, upwardSpeed * DisplayManager.getFrameTimeSeconds(), dz);
        float terrainHeight = TerrainGrid.getCurrentTerrainHeight(this.getPosition().x,this.getPosition().z);
        if(super.getPosition().y < terrainHeight && terrainHeight != 0){
            upwardSpeed = 0;
            isInAir = false;
            super.getPosition().y = terrainHeight;
        }
    }

    /**
     * This will increase the players upward speed by their jump power. The jump power will decrease over time
     */
    private  void jump(){
        if(!isInAir) {
            this.upwardSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    /**
     * Checks for user input and appropriately sets values to various variables related to the player's movement
     */
    private void checkInputs(){
        //Check for left/right strafing movement
        if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
            isMovingLeft = true;
            isMovingRight = false;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_E)){
            isMovingRight = true;
            isMovingLeft = false;
        }else {
            isMovingLeft = false;
            isMovingRight = false;
        }

        //Check for moving forward/backwards
        if(Keyboard.isKeyDown(Keyboard.KEY_W) || (Mouse.isButtonDown(0) && Mouse.isButtonDown(1))){
            isMovingForward = true;
            isMovingBackward = false;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            isMovingBackward = true;
            isMovingForward = false;
        }else {
            isMovingForward = false;
            isMovingBackward = false;
        }

        //Check for turning
        if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            this.currentTurnSpeed = -TURN_SPEED;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            this.currentTurnSpeed = TURN_SPEED;
        }else {
            this.currentTurnSpeed = 0;
        }
        //Check for mouse inputs
        if(Mouse.isButtonDown(1)){
            this.currentTurnSpeed = -Mouse.getDX()*20;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            jump();
        }
    }

    public void printCurrentLocation() {
        System.out.printf("x Position: %f\ny Position: %f\nz Position: %f\n",getPosition().getX(),getPosition().getY(),getPosition().getZ());
    }

    public float getCurrentStamina() {
        return currentStamina;
    }}
