package terrains;

/**
 * Created by Travis on 10/10/2016.
 */
public class TerrainGrid {
    private static final int DIMENSIONS = 3;
    private static TerrainSquare[][] terrainSquares = new TerrainSquare[DIMENSIONS][DIMENSIONS];

    public static void addTerrainSquare(TerrainSquare terrainSquare) {
        terrainSquares[terrainSquare.getGridXNormalized()][terrainSquare.getGridZNormalized()] = terrainSquare;
    }

    public static TerrainSquare[][] getTerrainSquares() {
        return terrainSquares;
    }

    public static float getCurrentTerrainHeight(float xPos, float zPos){
        int gridX = (int) Math.floor(xPos/ TerrainSquare.TERRAIN_SIZE);
        int gridZ = (int) Math.floor(zPos/ TerrainSquare.TERRAIN_SIZE);
        return terrainSquares[gridX][gridZ].getHeightOfTerrain(xPos,zPos);

    }

    public static TerrainSquare getTerrainByPosition(float xPos, float zPos) {
        int gridX = (int) Math.floor(xPos/ TerrainSquare.TERRAIN_SIZE);
        int gridZ = (int) Math.floor(zPos/ TerrainSquare.TERRAIN_SIZE);
        return terrainSquares[gridX][gridZ];
    }
}
