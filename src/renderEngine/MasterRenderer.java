package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import shadows.ShadowMapEntityRenderer;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import terrains.TerrainGrid;
import terrains.TerrainSquare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Travis on 1/10/2016.
 *
 */
public class MasterRenderer {

    public static final float FOV = 70;
    public static final float NEAR_PLANE = 1f;
    public static final float FAR_PLANE = 5000;

    private static float RED = 0.5444f;
    private static float GREEN = 0.62f;
    private static float BLUE = 0.69f;

    private Matrix4f projectionMatrix;

    private StaticShader shader = new StaticShader();
    private EntityRenderer renderer;

    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    private Map<TexturedModel,List<Entity>> entities = new HashMap<>();
    private List<TerrainSquare> terrains = new ArrayList<>();

    private SkyboxRenderer skyboxRenderer;
    private ShadowMapMasterRenderer shadowMapMasterRenderer;

    public MasterRenderer(Loader loader, Camera camera){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        createProjectionMatrix();
        renderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader,projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
        this.shadowMapMasterRenderer = new ShadowMapMasterRenderer(camera);
    }

    public Matrix4f getProjectionMatrix(){
        return projectionMatrix;
    }

    public static void enableCulling(){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling(){
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void renderScene(List<Entity> entities, List<Light> lights, Camera camera, Vector4f clipPlane){
        entities.forEach(this::processEntity);
        for(TerrainSquare[] terrainSquares : TerrainGrid.getTerrainSquares()) {
            for(TerrainSquare terrainSquare : terrainSquares) {
                this.processTerrain(terrainSquare);
            }
        }
        render(lights, camera, clipPlane);
    }

    public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
        prepare();
        shader.start();
        shader.loadClipPlane(clipPlane);
        shader.loadSkyColor(RED,GREEN,BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        renderer.render(entities, shadowMapMasterRenderer.getToShadowMapSpaceMatrix());
        shader.stop();
        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColor(RED,GREEN,BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains, shadowMapMasterRenderer.getToShadowMapSpaceMatrix());
        terrainShader.stop();
        skyboxRenderer.render(camera, RED, GREEN, BLUE);
        terrains.clear();
        entities.clear();
    }

    public void processTerrain(TerrainSquare terrain){
        terrains.add(terrain);
    }

    public void processEntity(Entity entity){
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if(batch!=null){
            batch.add(entity);
        }else {
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void prepare(){
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(RED,GREEN,BLUE,1);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
    }

    private void createProjectionMatrix(){
        projectionMatrix = new Matrix4f();
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public void renderShadowMap(List<Entity> entityList, Light sun) {
        entityList.forEach(this::processEntity);
        shadowMapMasterRenderer.render(entities,sun);
        entities.clear();
    }

    public int getShadowMapTexture() {
        return shadowMapMasterRenderer.getShadowMap();
    }

    public void cleanUp(){
        shader.cleanUp();
        terrainShader.cleanUp();
        shadowMapMasterRenderer.cleanUp();
    }

    public static void setColor(float RED,float GREEN, float BLUE) {
        MasterRenderer.RED = RED;
        MasterRenderer.GREEN = GREEN;
        MasterRenderer.BLUE = BLUE;
    }

    public static void incrementColor(float RED, float GREEN, float BLUE) {
        MasterRenderer.RED += RED;
        MasterRenderer.GREEN += GREEN;
        MasterRenderer.BLUE += BLUE;
    }
}
