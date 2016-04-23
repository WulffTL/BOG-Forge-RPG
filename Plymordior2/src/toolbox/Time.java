package toolbox;

/**
 * Created by Travis on 2/14/2016.
 */
public class Time {
    public static boolean isTopOfSecond(long compareTime){
        if((System.currentTimeMillis() - compareTime)/1000 >= 1){
            return true;
        } else return false;
    }
}
