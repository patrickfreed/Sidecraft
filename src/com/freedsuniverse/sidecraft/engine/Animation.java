package com.freedsuniverse.sidecraft.engine;

import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.Sidecraft;

public class Animation {
    private BufferedImage[] imgs;
    private int frequency, currentTick, currentIndex;
    
    public static BufferedImage[] read(String dir, int width, int height){
        BufferedImage img = Sidecraft.getImage(dir);
        BufferedImage[] results = new BufferedImage[img.getWidth() / width * img.getHeight() / height];
        
        int c = 0;
        for(int y = 0; y < img.getHeight() / height; y++) {
            for(int x = 0; x < img.getWidth() / width; x++) {                
                results[c] = img.getSubimage(x * width, y * height, width, height);
                c++;
            }
        }
        
        return results;
    }
    
    public Animation(BufferedImage[] i, int f){
        imgs = i;
        frequency = f;
        currentTick = 0;
        currentIndex = 0;
    }
    
    public BufferedImage[] getData() {
        return imgs;
    }
    
    public void update(){
        currentTick++;
        if(currentTick == frequency){
            currentIndex++;
            currentTick = 0;
        }    
        
        if(currentIndex > imgs.length - 1){
            currentIndex = 0;
        }
    }
    
    public BufferedImage getSlide(){
        return imgs[currentIndex];
    }
}
