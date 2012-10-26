package com.freedsuniverse.sidecraft.entity;

import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.world.Location;

public interface Entity {
    void update();

    void draw();

    void destroy();

    void setLocation(Location l);
    
    Location getLocation();

    Rectangle getBounds();
}
