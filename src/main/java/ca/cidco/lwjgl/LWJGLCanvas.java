/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.lwjgl;

import ca.cidco.math.*;
import ca.cidco.opengl.CAMERA_MOVEMENT;
import ca.cidco.opengl.Camera;
import ca.cidco.opengl.Image2D;
import ca.cidco.opengl.Shader;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

/**
 *
 * @author Maxime Lajoie
 */
public class LWJGLCanvas extends AWTGLCanvas implements KeyListener, MouseMotionListener, MouseWheelListener {

    private final int FLOAT_SIZE = Float.SIZE / Byte.SIZE;

    private Camera camera;
    
    int objectVAO;
    int objectVBO;
    int lampVAO;
    int lampVBO;
    
    Shader objectShader;
    Shader lampShader;
    Image2D diffuseMap;
    Image2D specularMap;
    
    public LWJGLCanvas(GLData data) {
        super(data);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }

    @Override
    public void initGL() {
        GL.createCapabilities();   
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        
        camera = new Camera(new Vector3f(0.0f, 0.0f, 3.0f), new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(0.0f, 1.0f, 0.0f), -90.0f, 0.0f, 45.0f);

        //Following the LeanOpenGL tutorial from https://learnopengl.com
        objectShader = new Shader("simpleshader.vs", "flashlight.fs");
        lampShader = new Shader("simpleshader.vs", "lampshader.fs");

        diffuseMap = new Image2D("container2.png", GL_REPEAT, GL_REPEAT, GL_LINEAR, GL_LINEAR, GL_TEXTURE0);
        specularMap = new Image2D("container2_specular.png", GL_REPEAT, GL_REPEAT, GL_LINEAR, GL_LINEAR, GL_TEXTURE1);
        
        objectVAO = glGenVertexArrays();
        glBindVertexArray(objectVAO);

        objectVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, objectVBO);
        glBufferData(GL_ARRAY_BUFFER, Cube.getCubeVertices(), GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * FLOAT_SIZE, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * FLOAT_SIZE, 3 * FLOAT_SIZE);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * FLOAT_SIZE, 6 * FLOAT_SIZE);
        glEnableVertexAttribArray(2);
        
        Vector3f lightPos = new Vector3f(1.2f, 1.0f, 2.0f);
        lampVAO = glGenVertexArrays();
        glBindVertexArray(lampVAO);
        
        lampVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, lampVBO);
        glBufferData(GL_ARRAY_BUFFER, Cube.getCubeVertices(), GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * FLOAT_SIZE, 0);
        glEnableVertexAttribArray(0);
        
        //Lighting
        objectShader.use();
        //Material
        objectShader.setInt("material.diffuse", 0);
        objectShader.setInt("material.specular", 1);
        objectShader.setFloat("material.shininess", 64.0f);
        //Light
        objectShader.setVect3f("light.ambient", new Vector3f(0.2f, 0.2f, 0.2f));
        objectShader.setVect3f("light.diffuse", new Vector3f(0.5f, 0.5f, 0.5f));
        objectShader.setVect3f("light.specular", new Vector3f(1.0f, 1.0f, 1.0f));
        
        objectShader.setVect3f("light.attenuation", new Vector3f(1.0f, 0.09f, 0.032f));
        
        lampShader.use();
        Matrix4f model = new Matrix4f();
        model = model.multiply(Matrix4f.translate(lightPos));
        model = model.multiply(Matrix4f.scale(0.2f, 0.2f, 0.2f));
        lampShader.setMatrix4f("model", model);
    }
    
    @Override
    public void paintGL() {
        int w = getWidth();
        int h = getHeight();
        float aspect = (float) w / h;

        glViewport(0, 0, w, h);
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //Matrix
        Matrix4f projection = Matrix4f.perspective(camera.getFov(), aspect, 0.1f, 100f);
        Matrix4f view = camera.getViewMatrix();

        //object positioning
        objectShader.use();
        objectShader.setMatrix4f("view", view);
        objectShader.setMatrix4f("projection", projection);
        objectShader.setVect3f("viewPos", camera.getPosition());
        
        //Flashlight
        objectShader.setVect3f("light.position", camera.getPosition());
        objectShader.setVect3f("light.direction", camera.getFront());
        objectShader.setFloat("light.inCutOff", (float)Math.cos(Math.toRadians(12.5f)));
        objectShader.setFloat("light.outCutOff", (float)Math.cos(Math.toRadians(17.5f)));
        
        diffuseMap.bind();
        specularMap.bind();
        glBindVertexArray(objectVAO);
        
        for (int i = 0; i < Cube.getCubePositions().length; i++) {
            objectShader.use();
            Matrix4f model = new Matrix4f();
            model = model.multiply(Matrix4f.translate(Cube.getCubePositions()[i]));
            model = model.multiply(Matrix4f.rotate(20f * i, 1.0f, 0.3f, 0.5f));
            objectShader.setMatrix4f("model", model);
            
            glDrawArrays(GL_TRIANGLES, 0, 36);
        }
        
        lampShader.use();
        lampShader.setMatrix4f("projection", projection);
        lampShader.setMatrix4f("view", view);
        
        glBindVertexArray(lampVAO);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        
        swapBuffers();
    }

    
    /**
     * Key Events
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_W) {
            camera.moveCamera(CAMERA_MOVEMENT.FORWARD);
        }
        if (keyCode == KeyEvent.VK_S) {
            camera.moveCamera(CAMERA_MOVEMENT.BACKWARD);
        }
        if (keyCode == KeyEvent.VK_A) {
            camera.moveCamera(CAMERA_MOVEMENT.LEFT);
        }
        if (keyCode == KeyEvent.VK_D) {
            camera.moveCamera(CAMERA_MOVEMENT.RIGHT);
        }

        render();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * Mouse events 
     */
    private Integer lastX = null;
    private Integer lastY = null;

    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastX != null && lastY != null) {
            float sensitivity = 0.1f;

            float offsetX = (e.getX() - lastX) * sensitivity;
            float offsetY = (lastY - e.getY()) * sensitivity;

            camera.rotateCamera(offsetX, offsetY);
            lastX = e.getX();
            lastY = e.getY();
            render();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        camera.zoom(e.getWheelRotation() * 1.0f);
        render();
    }

}
