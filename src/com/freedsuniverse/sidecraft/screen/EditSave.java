package com.freedsuniverse.sidecraft.screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.Settings;

public class EditSave extends Screen{

    private static final long serialVersionUID = -2030994089783662492L;
    private File save;
    private JTextField input;
    private JLabel sl;
    public EditSave(JLabel selected) {
        SpringLayout l = new SpringLayout();
        this.setLayout(l);
        
        this.sl = selected;
        
        save = new File(Settings.defaultDirectory() + sl.getText());        
        
        JLabel info = new JLabel("Rename your world.");
        info.setFont(new Font("Quartz MS", Font.BOLD, 16));
        info.setHorizontalAlignment(SwingConstants.CENTER);
        l.putConstraint(SpringLayout.NORTH, info, 20, SpringLayout.NORTH, this);
        l.putConstraint(SpringLayout.WEST, info, 0, SpringLayout.WEST, this);
        l.putConstraint(SpringLayout.EAST, info, 0, SpringLayout.EAST, this);
        add(info);
        
        input = new JTextField(save.getName());
        input.setOpaque(false);        
        input.setForeground(Color.white);
        input.setBorder(null);
        l.putConstraint(SpringLayout.NORTH, input, 100, SpringLayout.NORTH, this);
        l.putConstraint(SpringLayout.WEST, input, 50, SpringLayout.WEST, this);
        l.putConstraint(SpringLayout.EAST, input, -50, SpringLayout.EAST, this);
        add(input);
        
        JPanel back = new JPanel();
        back.setOpaque(true);
        back.setBackground(new Color(0,0,0, 50));
        back.setBorder(new LineBorder(Color.DARK_GRAY));
        l.putConstraint(SpringLayout.NORTH, back, -10, SpringLayout.NORTH, input);
        l.putConstraint(SpringLayout.WEST, back, -10, SpringLayout.WEST, input);
        l.putConstraint(SpringLayout.EAST, back, 10, SpringLayout.EAST, input);
        l.putConstraint(SpringLayout.SOUTH, back, 10, SpringLayout.SOUTH, input);
        add(back);
        
        JButton btnAccept = new JButton("Accept");
        l.putConstraint(SpringLayout.SOUTH, btnAccept, -25, SpringLayout.SOUTH, this);
        l.putConstraint(SpringLayout.WEST, btnAccept, 50, SpringLayout.WEST, this);
        add(btnAccept);
        btnAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                accept();
            }
        });
        
        JButton btnCancel = new JButton("Cancel");
        l.putConstraint(SpringLayout.SOUTH, btnCancel, -25, SpringLayout.SOUTH, this);
        l.putConstraint(SpringLayout.EAST, btnCancel, -50, SpringLayout.EAST, this);
        add(btnCancel);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                cancel();
            }
        });
    }
    
    private void accept() {       
        if(input.getText().length() > 0) {
            File newName =  new File(Settings.defaultDirectory() + input.getText());
            
            if(contains(new File(Settings.defaultDirectory()).listFiles(), newName)) {
               cancel(); 
            }else {
                if(save.exists()) {
                    save.renameTo(newName);       
                }
            }
            sl.setText(newName.getName());
            Main.getGame().setName(newName.getName());
        }
        cancel();
    }
    
    private void cancel() {
        Main.setScreen(SaveLoader.class.getName());
        if(Main.contentPane.getComponentZOrder(this) != -1) {
            Main.contentPane.remove(this);
        }
    }
    
    private boolean contains(File[] fs, File f0) {
        for(File f:fs) {
            if(f.getName().equals(f0.getName()))
                return true;
        }
        return false;
    }
}
