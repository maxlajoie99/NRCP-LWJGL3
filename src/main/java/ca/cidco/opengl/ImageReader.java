/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.opengl;

/**
 *
 * @author mlajoie
 */
public class ImageReader {
    
    public static String getImagePath(String filename){
        return ImageReader.class.getResource("images/" + filename).getPath();
    }
    
}
