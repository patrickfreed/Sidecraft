package com.freedsuniverse.sidecraft.world.active;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.engine.RenderQueueItem;
import com.freedsuniverse.sidecraft.entity.DropEntity;
import com.freedsuniverse.sidecraft.entity.Entity;
import com.freedsuniverse.sidecraft.input.Key;
import com.freedsuniverse.sidecraft.input.Mouse;
import com.freedsuniverse.sidecraft.inventory.PlayerInventory;
import com.freedsuniverse.sidecraft.inventory.Slot;
import com.freedsuniverse.sidecraft.material.CraftingRecipe;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.world.Block;

public class Workbench extends Block{    
    private int x,y;
    
    private Rectangle OUTPUT;
    
    final int X_DIFF = 13;
    final int Y_DIFF = 254;
    
    private boolean open = false;
    
    private Slot[][] craftingArea;
    private Slot outputSlot;
    
    private MaterialStack onMouse;

    public Workbench() {
        super(Material.WORKBENCH);
    }

    @Override
    public void interact(Entity e){    
        if(!open){
            Sidecraft.player.setAction(1);
            x = Sidecraft.width / 2 - Sidecraft.inventoryTile.getWidth() / 2;
            y = Sidecraft.height / 2 - Sidecraft.inventoryTile.getHeight() / 2;
            OUTPUT = new Rectangle(x + Sidecraft.workbenchTile.getWidth() - 49, y + 53, 31, 31);
            outputSlot = new Slot(null, OUTPUT);
            initiateBench();
        }else{
            Sidecraft.player.setAction(0);
            
            for(int x = 0; x < craftingArea.length; x++){
                for(int y =0; y < craftingArea[0].length; y++){
                    if(craftingArea[x][y].getContent() != null){
                        new DropEntity(craftingArea[x][y].getContent().getType(), this.getLocation()).spawn();
                        craftingArea[x][y].setContent(null);
                    }
                }
            }
            outputSlot.setContent(null);
        }
        open = !open;  
    }
    
    private void initiateBench(){
        if(craftingArea == null){
            craftingArea = new Slot[3][3];
            
            int xDiff = 39, originalX = x + 70;
            int yDiff = 38, originalY = y + 12;
            
            for(int x = 0; x < craftingArea.length; x++){
                for (int y = 0; y < craftingArea[0].length; y++){
                    craftingArea[x][y] = new Slot(null, new Rectangle(originalX + (x * xDiff), originalY + (yDiff * y), 31, 31));
                }
            }       
        }
    }
    
    @Override
    public void draw(){
        super.draw();
        
        if(open){
            Engine.addQueueItem(new RenderQueueItem(x, y, Sidecraft.workbenchTile));            
            
            for (int y = 0; y < Sidecraft.player.getInventory().getContents()[0].length; y++) {
                for (int x = 0; x < Sidecraft.player.getInventory().getContents().length; x++) {
                    Rectangle box = new Rectangle(this.x + X_DIFF + (38 * x), this.y + Y_DIFF - (38 * y) + 1, 30, 30);
                    if (Sidecraft.player.getInventory().getContents()[x][y] != null) {
                        Sidecraft.player.getInventory().getAt(x,y).draw(box, true);
                        //Engine.addQueueItem(new RenderQueueItem(Sidecraft.player.getInventory().getContents()[x][y].getType().getImage(), box));
                        //Engine.addQueueItem(new RenderQueueItem(String.valueOf(Sidecraft.player.getInventory().getAt(x, y).getAmount()), (int)box.getCenterX() + 5, (int)box.getCenterY() - 1, Color.WHITE));                        
                    }
                    if(box.contains(Mouse.getPoint())){
                        Engine.addQueueItem(new RenderQueueItem(box, PlayerInventory.HOVER_COLOR));
                    }
                }
            }
            
            for(int x = 0; x < craftingArea.length; x++){
                for(int y = 0; y < craftingArea[0].length; y++){
                    if(craftingArea[x][y].getContent() != null){
                        craftingArea[x][y].getContent().draw(craftingArea[x][y].getBounds(), true);
                        //Engine.addQueueItem(new RenderQueueItem(craftingArea[x][y].getContent().getType().getImage(), craftingArea[x][y].getBounds()));
                        //Engine.addQueueItem(new RenderQueueItem(String.valueOf(craftingArea[x][y].getContent().getAmount()), craftingArea[x][y].getBounds().x + 25, craftingArea[x][y].getBounds().y, Color.BLACK));
                    }
                    if(craftingArea[x][y].getBounds().contains(Mouse.getPoint())){
                        Engine.addQueueItem(new RenderQueueItem(craftingArea[x][y].getBounds(), PlayerInventory.HOVER_COLOR));
                    }
                }
            }
            
            if(outputSlot.getContent() != null){
                //Engine.addQueueItem(new RenderQueueItem(outputSlot.getContent().getType().getImage(), OUTPUT));
                //Engine.addQueueItem(new RenderQueueItem(String.valueOf(outputSlot.getContent().getAmount()), OUTPUT.x + 25, OUTPUT.y, Color.BLACK));
                outputSlot.getContent().draw(OUTPUT, true);
            }
            
            if (onMouse != null) {
                Engine.addQueueItem(new RenderQueueItem(Mouse.getX(), Mouse.getY(), onMouse.getType().getImage()));
            }
        }
    }
    
