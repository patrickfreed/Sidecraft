package com.freedsuniverse.sidecraft.screen;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import com.freedsuniverse.sidecraft.Main;

public class Loading extends JPanel {
    private static final long serialVersionUID = 4991241640096068529L;

    private String screen;

    public Loading(String next) {
        setLayout(new BorderLayout());

        screen = next;

        JLabel info = new JLabel("Loading...");
        info.setLocation(Main.getPaneWidth() / 2, Main.getPaneHeight() / 2);
        add(info);
    }

    public void close() {
        if (Main.getContentPane().getComponentZOrder(this) != -1)
            Main.getContentPane().remove(this);
        Main.setScreen(screen);
    }

}
