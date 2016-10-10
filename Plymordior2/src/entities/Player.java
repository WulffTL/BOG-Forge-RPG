package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
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

    /**
     * Moves the player based on user input as well as the current terrain
     * @param terrain the terrain the player is currently occupying, used for yPos ((TODO: This must be removed))
     */
    public void move(TerrainSquare terrain){
        //checkInputs() will set the values for movement
        checkInputs();
        currentSpeed *= (currentStamina/100);
        super.increaseHRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);

        float maxMove = RUN_SPEED * DisplayManager.getFrameTimeSeconds();
        float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
        float strafeDistance = strafeSpeed * DisplayManager.getFrameTimeSeconds();

        float dx = (float) (distance * Math.sin(Math.toRadians(super.gethRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.gethRotY())));
        float strafex = (float) (strafeDistance * Math.sin(Math.toRadians(super.gethRotY() + 90)));
        float strafez = (float) (strafeDistance * Math.cos(Math.toRadians(super.gethRotY() + 90)));

        float moveX = Maths.betweenValues((strafex+dx),-maxMove, maxMove);
        float moveZ = Maths.betweenValues((strafez+dz),-maxMove, maxMove);
        super.increasePosition(moveX,0,moveZ);

        upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x,super.getPosition().z);
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
        //Check for moving forward/backwards
        if(Keyboard.isKeyDown(Keyboard.KEY_W) || (Mouse.isButtonDown(0) && Mouse.isButtonDown(1))){
            this.currentSpeed = RUN_SPEED;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            this.currentSpeed = -0.5f*RUN_SPEED;
        }else {
            this.currentSpeed = 0;
        }
        //Check for left/right strafing movement
        if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
            this.strafeSpeed = RUN_SPEED;
            this.staminaLost = STAMINA_DRAIN;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_E)){
            this.strafeSpeed = -RUN_SPEED;
            this.staminaLost = STAMINA_DRAIN;
        }else {
            this.strafeSpeed = 0;
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
            this.staminaLost = STAMINA_DRAIN;
        }

        if(this.currentSpeed == 0){
            this.staminaLost = -20* STAMINA_DRAIN;
        }

    }

    public void printCurrentLocation() {
        System.out.printf("x Position: %f\ny Position: %f\nz Position: %f\n",getPosition().getX(),getPosition().getY(),getPosition().getZ());
    }

    public float getCurrentStamina() {
        return currentStamina;
    }}
