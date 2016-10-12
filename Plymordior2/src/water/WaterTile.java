package water;

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
