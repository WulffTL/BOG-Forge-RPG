package entities;

import models.TexturedModel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import terrains.TerrainGrid;
import water.WaterTile;

/**
 * Created by Travis on 10/25/2015.
 *
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

    public float getRotX() {
        return hRotX;
    }

    public float getRotY() {
        return hRotY;
    }

    public float getRotZ() {
        return hRotZ;
    }

    public float getScale() {
        return scale;
    }

    public float getDistanceToWater() {
        boolean hasHitWater = false;
        float noiseThreshold = 150f;
        int checksPerSide = 3;
        int radiusJump = 5;
        float xPos = this.getPosition().x;
        float zPos = this.getPosition().z;
        int radius = 0;
        while(!hasHitWater && radius < noiseThreshold) {
            for(int x = -radius; x <= radius; x += Math.max(1,radius/checksPerSide)) {
                if(TerrainGrid.getTerrainByPosition(xPos + x, zPos + radius) != null && TerrainGrid.getCurrentTerrainHeight(xPos + x, zPos + radius) <= WaterTile.HEIGHT) {
                    hasHitWater = true;
                    break;
                }
                if(TerrainGrid.getTerrainByPosition(xPos + x, zPos - radius) != null && TerrainGrid.getCurrentTerrainHeight(xPos + x, zPos - radius) <= WaterTile.HEIGHT) {
                    hasHitWater = true;
                    break;
                }
            }
            for(int z = -radius; z < radius; z += Math.max(1,radius/checksPerSide)) {
                if(TerrainGrid.getTerrainByPosition(xPos + radius, zPos + z) != null && TerrainGrid.getCurrentTerrainHeight(xPos + radius, zPos + z) <= WaterTile.HEIGHT) {
                    hasHitWater = true;
                    break;
                }
                if(TerrainGrid.getTerrainByPosition(xPos - radius, zPos + z) != null && TerrainGrid.getCurrentTerrainHeight(xPos - radius, zPos + z) <= WaterTile.HEIGHT) {
                    hasHitWater = true;
                    break;
                }
            }
            radius += radiusJump;
        }
        return radius;
    }
}
