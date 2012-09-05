package com.freedsuniverse.sidecraft.entity;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.input.Key;
import com.freedsuniverse.sidecraft.input.Mouse;
import com.freedsuniverse.sidecraft.inventory.PlayerInventory;
import com.freedsuniverse.sidecraft.inventory.Toolbar;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.world.ActiveBlock;
import com.freedsuniverse.sidecraft.world.Block;
import com.freedsuniverse.sidecraft.world.Location;
import com.freedsuniverse.sidecraft.world.World;
import com.freedsuniverse.sidecraft.world.active.Workbench;

public class Player implements Entity {
    private final double RIGHT_ARM_LENGTH = 3;

    public Rectangle ScreenPosition;

    private Toolbar toolbar;
    private PlayerInventory inventory;

    private BufferedImage texture;

    //private static int milliseconds = 0;
    //private float interval = 250f;

    private int ySpeed, xSpeed, yDirection, xDirection;
    
    private double originalY;

    private MovementState moveState;
    private ActionState action, oldAction;

    final double JUMP_HEIGHT = 1.3;
    final int LEFT = -1, UP = 1, RIGHT = 1, DOWN = -1, STABLE = 0, ARM_LENGTH = 4, IDLE = 0, BUSY = 1;

    private int MOVEMENT_SPEED;
    float startX, startY, currentX, currentY, oldX, oldY;

    private int oldWheelValue, currentWheelValue;

    public String world;

    public Location coordinates;

    private boolean oldIState, oldBState;

    private boolean iState, bState;

    enum MovementState {
        WALKING,
        JUMPING,
        FALLING
    }

    enum ActionState {
        IDLE,
        BUSY
    }

    public Player() {
        MOVEMENT_SPEED = Settings.BLOCK_SIZE / 8;
        ScreenPosition = new Rectangle(Sidecraft.width/2, Sidecraft.height/2, Settings.BLOCK_SIZE, Settings.BLOCK_SIZE);

        texture = Sidecraft.playerTile;
        
        startX = 388f;
        startY = 224f;
        currentX = startX;
        currentY = startY;

        xDirection = STABLE;
        yDirection = STABLE;
        
        oldIState = false;
        iState = false;
        
        oldBState = false;
        bState = false;
        
        coordinates = new Location(0, 0, "world");

        oldWheelValue = Mouse.getScrollWheelValue();

        toolbar = new Toolbar();
        inventory = new PlayerInventory();

        moveState = MovementState.WALKING;
        action = ActionState.IDLE;
        oldAction = action;
    }

    @Override
    public void update() {
        oldWheelValue = currentWheelValue;
        currentWheelValue = Mouse.getScrollWheelValue();
        
        oldIState = iState;
        iState = Key.I.isDown();
        
        oldBState = bState;
        bState = Key.B.isDown();
        
        updateMovement();
        updateCollision();
        updateInteraction();
        updateToolbar();
        updatePosition();
        
        if(oldAction != action){
            oldAction = action;
        }
    }

    private void updateToolbar() {
        if (currentWheelValue < oldWheelValue || (bState && !oldBState)) {
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
            if(iState && !oldIState && oldAction != ActionState.BUSY){
                setAction(BUSY);
                inventory.open();
            }
            else if (Mouse.isDown(MouseEvent.BUTTON1)) {
                Location mouseCoords = Location.valueOf(Mouse.getX(), Mouse.getY());

                if (Math.abs(mouseCoords.getX() - coordinates.getX()) <= 4 && Math.abs(mouseCoords.getY() - coordinates.getY()) <= 4) {
                    Block block = getWorld().getBlockAt(mouseCoords);

                    if (!block.getType().isSolid()) {
                        return;
                    } 

                    getWorld().getBlockAt(mouseCoords).damage(5);
                }
            }
            else if (Mouse.clicked(MouseEvent.BUTTON3)) {
                Location mouseCoords = Location.valueOf(Mouse.getX(), Mouse.getY());
                Block block = getWorld().getBlockAt(mouseCoords);

                if (canPlaceBlock(block)) {
                    if(getToolbar().getSelectedObj().getType() == Material.WORKBENCH){                
                        getWorld().setBlockAt(block.getLocation(), new Workbench());
                    }else{       
                        block.setType(getToolbar().getSelectedObj().getType());
                    }
                    getToolbar().getSelectedObj().modifyAmount(-1);

                    if (getToolbar().getSelectedObj().getAmount() <= 0) {
                        getInventory().setAt(getToolbar().getCurrentIndex(), 0, null);
                    }
                }else{   
                    if(block instanceof ActiveBlock){
                        ActiveBlock activeBlock = (ActiveBlock) block;
                        activeBlock.interact(this);
                    }
                }
            }
        }else{
            if (iState && !oldIState) {
                setAction(IDLE);
                getInventory().close();
            }
            else {
                getInventory().update();
            }
        }
    }

    /*
     * To do: Clean up all of this to match new use of rectangles
     */

