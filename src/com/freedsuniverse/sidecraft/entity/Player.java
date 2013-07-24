package com.freedsuniverse.sidecraft.entity;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.engine.Animation;
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
import com.freedsuniverse.sidecraft.world.Location;
import com.freedsuniverse.sidecraft.world.World;
import com.freedsuniverse.sidecraft.world.active.Torch;
import com.freedsuniverse.sidecraft.world.active.Workbench;

public class Player extends Entity{

    private final double RIGHT_ARM_LENGTH = 3;

    private Toolbar toolbar;
    private EntityInventory inventory;

    private BufferedImage texture;

    private int ySpeed, xSpeed, yDirection, xDirection;

    private double originalY;

    private MovementState moveState;
    private ActionState action, oldAction;

    private Rectangle rec;

    private Animation[] animations;

    final double JUMP_HEIGHT = 1.3;
    final int ATTACK_DAMAGE = 1;
    final int LEFT = -1, UP = 1, RIGHT = 1, DOWN = -1, STABLE = 0, ARM_LENGTH = 4, IDLE = 0, BUSY = 1;

    private static final double STEP_LENGTH = 3.0;
    
    private int MOVEMENT_SPEED;

    private long lastAttack;

    private boolean isAttacking = false, canDamage = true;

    private int oldWheelValue, currentWheelValue, currentAnimation;

    //private LightSource lantern;
    
    public String world;

    private int spaceMoved;

    enum MovementState {
        WALKING,
        JUMPING,
        FALLING
    }

    enum ActionState {
        IDLE,
        BUSY
    }
    
    public void load(String w) {
        load(new Location(0, 0, w), new EntityInventory(this));
    }

    public void load(Location l, EntityInventory inv) {
        MOVEMENT_SPEED = Settings.BLOCK_SIZE / 8;

        this.setLocation(l);
        rec = new Rectangle(Main.contentPane.getWidth() / 2, Main.contentPane.getHeight() / 2, Settings.BLOCK_SIZE, Settings.BLOCK_SIZE);

        texture = Main.playerTile;
        loadTextures();

        xDirection = STABLE;
        yDirection = STABLE;      

        oldWheelValue = Mouse.getScrollWheelValue();

        toolbar = new Toolbar();
        inventory = inv;
        inv.add(new MaterialStack(Material.GRASS, 64));

        moveState = MovementState.WALKING;
        action = ActionState.IDLE;
        oldAction = action;
        
        //lantern = new LightSource(Color.white, 255, 5);
        //lantern.setLocation(getLocation());
        //lantern.getLocation().getWorld().registerEntity(lantern);
    }

    private void loadTextures(){
        MovementState[] stuff = MovementState.values();
        animations = new Animation[stuff.length];
        for(int x = 0; x < stuff.length; x++){
            animations[x] = new Animation(Animation.read("/player/texture/" + stuff[x].toString().toLowerCase() + "/" + stuff[x].toString().toLowerCase() + ".png", 32, 64), 7);
        }
        currentAnimation = 0;
    }

    @Override
    public void update() {
        if(isAttacking){            
            if(System.currentTimeMillis() - this.lastAttack >= this.getAttackSpeed()){
                canDamage = true;
                isAttacking = false;
            }
        }

        oldWheelValue = currentWheelValue;
        currentWheelValue = Mouse.getScrollWheelValue();
        
        updateMovement();
        updateCollision();
        updateInteraction();
        updateToolbar();
        updatePosition(xDirection, yDirection, xSpeed, ySpeed);
        //lantern.setLocation(getLocation());
        animations[currentAnimation].update();

        if(oldAction != action){
            oldAction = action;
        }
    }

    public void updatePosition(int xDirection, int yDirection, int xSpeed, int ySpeed){
        double xDiff = (xDirection * xSpeed);
        double yDiff = (yDirection * ySpeed);

        getLocation().modifyX(xDiff / 32);
        getLocation().modifyY(yDiff / 32);
               
        spaceMoved += Math.abs(xDiff);
        if(getLocation().modify(0, -1).getBlockAt().getType().isSolid()){
            if(spaceMoved > STEP_LENGTH * 32){
                Material.getSound(getLocation().getBlockAt().getTypeId()).play();
                spaceMoved = 0;
            }
        }
        
        if(xDiff != 0 || yDiff != 0) {
            getWorld().lightUpdate();
        }
    }

    private long getAttackSpeed() {       
        return 500;
    }

    private void updateToolbar() {
        if (currentWheelValue < oldWheelValue || (Key.B.toggled())) {
            getToolbar().addToCurrentIndex(1);
        }
        else if (currentWheelValue > oldWheelValue) {
            getToolbar().addToCurrentIndex(-1);
        } else {
            if (Key.ONE.isDown()) getToolbar().setCurrentIndex(0);
            else if (Key.TWO.isDown()) getToolbar().setCurrentIndex(1);
            else if (Key.THREE.isDown()) getToolbar().setCurrentIndex(2);
            else if (Key.FOUR.isDown()) getToolbar().setCurrentIndex(3);
            else if (Key.FIVE.isDown()) getToolbar().setCurrentIndex(4);
        }
    }

