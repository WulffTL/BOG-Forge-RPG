package entities;

import models.TexturedModel;
import org.lwjgl.input.Mouse;
import renderEngine.DisplayManager;

import java.util.List;

/**
 * Created by Travis on 4/21/2016.
 */
public class StartMenuEntity extends Entity {

    public StartMenuEntity(TexturedModel model) {
        super(model);
    }

    public void switchModel(List<TexturedModel> models, int modelIndex){
        setModel(models.get(modelIndex));
    }

    public void rotate(){
        float currentTurnSpeed;
        if(Mouse.isButtonDown(0)){
            currentTurnSpeed = Mouse.getDX()*100;
        }else{
            currentTurnSpeed = 0;
        }

        super.increaseHRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
    }
}
