/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.lwjgl;

import ca.cidco.opengl.ImageReader;
import ca.cidco.opengl.Shader;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
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
    
    float scale = 1.0f;
    float rotateX = 0.0f;
    float rotateY = 0.0f;
    float panX = 0.0f;
    float panY = 0.0f;
    
    float time = 0.0f;
    int FLOAT_SIZE = Float.SIZE/Byte.SIZE;
    float mixTextures = 0.0f;

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
        //glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void paintGL() {
        int w = getWidth();
        int h = getHeight();
        float aspect = (float) w / h;

        glClear(GL_COLOR_BUFFER_BIT);
        
        glViewport(0, 0, w, h);
        
        //Following the LeanOpenGL tutorial from https://learnopengl.com
        Shader shader = new Shader("simpleshader.vs", "simpleshader.fs");
          
        float vertices[] = {
            // positions          // colors           // texture coords
             0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,  1.0f, 1.0f,   // top right
             0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,  1.0f, 0.0f,   // bottom right
            -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,  0.0f, 0.0f,   // bottom left
            -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,  0.0f, 1.0f    // top left 
        };
        
        int indices[] = {  // note that we start from 0!
            0, 1, 3,   // first triangle
            1, 2, 3    // second triangle
        };  
                
        int VAO = glGenVertexArrays();
        glBindVertexArray(VAO);
        
        int VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        int EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        
        //position
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * FLOAT_SIZE, 0);
        glEnableVertexAttribArray(0);
        //color
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * FLOAT_SIZE, 3 * FLOAT_SIZE);
        glEnableVertexAttribArray(1);
        //texture
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * FLOAT_SIZE, 6 * FLOAT_SIZE);
        glEnableVertexAttribArray(2);

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
        
        shader.use();
        shader.setInt("texture1", 0);
        shader.setInt("texture2", 1);
        shader.setFloat("mixTextures", mixTextures);
        
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture1);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texture2);
        
        
        shader.use();
        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        
        /* Old code
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glLoadIdentity();
        glViewport(0, 0, w, h);
        
        glScalef(scale, scale, 1.0f);
        glTranslatef(panX, panY, 0.0f);
        
        //for (float i = -250.0f; i < 250.0f; i+=0.5f) {
            //for (float j = -250.0f; j < 250.0f; j+=0.5f) {
                glPushMatrix();
                //glTranslatef(i, j, 0.0f);
                glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
                glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
                DrawUtils.drawCube(0.3f, 0.3f, 0.3f);
                glPopMatrix();
            //}
        //} */
        swapBuffers();
        image = createImage();
    }

    public void mix(float value){
        mixTextures += value;
        if (mixTextures <= 0.0f)
            mixTextures = 0.0f;
        else if (mixTextures >= 1.0f)
            mixTextures = 1.0f;
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
