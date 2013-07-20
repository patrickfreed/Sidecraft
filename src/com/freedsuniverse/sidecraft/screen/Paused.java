package com.freedsuniverse.sidecraft.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import com.freedsuniverse.sidecraft.Main;

public class Paused extends JPanel {
    private static final long serialVersionUID = 1L;
    
    public Paused() {      
        setOpaque(true);
        setLayout(new BorderLayout(0, 0));
        
        Color toUse = Color.gray;
        setBackground(new Color(toUse.getRed(), toUse.getBlue(), toUse.getGreen(), 150));
        
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        add(panel, BorderLayout.CENTER);
        SpringLayout sl_panel = new SpringLayout();
        panel.setLayout(sl_panel);
        
        JButton btnSaveAndQuit = new JButton("Save and Quit");
        sl_panel.putConstraint(SpringLayout.NORTH, btnSaveAndQuit, 153, SpringLayout.NORTH, panel);
        sl_panel.putConstraint(SpringLayout.WEST, btnSaveAndQuit, 300, SpringLayout.WEST, panel);
        sl_panel.putConstraint(SpringLayout.EAST, btnSaveAndQuit, -300, SpringLayout.EAST, panel);
        panel.add(btnSaveAndQuit);
        btnSaveAndQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                //Main.saveGame(Main.getGame().getName());
                Main.getGame().stop();
                Main.setScreen(Menu.class.getName());               
            }
        });
        
        JButton btnResume = new JButton("Resume");
        sl_panel.putConstraint(SpringLayout.WEST, btnResume, 0, SpringLayout.WEST, btnSaveAndQuit);
        sl_panel.putConstraint(SpringLayout.NORTH, btnResume, 10, SpringLayout.SOUTH, btnSaveAndQuit);
        sl_panel.putConstraint(SpringLayout.EAST, btnResume, 0, SpringLayout.EAST, btnSaveAndQuit);
        panel.add(btnResume);
        
        JLabel lblPaused = new JLabel("PAUSED");
        sl_panel.putConstraint(SpringLayout.WEST, lblPaused, 0, SpringLayout.WEST, btnResume);
        sl_panel.putConstraint(SpringLayout.EAST, lblPaused, 0, SpringLayout.EAST, btnResume);
        sl_panel.putConstraint(SpringLayout.NORTH, lblPaused, 20, SpringLayout.NORTH, panel);
        panel.add(lblPaused);
        lblPaused.setHorizontalAlignment(SwingConstants.CENTER);
        lblPaused.setFont(new Font("Quartz MS", Font.PLAIN, 27));
        btnResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Main.getGame().pause();
            }
        });
    }   
    
    public void paintComponent(Graphics g) {
        Main.getGame().update(g);
        Main.getGame().paint(g);
        super.paintComponent(g);
    }
    
}
