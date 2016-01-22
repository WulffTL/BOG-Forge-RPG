package superPowers;

import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import terrains.Terrain;
import toolbox.Maths;
import toolbox.MousePicker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Travis on 1/15/2016.
 */
public class Powers {
    private Player player;
    private Terrain terrain;
    private List<Light> lights;
    private List<Entity> entities;

    public Powers(Player player, Terrain terrain, List<Light> lights, List<Entity> entities){
        this.player = player;
        this.terrain = terrain;
        this.lights = lights;
        this.entities = entities;
    }
    public static void powerJump(){

    }

    public void forcePull(MousePicker picker){
        Light powerLight = lights.get(0);
        for(Entity entity : entities) {
            if(Keyboard.isKeyDown(Keyboard.KEY_T)) {
                Vector3f terrainPoint = picker.getCurrentTerrainPoint();
                if(terrainPoint != null){
                    powerLight.setAttenuation(1,0.01f,0.002f);
                    powerLight.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 1, terrainPoint.z));
                }
                if (picker.isIntersectingSphere(terrainPoint, entity)) {
                    entity.moveTowards(player.getPosition(), terrain);
                }
            }else powerLight.setPosition(new Vector3f(0,999,-7000));
        }

    }

    public void forcePush(){
        float distance = 20;
        if(Keyboard.isKeyDown(Keyboard.KEY_P))
        for(Entity entity : entities){
            if (Maths.isBetween(entity.getPosition().x,player.getPosition().x - distance,player.getPosition().x + distance) &&
                    Maths.isBetween(entity.getPosition().y,player.getPosition().y - distance,player.getPosition().y + distance) &&
            Maths.isBetween(entity.getPosition().z,player.getPosition().z - distance,player.getPosition().z + distance)
                    ) {
                entity.moveAway(player.getPosition());
            }
        }
    }



}