    private void updateInteraction() {
        if (action != ActionState.BUSY) {
            
            if(Key.I.toggled() && oldAction != ActionState.BUSY){
                setAction(BUSY);
                inventory.open();
            }
            else if (Mouse.isDown(MouseEvent.BUTTON1)) {
                Location mouseCoords = Location.valueOf(Mouse.getX(), Mouse.getY());
                
                if (Math.abs(mouseCoords.getX() - getLocation().getX()) <= 4 && Math.abs(mouseCoords.getY() - getLocation().getY()) <= 4) {
                
                ArrayList<Entity> es = getLocation().getWorld().getNearbyEntities(mouseCoords, 2);
                
                for(Entity e:es) {
                    if(e != null && e.getBounds().contains(Mouse.getPoint())) {
                        attack(e);
                        
                    }
                }
                    Block block = getWorld().getBlockAt(mouseCoords);

                    if (!block.getType().isSolid()) {
                        return;
                    } 
                    
                    //System.out.println(block.getLight().getColor().getAlpha());
                    attack(getWorld().getBlockAt(mouseCoords));
                 }
            }
            else if (Mouse.clicked(MouseEvent.BUTTON3)) {
                Location mouseCoords = Location.valueOf(Mouse.getX(), Mouse.getY());
                Block block = getWorld().getBlockAt(mouseCoords);

                if (canPlaceBlock(block)) {
                    if(getToolbar().getSelectedObj().getType() == Material.WORKBENCH){                
                        getWorld().setBlockAt(block.getLocation(), new Workbench());
                    }else if(getToolbar().getSelectedObj().getType() == Material.GRASS) {
                        getWorld().setBlockAt(block.getLocation(), new Torch());
                    }else{       
                        block.setType(getToolbar().getSelectedObj().getType());
                    }
                    getToolbar().getSelectedObj().setAmount(getToolbar().getSelectedObj().getAmount() - 1);

                    if (getToolbar().getSelectedObj().getAmount() <= 0) {
                        getInventory().setAt(getToolbar().getCurrentIndex(), 0, null);
                    }
                }else{   
                    block.interact(this);
                }
            }
        }else{
            if (Key.I.toggled()) {
                setAction(IDLE);
                getInventory().close();
            }
            else {
                getInventory().update();
            }
        }
    }

    private void updateMovement() {
        Location above = Location.valueOf(getBounds().getCenterX(), getBounds().y + Settings.BLOCK_SIZE / 8);

        if(action != ActionState.BUSY){
            if (Key.A.isDown() || Key.S.isDown()) {
                xSpeed = MOVEMENT_SPEED;
                xDirection = LEFT;
            }
            else if (Key.W.isDown() || Key.D.isDown()) {
                xSpeed = MOVEMENT_SPEED;
                xDirection = RIGHT;
            }
            else {  
                xSpeed = 0;
            }

            int left = getBounds().x;
            int right = (int)getBounds().getMaxX();

            Location leftFoot = Location.valueOf(left + Settings.BLOCK_SIZE / 8, getBounds().getMaxY());
            Location rightFoot = Location.valueOf(right - Settings.BLOCK_SIZE / 8, getBounds().getMaxY());

            //Location rightEar = Location.valueOf(right - Settings.BLOCK_SIZE / 8, getBounds().getTop());
            //Location leftEar = Location.valueOf(left + Settings.BLOCK_SIZE / 8, getBounds().getTop());   

            if (moveState == MovementState.WALKING) {
                if (Key.SPACE.isDown()) {
                    boolean canJump = (getWorld().getBlockAt(leftFoot).getType().isSolid() || getWorld().getBlockAt(rightFoot).getType().isSolid()) && !getWorld().getBlockAt(above).getType().isSolid();
                    if (canJump) {
                        ySpeed = 4;
                        yDirection = UP;
                        setMovement(MovementState.JUMPING);
                        originalY = getLocation().getY();
                    }
                }
                else {
                    ySpeed = STABLE;
                    yDirection = STABLE;
                }

            }
        }else{
            xDirection = STABLE;
            xSpeed = STABLE;
        }
        if (moveState == MovementState.JUMPING && ((getLocation().getY() - this.originalY >= JUMP_HEIGHT) || getWorld().getBlockAt(above).getType().isSolid())) {
            setMovement(MovementState.FALLING);
            ySpeed = 4;
            yDirection = DOWN;
            originalY = 0;
        }
    }

