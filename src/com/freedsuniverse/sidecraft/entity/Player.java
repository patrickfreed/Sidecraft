package com.freedsuniverse.sidecraft.entity;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.input.Key;
import com.freedsuniverse.sidecraft.input.Mouse;
import com.freedsuniverse.sidecraft.inventory.EntityInventory;
import com.freedsuniverse.sidecraft.inventory.Toolbar;
import com.freedsuniverse.sidecraft.material.Item;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.material.Tool;
import com.freedsuniverse.sidecraft.world.Block;
import com.freedsuniverse.sidecraft.world.GameWorld;
import com.freedsuniverse.sidecraft.world.Location;
import com.freedsuniverse.sidecraft.world.active.Torch;
import com.freedsuniverse.sidecraft.world.active.Workbench;

public class Player extends LivingEntity {

    private final static int ATTACK_DAMAGE = 1;
    private final static float WALKING_SPEED = 4.75f;
    private final static long ATTACK_SPEED = 500;

    private Toolbar toolbar;

    private EntityInventory inventory;

    private ActionState action;
    private ActionState oldAction;

    private Rectangle rec;

    private long lastAttack;

    private boolean isAttacking = false;
    private boolean canDamage = true;

    private int oldWheelValue;
    private int currentWheelValue;

    public enum MovementState {
        WALKING, JUMPING, FALLING
    }

    public enum ActionState {
        IDLE, BUSY
    }

    public Player() {
        super("player", 32, 64, 10, 15);
    }

    public void spawn(Location l) {
        spawn(l, new EntityInventory(this));
    }

    public void spawn(String w) {
        spawn(new Location(0, 1, w));
    }

    public void spawn(Location l, EntityInventory inv) {
        super.spawn(l);

        this.setLocation(l);
        rec = new Rectangle(Main.contentPane.getWidth() / 2 + Settings.BLOCK_SIZE / 2, Main.contentPane.getHeight() / 2 + Settings.BLOCK_SIZE / 2, Settings.BLOCK_SIZE, Settings.BLOCK_SIZE * 2);

        oldWheelValue = Mouse.getScrollWheelValue();

        toolbar = new Toolbar(inv);
        inventory = inv;
        inv.add(new MaterialStack(Material.SILVER_ORE, 64));
        inv.add(new MaterialStack(Material.STONE, 64));
        inv.add(new MaterialStack(Material.TORCH, 64));

        action = ActionState.IDLE;
        oldAction = action;
    }

    // private void loadTextures(){
    // MovementState[] stuff = MovementState.values();
    // animations = new Animation[stuff.length];
    // for(int x = 0; x < stuff.length; x++){
    // animations[x] = new Animation(Animation.read("/player/texture/" +
    // stuff[x].toString().toLowerCase() + "/" +
    // stuff[x].toString().toLowerCase() + ".png", 32, 64), 10);
    // }
    // currentAnimation = 0;
    // }

    @Override
    public void update() {
        super.update();

        if (isAttacking) {
            if (System.currentTimeMillis() - this.lastAttack >= this.getAttackSpeed()) {
                canDamage = true;
                isAttacking = false;
            }
        }

        oldWheelValue = currentWheelValue;
        currentWheelValue = Mouse.getScrollWheelValue();

        getLocation().setCoordinates(b.getPosition().x, b.getPosition().y);
        updateInteraction();
        updateToolbar();
        updateMovement();

        if (oldAction != action) {
            oldAction = action;
        }
    }

    private long getAttackSpeed() {
        return ATTACK_SPEED; // Attacks per second
    }

    private void updateMovement() {
        if (Key.W.isDown() && footContacts > 0) {
            float impulse = 0;
            float intendedV = WALKING_SPEED;

            // if(footContacts <= 0) {
            // intendedV = WALKING_SPEED / 4;
            // }

            float dV = intendedV - this.b.getLinearVelocity().x;

            impulse = this.getBody().getMass() * dV;
            this.getBody().applyLinearImpulse(new Vec2(impulse, 0), this.getBody().getWorldCenter());
            // if(footContacts <= 0) {
            // this.b.getLinearVelocity().x = WALKING_SPEED / 4.0f;
            // }

            // this.b.getLinearVelocity().x = WALKING_SPEED;
        } else if (Key.S.isDown() && this.footContacts > 0) {
            float impulse = 0;
            float intendedV = -WALKING_SPEED;

            // if(footContacts <= 0) {
            // intendedV = WALKING_SPEED / 4;
            // }

            float dV = intendedV - this.b.getLinearVelocity().x;

            impulse = this.getBody().getMass() * dV;
            this.getBody().applyLinearImpulse(new Vec2(impulse, 0), this.getBody().getWorldCenter());
        }

        if (Key.SPACE.toggled() && this.footContacts > 0) {
            this.b.getLinearVelocity().y = 6.0f;
        }
    }

    private void updateToolbar() {
        if (currentWheelValue < oldWheelValue) {
            getToolbar().addToCurrentIndex(1);
        } else if (currentWheelValue > oldWheelValue) {
            getToolbar().addToCurrentIndex(-1);
        } else {
            if (Key.ONE.isDown())
                getToolbar().setCurrentIndex(0);
            else if (Key.TWO.isDown())
                getToolbar().setCurrentIndex(1);
            else if (Key.THREE.isDown())
                getToolbar().setCurrentIndex(2);
            else if (Key.FOUR.isDown())
                getToolbar().setCurrentIndex(3);
            else if (Key.FIVE.isDown())
                getToolbar().setCurrentIndex(4);
        }
    }

