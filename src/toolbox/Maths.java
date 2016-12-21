package toolbox;

import entities.Camera;
import entities.Entity;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

/**
 * Created by Travis on 10/25/2015.
 *
 */
public class Maths {

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale){
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
        Matrix4f.scale(new Vector3f(scale,scale,scale),matrix,matrix);
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1,0,0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;

    }

    public static float betweenValues(float value, float min, float max){
        return Math.min(Math.max(value,min),max);
    }

    public static boolean isBetween(float value, float lowerLimit, float upperLimit){
        boolean between = false;
        if(value >= lowerLimit && value <= upperLimit){
            between = true;
        }
        return between;
    }

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
        return matrix;
    }

    public static float getMinValue(List<Float> array){
        float minValue = array.get(0);
        for(int i = 1; i < array.size(); i++){
            if(array.get(i) < minValue){
                minValue = array.get(i);
            }
        }
        return minValue;
    }

    public static float distanceBetween(Vector3f v1, Vector3f v2){
        float x1 = v1.x;
        float x2 = v2.x;
        float y1 = v1.y;
        float y2 = v2.y;
        float z1 = v1.z;
        float z2 = v2.z;

        return (float) Math.sqrt(Math.pow((x1-x2),2) + Math.pow((y1-y2),2) + Math.pow((z1-z2),2));
    }

    public static float findSlope(Vector2f v1, Vector2f v2) {
        return (v1.x - v2.x)/(v1.y - v2.y);
    }

    public static float distanceBetween(Vector2f v1, Vector2f v2){
        float x1 = v1.x;
        float x2 = v2.x;
        float y1 = v1.y;
        float y2 = v2.y;

        return (float) Math.sqrt(Math.pow((x1-x2),2) + Math.pow((x1-x2),2));
    }

    public static float pythagreonDistance(float a, float b) {
        return (float) Math.sqrt(Math.pow(a,2) + Math.pow(b,2));
    }
}
