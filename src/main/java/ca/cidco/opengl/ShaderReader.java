/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author mlajoie
 */
public class ShaderReader {

    public static StringBuilder loadVertex(String filename) {
        StringBuilder shaderSource = new StringBuilder();

        try {
            InputStream inputStream = ShaderReader.class.getResourceAsStream("shaders/vertex/" + filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return shaderSource;
    }

    public static StringBuilder loadFragment(String filename){
        StringBuilder shaderSource = new StringBuilder();

        try {
            InputStream inputStream = ShaderReader.class.getResourceAsStream("shaders/fragment/" + filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return shaderSource;
    }
    
}
