package com.freedsuniverse.sidecraft.screen;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.input.Mouse;

public class Button {
    
    public static BufferedImage DEFAULT_TILE; 
    
    private static BufferedImage getBrighter(BufferedImage img){
        RescaleOp op = new RescaleOp(1.3f, 0, null);
        return op.filter(img, null);
    }
    
    private Color back;
    private String text;
    private Rectangle r;
    private BufferedImage img, selectedImg;
    
    
    public Button(String text, Color c, Rectangle rec){
        back = c;
        this.text = text;
        r = rec;
    }
    
    public Button(String text, BufferedImage tile, int x, int y, int width, int height){
        this(text, tile, new Rectangle(x, y, width, height));
    }
    
    public Button(String text, BufferedImage tile, Rectangle rec){
        this.text = text;
        img = tile;
        selectedImg = getBrighter(img);
        r = rec;
    }
    
    public boolean equals(Button b){
        return b.getBounds().equals(r);
    }
    
    public Rectangle getBounds(){
        return r;
    }
    
    public void draw(){
        Engine.graphics.setColor(Color.DARK_GRAY);
        
        if(img != null){
            Engine.graphics.drawRect(r.x - 1, r.y - 1, r.width + 1, r.height + 1);
            
            if(r.contains(Mouse.getPoint())){
                for(int x = 0; x < r.width; x+=img.getWidth()){
                    Engine.render(r.x + x, r.y, selectedImg);
                }
            }else{
                for(int x = 0; x < r.width; x+=img.getWidth()){
                    Engine.render(r.x + x, r.y, img);
                }
            }
        }else{
            if(r.contains(Mouse.getPoint())){
                Engine.renderRectangle(r, back.brighter());
            }else{
                Engine.renderRectangle(r, back);
            }            
        }
        Engine.renderString(text, r.x + r.width / 2 - text.length() * 3, r.y + r.height / 2 + 4, Color.black);
    }

    public void setText(String t) {
        text = t;
    }
}
