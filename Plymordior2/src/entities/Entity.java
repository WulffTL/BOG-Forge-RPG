package entities;

import models.TexturedModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import terrains.TerrainGrid;

/**
 * Created by Travis on 10/25/2015.
 */
public class Entity {
    private TexturedModel model;
    private Vector3f position;
    private float hRotX, hRotY, hRotZ;
    private float scale;
    private int textureIndex = 0;

    public Entity (TexturedModel model) {
        this.model = model;
        this.position = new Vector3f(400,12,400);
        this.hRotX = 0;
        this.hRotY = 0;
        this.hRotZ = 0;
        this.scale = 1;
    }

    public Entity(TexturedModel model, Vector2f position) {
        this.model = model;
        this.position = new Vector3f(position.x, TerrainGrid.getCurrentTerrainHeight(position.x,position.y),position.y);
        this.hRotX = 0;
        this.hRotY = 0;
        this.hRotZ = 0;
        this.scale = 1;
    }

    public Entity(TexturedModel model, Vector2f position, Vector3f rotations, float scale) {
        this.model = model;
        this.position = new Vector3f(position.x, TerrainGrid.getCurrentTerrainHeight(position.x,position.y),position.y);
        this.hRotX = rotations.x;
        this.hRotY = rotations.y;
        this.hRotZ = rotations.z;
        this.scale = scale;
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
}
