package superPowers;

import entities.Light;
import entities.Player;
import models.RawModel;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import terrains.Terrain;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Travis on 1/15/2016.
 */
public class powers {
    private Player player;
    private Terrain terrain;
    private Light light;

    public powers (Player player, Terrain terrain, Light light){
        this.player = player;
        this.terrain = terrain;
        this.light = light;
    }
    public static void powerJump(){

    }

}


