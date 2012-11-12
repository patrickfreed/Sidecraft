package com.freedsuniverse.sidecraft;

import java.applet.AudioClip;

public class Sound {
    public static Sound blockDamage = new Sound("/audio/block_damage.wav"), 
            blockBrea1k, 
            sedimentWalk = new Sound("/audio/material/sedimentWalk.wav"), 
            rockWalk = new Sound("/audio/material/sedimentWalk.wav");
    
    private AudioClip soundFile;
    private String name;
    
    public Sound(String file){
        name = file;
        soundFile = Sidecraft.newAudioClip(Sound.class.getResource(file));
    }
    
    public Sound(AudioClip sound) {
        name = sound.toString();
        soundFile = sound;
    }
    
    public String getName(){
        return name;
    }
    
    public void play() {
        if(!Settings.SOUND) return;
        
        try {
            new Thread() {
                public void run() {
                    soundFile.play();
                }
            }.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public void stop(){
        soundFile.stop();
    }
}
