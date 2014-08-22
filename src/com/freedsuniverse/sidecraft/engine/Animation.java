package com.freedsuniverse.sidecraft.engine;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.Sidecraft;

public class Animation {
    private BufferedImage[] imgs;
    private int frequency, currentTick, currentIndex, length;
    
    public static BufferedImage flip(BufferedImage i) {       
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-i.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(i, null);
    }
    
    public static Animation read(String dir, int width, int height, int f){
        BufferedImage img = Sidecraft.getImage(dir);
        BufferedImage[] results = new BufferedImage[2 * img.getWidth() / width * img.getHeight() / height];
        
        int c = 0;
        for(int y = 0; y < img.getHeight() / height; y++) {
            for(int x = 0; x < img.getWidth() / width; x++) {                
                results[c] = img.getSubimage(x * width, y * height, width, height);
                c++;
            }
        }
        
        for(int x = 0; x < results.length / 2; x++) {                
            results[c] = flip(results[x]);
            c++;
        }
        
        return new Animation(results, f);
    }
    
    public Animation(BufferedImage[] i, int f){
        imgs = i;
        frequency = f;
        currentTick = 0;
        currentIndex = 0;
        length = i.length / 2;
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
        
        if(currentIndex > length - 1){
            currentIndex = 0;
        }
    }
    
    public BufferedImage getSlide(boolean flip) {
        
        if(flip) {
            return imgs[currentIndex + length];
        }
        
        return getSlide();
    }
    
    public BufferedImage getSlide(){
        return imgs[currentIndex];
    }
}
