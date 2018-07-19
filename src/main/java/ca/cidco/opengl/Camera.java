/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.opengl;

import org.lwjgl.math.Matrix4f;
import org.lwjgl.math.Vector3f;

/**
 *
 * @author mlajoie
 */
public class Camera {
    private final float MAX_FOV = 90.0f;
    private final float MIN_FOV = 1.0f;
    
    private final float SPEED = 0.05f;
    
    Vector3f position;
    Vector3f front;
    Vector3f up;
    Vector3f right;
    Vector3f worldUp;
    
    float pitch;
    float yaw;
        
    float fov;

    public Camera(Vector3f position, Vector3f front, Vector3f worldUp, float yaw, float pitch, float fov) {
        this.position = position;
        this.front = front;
        this.worldUp = worldUp;
        this.yaw = yaw;
        this.pitch = pitch;
        this.fov = fov;
        
        UpdateVectors();
    }
    
    public Matrix4f getViewMatrix(){
        return Matrix4f.lookAt(position, position.add(front), up);
    }
    
    public void moveCamera(CAMERA_MOVEMENT move){
        if (move == CAMERA_MOVEMENT.FORWARD)
            position = position.add(front.scale(SPEED));
        if (move == CAMERA_MOVEMENT.BACKWARD)
            position = position.subtract(front.scale(SPEED));
        if (move == CAMERA_MOVEMENT.LEFT)
            position = position.subtract(front.cross(up).normalize().scale(SPEED));
        if (move == CAMERA_MOVEMENT.RIGHT)
            position = position.add(front.cross(up).normalize().scale(SPEED));
    }
    
    public void rotateCamera(float angleX, float angleY){
        yaw += angleX;
        pitch += angleY;
        
        UpdateVectors();
    }
    
    public void zoom(float angleY){
        if (fov >= MIN_FOV && fov <= MAX_FOV)
            fov += angleY;
        if (fov <= MIN_FOV)
            fov = MIN_FOV;
        if (fov >= MAX_FOV)
            fov = MAX_FOV;
    }
    
    public float getFov(){
        return fov;
    }
    
    public Vector3f getPosition(){
        return position;
    }
    
    public Vector3f getFront(){
        return front;
    }
    
    private void UpdateVectors(){
        Vector3f newFront = new Vector3f();
        newFront.x = (float)Math.cos(Math.toRadians(pitch)) * (float)Math.cos(Math.toRadians(yaw));
        newFront.y = (float)Math.sin(Math.toRadians(pitch));
        newFront.z = (float)Math.cos(Math.toRadians(pitch)) * (float)Math.sin(Math.toRadians(yaw));
        this.front = newFront.normalize();
        
        right = front.cross(worldUp).normalize();
        up = right.cross(front).normalize();
    }
    
}
