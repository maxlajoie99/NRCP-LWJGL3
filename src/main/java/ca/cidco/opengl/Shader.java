/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.opengl;

import ca.cidco.math.Matrix4f;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 *
 * @author mlajoie
 */
public class Shader {
    
    private int ID;
    
    public Shader(String vertexFile, String fragmentFile){
        int vertex;
        int fragment;
        
        vertex = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertex, ShaderReader.loadVertex(vertexFile));
        glCompileShader(vertex);
        int success = glGetShaderi(vertex, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            System.out.println(glGetShaderInfoLog(vertex));
        }
        
        fragment = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragment, ShaderReader.loadFragment(fragmentFile));
        glCompileShader(fragment);
        success = glGetShaderi(fragment, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            System.out.println(glGetShaderInfoLog(fragment));
        }
        
        ID = glCreateProgram();
        glAttachShader(ID, vertex);
        glAttachShader(ID, fragment);
        glLinkProgram(ID);
        success = glGetProgrami(ID, GL_LINK_STATUS);
        if (success == GL_FALSE){
            System.out.println(glGetProgramInfoLog(ID));
        }
        
        glDeleteShader(vertex);
        glDeleteShader(fragment);
    }
    
    public void use(){
        glUseProgram(ID);
    }
    
    public void setBool(String name, boolean value){
        glUniform1i(glGetUniformLocation(ID, name), value ? 1 : 0);
    }
    
    public void setInt(String name, int value){
        glUniform1i(glGetUniformLocation(ID, name), value);
    }
    
    public void setFloat(String name, float value){
        glUniform1f(glGetUniformLocation(ID, name), value);
    }
    
    public void setMatrix4f(String name, Matrix4f mat4){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        mat4.toBuffer(buffer);
        glUniformMatrix4fv(glGetUniformLocation(ID, name), false, buffer);
    }
    
}