    private void updateMovement() {
        Location above = Location.valueOf(ScreenPosition.getCenterX(), ScreenPosition.y + Settings.BLOCK_SIZE / 8);
        
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
                xDirection = STABLE;
                xSpeed = 0;
            }

            int left = ScreenPosition.x;
            int right = (int)ScreenPosition.getMaxX();

            Location leftFoot = Location.valueOf(left + Settings.BLOCK_SIZE / 8, ScreenPosition.getMaxY());
            Location rightFoot = Location.valueOf(right - Settings.BLOCK_SIZE / 8, ScreenPosition.getMaxY());

            //Location rightEar = Location.valueOf(right - Settings.BLOCK_SIZE / 8, ScreenPosition.getTop());
            //Location leftEar = Location.valueOf(left + Settings.BLOCK_SIZE / 8, ScreenPosition.getTop());   

            if (moveState == MovementState.WALKING) {
                if (Key.SPACE.isDown()) {
                    boolean canJump = (getWorld().getBlockAt(leftFoot).getType().isSolid() || getWorld().getBlockAt(rightFoot).getType().isSolid()) && !getWorld().getBlockAt(above).getType().isSolid();
                    if (canJump) {
                        ySpeed = 4;
                        yDirection = UP;
                        moveState = MovementState.JUMPING;
                        originalY = coordinates.getY();
                    }
                }
                else {
                    ySpeed = STABLE;
                    yDirection = STABLE;
                }

            }
        }else{
            ySpeed = STABLE;
            yDirection = STABLE;
            xDirection = STABLE;
            xSpeed = STABLE;
        }
        if (moveState == MovementState.JUMPING && ((coordinates.getY() - this.originalY >= JUMP_HEIGHT) || getWorld().getBlockAt(above).getType().isSolid())) {
            moveState = MovementState.FALLING;
            ySpeed = 4;
            yDirection = DOWN;
            originalY = 0;
        }
    }

    private void updateCollision() {
        Location leftFoot = Location.valueOf(ScreenPosition.x + (int)Math.floor((double)Settings.BLOCK_SIZE / 16), ScreenPosition.getMaxY());
        Location rightFoot = Location.valueOf(ScreenPosition.getMaxX() - (int)Math.floor((double)Settings.BLOCK_SIZE / 16), ScreenPosition.getMaxY());

        Block left = getWorld().getBlockAt(leftFoot);
        Block right = getWorld().getBlockAt(rightFoot);

        if (moveState == MovementState.WALKING) {
            if ((!left.getType().isSolid() && !right.getType().isSolid())) {
                ySpeed = 4;
                yDirection = DOWN;
                moveState = MovementState.FALLING;
            }
        }
        else if (moveState == MovementState.FALLING) {
            if (left.getType().isSolid() || right.getType().isSolid()) {
                ySpeed = 0;
                moveState = MovementState.WALKING;
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
            botBlock = getWorld().getBlockAt(bottom);

            if (sideBlock.getType().isSolid() || topBlock.getType().isSolid() || (botBlock.getType().isSolid() && moveState == MovementState.FALLING)) {
                xDirection = STABLE;
            }
        }
    }

    private void updatePosition() {
        oldX = currentX;
        oldY = currentY;

        currentX += xSpeed * xDirection;
        currentY += ySpeed * yDirection;

        coordinates.modifyX((currentX - oldX) / 32);
        coordinates.modifyY((currentY - oldY) / 32);
    }

    public void setAction(int i){
        oldAction = action;
        
        if(i == IDLE){
            action = ActionState.IDLE;
        }else if(i == BUSY){
            action = ActionState.BUSY;
        }
    }
    
    public void draw() {
        Engine.render(coordinates, Settings.BLOCK_SIZE, Settings.BLOCK_SIZE, texture);
        
        MaterialStack itemInHand = getInventory().getAt(getToolbar().getCurrentIndex(), 0);

        if(itemInHand != null){
            Engine.render(new Rectangle((int)ScreenPosition.getMaxX(), ScreenPosition.y, 16, 16), itemInHand.getType().getImage());
        }

        Location mouseCoords = Location.valueOf(Mouse.getX(), Mouse.getY());

        toolbar.Draw();
        getInventory().draw();
        
        if(action != ActionState.BUSY){
            if (Math.abs(mouseCoords.getX() - coordinates.getX()) <= 4 && Math.abs(mouseCoords.getY() - coordinates.getY()) <= 4)
                Engine.render(getWorld().getBlockAt(mouseCoords).getLocation(), Settings.BLOCK_SIZE, Settings.BLOCK_SIZE, Sidecraft.selectionTile);
        }
    }

    public PlayerInventory getInventory() {
        return this.inventory;
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }

    public World getWorld() {
        return Sidecraft.worlds.get(world);
    }

    private boolean canPlaceBlock(Block block) {
        return getToolbar().getSelectedObj() != null && !block.getType().isSolid();
    }

    public Rectangle getBounds() {
        return ScreenPosition;
    }

    public Location getLocation() {
        return this.coordinates;
    }

    public BufferedImage getSkin() {
        return this.texture;
    }

    public void destroy() {
        //Todo
    }
}
