package water;

import terrains.TerrainGrid;

public class WaterTile {
	
	public static final float TILE_SIZE = 60;
	
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
