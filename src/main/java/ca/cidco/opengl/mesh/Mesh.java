/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.opengl.mesh;

import ca.cidco.opengl.Shader;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
/**
 *
 * @author mlajoie
 */
public class Mesh {
    
    public Vertex[] vertices;
    public int[] indices;
    public Texture[] textures;
    public int VAO;
    
    private int VBO;
    private int EBO;
    
    public Mesh(Vertex[] vertices, int[] indices, Texture[] textures) {
        this.vertices = vertices;
        this.indices = indices;
        this.textures = textures;
        
        SetupMesh();
    }
    
    public void draw(Shader shader){
        int diffuseNB = 1;
        int specularNB = 1;
        int normalNB = 1;
        int heightNB = 1;
        
        for (int i = 0; i < textures.length; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            
            String number = "";
            String name = textures[i].type;
            if(name.equals("texture_diffuse")){
                number = "" + diffuseNB++;
            } else if (name.equals("texture_specular")){
                number = "" + specularNB++;
            } else if (name.equals("texture_normal")){
                number = "" + normalNB++;
            } else if (name.equals("texture_height")){
                number = "" + heightNB++;
            }
            
            shader.setInt(name + number, i);
            glBindTexture(GL_TEXTURE_2D, textures[i].id);
        }
        
        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        
        glActiveTexture(GL_TEXTURE0);
    }
    
    private void SetupMesh(){
        VAO = glGenVertexArrays();
        VBO = glGenBuffers();
        EBO = glGenBuffers();
        
        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        
        glBufferData(GL_ARRAY_BUFFER, Vertex.getFloatArray(vertices), GL_STATIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        
        int size = vertices[0].sizeof();
        
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, size * Float.SIZE/Byte.SIZE, 0);
        
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, size * Float.SIZE/Byte.SIZE, 3 * Float.SIZE/Byte.SIZE);
        
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, size * Float.SIZE/Byte.SIZE, 6 * Float.SIZE/Byte.SIZE);
        
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, size * Float.SIZE/Byte.SIZE, 8 * Float.SIZE/Byte.SIZE);
        
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 3, GL_FLOAT, false, size * Float.SIZE/Byte.SIZE, 11 * Float.SIZE/Byte.SIZE);
        
        glBindVertexArray(0);
    }
}
