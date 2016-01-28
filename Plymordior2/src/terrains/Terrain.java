package terrains;

import entities.Player;
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
import java.util.List;

/**
 * Created by Travis on 1/10/2016.
 */
public class Terrain {
    private static final float SIZE = 800;
    private static final float MAX_HEIGHT = 20;
    private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

    private float x;
    private float z;
    private RawModel model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;
    private boolean useHeightMap;

    private float[][] heights;

    private FileWriter normalsWriter = null;
    private FileWriter textureCoordsWriter = null;
    private FileWriter vertexPointersWriter = null;

    public RawModel getModel() {
        return model;
    }

    public float getZ() {
        return z;
    }

    public static float getSIZE() {
        return SIZE;
    }

    public float getX() {
        return x;
    }

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap,
                   String heightMap) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(loader, heightMap);
    }

    private static Terrain getTerrain(Terrain[][] terrains, Player player) {

        float x = player.getPosition().x;
        float z = player.getPosition().z;

        float size = Terrain.SIZE;

        if (x > terrains.length * size || x < 0 || z > terrains.length * size || z < 0) {
            System.err.println("Player out of the terrain !");
            System.exit(-1);
        }

        int gridX = (int) Math.floor(x / size);
        int gridZ = (int) Math.floor(z / size);

        return terrains[gridX][gridZ];
    }

    public float getHeightOfTerrain(float worldX, float worldZ){
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float)heights.length - 1);
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

    private RawModel generateTerrain(Loader loader, String heightMap){
        try {
            normalsWriter = new FileWriter(new File("src/terrains/terrainNormals.txt"));
            textureCoordsWriter = new FileWriter(new File("src/terrains/terrainTextureCoords.txt"));
            vertexPointersWriter = new FileWriter(new File("src/terrains/terrainVertexPointers.txt"));
        }catch (IOException e){
            e.printStackTrace();
        }

        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("res/" + heightMap + ".png"));
        }catch (IOException e){
            e.printStackTrace();
        }
        int VERTEX_COUNT = image.getHeight();
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                float height = getHeight(j, i, image);
                heights[j][i] = height;
                vertices[vertexPointer*3+1] = getHeight(j,i,image);
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(j, i, image);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                try {
                    if (vertexPointersWriter != null) {
                        vertexPointersWriter.write(vertices[vertexPointer*3] + "," + vertices[vertexPointer*3+1] + "," +
                                vertices[vertexPointer*3+2] + "\n");
                    }
                    if (textureCoordsWriter != null) {
                        textureCoordsWriter.write(vertices[vertexPointer*3] + "," + vertices[vertexPointer*3+1] + "," +
                                vertices[vertexPointer*3+2] + "\n");
                    }
                    if (normalsWriter != null) {
                        normalsWriter.write(vertices[vertexPointer*3] + "," + vertices[vertexPointer*3+1] + "," +
                                vertices[vertexPointer*3+2] + "\n");
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
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

    private Vector3f calculateNormal(int x, int z, BufferedImage image){
        float heightL = getHeight(x-1, z, image);
        float heightR = getHeight(x+1, z, image);
        float heightD = getHeight(x, z-1, image);
        float heightU = getHeight(x, z+1, image);
        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    private  float getHeight(int x, int z, BufferedImage image){
        float height = 0;
            if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
                return 0;
            }
            height = image.getRGB(x, z);
            height += MAX_PIXEL_COLOR / 2f;
            height /= MAX_PIXEL_COLOR / 2f;
            height *= MAX_HEIGHT;
        return height;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public static Terrain getCurrentTerrain(Terrain[][] terrains, Player player){
        int gridX = (int) Math.floor(player.getPosition().x/Terrain.SIZE);
        int gridZ = (int) Math.floor(player.getPosition().z/Terrain.SIZE);
        return terrains[gridX][gridZ];
    }

    public boolean isHeightMap (String normals, String textureCoords, String vertexPointers){
        try{
            BufferedReader normalsBReader = new BufferedReader(new FileReader(new File(normals)));
            BufferedReader textureCoordsBReader = new BufferedReader(new FileReader(new File(textureCoords)));
            BufferedReader vertexPointersBReader = new BufferedReader(new FileReader(new File(vertexPointers)));
            if(normalsBReader.readLine() == null && textureCoordsBReader.readLine() == null && vertexPointersBReader.readLine() == null){
                useHeightMap = true;
            }else useHeightMap = false;
            normalsBReader.close();
            textureCoordsBReader.close();
            vertexPointersBReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return useHeightMap;
    }
}
