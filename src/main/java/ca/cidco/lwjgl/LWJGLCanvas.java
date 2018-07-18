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
import static org.lwjgl.opengl.GL12.*;
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
    
    int cubeVAO;
    int cubeVBO;
    
    int planeVAO;
    int planeVBO;
    
    int plantVAO;
    int plantVBO;
    
    Shader myShader;
    
    Image2D cubeTexture;
    Image2D planeTexture;
    Image2D plantTexture;

    public LWJGLCanvas(GLData data) {
        super(data);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }
    float[] planeVertices = {
        // positions         
         5.0f, -0.5f,  5.0f,  2.0f, 0.0f,
        -5.0f, -0.5f,  5.0f,  0.0f, 0.0f,
        -5.0f, -0.5f, -5.0f,  0.0f, 2.0f,

         5.0f, -0.5f,  5.0f,  2.0f, 0.0f,
        -5.0f, -0.5f, -5.0f,  0.0f, 2.0f,
         5.0f, -0.5f, -5.0f,  2.0f, 2.0f								
    };
    
    Vector3f[] plants = {
        new Vector3f(-1.5f, 0.0f, -0.48f),
        new Vector3f(1.5f, 0.0f, 0.51f),
        new Vector3f(0.0f, 0.0f, 0.7f),
        new Vector3f(-0.3f, 0.0f, -2.3f),
        new Vector3f(0.5f, 0.0f, -0.6f)
    };
    
    float[] plantVertices = {
        // positions         // texture Coords (swapped y coordinates because texture is flipped upside down)
        0.0f,  0.5f,  0.0f,  0.0f,  1.0f,
        0.0f, -0.5f,  0.0f,  0.0f,  0.0f,
        1.0f, -0.5f,  0.0f,  1.0f,  0.0f,

        0.0f,  0.5f,  0.0f,  0.0f,  1.0f,
        1.0f, -0.5f,  0.0f,  1.0f,  0.0f,
        1.0f,  0.5f,  0.0f,  1.0f,  1.0f
    };

    @Override
    public void initGL() {
        GL.createCapabilities();   
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glEnable(GL_DEPTH_TEST);
              
        camera = new Camera(new Vector3f(0.0f, 0.0f, 3.0f), new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(0.0f, 1.0f, 0.0f), -90.0f, 0.0f, 45.0f);

        //The cubes
        cubeVAO = glGenVertexArrays();
        cubeVBO = glGenBuffers();
        glBindVertexArray(cubeVAO);
        glBindBuffer(GL_ARRAY_BUFFER, cubeVBO);
        glBufferData(GL_ARRAY_BUFFER, Cube.getCubeVertices(), GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * FLOAT_SIZE, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * FLOAT_SIZE, 3 * FLOAT_SIZE);
        glBindVertexArray(0);
        
        //The plane
        planeVAO = glGenVertexArrays();
        planeVBO = glGenBuffers();
        glBindVertexArray(planeVAO);
        glBindBuffer(GL_ARRAY_BUFFER, planeVBO);
        glBufferData(GL_ARRAY_BUFFER, planeVertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * FLOAT_SIZE, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * FLOAT_SIZE, 3 * FLOAT_SIZE);
        glBindVertexArray(0);
        
        //The plants
        plantVAO = glGenVertexArrays();
        plantVBO = glGenBuffers();
        glBindVertexArray(plantVAO);
        glBindBuffer(GL_ARRAY_BUFFER, plantVBO);
        glBufferData(GL_ARRAY_BUFFER, plantVertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * FLOAT_SIZE, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * FLOAT_SIZE, 3 * FLOAT_SIZE);
        glBindVertexArray(0);
        
        //Textures
        cubeTexture = new Image2D("marble.jpg", GL_REPEAT, GL_REPEAT, GL_LINEAR, GL_LINEAR, GL_TEXTURE0);
        planeTexture = new Image2D("metal.png", GL_REPEAT, GL_REPEAT, GL_LINEAR, GL_LINEAR, GL_TEXTURE0);
        plantTexture = new Image2D("grass.png", GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_LINEAR, GL_LINEAR, GL_TEXTURE0);
        
        myShader = new Shader("depthshader.vs", "depthshader.fs");
        myShader.use();
        myShader.setInt("texture1", 0);
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
        Matrix4f model = new Matrix4f();
        
        myShader.use();
        myShader.setMatrix4f("projection", projection);
        myShader.setMatrix4f("view", view);
        
        //Plane
        glBindVertexArray(planeVAO);
        planeTexture.bind();
        myShader.setMatrix4f("model", new Matrix4f());
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);
        
        //Cube 1
        glBindVertexArray(cubeVAO);
        cubeTexture.bind();
        model = model.multiply(Matrix4f.translate(-1.0f, 0.0f, -1.0f));
        myShader.setMatrix4f("model", model);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        //Cube 2
        model = new Matrix4f();
        model = model.multiply(Matrix4f.translate(2.0f, 0.0f, 0.0f));
        myShader.setMatrix4f("model", model);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        
        //Plants
        glBindVertexArray(plantVAO);
        plantTexture.bind();
        for (Vector3f p : plants) {
            model = new Matrix4f();
            model = model.multiply(Matrix4f.translate(p));
            myShader.setMatrix4f("model", model);
            glDrawArrays(GL_TRIANGLES, 0, 6);
        }
        
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
