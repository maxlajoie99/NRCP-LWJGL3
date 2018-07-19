/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.opengl.mesh;

import org.lwjgl.math.Vector2f;
import org.lwjgl.math.Vector3f;
import ca.cidco.opengl.Image2D;
import ca.cidco.opengl.Shader;
import java.io.File;
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
    private String folder;
    
    private boolean gammaCorrection;

    private List<Texture> loadedTexture = new ArrayList<>();

    public Model(String path, boolean gamma) {
        gammaCorrection = gamma;
        LoadModel(path);
    }

    public void draw(Shader shader) {
        for (Mesh mesh : meshes) {
            mesh.draw(shader);
        }
    }

    //https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter27/chapter27.html
    private void LoadModel(String path) {
        AIScene scene = Assimp.aiImportFile(new File(path).getAbsolutePath(), Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs | Assimp.aiProcess_CalcTangentSpace);

        if (scene == null) {
            System.out.println("Error while loading the model : " + Assimp.aiGetErrorString());
            return;
        }

        folder = path.substring(0, path.lastIndexOf("/"));
        ProcessNode(scene.mRootNode(), scene);

    }

    private void ProcessNode(AINode node, AIScene scene) {
        for (int i = 0; i < scene.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
            meshes.add(ProcessMesh(mesh, scene));
        }

        for (int i = 0; i < node.mNumChildren(); i++) {
            ProcessNode(AINode.create(node.mChildren().get(i)), scene);
        }
    }

    private Mesh ProcessMesh(AIMesh mesh, AIScene scene) {
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

            Vector2f texCoords = new Vector2f(0.0f, 0.0f);
            if (mesh.mTextureCoords() != null) {
                texCoords.x = mesh.mTextureCoords(0).get(i).x();
                texCoords.y = mesh.mTextureCoords(0).get(i).y();
            }
            vertex.TexCoords = texCoords;
            
            Vector3f tangent = new Vector3f();
            tangent.x = mesh.mTangents().get(i).x();
            tangent.y = mesh.mTangents().get(i).y();
            tangent.z = mesh.mTangents().get(i).z();
            vertex.Tangent = tangent;
            
            Vector3f bitangent = new Vector3f();
            bitangent.x = mesh.mBitangents().get(i).x();
            bitangent.y = mesh.mBitangents().get(i).y();
            bitangent.z = mesh.mBitangents().get(i).z();
            vertex.Bitangent = bitangent;
            
            vertices.add(vertex);
        }

        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                indices.add(face.mIndices().get(j));
            }
        }

        AIMaterial material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));
        
        List<Texture> diffuseMaps = LoadMaterialTextures(material, Assimp.aiTextureType_DIFFUSE, "texture_diffuse");
        textures.addAll(diffuseMaps);
        List<Texture> specularMaps = LoadMaterialTextures(material, Assimp.aiTextureType_SPECULAR, "texture_specular");
        textures.addAll(specularMaps);
        List<Texture> normalMaps = LoadMaterialTextures(material, Assimp.aiTextureType_HEIGHT, "texture_normal");
        textures.addAll(normalMaps);
        List<Texture> heightMaps = LoadMaterialTextures(material, Assimp.aiTextureType_AMBIENT, "texture_height");
        textures.addAll(heightMaps);

        Vertex[] arrayV = new Vertex[vertices.size()];
        for (int i = 0; i < arrayV.length; i++) {
            arrayV[i] = vertices.get(i);
        }
        
        int[] arrayI = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            arrayI[i] = indices.get(i);
        }
        
        Texture[] arrayT = new Texture[textures.size()];
        for (int i = 0; i < arrayT.length; i++) {
            arrayT[i] = textures.get(i);
        }

        return new Mesh(arrayV, arrayI, arrayT);
    }

    private List<Texture> LoadMaterialTextures(AIMaterial mat, int type, String typeName) {
        List<Texture> textures = new ArrayList<>();
        
        for (int i = 0; i < Assimp.aiGetMaterialTextureCount(mat, type); i++) {
            boolean skip = false;

            AIString path = AIString.calloc();
            Assimp.aiGetMaterialTexture(mat, type, 0, path, (IntBuffer) null, null, null, null, null, null);
            
            for (Texture t : loadedTexture) {
                if (t.path.equals(path.dataString())){
                    textures.add(t);
                    skip = true;
                    break;
                }
            }
            
            if (!skip) {
                Texture texture = new Texture();
                texture.id = new Image2D(folder + "/" + path.dataString(), GL_REPEAT, GL_REPEAT, GL_LINEAR, GL_LINEAR).getID();
                texture.type = typeName;
                texture.path = path.dataString();
                textures.add(texture);
                loadedTexture.add(texture);
            }

        }

        return textures;
    }
}
