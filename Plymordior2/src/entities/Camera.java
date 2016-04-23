package entities;

import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;

/**
 * Created by Travis on 10/26/2015.
 */
public class Camera {

    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    private Vector3f position = new Vector3f(400,15,300);
    private float pitch = 10;
    private float yaw = 180;
    private float roll;

    private Player player;
    private Terrain terrain;

    public Camera(Player player){
        this.player = player;
    }

    public Camera(){}

    /**
     * This method is to be used for the start menu to create an environment that controls the scope of the camera
     * only to where the entity is in view
     */
    public void startMenuMove(){
        if(Mouse.isButtonDown(0)){
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
            if(pitch < 2){
                pitch = 2;
            } else if(pitch > 14.5f){
                pitch = 14.5f;
            }
        }
    }

    public void move(){
        calculateZoom();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance,verticalDistance);
        this.yaw = 180 - (player.gethRotY() + angleAroundPlayer);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
        float theta = player.gethRotY() + angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticalDistance;
    }

    private float calculateHorizontalDistance(){
        float horiztontalDistance = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
        if(horiztontalDistance < 0){
            horiztontalDistance = 0;
        }
        return horiztontalDistance;

    }

    private float calculateVerticalDistance(){
        float verticalDistance = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
        if(verticalDistance < 0){
            verticalDistance = 0;
        }
        return verticalDistance;

    }

    public void calculateZoom(){
        float zoomLevel = Mouse.getDWheel() * 0.1f;
        distanceFromPlayer -= zoomLevel;
    }

    public void calculateAngleAroundPlayer(){
        if(Mouse.isButtonDown(0)){
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
            if(pitch < 0){
                pitch = 0;
            } else if(pitch > 90){
                pitch = 90;
            }
        }if(Mouse.isButtonDown(1)){
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
            if(pitch < 0){
                pitch = 0;
            } else if(pitch > 90){
                pitch = 90;
            }
        }
    }
}
