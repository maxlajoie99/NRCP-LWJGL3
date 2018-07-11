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
public class Panning implements MouseMotionListener {

    Integer lastX = null;
    Integer lastY = null;
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK + MouseEvent.CTRL_DOWN_MASK){
            if (e.getSource() instanceof LWJGLTopComponent){
                
                LWJGLTopComponent tc = (LWJGLTopComponent)e.getSource();
                
                float panX = 0.0f;
                float panY = 0.0f;
                
                int currentX = e.getX();
                int currentY = e.getY();
                int width = tc.getWidth();
                int height = tc.getHeight();
                
                panX = (currentX-lastX)/(float)width;
                panY = -(currentY-lastY)/(float)height;
                
                lastX = e.getX();
                lastY = e.getY();
                
                tc.getPanel().getCanvas().panning(panX, panY);
                tc.repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) { 
        lastX = e.getX();
        lastY = e.getY();
    }
    
}