    private void updateCollision() {
        Location leftFoot = Location.valueOf(getBounds().x + (int)Math.floor((double)Settings.BLOCK_SIZE / 16), getBounds().getMaxY());
        Location rightFoot = Location.valueOf(getBounds().getMaxX() - (int)Math.floor((double)Settings.BLOCK_SIZE / 16), getBounds().getMaxY());

        Block left = getWorld().getBlockAt(leftFoot);
        Block right = getWorld().getBlockAt(rightFoot);

        if (moveState == MovementState.WALKING) {
            if ((!left.getType().isSolid() && !right.getType().isSolid())) {
                ySpeed = 4;
                yDirection = DOWN;
                setMovement(MovementState.FALLING);
            }
        }
        else if (moveState == MovementState.FALLING) {
            if (left.getType().isSolid() || right.getType().isSolid()) {
                ySpeed = 0;
                setMovement(MovementState.WALKING);
            }
        }

        Block sideBlock, topBlock, botBlock;
        Location top, side, bottom; 

        if (xDirection != STABLE) {
            if (xDirection == RIGHT) {
                side = Location.valueOf(getBounds().getMaxX() + RIGHT_ARM_LENGTH, getBounds().getCenterY());
                top = Location.valueOf(getBounds().getMaxX() + RIGHT_ARM_LENGTH, getBounds().y);
                bottom = Location.valueOf(getBounds().getMaxX() + RIGHT_ARM_LENGTH, getBounds().getMaxY());
            }
            else { //left
                side = Location.valueOf(getBounds().x - Settings.BLOCK_SIZE / 8, getBounds().getCenterY());
                top = Location.valueOf(getBounds().x - Settings.BLOCK_SIZE / 8, getBounds().y);
                bottom = Location.valueOf(getBounds().x - Settings.BLOCK_SIZE / 8, getBounds().getMaxY());
            }

            sideBlock = getWorld().getBlockAt(side);
            topBlock = getWorld().getBlockAt(top);
            botBlock = getLocation().getWorld().getBlockAt(bottom);

            if (sideBlock.getType().isSolid() || topBlock.getType().isSolid() || (botBlock.getType().isSolid() && moveState == MovementState.FALLING)) {
                xSpeed = STABLE;
            }
        }
    }

    public void setAction(int i){
        oldAction = action;

        if(i == IDLE){
            action = ActionState.IDLE;
        }else if(i == BUSY){
            action = ActionState.BUSY;
        }
    }

    public void setMovement(MovementState s){
        moveState = s;
        currentAnimation = s.ordinal();
    }

    @Override
    public void draw() {
        Engine.render(getLocation(), Settings.BLOCK_SIZE, Settings.BLOCK_SIZE, getSkin());

        Item itemInHand = getInventory().getAt(getToolbar().getCurrentIndex(), 0);

        if(itemInHand != null){
            //Engine.render(new Rectangle((int)getBounds().getMaxX(), getBounds().y, 16, 16), itemInHand.getType().getImage());
        }

        Location mouseCoords = Location.valueOf(Mouse.getX(), Mouse.getY());

        toolbar.draw();
        getInventory().draw();

        if(action != ActionState.BUSY){
            if (Math.abs(mouseCoords.getX() - getLocation().getX()) <= 4 && Math.abs(mouseCoords.getY() - getLocation().getY()) <= 4)
                if(!Main.getGame().isPaused())
                    Engine.render(mouseCoords.getWorld().getBlockAt(mouseCoords).getLocation(), Settings.BLOCK_SIZE, Settings.BLOCK_SIZE, Main.selectionTile);
        }
    }

    public EntityInventory getInventory() {
        return this.inventory;
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }

    @Deprecated
    public World getWorld() {
        return getLocation().getWorld();
    }

    private boolean canPlaceBlock(Block block) {
        return getToolbar().getSelectedObj() != null 
                && !block.getType().isSolid() && Math.abs(block.getLocation().getX() - getLocation().getX()) <= 4 
                && Math.abs(block.getLocation().getY() - getLocation().getY()) <= 4
                && !(getToolbar().getSelectedObj() instanceof Tool);
    }

    public BufferedImage getSkin() {
        if(xSpeed == 0 && ySpeed == 0){
            if(xDirection == LEFT) {
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-this.texture.getWidth(null), 0);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                return op.filter(this.texture, null); 
            }
            return this.texture;
        }else{
            if(xDirection == RIGHT){
                return animations[currentAnimation].getSlide();
            }else if(xDirection == LEFT){
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-animations[currentAnimation].getSlide().getWidth(null), 0);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                return op.filter(animations[currentAnimation].getSlide(), null);           
            }
        }
        return texture;
    }

    public void attack(Entity e){
        if(!canDamage) return;

        if(e instanceof Block){
            Block b = (Block) e;
            b.damage(getDamage(e));
            isAttacking = true;
            lastAttack = System.currentTimeMillis();
            canDamage = false;
        }else if(e instanceof LivingEntity) {
            ((LivingEntity) e).damage(2);
            isAttacking = true;
            lastAttack = System.currentTimeMillis();
            canDamage = false;
        }
    }

    private int getDamage(Entity e) {
        Item mat = this.getToolbar().getSelectedObj();
        
        if(mat != null){
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
        //TODO: implement
    }
}
