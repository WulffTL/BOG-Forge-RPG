package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import terrains.TerrainSquare;

/**
 * Created by Travis on 10/26/2015.
 *
 */
public class Camera {

    private float distanceFromPlayer = 100;
    private float angleAroundPlayer = 0;

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    private Vector3f position = new Vector3f(400,15,350);
    private float pitch = 30;
    private float yaw = 180;
    private float roll;

    private Player player;

    public Camera(Player player){
        this.player = player;
    }

    public Camera(){}

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
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticalDistance;
    }

    private float calculateHorizontalDistance(){
        float horizontalDistance = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
        if(horizontalDistance < 0){
            horizontalDistance = 0;
        }
        return horizontalDistance;

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

    /**
     * Changes the angle around the player based on mouse input from the user
     */
    public void calculateAngleAroundPlayer(){
        if(Mouse.isButtonDown(0)){
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
            float pitchChange = Mouse.getDY() * 0.3f;
            pitch -= pitchChange;
            if(pitch < 10){
                pitch = 10;
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
        if(Mouse.isButtonDown(0) && Mouse.isButtonDown(1)) {
            movePlayerParallelWithCamera();
        }
    }

    private void movePlayerParallelWithCamera() {
        while(angleAroundPlayer <= -0.3f || angleAroundPlayer >= 0.3f) {
            int multiplier = ((angleAroundPlayer < 0 ? -1 : 1));
            player.increaseHRotation(0,multiplier*0.1f,0);
            angleAroundPlayer -= multiplier*0.1f;
        }
    }
}