    private void updateInteraction() {
        if (action != ActionState.BUSY) {
            if (Key.I.toggled() && oldAction != ActionState.BUSY) {
                setAction(ActionState.BUSY);
                inventory.open();
            } else if (Mouse.isDown(MouseEvent.BUTTON1)) {
                Location mouseCoords = Mouse.getLocation();

                if (Math.abs(mouseCoords.getX() - getLocation().getX()) <= 4 && Math.abs(mouseCoords.getY() - getLocation().getY()) <= 4) {

                    ArrayList<Entity> es = getLocation().getWorld().getNearbyEntities(mouseCoords, 2);

                    for (Entity e : es) {
                        if (e != null && !(e instanceof Player) && e.getBounds().contains(Mouse.getPoint())) {
                            attack(e);

                        }
                    }
                    Block block = getLocation().getWorld().getBlockAt(mouseCoords);

                    if (block.getType().getDurability() > 0) {
                        attack(getLocation().getWorld().getBlockAt(mouseCoords));
                    }
                }
            } else if (Mouse.clicked(MouseEvent.BUTTON3)) {
                Location mouseCoords = Mouse.getLocation();
                Block block = getWorld().getBlockAt(mouseCoords);

                if (canPlaceBlock(block)) {
                    if (getToolbar().getSelectedObj().getType() == Material.WORKBENCH) {
                        getLocation().getWorld().setBlockAt(block.getLocation(), new Workbench());
                    } else if (getToolbar().getSelectedObj().getType() == Material.TORCH) {
                        getLocation().getWorld().setBlockAt(block.getLocation(), new Torch());
                    } else {
                        block.setType(getToolbar().getSelectedObj().getType());
                    }
                    getToolbar().getSelectedObj().setAmount(getToolbar().getSelectedObj().getAmount() - 1);

                    if (getToolbar().getSelectedObj().getAmount() <= 0) {
                        getInventory().setAt(getToolbar().getCurrentIndex(), 0, null);
                    }
                } else {
                    block.interact(this);
                }
            }
        } else {
            if (Key.I.toggled()) {
                setAction(ActionState.IDLE);
                getInventory().close();
            } else {
                getInventory().update();
            }
        }
    }

    public void setAction(ActionState a) {
        oldAction = action;
        action = a;
    }

    @Override
    public void draw() {
        super.draw();

        Item itemInHand = getInventory().getAt(getToolbar().getCurrentIndex(), 0);

        if (itemInHand != null) {
            // Engine.render(new Rectangle((int)getBounds().getMaxX(),
            // getBounds().y, 16, 16), itemInHand.getType().getImage());
        }

        Location mouseCoords = Mouse.getLocation();

        if (!getInventory().isOpen()) {
            toolbar.draw();
        } else {
            getInventory().draw();
        }

        if (action != ActionState.BUSY) {
            if (Math.abs(mouseCoords.getX() - getLocation().getX()) <= 4 && Math.abs(mouseCoords.getY() - getLocation().getY()) <= 4)
                if (!Main.getGame().isPaused())
                    Engine.render(mouseCoords.getWorld().getBlockAt(mouseCoords).getLocation(), Main.selectionTile);
        }
    }

    public EntityInventory getInventory() {
        return this.inventory;
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }

    @Deprecated
    public GameWorld getWorld() {
        return getLocation().getWorld();
    }

    private boolean canPlaceBlock(Block block) {
        return getToolbar().getSelectedObj() != null && block.getType() == Material.AIR && Math.abs(block.getLocation().getX() - getLocation().getX()) <= 4
                && Math.abs(block.getLocation().getY() - getLocation().getY()) <= 4 && !(getToolbar().getSelectedObj() instanceof Tool);
    }

    // @SuppressWarnings("unused")
    // public BufferedImage getSkin1() {
    // if(this.b.getLinearVelocity().x == 0){
    // if(false) {
    // AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
    // tx.translate(-this.texture.getWidth(null), 0);
    // AffineTransformOp op = new AffineTransformOp(tx,
    // AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    // return op.filter(this.texture, null);
    // }
    // return this.texture;
    // }else{
    // if(this.b.getLinearVelocity().x > 0){
    // return animations[currentAnimation].getSlide();
    // }else {
    // AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
    // tx.translate(-animations[currentAnimation].getSlide().getWidth(null), 0);
    // AffineTransformOp op = new AffineTransformOp(tx,
    // AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    // return op.filter(animations[currentAnimation].getSlide(), null);
    // }
    // }
    // }

    public void attack(Entity e) {
        if (!canDamage)
            return;

        if (e instanceof Block) {
            Block b = (Block) e;
            b.damage(getDamage(e));
            isAttacking = true;
            lastAttack = System.currentTimeMillis();
            canDamage = false;
        } else if (e instanceof LivingEntity) {
            ((LivingEntity) e).damage(ATTACK_DAMAGE);
            isAttacking = true;
            lastAttack = System.currentTimeMillis();
            canDamage = false;
        }
    }

    private int getDamage(Entity e) {
        Item mat = this.getToolbar().getSelectedObj();

        if (mat != null) {
            return mat.getDamage(e);
        }
        return ATTACK_DAMAGE;
    }

    @Override
    public Rectangle getBounds() {
        return rec;
    }

    @Override
    public void destroy() {
        // TODO: implement
    }
}