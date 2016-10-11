package terrains;

import toolbox.Maths;

/**
 * Created by Travis on 10/10/2016.
 */
public class TerrainGrid {
    public static final int DIMENSIONS = 3;
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
        if(Maths.isBetween(gridX,0,DIMENSIONS) && Maths.isBetween(gridZ,0,DIMENSIONS)) {
            return terrainSquares[gridX][gridZ];
        } else {
            System.out.println("ERROR: Terrain Array Index Out of Bounds Exception");
            return null;
        }

    }
}
