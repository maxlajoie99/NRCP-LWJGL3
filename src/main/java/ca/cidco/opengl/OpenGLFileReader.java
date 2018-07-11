/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.opengl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author mlajoie
 */
public class OpenGLFileReader {

    public static StringBuilder loadShader(String filename) {
        StringBuilder shaderSource = new StringBuilder();

        try {
            InputStream inputStream = OpenGLFileReader.class.getResourceAsStream("ca/cidco/opengl/" + filename);
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
