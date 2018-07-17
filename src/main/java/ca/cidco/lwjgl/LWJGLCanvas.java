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
import ca.cidco.opengl.mesh.Model;
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
    
    Shader myShader;
    Model myModel;
            
    public LWJGLCanvas(GLData data) {
        super(data);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }
    
    Vector3f[] pointLightPositions = {
	new Vector3f( 0.7f,  0.2f,  2.0f),
	new Vector3f( 2.3f, -3.3f, -4.0f),
	new Vector3f(-4.0f,  2.0f, -12.0f),
	new Vector3f( 0.0f,  0.0f, -3.0f)
    };  

    @Override
    public void initGL() {
        GL.createCapabilities();   
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        
        camera = new Camera(new Vector3f(0.0f, 0.0f, 3.0f), new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(0.0f, 1.0f, 0.0f), -90.0f, 0.0f, 45.0f);

        myShader = new Shader("modelshader.vs", "modelshader.fs");
        myModel = new Model("/home/mlajoie/Workspace/MavenLWJGL/src/main/resources/ca/cidco/lwjgl/nanosuit.obj");
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
        model = model.multiply(Matrix4f.translate(0.0f, -1.75f, 0.0f));
        model = model.multiply(Matrix4f.scale(0.2f, 0.2f, 0.2f));
        
        myShader.use();
        myShader.setMatrix4f("model", model);
        myShader.setMatrix4f("projection", projection);
        myShader.setMatrix4f("view", view);
        
        myModel.draw(myShader);
        
        
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
