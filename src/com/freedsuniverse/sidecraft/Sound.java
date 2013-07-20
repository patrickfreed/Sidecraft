package com.freedsuniverse.sidecraft;

import java.io.IOException;

import org.newdawn.easyogg.OggClip;

/*currently this class is just a wrapper until I find something useful to do with it*/

public class Sound {
    public static Sound blockDamage = new Sound("/audio/material/sedimentWalk.ogg"), 
            sedimentWalk = new Sound("/audio/material/sedimentWalk.ogg"), 
            rockWalk = new Sound("/audio/material/sedimentWalk.ogg");
   
    private OggClip clip;
    
    public Sound(String file) {
        try {
            clip = new OggClip(Sidecraft.class.getResourceAsStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Sound(OggClip sound) {
        clip = sound;
    }
    
    public void stop() {
        clip.stop();
    }
    
    public void play() {
        if(clip.isPaused() || clip.stopped())
            clip.play();
    }
}
