package com.freedsuniverse.sidecraft.screen;

import javax.swing.JLabel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import com.freedsuniverse.sidecraft.Main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SettingsMenu extends Screen {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> comboBox;

    public SettingsMenu() {
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);

        JLabel lblResolution = new JLabel("Resolution");
        lblResolution.setHorizontalAlignment(SwingConstants.CENTER);
        springLayout.putConstraint(SpringLayout.NORTH, lblResolution, 60, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, lblResolution, 190, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, lblResolution, -190, SpringLayout.EAST, this);
        add(lblResolution);

        comboBox = new JComboBox<String>();
        comboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "1920x1080", "1280x720", "1024x576", "960x540" }));
        comboBox.setSelectedIndex(3);
        springLayout.putConstraint(SpringLayout.NORTH, comboBox, 6, SpringLayout.SOUTH, lblResolution);
        springLayout.putConstraint(SpringLayout.WEST, comboBox, 151, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, comboBox, -151, SpringLayout.EAST, this);
        add(comboBox);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.setScreen(getPreviousScreen());
            }
        });
        springLayout.putConstraint(SpringLayout.NORTH, btnBack, 10, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, btnBack, 10, SpringLayout.WEST, this);
        add(btnBack);

        JButton btnAccept = new JButton("Accept");
        btnAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String res = (String) (comboBox.getSelectedItem());
                String[] items = res.split("x");

                Main.setResolution(Integer.valueOf(items[0]).intValue(), Integer.valueOf(items[1]).intValue());
                System.out.println("yo");
            }
        });
        springLayout.putConstraint(SpringLayout.WEST, btnAccept, 179, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.SOUTH, btnAccept, -35, SpringLayout.SOUTH, this);
        add(btnAccept);
    }
}