package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;

/**
 * Created by Travis on 10/26/2015.
 */
public class Camera {

    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    private Vector3f position = new Vector3f(100,35,50);
    private float pitch = 10;
    private float yaw = 0;
    private float roll;

    private Player player;
    private Terrain terrain;

    public Camera(Player player){
        this.player = player;
    }

    public void move(){
        calculateZoom();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance,verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
        float theta = player.getRotY() + angleAroundPlayer;
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
