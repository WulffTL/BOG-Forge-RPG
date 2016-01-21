package entities;

import models.RawModel;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;

import java.util.Random;

/**
 * Created by Travis on 10/25/2015.
 */
public class Entity {
    private TexturedModel model;
    private Vector3f position;
    private float rotX,rotY,rotZ;
    private float scale;
    private float boundingRadius;

    private int textureIndex = 0;

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
                  float scale, float boundingRadius) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.boundingRadius = boundingRadius;
    }

    public Entity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ,
                  float scale, float boundingRadius) {
        this.textureIndex = index;
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.boundingRadius = boundingRadius;
    }

    public float getTextureXOffset(){
        int column = textureIndex%model.getTexture().getNumberOfRows();
        return (float) column/(float)model.getTexture().getNumberOfRows();
    }

    public float getTextureYOffset(){
        int row = textureIndex/model.getTexture().getNumberOfRows();
        return (float)row/(float)model.getTexture().getNumberOfRows();
    }

    public void increasePosition(float dx, float dy, float dz){
        this.position.x+=dx;
        this.position.y+=dy;
        this.position.z+=dz;
    }

    public void increaseRotation(float dx, float dy, float dz){
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }

    public void moveTowards(Vector3f position, Terrain terrain){
        this.position.x = (this.position.x + position.x)/2;
        this.position.z = (this.position.z + position.z)/2;
        this.position.y = terrain.getHeightOfTerrain(this.position.x, this.position.z);
    }

    public void moveAway(Vector3f movingFrom){
        boolean PosX = movingFrom.x - this.position.x >0;
        boolean PosZ = movingFrom.z - this.position.z >0;
        if(PosX && PosZ){

        }else if(!PosX && PosZ){

        }else if(PosX && !PosZ){

        }else {

        }
    }

    public TexturedModel getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public float getScale() {
        return scale;
    }

    public float getBoundingRadius() {
        return boundingRadius;
    }
}
