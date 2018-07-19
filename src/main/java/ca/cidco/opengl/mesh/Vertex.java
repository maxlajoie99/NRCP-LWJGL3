/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.opengl.mesh;

import org.lwjgl.math.Vector2f;
import org.lwjgl.math.Vector3f;

/**
 *
 * @author mlajoie
 */
public class Vertex {
    public Vector3f Position;
    public Vector3f Normal;
    public Vector2f TexCoords;
    public Vector3f Tangent;
    public Vector3f Bitangent;
    
    public float[] getFloatArray(){
        return new float[] {Position.x, Position.y, Position.z, 
                            Normal.x, Normal.y, Normal.z, 
                            TexCoords.x, TexCoords.y, 
                            Tangent.x, Tangent.y, Tangent.z,
                            Bitangent.x, Bitangent.y, Bitangent.z};
    }
    
    public static float[] getFloatArray(Vertex[] vertices){
        
        float[] array = new float[vertices.length * vertices[0].getFloatArray().length];
        
        int compteur = 0;
        for (Vertex v : vertices) {
            for (float f : v.getFloatArray()) {
                array[compteur] = f;
                compteur++;
            }
        }
        
        return array;
    }
    
    public int sizeof(){
        int size = 0;
        
        if (Position != null)
            size += 3;
        
        if (Normal != null)
            size += 3;
        
        if (TexCoords != null)
            size += 2;
        
        if (Tangent != null)
            size += 3;
        
        if (Bitangent != null)
            size += 3;
        
        return size;
    }
}
