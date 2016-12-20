package entities;

import models.RawModel;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import terrains.TerrainGrid;
import terrains.TerrainSquare;
import textures.ModelTexture;
import water.WaterTile;

/**
 * Created by Travis on 10/25/2015.
 *
 */
public class Entity {
    private TexturedModel model;
    private Vector3f position;
    private Vector3f rotation;
    //private float hRotX, hRotY, hRotZ;
    private float scale;
    private int textureIndex = 0;

    public Entity (Loader loader, String modelPath, String texture) {
        RawModel rawModel = OBJLoader.loadObjModel(modelPath, loader);
        ModelTexture modelTexture = new ModelTexture(loader.loadTexture("/objectTextures/" + texture));
        this.model = new TexturedModel(rawModel, modelTexture);
        float middleOfMap = TerrainSquare.TERRAIN_SIZE/2;
        this.position = new Vector3f(middleOfMap,TerrainGrid.getCurrentTerrainHeight(middleOfMap,middleOfMap),middleOfMap);
        this.rotation = new Vector3f(0,0,0);
        this.scale = 1;
    }

    public Entity (TexturedModel model){
        float middleOfMap = TerrainSquare.TERRAIN_SIZE/2;
        this.model = model;
        this.position = new Vector3f(middleOfMap,TerrainGrid.getCurrentTerrainHeight(middleOfMap,middleOfMap),middleOfMap);
        this.rotation = new Vector3f(0,0,0);
        this.scale = 1;
    }

    public Entity(TexturedModel model, Vector2f position) {
        this.model = model;
        this.position = new Vector3f(position.x, TerrainGrid.getCurrentTerrainHeight(position.x,position.y),position.y);
        this.rotation = new Vector3f(0,0,0);
        this.scale = 1;
    }

    public Entity(TexturedModel model, Vector2f position, Vector3f rotations, float scale) {
        this.model = model;
        this.position = new Vector3f(position.x, TerrainGrid.getCurrentTerrainHeight(position.x,position.y),position.y);
        this.rotation = new Vector3f(0,0,0);
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
        Vector3f.add(this.position, new Vector3f(dx,dy,dz), this.position);
    }

    public void increaseHRotation(float dx, float dy, float dz){
        Vector3f.add(this.rotation, new Vector3f(dx,dy,dz), this.rotation);
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

    public void setRotation(Vector3f rotation) { this.rotation = rotation; }

    public void setEntityHeight(float height){
        this.position.y = height;
    }

    public void setScale(float scale) { this.scale = scale; }

    public float getRotX() {
        return rotation.x;
    }

    public float getRotY() {
        return rotation.y;
    }

    public float getRotZ() {
        return rotation.z;
    }

    public float getScale() {
        return scale;
    }

    /**
     * Returns an estimate of the distance to water as a multiple of 5 or the max terrain size, whichever is lower.
     * @return rounded distance to water
     */
    public float getRoundedDistanceToElevation(float elevation) {
        boolean hasHitElevation = false;
        int checksPerSide = 3;
        int radiusJump = 5;
        float xPos = this.getPosition().getX();
        float zPos = this.getPosition().getZ();
        int radius = 0;
        while(!hasHitElevation && radius < TerrainSquare.TERRAIN_SIZE) {
            //check top and bottom of square
            for(int x = -radius; x <= radius; x += Math.max(1,radius/checksPerSide)) {
                //check top
                if(TerrainGrid.getTerrainByPosition(xPos + x, zPos + radius) != null && TerrainGrid.getCurrentTerrainHeight(xPos + x, zPos + radius) <= elevation) {
                    hasHitElevation = true;
                    break;
                }
                //check bottom
                if(TerrainGrid.getTerrainByPosition(xPos + x, zPos - radius) != null && TerrainGrid.getCurrentTerrainHeight(xPos + x, zPos - radius) <= elevation) {
                    hasHitElevation = true;
                    break;
                }
            }
            //check sides of square
            for(int z = -radius; z < radius; z += Math.max(1,radius/checksPerSide)) {
                //check right side
                if(TerrainGrid.getTerrainByPosition(xPos + radius, zPos + z) != null && TerrainGrid.getCurrentTerrainHeight(xPos + radius, zPos + z) <= elevation) {
                    hasHitElevation = true;
                    break;
                }
                //check left side
                if(TerrainGrid.getTerrainByPosition(xPos - radius, zPos + z) != null && TerrainGrid.getCurrentTerrainHeight(xPos - radius, zPos + z) <= elevation) {
                    hasHitElevation = true;
                    break;
                }
            }
            radius += radiusJump;
        }
        return radius;
    }

    public void move() {

    }
}
