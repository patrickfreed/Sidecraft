package com.freedsuniverse.sidecraft.screen;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import com.freedsuniverse.sidecraft.Main;

public class Menu extends Screen {
    private static final long serialVersionUID = 1L;

    public Menu() {
        setBackground(Color.LIGHT_GRAY);
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);
        
        JButton startGame = new JButton("Start Game");
        springLayout.putConstraint(SpringLayout.SOUTH, startGame, -149, SpringLayout.SOUTH, this);
        startGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Main.setScreen(SaveLoader.class.getName());
            }
        });
        add(startGame);
        
        JLabel label = new JLabel("");
        springLayout.putConstraint(SpringLayout.WEST, startGame, 150, SpringLayout.WEST, label);
        springLayout.putConstraint(SpringLayout.EAST, startGame, -150, SpringLayout.EAST, label);
        springLayout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, label, 167, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.SOUTH, label, 141, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.EAST, label, -162, SpringLayout.EAST, this);
        label.setMaximumSize(label.getSize());
        label.setMinimumSize(label.getSize());
        label.setIcon(new ImageIcon(Menu.class.getResource("/misc/menu/background.png")));
        add(label);
        
        JButton btnSettings = new JButton("Settings");
        btnSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Main.setScreen(SettingsMenu.class.getName());
            }
        });
        springLayout.putConstraint(SpringLayout.NORTH, btnSettings, 6, SpringLayout.SOUTH, startGame);
        springLayout.putConstraint(SpringLayout.WEST, btnSettings, 362, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, btnSettings, -362, SpringLayout.EAST, this);
        add(btnSettings);
    } 
}
