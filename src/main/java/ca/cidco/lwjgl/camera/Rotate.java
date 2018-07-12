/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.lwjgl.camera;

import ca.cidco.lwjgl.LWJGLTopComponent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author mlajoie
 */
public class Rotate implements MouseMotionListener{

    Integer lastX = null;
    Integer lastY = null;
    
    private final float ANGLE = 360f;
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getSource() instanceof LWJGLTopComponent  && e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK){
            LWJGLTopComponent tc = (LWJGLTopComponent)e.getSource();

            if (lastX != null && lastY != null){
                float angleX = 0.0f;
                float angleY = 0.0f;
                
                int currentX = e.getX();
                int currentY = e.getY();
                int width = tc.getWidth();
                int height = tc.getHeight();
                
                angleY = -(currentX-lastX)/(float)width * ANGLE;
                angleX = -(currentY-lastY)/(float)height * ANGLE;

                //tc.getPanel().getCanvas().rotate(angleX, angleY);
            }

            lastX = e.getX();
            lastY = e.getY();
            tc.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }
    
}
