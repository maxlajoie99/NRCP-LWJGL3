/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.opengl;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

/**
 *
 * @author mlajoie
 */
public class Image2D {
    
    int ID;
    int Unit;
    
    int[] width = new int[1];
    int[] height = new int[1];
    
    public Image2D(String fileName, int wrapS, int wrapT, int minFilter, int magFilter, int textureUnit){
        Unit = textureUnit;
        SetupTexture(fileName, wrapS, wrapT, minFilter, magFilter, true);
    }
    
    public Image2D(String fileName, int wrapS, int wrapT, int minFilter, int magFilter){
        SetupTexture(fileName, wrapS, wrapT, minFilter, magFilter, false);
    }
    
    private void SetupTexture(String fileName, int wrapS, int wrapT, int minFilter, int magFilter, boolean inJAR){
        ID = glGenTextures();
        
        glBindTexture(GL_TEXTURE_2D, ID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);    
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
        
        ByteBuffer data = loadImage(fileName, width, height, inJAR);
        if (data != null){
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width[0], height[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
            glGenerateMipmap(GL_TEXTURE_2D);
        }
        else{
            System.out.println("Failed to load texture");
        }
    }
    
    public void bind(int unit){
        Unit = unit;
        glActiveTexture(Unit);
        glBindTexture(GL_TEXTURE_2D, ID);
    }
    
    public void bind(){
        glActiveTexture(Unit);
        glBindTexture(GL_TEXTURE_2D, ID);
    }
    
    public int getWidth(){
        return width[0];
    }
    
    public int getHeight(){
        return height[0];
    }
    
    public int getID(){
        return ID;
    }
    
    public static ByteBuffer loadImage(String filename, int[] width, int[] height, boolean fromJAR) {
        //https://github.com/SilverTiger/lwjgl3-tutorial/wiki/Textures
        try {
            BufferedImage image;
            if (fromJAR)
                image = ImageIO.read(Image2D.class.getResourceAsStream("images/" + filename));
            else
                image = ImageIO.read(new File(filename).getAbsoluteFile());

            //Flip
            AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
            transform.translate(0, -image.getHeight());
            AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            image = operation.filter(image, null);

            width[0] = image.getWidth();
            height[0] = image.getHeight();

            int[] pixels = new int[width[0] * height[0]];
            image.getRGB(0, 0, width[0], height[0], pixels, 0, width[0]);

            ByteBuffer buffer = BufferUtils.createByteBuffer(width[0] * height[0] * 4);

            for (int y = 0; y < height[0]; y++) {
                for (int x = 0; x < width[0]; x++) {
                    /* Pixel as RGBA: 0xAARRGGBB */
                    int pixel = pixels[y * width[0] + x];

                    /* Red component 0xAARRGGBB >> (4 * 4) = 0x0000AARR */
                    buffer.put((byte) ((pixel >> 16) & 0xFF));

                    /* Green component 0xAARRGGBB >> (2 * 4) = 0x00AARRGG */
                    buffer.put((byte) ((pixel >> 8) & 0xFF));

                    /* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
                    buffer.put((byte) (pixel & 0xFF));

                    /* Alpha component 0xAARRGGBB >> (6 * 4) = 0x000000AA */
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }

            /* Do not forget to flip the buffer! */
            buffer.flip();
            
            return buffer;

        } catch (IOException e) {
            System.out.println("Failed to load image");
        }
        
        return null;
    }
    
}
