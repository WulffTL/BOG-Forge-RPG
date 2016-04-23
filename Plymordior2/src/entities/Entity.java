package entities;

import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Travis on 10/25/2015.
 */
public class Entity {
    private TexturedModel model;
    private Vector3f position;
    private float hRotX, hRotY, hRotZ;
    private float scale;
    private float boundingRadius;

    private int textureIndex = 0;

    public Entity (TexturedModel model) {
        this.model = model;
        this.position = new Vector3f(400,12,400);
        this.hRotX = 0;
        this.hRotY = 0;
        this.hRotZ = 0;
        this.scale = 1;
        this.boundingRadius = 0;
    }

    public Entity(TexturedModel model, Vector3f position, float hRotX, float hRotY, float hRotZ,
                  float scale, float boundingRadius) {
        this.model = model;
        this.position = position;
        this.hRotX = hRotX;
        this.hRotY = hRotY;
        this.hRotZ = hRotZ;
        this.scale = scale;
        this.boundingRadius = boundingRadius;
    }

    public Entity(TexturedModel model, int index, Vector3f position, float hRotX, float hRotY, float hRotZ,
                  float scale, float boundingRadius) {
        this.textureIndex = index;
        this.model = model;
        this.position = position;
        this.hRotX = hRotX;
        this.hRotY = hRotY;
        this.hRotZ = hRotZ;
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

    public void increaseHRotation(float dx, float dy, float dz){
        this.hRotX += dx;
        this.hRotY += dy;
        this.hRotZ += dz;
    }

    public TexturedModel getModel() {
        return model;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setEntityHeight(float height){
        this.position.y = height;
    }

    public float gethRotX() {
        return hRotX;
    }

    public float gethRotY() {
        return hRotY;
    }

    public float gethRotZ() {
        return hRotZ;
    }

    public float getScale() {
        return scale;
    }

    public float getBoundingRadius() {
        return boundingRadius;
    }
}
