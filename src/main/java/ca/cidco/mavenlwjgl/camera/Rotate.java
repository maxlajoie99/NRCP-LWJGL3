/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.mavenlwjgl.camera;

import ca.cidco.mavenlwjgl.LWJGLTopComponent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author mlajoie
 */
public class Rotate implements MouseMotionListener{

    Integer lastX = null;
    Integer lastY = null;
    
    private final float ANGLE = 5.0f;
    
    @Override
    public void mouseDragged(MouseEvent e) {
        
        LWJGLTopComponent tc = (LWJGLTopComponent)e.getSource();

        if (lastX != null && lastY != null){
            float angleX = 0.0f;
            float angleY = 0.0f;
            
            if (lastX < e.getX())   //Going to the right
                angleY = -ANGLE;
            else if (lastX > e.getX())  //Going to the left
                angleY = ANGLE;
            
            if (lastY < e.getY())   //Going to the bottom
                angleX = -ANGLE;
            else if (lastY > e.getY())  //Going to the Top
                angleX = ANGLE;
            
            tc.getPanel().getCanvas().rotate(angleX, angleY);
        }
        
        lastX = e.getX();
        lastY = e.getY();
        tc.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }
    
}
