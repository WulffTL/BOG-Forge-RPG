package water;

import org.lwjgl.util.vector.Vector3f;
import terrains.HeightsGenerator;
import terrains.TerrainGrid;
import terrains.TerrainSquare;

public class WaterTile {
	
	public static final float TILE_SIZE = TerrainSquare.TERRAIN_SIZE * TerrainGrid.DIMENSIONS;
	
	public static final float HEIGHT = -HeightsGenerator.AMPLITUDE/10;
	private float x,z;

	public WaterTile(float centerX, float centerZ) {
        this.x = centerX;
        this.z = centerZ;
    }

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}



}
