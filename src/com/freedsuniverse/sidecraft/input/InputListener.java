package com.freedsuniverse.sidecraft.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputListener implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    public static InputListener i = new InputListener();

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Mouse.toggle(e.getButton(), true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Mouse.toggle(e.getButton(), false);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        toggleKeys(e, true);
    }

    private void toggleKeys(KeyEvent e, boolean toggleType) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_A:
            Key.A.toggle(toggleType);
            break;
        case KeyEvent.VK_W:
            Key.W.toggle(toggleType);
            break;
        case KeyEvent.VK_S:
            Key.S.toggle(toggleType);
            break;
        case KeyEvent.VK_D:
            Key.D.toggle(toggleType);
            break;
        case KeyEvent.VK_B:
            Key.B.toggle(toggleType);
            break;
        case KeyEvent.VK_F5:
            Key.F5.toggle(toggleType);
            break;
        case KeyEvent.VK_I:
            Key.I.toggle(toggleType);
            break;
        case KeyEvent.VK_F:
            Key.F.toggle(toggleType);
            break;
        case KeyEvent.VK_M:
            Key.M.toggle(toggleType);
            break;
        case KeyEvent.VK_SPACE:
            Key.SPACE.toggle(toggleType);
            break;
        case KeyEvent.VK_1:
            Key.ONE.toggle(toggleType);
            break;
        case KeyEvent.VK_2:
            Key.TWO.toggle(toggleType);
            break;
        case KeyEvent.VK_3:
            Key.THREE.toggle(toggleType);
            break;
        case KeyEvent.VK_4:
            Key.FOUR.toggle(toggleType);
            break;
        case KeyEvent.VK_5:
            Key.FIVE.toggle(toggleType);
            break;
        case KeyEvent.VK_ESCAPE:
            Key.ESCAPE.toggle(toggleType);
            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        toggleKeys(e, false);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Mouse.setX(e.getX());
        Mouse.setY(e.getY());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            Mouse.modifyScrollWheelValue(1);
        } else {
            Mouse.modifyScrollWheelValue(-1);
        }

    }
}
