/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.lwjgl;

import ca.cidco.math.*;
import ca.cidco.opengl.CAMERA_MOVEMENT;
import ca.cidco.opengl.Camera;
import ca.cidco.opengl.ImageReader;
import ca.cidco.opengl.Shader;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

/**
 *
 * @author mlajoie
 */
public class LWJGLCanvas extends AWTGLCanvas implements KeyListener, MouseMotionListener, MouseWheelListener {

    BufferedImage image;

    private final int FLOAT_SIZE = Float.SIZE/Byte.SIZE;
  
    private Integer lastX = null;
    private Integer lastY = null;
    
    private Camera camera;

    public LWJGLCanvas() {
        super();
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }

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
    }

    @Override
    public void paintGL() {
        int w = getWidth();
        int h = getHeight();
        float aspect = (float) w / h;

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glViewport(0, 0, w, h);
        
        //Following the LeanOpenGL tutorial from https://learnopengl.com
        Shader shader = new Shader("simpleshader.vs", "simpleshader.fs");
          
        float vertices[] = {
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
             0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
             0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
             0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
             0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
             0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
             0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

            -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

             0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
             0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
             0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
             0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
             0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
             0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
             0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
             0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
             0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
             0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
             0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
             0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
        };
        
        Vector3f[] cubePositions = {
            new Vector3f(0.0f, 0.0f, 0.0f), 
            new Vector3f(2.0f, 5.0f, -15.0f), 
            new Vector3f(-1.5f, -2.2f, -2.5f),  
            new Vector3f(-3.8f, -2.0f, -12.3f),  
            new Vector3f(2.4f, -0.4f, -3.5f),  
            new Vector3f(-1.7f, 3.0f, -7.5f),  
            new Vector3f(1.3f, -2.0f, -2.5f),  
            new Vector3f(1.5f, 2.0f, -2.5f), 
            new Vector3f(1.5f, 0.2f, -1.5f), 
            new Vector3f(-1.3f, 1.0f, -1.5f)  
        };
        
        int VAO = glGenVertexArrays();
        glBindVertexArray(VAO);
        
        int VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        //position
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * FLOAT_SIZE, 0);
        glEnableVertexAttribArray(0);
        //texture
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * FLOAT_SIZE, 3 * FLOAT_SIZE);
        glEnableVertexAttribArray(1);

        //Generating texture
        int texture1 = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture1);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
        //If using GL_CLAMP_TO_BORDER
        //float borderColor[] = {1.0f, 1.0f, 0.0f, 1.0f};
        //glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER, borderColor);
        
        //Texture filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);      //Scaling down using Mipmapping
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);       //Scaling up (Don't use mipmap, useless + error)
        
        //Load image
        int[] width = new int[1],
            height = new int[1];

        ByteBuffer data = ImageReader.loadImage("container.jpg",width, height);
        if (data != null){
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width[0], height[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
            glGenerateMipmap(GL_TEXTURE_2D);
        }
        else{
            System.out.println("Failed to load texture");
        }
        
        int texture2 = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture2);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);    
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);    
        
        data = ImageReader.loadImage("awesomeface.png",width, height);
        if (data != null){
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width[0], height[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
            glGenerateMipmap(GL_TEXTURE_2D);
        }
        else{
            System.out.println("Failed to load texture");
        }

        Matrix4f model = Matrix4f.rotate(50.0f * (new Random()).nextInt(360), 0.5f, 1.0f, 0.0f);
        Matrix4f projection = Matrix4f.perspective(camera.getFov(), aspect, 0.1f, 100f);
           
        Matrix4f view = camera.getViewMatrix();
        
        shader.use();
        shader.setInt("texture1", 0);
        shader.setInt("texture2", 1);
        shader.setMatrix4f("model", model);
        shader.setMatrix4f("view", view);
        shader.setMatrix4f("projection", projection);
        
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture1);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texture2);
        
        shader.use();
        glBindVertexArray(VAO);
        for (int i = 0; i < cubePositions.length; i++) {
            model = Matrix4f.translate(cubePositions[i].x, cubePositions[i].y, cubePositions[i].z);
            model = model.multiply(Matrix4f.rotate(50.0f * i, 1.0f, 0.3f, 0.5f));
            
            shader.setMatrix4f("model", model);
            
            glDrawArrays(GL_TRIANGLES, 0, 36);
        }
        
        
        swapBuffers();
        //image = createImage();
    }
    
    //https://stackoverflow.com/questions/21948804/how-would-i-get-a-bufferedimage-from-an-opengl-window
    public BufferedImage createImage() {
        int width = getWidth();
        int height = getHeight();
        FloatBuffer imageData = BufferUtils.createFloatBuffer(width * height * 3);
        glReadPixels(0, 0, width, height, GL_RGB, GL_FLOAT, imageData);
        imageData.rewind();

        // fill rgbArray for BufferedImage
        int[] rgbArray = new int[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int r = (int) (imageData.get() * 255) << 16;
                int g = (int) (imageData.get() * 255) << 8;
                int b = (int) (imageData.get() * 255);
                int i = ((height - 1) - y) * width + x;
                rgbArray[i] = r + g + b;
            }
        }

        // create and save image
        if (width <= 0 || height <= 0) {
            return null;
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, rgbArray, 0, width);

        /*try {
                ImageIO.write(image, "png", new File("image"));
            } catch (Exception e) {
                System.err.println("Can not save screenshot!");
            }*/
        return image;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (keyCode == KeyEvent.VK_W)
            camera.moveCamera(CAMERA_MOVEMENT.FORWARD);
        if (keyCode == KeyEvent.VK_S)
            camera.moveCamera(CAMERA_MOVEMENT.BACKWARD);
        if (keyCode == KeyEvent.VK_A)
            camera.moveCamera(CAMERA_MOVEMENT.LEFT);
        if (keyCode == KeyEvent.VK_D)
            camera.moveCamera(CAMERA_MOVEMENT.RIGHT);
        
        render();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastX != null && lastY != null){
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
