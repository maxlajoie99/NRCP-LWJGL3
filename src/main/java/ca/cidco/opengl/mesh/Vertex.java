/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.opengl.mesh;

import ca.cidco.math.Vector2f;
import ca.cidco.math.Vector3f;

/**
 *
 * @author mlajoie
 */
public class Vertex {
    public Vector3f Position;
    public Vector3f Normal;
    public Vector2f TexCoords;
    
    public float[] getFloatArray(){
        return new float[] {Position.x, Position.y, Position.z, Normal.x, Normal.y, Normal.z, TexCoords.x, TexCoords.y};
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
}
