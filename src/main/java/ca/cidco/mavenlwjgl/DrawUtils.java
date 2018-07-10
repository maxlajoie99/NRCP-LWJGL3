/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.mavenlwjgl;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author mlajoie
 */
public class DrawUtils {
    
    public static void drawCube(float x, float y, float z){
        
        x = x/2;
        y = y/2;
        z = z/2;
        
        glBegin(GL_POLYGON);
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(x, -y, -z);
        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(x, y, -z);
        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(-x, y, -y);
        glColor3f(1.0f, 0.0f, 1.0f);
        glVertex3f(-x, -y, -z);
        glEnd();

        // White side - BACK
        glBegin(GL_POLYGON);
        glColor3f(1.0f, 1.0f, 1.0f);
        glVertex3f(x, -y, z);
        glVertex3f(x, y, z);
        glVertex3f(-x, y, z);
        glVertex3f(-x, -y, z);
        glEnd();

        // Purple side - RIGHT
        glBegin(GL_POLYGON);
        glColor3f(1.0f, 0.0f, 1.0f);
        glVertex3f(x, -y, -z);
        glVertex3f(x, y, -z);
        glVertex3f(x, y, z);
        glVertex3f(x, -y, z);
        glEnd();

        // Green side - LEFT
        glBegin(GL_POLYGON);
        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(-x, -y, z);
        glVertex3f(-x, y, z);
        glVertex3f(-x, y, -z);
        glVertex3f(-x, -y, -z);
        glEnd();

        // Blue side - TOP
        glBegin(GL_POLYGON);
        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(x, y, z);
        glVertex3f(x, y, -z);
        glVertex3f(-x, y, -z);
        glVertex3f(-x, y, z);
        glEnd();

        // Red side - BOTTOM
        glBegin(GL_POLYGON);
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(x, -y, -z);
        glVertex3f(x, -y, z);
        glVertex3f(-x, -y, z);
        glVertex3f(-x, -y, -z);
        glEnd();
    }
    
}
