package com.freedsuniverse.sidecraft.material.tools;

import com.freedsuniverse.sidecraft.entity.Entity;
import com.freedsuniverse.sidecraft.entity.LivingEntity;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.Tool;
import com.freedsuniverse.sidecraft.world.Block;

public class Pickaxe extends Tool {

    public Pickaxe(Quality q) {
        super(Material.PICKAXE, q);
    }

    @Override
    public int getDamage(Entity v) {
        if (v instanceof Block) {
            Block b = (Block) v;
            if (b.getType().getMaterialType() == Material.ROCK) {
                return b.getType().getDurability() / getQuality().getDamageModifier();
            }
        } else if (v instanceof LivingEntity) {
            LivingEntity l = (LivingEntity) v;
            return (int) (l.getHealth() / 5.0);
        }
        return 1;
    }

    public String toString() {
        return super.toString() + ":" + getQuality().toString();
    }
}
