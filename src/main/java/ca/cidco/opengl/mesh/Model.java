/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.opengl.mesh;

import ca.cidco.math.Vector2f;
import ca.cidco.math.Vector3f;
import ca.cidco.opengl.Image2D;
import ca.cidco.opengl.Shader;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;

/**
 *
 * @author mlajoie
 */
public class Model {
    
    private List<Mesh> meshes = new ArrayList<>();
    private String directory;
    private String folder;
    
    public Model(String path){
        LoadModel(path);
    }
    
    public void draw(Shader shader){
        for (Mesh mesh : meshes) {
            mesh.draw(shader);
        }
    }
    
    //https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter27/chapter27.html
    private void LoadModel(String path){
        AIScene scene = Assimp.aiImportFile(path, Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs);
        
        if (scene == null){
            System.out.println("Error while loading the model\n" + Assimp.aiGetErrorString());
            return;
        }
        
        directory = path.substring(0, path.lastIndexOf("/"));
        folder = path.substring(path.lastIndexOf("/"), path.length() - 1);
        ProcessNode(scene.mRootNode(), scene);
        
    }
    
    private void ProcessNode(AINode node, AIScene scene){
        for (int i = 0; i < scene.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
            meshes.add(ProcessMesh(mesh, scene));
        }
        
        for (int i = 0; i < node.mNumChildren(); i++) {
            ProcessNode(AINode.create(node.mChildren().get(i)), scene);
        }
    }
    
    private Mesh ProcessMesh(AIMesh mesh, AIScene scene){
        List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Texture> textures = new ArrayList<>();
        
        for (int i = 0; i < mesh.mNumVertices(); i++) {
            Vertex vertex = new Vertex();
            
            Vector3f pos = new Vector3f();
            pos.x = mesh.mVertices().get(i).x();
            pos.y = mesh.mVertices().get(i).y();
            pos.z = mesh.mVertices().get(i).z();
            vertex.Position = pos;
            
            Vector3f normal = new Vector3f();
            normal.x = mesh.mNormals().get(i).x();
            normal.y = mesh.mNormals().get(i).y();
            normal.z = mesh.mNormals().get(i).x();
            vertex.Normal = normal;
            
            Vector2f texCoords = new Vector2f();
            if(mesh.mTextureCoords() != null){
                texCoords.x = mesh.mTextureCoords(0).x();
                texCoords.y = mesh.mTextureCoords(0).y();
            }
            vertex.TexCoords = texCoords;
            
            vertices.add(vertex);
        }
        
        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                indices.add(face.mIndices().get(j));
            }
        }
        
        if(mesh.mMaterialIndex() >= 0){
            AIMaterial material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));
            List<Texture> diffuseMaps = LoadMaterialTextures(material, Assimp.aiTextureType_DIFFUSE, "texture_diffuse");
            textures.addAll(diffuseMaps);
            List<Texture> specularMaps = LoadMaterialTextures(material, Assimp.aiTextureType_SPECULAR, "texture_specular");
            textures.addAll(specularMaps);
        }
        
        int[] indis = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indis[i] = indices.get(i);
        }
        
        return new Mesh(vertices.toArray(new Vertex[vertices.size()]), indis, textures.toArray(new Texture[textures.size()]));
    }
    
    private List<Texture> LoadMaterialTextures(AIMaterial mat, int type, String typeName){
        List<Texture> textures = new ArrayList<>();
        
        for (int i = 0; i < Assimp.aiGetMaterialTextureCount(mat, type); i++) {
            AIString path = AIString.calloc();
            Assimp.aiGetMaterialTexture(mat, type, 0, path, (IntBuffer) null, null, null, null, null, null);
            Texture texture = new Texture();
            texture.id = new Image2D(folder + "/" + path.dataString(), GL_REPEAT, GL_REPEAT, GL_LINEAR, GL_LINEAR).getID();
            texture.type = typeName;
            texture.path = path.dataString();
            textures.add(texture);
        }
        
        return textures;
    } 
}