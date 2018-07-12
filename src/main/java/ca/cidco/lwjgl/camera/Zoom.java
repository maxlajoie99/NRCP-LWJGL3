/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.lwjgl.camera;

import ca.cidco.lwjgl.LWJGLPanel;
import ca.cidco.lwjgl.LWJGLTopComponent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 *
 * @author mlajoie
 */
public class Zoom implements MouseWheelListener{

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getSource() instanceof LWJGLTopComponent){
            LWJGLTopComponent tc = (LWJGLTopComponent)e.getSource();
            tc.getPanel().getCanvas().zoom(e.getWheelRotation());
            tc.repaint();
        }
        else if (e.getSource() instanceof LWJGLPanel) {
            LWJGLPanel p = (LWJGLPanel)e.getSource();
            p.getCanvas().zoom(e.getWheelRotation());
            p.repaint();
        }
    }
    
}
