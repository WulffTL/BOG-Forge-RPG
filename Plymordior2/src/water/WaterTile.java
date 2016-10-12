package water;

import org.lwjgl.util.vector.Vector3f;
import terrains.TerrainGrid;
import terrains.TerrainSquare;

public class WaterTile {
	
	public static final float TILE_SIZE = TerrainSquare.TERRAIN_SIZE * TerrainGrid.DIMENSIONS;
	
	private float height;
	private float x,z;
	
	public WaterTile(float centerX, float centerZ, float heightOffset){
		this.x = centerX;
		this.z = centerZ;
		this.height = TerrainGrid.getCurrentTerrainHeight(centerX,centerZ) + heightOffset;
	}

	public WaterTile(Vector3f position) {
        this.x = position.x;
        height = position.y;
        this.z = position.z;
    }

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}



}
