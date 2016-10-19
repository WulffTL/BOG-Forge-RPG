package terrains;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

/**
 * Created by Travis on 10/10/2016.
 *
 */
public class TerrainGrid {
    public static final int DIMENSIONS = 2;
    private static TerrainSquare[][] terrainSquares = new TerrainSquare[DIMENSIONS][DIMENSIONS];

    public static void addTerrainSquare(TerrainSquare terrainSquare) {
        terrainSquares[(int)terrainSquare.getGridXNormalized()][(int)terrainSquare.getGridZNormalized()] = terrainSquare;
    }

    public static TerrainSquare[][] getTerrainSquares() {
        return terrainSquares;
    }

    public static float getCurrentTerrainHeight(float xPos, float zPos){
        return getTerrainByPosition(xPos,zPos).getHeightOfTerrain(xPos,zPos);

    }

    public static TerrainSquare getTerrainByPosition(float xPos, float zPos) {
        int gridX = (int) Math.floor(xPos/ TerrainSquare.TERRAIN_SIZE);
        int gridZ = (int) Math.floor(zPos/ TerrainSquare.TERRAIN_SIZE);
        if(Maths.isBetween(gridX,0,DIMENSIONS - 1) && Maths.isBetween(gridZ,0,DIMENSIONS - 1)) {
            return terrainSquares[gridX][gridZ];
        } else {
            return null;
        }

    }

    public static Vector3f get3fPosition(int gridX, int gridZ, float percentX, float percentZ) {
        float x = TerrainSquare.TERRAIN_SIZE * (gridX + percentX);
        float z = TerrainSquare.TERRAIN_SIZE * (gridZ + percentZ);
        float y = TerrainGrid.getCurrentTerrainHeight(x,z);
        return new Vector3f(x,y,z);
    }

    public static Vector2f getPosition(int gridX, int gridZ, float percentX, float percentZ) {
        float x = TerrainSquare.TERRAIN_SIZE * (gridX + percentX);
        float z = TerrainSquare.TERRAIN_SIZE * (gridZ + percentZ);
        return new Vector2f(x,z);
    }
}
