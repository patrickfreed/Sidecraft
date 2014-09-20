package com.freedsuniverse.sidecraft.world.active;

import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.entity.LightSource;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.world.Block;

public class Torch extends Block {
    private LightSource s;

    public Torch() {
        super(Material.TORCH);
        s = new LightSource(0.9, Settings.BLOCK_SIZE * 8);
    }

    public void destroy() {
        s.destroy();
        s = null;
        getLocation().getWorld().lightUpdate();

        super.destroy();
    }

    public void update() {
        super.update();

        if (getLocation() != null && s.getLocation() == null) {
            s.spawn(getLocation());
        }
    }
}
