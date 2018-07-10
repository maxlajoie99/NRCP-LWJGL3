/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.cidco.mavenlwjgl.camera;

import ca.cidco.mavenlwjgl.LWJGLTopComponent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 *
 * @author mlajoie
 */
public class Zoom implements MouseWheelListener{

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        LWJGLTopComponent tc = (LWJGLTopComponent)e.getSource();
        tc.getPanel().getCanvas().zoom(e.getWheelRotation());
        tc.repaint();
    }
    
}
