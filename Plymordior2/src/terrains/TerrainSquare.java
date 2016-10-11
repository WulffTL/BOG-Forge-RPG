package terrains;

import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

/**
 * Created by Travis on 1/10/2016.
 */
public class TerrainSquare {
    public static final float TERRAIN_SIZE = 1024;

    private static final int VERTEX_COUNT = 256;
    private static final int SEED = new Random().nextInt(1000000000);

    private float gridX;
    private float gridZ;
    private RawModel model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;

    private float[][] heights;

    public TerrainSquare(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.gridX = gridX * TERRAIN_SIZE;
        this.gridZ = gridZ * TERRAIN_SIZE;
        HeightsGenerator generator = new HeightsGenerator(gridX, gridZ, VERTEX_COUNT, SEED);
        this.model = generateTerrain(loader, generator);
    }

    public RawModel getModel() {
        return model;
    }

    public float getGridZ() {
        return gridZ;
    }

    public float getGridZNormalized() {
        return gridZ/TERRAIN_SIZE;
    }

    public float getGridX() {
        return gridX;
    }

    public float getGridXNormalized() {
        return gridX/TERRAIN_SIZE;
    }

    public float getHeightOfTerrain(float worldX, float worldZ){
        float terrainX = worldX - this.gridX;
        float terrainZ = worldZ - this.gridZ;
        float gridSquareSize = TERRAIN_SIZE / ((float)heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if(gridX >= heights.length - 1 || gridZ >= heights.length -1 || gridX < 0 || gridZ < 0){
            return 0;
        }
        float xCoord = (terrainX % gridSquareSize)/gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize)/gridSquareSize;
        float answer;
        if (xCoord <= (1-zCoord)) {
            answer = Maths
                    .barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ], 0), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            answer = Maths
                    .barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        return answer;
        }

    private RawModel generateTerrain(Loader loader, HeightsGenerator generator){

        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6* (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for(int i = 0; i < VERTEX_COUNT; i++){
            for(int j = 0; j < VERTEX_COUNT; j++){
                vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * TERRAIN_SIZE;
                float height = getHeight(j, i, generator);
                vertices[vertexPointer * 3 + 1] = height;
                heights[j][i] = height;
                vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * TERRAIN_SIZE;
                Vector3f normal = calculateNormal(j, i, generator);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3 + 1] = normal.y;
                normals[vertexPointer*3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) j /((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i /((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz = 0; gz < VERTEX_COUNT - 1; gz++){
            for(int gx = 0; gx < VERTEX_COUNT - 1; gx++){
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    private Vector3f calculateNormal(int x, int z, HeightsGenerator generator){
        float heightL = getHeight(x-1, z, generator);
        float heightR = getHeight(x+1, z, generator);
        float heightD = getHeight(x, z-1, generator);
        float heightU = getHeight(x, z+1, generator);
        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    private  float getHeight(int x, int z, HeightsGenerator generator){
        return generator.generateHeight(x,z);
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

}
