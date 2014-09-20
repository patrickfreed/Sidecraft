package com.freedsuniverse.sidecraft.engine;

import java.awt.Color;

public class Light {
    public static final Color DARK = new Color(0, 0, 0, 255), WHITE = new Color(0, 0, 0, 255), NOTHING = new Color(0, 0, 0, 0);

    private Color c;

    public static Color mix(Color c1, Color c2) {
        int alpha = 0;

        if (c2.getAlpha() <= c1.getAlpha()) {
            alpha = c2.getAlpha();
        } else {
            alpha = c1.getAlpha();
        }

        return new Color((c1.getRed() + c2.getRed()) / 2, (c1.getGreen() + c2.getGreen()) / 2, (c1.getBlue() + c2.getBlue()) / 2, alpha);
    }

    public Light(Color c, int intensity) {
        this(new Color(c.getRed(), c.getGreen(), c.getBlue(), intensity));
    }

    public Light(Color c) {
        this.c = c;
    }

    public Light() {
        this(NOTHING);
    }

    public Color getColor() {
        return c;
    }

    public void setColor(Color c2) {
        c = c2;
    }
}
