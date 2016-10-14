package skybox;

import engineTester.MainGameLoop;
import entities.Camera;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.TerrainGrid;
import terrains.TerrainSquare;

/**
 * Created by Travis on 1/17/2016.
 */
public class SkyboxRenderer {

    private static final float SIZE = TerrainSquare.TERRAIN_SIZE*TerrainGrid.DIMENSIONS;

    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };

    private static String[] TEXTURE_FILES = {"/day/right", "/day/left", "/day/top", "/day/bottom", "/day/back", "/day/front"};
    private static String[] NIGHT_TEXTURE_FILES = {"/night/nightRight", "/night/nightLeft", "/night/nightTop", "/night/nightBottom", "/night/nightBack", "/night/nightFront"};

    private RawModel cube;
    private int texture;
    private int nightTexture;
    private SkyboxShader shader;

    public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix){
        cube = loader.loadToVAO(VERTICES, 3);
        texture = loader.loadCubeMap(TEXTURE_FILES);
        nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES);
        shader = new SkyboxShader();
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Camera camera, float r, float g, float b){
        shader.start();
        shader.loadViewMatrix(camera);
        shader.loadFogColor(r,g,b);
        GL30.glBindVertexArray(cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        bindTextures();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void bindTextures(){
        float dayRed = 0.54444f;
        float dayBlue = 0.62f;
        float dayGreen = 0.69f;

        int texture1 = nightTexture;
        int texture2 = texture;
        float blendFactor;
        float time = MainGameLoop.getTimeInSeconds() % MainGameLoop.MIDNIGHT;
        if(time < MainGameLoop.MIDDAY) {
            if(time == 0) {
                MasterRenderer.setColor(0,0,0);
            } else {
                MasterRenderer.setColor(time*dayRed/MainGameLoop.MIDDAY,time*dayBlue/MainGameLoop.MIDDAY,time*dayGreen/MainGameLoop.MIDDAY);
            }
            blendFactor = time/MainGameLoop.MIDDAY;

        } else {
            if(time == MainGameLoop.MIDDAY) {
                MasterRenderer.setColor(dayRed,dayBlue,dayGreen);
            } else {
                MasterRenderer.setColor((MainGameLoop.MIDNIGHT-time)*dayRed/MainGameLoop.MIDDAY, (MainGameLoop.MIDNIGHT-time)*dayBlue/MainGameLoop.MIDDAY, (MainGameLoop.MIDNIGHT-time)*dayGreen/MainGameLoop.MIDDAY);
            }
            blendFactor = (MainGameLoop.MIDNIGHT-time)/MainGameLoop.MIDDAY;
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
        shader.loadBlendFactor(blendFactor);
    }
}
