package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

/**
 * Created by Travis on 10/25/2015.
 */
public class DisplayManager {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int FPS_CAP = 120;

    private static long lastFrameTime;
    private static float delta;


    public static void createDisplay(){

        ContextAttribs attribs = new ContextAttribs(3,3).withForwardCompatible(true).withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat().withSamples(8).withDepthBits(24), attribs);
            Display.setTitle("BOG-Forge RPG");
            GL11.glEnable(GL13.GL_MULTISAMPLE);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, WIDTH, HEIGHT);
        lastFrameTime = getCurrentTime();
    }

    public static void updateDisplay(){

        Display.sync(FPS_CAP);
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime)/1000f;
        lastFrameTime = currentFrameTime;

    }

    public static float getFrameTimeSeconds(){
        return delta;
    }

    public static void closeDisplay(){}

    private static long getCurrentTime(){
        return Sys.getTime()*1000/Sys.getTimerResolution();
    }

}
