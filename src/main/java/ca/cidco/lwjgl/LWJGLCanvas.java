/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.lwjgl;

import ca.cidco.math.*;
import ca.cidco.opengl.ImageReader;
import ca.cidco.opengl.Shader;
import java.awt.event.KeyEvent;
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
public class LWJGLCanvas extends AWTGLCanvas {

    BufferedImage image;

    int FLOAT_SIZE = Float.SIZE/Byte.SIZE;
   
    //Camera/View Space
    Vector3f camPos = new Vector3f(0.0f, 0.0f, 3.0f);
    Vector3f camFront = new Vector3f(0.0f, 0.0f, -1.0f);
    Vector3f camUp = new Vector3f(0.0f, 1.0f, 0.0f);

    public LWJGLCanvas() {
        super();
    }

    public LWJGLCanvas(GLData data) {
        super(data);
    }

    @Override
    public void initGL() {
        GL.createCapabilities();
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        glEnable(GL_DEPTH_TEST);
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
        Matrix4f projection = Matrix4f.perspective(45.0f, aspect, 0.1f, 100f);
           
        Matrix4f view = Matrix4f.lookAt(camPos, camPos.add(camFront), camUp);
        
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
        image = createImage();
    }
    
    public void moveCamera(int keyCode){
        float camSpeed = 0.05f;
        if (keyCode == KeyEvent.VK_W)
            camPos = camPos.add(camFront.scale(camSpeed));
        if (keyCode == KeyEvent.VK_S)
            camPos = camPos.subtract(camFront.scale(camSpeed));
        if (keyCode == KeyEvent.VK_A)
            camPos = camPos.subtract(camFront.cross(camUp).normalize().scale(camSpeed));
        if (keyCode == KeyEvent.VK_D)
            camPos = camPos.add(camFront.cross(camUp).normalize().scale(camSpeed));
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

}
