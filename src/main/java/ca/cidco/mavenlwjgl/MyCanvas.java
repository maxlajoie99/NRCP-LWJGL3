/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.mavenlwjgl;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

/**
 *
 * @author mlajoie
 */
public class MyCanvas extends AWTGLCanvas {

    BufferedImage image;
    
    float scale = 1.0f;
    float rotateX = 0.0f;
    float rotateY = 0.0f;

    public MyCanvas() {
        super();
    }

    public MyCanvas(GLData data) {
        super(data);
    }

    @Override
    public void initGL() {
        GL.createCapabilities();
        glClearColor(0.0f, 0.0f, 0.0f, 1);
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void paintGL() {
        int w = getWidth();
        int h = getHeight();
        float aspect = (float) w / h;

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glLoadIdentity();
        glViewport(0, 0, w, h);
        glScalef(scale, scale, 1.0f);
        
        glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
        glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
        
        for (int i = -500; i < 501; i++) {
            for (int j = -500; j < 501; j++) {
                glPushMatrix();
                glTranslatef(i, j, 0.0f);
                DrawUtils.drawCube(0.5f, 0.5f, 0.5f);
                glPopMatrix();
            }
        }
        
        swapBuffers();
        image = createImage();
    }

    public void zoom(int time) {
        scale += time * -0.025f;
        if (scale <= 0.0f)
            scale = 0.0f;
    }
    
    public void rotate(float angleX, float angleY){
        rotateX += angleX;
        rotateY += angleY;
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
