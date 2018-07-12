/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.opengl;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;

/**
 *
 * @author mlajoie
 */
public class ImageReader {

    public static ByteBuffer loadImage(String filename, int[] width, int[] height) {
        //https://github.com/SilverTiger/lwjgl3-tutorial/wiki/Textures
        try {
            BufferedImage image = ImageIO.read(ImageReader.class.getResourceAsStream("images/" + filename));

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

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