    //TODO: Make mor prity
    @Override
    public void update(){
        if(open){
            if(Key.I.toggled()){
                interact(null);
            }else{
                updateContents();
            }
        }
    }
    
    private void updateCrafting() {
        MaterialStack outcome = CraftingRecipe.getMatch(this.getRecipeData());
        
        this.outputSlot.setContent(outcome);
    }

    private void updateContents(){
        if(Mouse.clicked(MouseEvent.BUTTON1) || Mouse.clicked(MouseEvent.BUTTON3)){
            if(getSelectedIndecies()[0] != -1){
                int index[] = getSelectedIndecies();
                MaterialStack[] handledMaterials = PlayerInventory.handleSlot(Sidecraft.player.getInventory().getAt(index[0], index[1]), onMouse);
                
                Sidecraft.player.getInventory().setAt(index[0], index[1], handledMaterials[0]);
                onMouse = handledMaterials[1];
            }else if(getWorkbenchSlot() != null){
                Slot slot = getWorkbenchSlot();
                MaterialStack[] handledMaterials = PlayerInventory.handleSlot(slot.getContent(), onMouse);
                
                slot.setContent(handledMaterials[0]);
                onMouse = handledMaterials[1];
                updateCrafting();
            }else if(outputSlot.getBounds().contains(Mouse.getPoint())){
                if(outputSlot.getContent() != null){
                    onMouse = outputSlot.getContent();
                    outputSlot.setContent(null);
                    clearCraftingAreaFromRecipe();               
                }
            }
        }
    }
    
    public void clearCraftingAreaFromRecipe(){       
        for(int x = 0; x < craftingArea.length; x++){
            for(int y = 0; y < craftingArea[0].length; y++){
                if(craftingArea[x][y].getContent() != null){
                    craftingArea[x][y].getContent().modifyAmount(-1);
                    
                    if(craftingArea[x][y].getContent().getAmount() <= 0){
                        craftingArea[x][y].setContent(null);
                    }
                }
            }
        }
    }
    
    private Slot getWorkbenchSlot(){       
        Point p = Mouse.getPoint();
        
        for(int x = 0; x < craftingArea.length; x++){
            for(int y = 0; y < craftingArea[0].length; y++){
                if(craftingArea[x][y].getBounds().contains(p)){
                    return craftingArea[x][y];
                }
            }
        }
        return null;
    }
    
    private int[] getSelectedIndecies() {
        Rectangle mouse = new Rectangle(Mouse.getX(), Mouse.getY(), 3, 3);
        
        for (int y = 0; y < Sidecraft.player.getInventory().getContents()[0].length; y++) {
            for (int x = 0; x < Sidecraft.player.getInventory().getContents().length; x++) {
                Rectangle box = new Rectangle(this.x + X_DIFF + (38 * x), this.y + Y_DIFF - (37 * y), 30, 30);
                if (mouse.intersects(box)) {
                    return new int[] { x, y };
                }
            }
        }
        return new int[] { -1, -1 };
    }

    private int[] getRecipeData(){
        int[] r = new int[9];
        int counter = 0;
        
        for(int y = 0; y < craftingArea.length; y++){
            for(int x = 0; x < craftingArea[0].length; x++){               
                if(craftingArea[x][y].getContent() != null){
                    r[counter] = craftingArea[x][y].getContent().getType().getId();
                }else{
                    r[counter] = -1;
                }
                counter++;
            }
        }
        return r;
    }
}
