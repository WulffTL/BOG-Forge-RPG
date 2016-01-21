package entities;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Travis on 10/27/2015.
 */
public class Light {

    private Vector3f position;
    private Vector3f color;
    private Vector3f attenuation = new Vector3f(1,0,0);

    public Light(Vector3f position, Vector3f color){
        this.position = position;
        this.color = color;
    }

    public Light(Vector3f position, Vector3f color, Vector3f attenuation){
        this.position = position;
        this.color = color;
        this.attenuation = attenuation;
    }

    public Vector3f getAttenuation() {
        return  attenuation;
    }

    public void setAttenuation(float a, float b, float c) {
        this.attenuation = new Vector3f(a,b,c);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }
}
