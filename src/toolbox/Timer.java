package toolbox;

import renderEngine.DisplayManager;

/**
 * Created by Travis on 10/18/2016.
 */
public class Timer {

    private static int daylength;
    private static float time;

    public static void update() {
        time += DisplayManager.getFrameTimeSeconds();
        time %= daylength;
    }

    public static void setTime(float time) {
        Timer.time = time;
        Timer.time %= daylength;
    }

    public static int getDaylength() {
        return daylength;
    }

    public static void setDaylength(int daylength) {
        Timer.daylength = daylength;
    }

    public static float getTime() {
        return time;
    }
}
