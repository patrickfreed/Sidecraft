package com.freedsuniverse.sidecraft.screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.Sidecraft;

public class SaveLoader extends Screen {
    private static final long serialVersionUID = 1L;
    
    private JLabel info, selected;
    private final int SAVES = 5;

    private JPanel back;
    public SaveLoader() {  
        SpringLayout springLayout = new SpringLayout();
        this.setLayout(springLayout);
        
        File[] dirs = new File(Settings.defaultDirectory()).listFiles();
        
        //-----------------------------
        //Add the labels
        //-----------------------------
        
        info = new JLabel("Click on a world to select it.");
        info.setFont(new Font("Quartz MS", Font.BOLD, 16));
        info.setHorizontalAlignment(SwingConstants.CENTER);
        springLayout.putConstraint(SpringLayout.NORTH, info, 20, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, info, 0, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, info, 0, SpringLayout.EAST, this);
        
        JLabel lblFirstLabel = new JLabel("World 1");
        lblFirstLabel.setHorizontalAlignment(SwingConstants.CENTER);
        springLayout.putConstraint(SpringLayout.NORTH, lblFirstLabel, 90, SpringLayout.NORTH, Main.contentPane);
        springLayout.putConstraint(SpringLayout.WEST, lblFirstLabel, 160, SpringLayout.WEST, Main.contentPane);
        springLayout.putConstraint(SpringLayout.EAST, lblFirstLabel, -160, SpringLayout.EAST, Main.contentPane);
        lblFirstLabel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Main.contentPane.getHeight() / 11));
        
        if(dirs.length > 0) {
            lblFirstLabel.setText(dirs[0].getName());
        }
        
        lblFirstLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                setSelected((JLabel)e.getComponent());
            }
        });
        
        add(lblFirstLabel);
        
        for(int x = 1; x < SAVES; x++) {
            JLabel lbl = new JLabel();
            lbl.setText("World " + (x + 1));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setPreferredSize(new Dimension(Integer.MAX_VALUE, Main.contentPane.getHeight() / 11));
            springLayout.putConstraint(SpringLayout.NORTH, lbl, 10, SpringLayout.SOUTH, this.getComponent(x - 1));
            springLayout.putConstraint(SpringLayout.WEST, lbl, 160, SpringLayout.WEST, this);
            springLayout.putConstraint(SpringLayout.EAST, lbl, -160, SpringLayout.EAST, this);
            
            if(dirs.length > x) {
                lbl.setText(dirs[x].getName());
            }
            
            lbl.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    setSelected((JLabel)e.getComponent());
                }
            });
            
            add(lbl);   
        }
        
        for(Component c:this.getComponents()) {
            if(c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                lbl.setForeground(Color.DARK_GRAY.darker());
            }
        }        
        
        back = new JPanel();
        springLayout.putConstraint(SpringLayout.SOUTH, back, 5, SpringLayout.SOUTH, this.getComponent(SAVES - 1));
        springLayout.putConstraint(SpringLayout.NORTH, back, -5, SpringLayout.NORTH, lblFirstLabel);
        springLayout.putConstraint(SpringLayout.WEST, back, -5, SpringLayout.WEST, this.getComponent(SAVES - 1));
        springLayout.putConstraint(SpringLayout.EAST, back, 5, SpringLayout.EAST, lblFirstLabel);
        back.setBackground(new Color(0, 0, 0, 50));
        back.setOpaque(true);                 
        
        //------------------------
        // Add buttons
        //------------------------
        
        JButton btnLoad = new JButton("Load World");
        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                //if(new File(Settings.defaultDirectory() + selected.getText()).exists()) {
                //    Main.loadGame(selected.getText());
               // }else {
                    Main.getGame().startNewGame(selected.getText());
               //}

                Main.setScreen(Sidecraft.class.getName());               
                Main.getGame().start();
            }
        });
        springLayout.putConstraint(SpringLayout.NORTH, btnLoad, 10, SpringLayout.SOUTH, back);
        springLayout.putConstraint(SpringLayout.WEST, btnLoad, 60, SpringLayout.WEST, back);
        springLayout.putConstraint(SpringLayout.EAST, btnLoad, -60, SpringLayout.EAST, back);
        
        JButton btnEdit = new JButton("Edit");
        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                editSave();
            }
        });
        
        springLayout.putConstraint(SpringLayout.NORTH, btnEdit, 10, SpringLayout.SOUTH, btnLoad);
        springLayout.putConstraint(SpringLayout.WEST, btnEdit, 0, SpringLayout.WEST, btnLoad);
        springLayout.putConstraint(SpringLayout.EAST, btnEdit, 0, SpringLayout.EAST, btnLoad);
        
        //JButton btnDelete = new JButton("Delete");
        
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Main.setScreen(getPreviousScreen());
            }
        });
        
        springLayout.putConstraint(SpringLayout.NORTH, btnBack, 10, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, btnBack, 10, SpringLayout.WEST, this);
        
        add(btnEdit);
        add(btnLoad);
        add(btnBack);
        add(info);
        add(back); 
        setSelected(lblFirstLabel);
    }
    
    private void editSave() {
        Main.contentPane.add(new EditSave(selected), EditSave.class.getName());
        Main.setScreen(EditSave.class.getName());
    }
    
    public JLabel getSelected() {
        return selected;
    }
 
    private void setSelected(JLabel l) {
        if(selected != null) {
            selected.setOpaque(false);
            selected.setBorder(null);
            selected.setForeground(l.getForeground());
        }
        
        selected = l;
        selected.setBorder(new LineBorder(Color.black));
        selected.setBackground(Color.DARK_GRAY);
        selected.setForeground(Color.LIGHT_GRAY);
        selected.setOpaque(true);
    }
    
    public void updateSelected() {
        
    }
    
}
