package com.freedsuniverse.sidecraft.inventory;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.engine.RenderQueueItem;
import com.freedsuniverse.sidecraft.entity.DropEntity;
import com.freedsuniverse.sidecraft.entity.Entity;
import com.freedsuniverse.sidecraft.input.Mouse;
import com.freedsuniverse.sidecraft.material.CraftingRecipe;
import com.freedsuniverse.sidecraft.material.Item;
import com.freedsuniverse.sidecraft.material.MaterialStack;

public class EntityInventory extends Inventory {
    public static final Color HOVER_COLOR = new Color(Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getGreen(), 150);

    private Item onMouse;
    
    private int x, y;
    
    private Entity owner;
    
    private Slot[][] craftingArea;
    
    private boolean isOpen = false;

    final int X_DIFF = 13;
    final int Y_DIFF = 254;

    private Slot outputSlot;

    public static Item[] handleSlot(Item item, Item onMouse2) {
        if(Mouse.clicked(MouseEvent.BUTTON1)) {
            if(onMouse2 != null) {
                if(item == null) {
                    item = onMouse2;
                    onMouse2 = null; 
                }else {
                    if(item.getType() == onMouse2.getType()) {
                        if(item.getAmount() < item.getType().getMaxStackSize()){
                            int amount = item.getType().getMaxStackSize() - item.getAmount();

                            if(amount > onMouse2.getAmount()){
                                amount = onMouse2.getAmount();
                            }

                            item.modifyAmount(amount);
                            onMouse2.modifyAmount(-amount);

                            if(onMouse2.getAmount() == 0){
                                onMouse2 = null;
                            }
                        }
                    }else{
                        Item temp = item;
                        item = onMouse2;
                        onMouse2 = temp;
                    }
                }
            }else{
                onMouse2 = item;
                item = null;
            }
        }else if(Mouse.clicked(MouseEvent.BUTTON3)){
            if(onMouse2 != null){
                if(item == null || item.getType() == onMouse2.getType()){
                    if(item == null){
                        item = new MaterialStack(onMouse2.getType(), 0);
                    }
                    
                    if(item.getAmount() < item.getType().getMaxStackSize()){
                        item.modifyAmount(1);
                        onMouse2.modifyAmount(-1);
                        
                        if(onMouse2.getAmount() <= 0) onMouse2 = null;
                    }
                }else if(item.getType() != onMouse2.getType()){                   
                    Item temp = onMouse2;
                    onMouse2 = item;
                    item = temp;
                }
            }else if(item != null){  
                if(item.getAmount() == 1){
                    onMouse2 = item;
                    item = null;
                }else{
                    int amount = (int)Math.floor((double)item.getAmount() / 2);
                    onMouse2 = new MaterialStack(item.getType(), amount);
                    item.modifyAmount(-amount);
                }
            }
        }
        return new Item[]{item, onMouse2};
    }
    
    public EntityInventory(Entity e){   
        super();
        owner = e;
        x = Main.getGame().width / 2 - Main.inventoryTile.getWidth() / 2;
        y = Main.getGame().height / 2 - Main.inventoryTile.getHeight() / 2;
        craftingArea = new Slot[2][2];
        outputSlot = new Slot(null, new Rectangle(x + Main.inventoryTile.getWidth() - 48, y + 54, 30, 30));
    }
    
    public void update(){
        if(isOpen){
            if(Mouse.clicked(MouseEvent.BUTTON1) || Mouse.clicked(MouseEvent.BUTTON3)){
                int[] selected = getSelectedBox();
                if(selected[0] != -1){
                    Item[] handled = EntityInventory.handleSlot(getAt(selected[0], selected[1]), onMouse);
                    setAt(selected[0], selected[1], handled[0]);
                    onMouse = handled[1];
                }else if(getSelectedSlot() != null){
                    Slot s = getSelectedSlot();
                    Item[] handled = EntityInventory.handleSlot(s.getContent(), onMouse);
                    s.setContent(handled[0]);
                    onMouse = handled[1];
                    updateCrafting();
                }else if(outputSlot.getBounds().contains(Mouse.getPoint())){
                    if(onMouse == null){
                        if(outputSlot.getContent() != null){
                            onMouse = outputSlot.getContent();
                            clearCraftingAreaFromRecipe();
                        }
                    }
                }
            }
        }
    }
    
    private void updateCrafting() {
        Item outcome = CraftingRecipe.getMatch(this.getRecipeData());            
        outputSlot.setContent(outcome);
    }

    private int[] getRecipeData() {
        int[] r = new int[4];
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
    
    private Slot getSelectedSlot() {
        Point p = Mouse.getPoint();
        
        for(int x = 0; x < this.craftingArea.length; x++){
            for(int y = 0; y < this.craftingArea[0].length; y++){
                if(craftingArea[x][y].getBounds().contains(p)){
                    return craftingArea[x][y];
                }
            }
        }
        return null;
    }

    private int[] getSelectedBox() {
        Rectangle mouse = new Rectangle(Mouse.getX(), Mouse.getY(), 3, 3);

        for (int y = 0; y < this.getContents().length; y++) {
            for (int x = 0; x < this.getContents()[0].length; x++) {
                Rectangle box = new Rectangle(this.x + X_DIFF + (38 * x), this.y + Y_DIFF - (37 * y), 30, 30);
                if (mouse.intersects(box)) {
                    return new int[] { x, y };
                }
            }
        }
        return new int[] { -1, -1 };
    }

    public void draw() {           
        if(!isOpen) return;
        
        Engine.addQueueItem(new RenderQueueItem(x, y, Main.inventoryTile));
        Engine.addQueueItem(new RenderQueueItem(owner.getSkin(), new Rectangle(x + 25, y + 25, 90, 90)));

        for (int y = 0; y < this.getContents().length; y++) {
            for (int x = 0; x < this.getContents()[0].length; x++) {
                Rectangle box = new Rectangle(this.x + X_DIFF + (38 * x), this.y + Y_DIFF - (38 * y) + 1, 30, 30);             
                if (getContents()[y][x] != null) {                   
                    getContents()[y][x].draw(box, true);                 
                }
                if(box.contains(Mouse.getPoint())){
                    if(box.contains(Mouse.getPoint())){
                        Engine.addQueueItem(new RenderQueueItem(box, HOVER_COLOR));
                    }
                }
            }
        }
        
        for(int y = 0; y < craftingArea.length; y++){
            for(int x = 0; x < craftingArea[0].length; x++){
                Rectangle box = craftingArea[y][x].getBounds();               
                
                if(craftingArea[y][x].getContent() != null){
                    craftingArea[y][x].getContent().draw(box, true);
                }
                
                if(box.contains(Mouse.getPoint())){
                    if(box.contains(Mouse.getPoint())){
                        Engine.addQueueItem(new RenderQueueItem(box, HOVER_COLOR));
                    }
                }
            }
        }
        
        if(outputSlot.getContent() != null){
            outputSlot.getContent().draw(outputSlot.getBounds(), true);
        }       
        
        if (onMouse != null) {
            onMouse.draw(Mouse.getPoint().x, Mouse.getPoint().y, true);
        }             
    }

    public void open(){
        isOpen = true;
        
        for(int x = 0; x < 2; x++){
            for(int y = 0; y < 2; y++){
                craftingArea[x][y] = new Slot(null, new Rectangle(this.x + 134 + (38 * x), this.y + 35 + (38 * y), 30, 30));
            }
        }
    }
    
    public void close() {
        if (onMouse != null) {
            for (int x = 0; x < onMouse.getAmount(); x++) {
                new DropEntity(onMouse.getType()).spawn(owner.getLocation());
            }
            onMouse = null;
        }
        
        for (int y = 0; y < this.craftingArea[0].length; y++) {
            for (int x = 0; x < this.craftingArea.length; x++) {
                if(craftingArea[x][y].getContent() != null)
                    new DropEntity(craftingArea[x][y].getContent().getType()).spawn(owner.getLocation());
            }
        }
        outputSlot.setContent(null);
        isOpen = false;
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
        outputSlot.setContent(null);
    }

    public String toString() {
        String s = "";
        
        for(int y = 0; y < this.getContents()[0].length; y++)
            for(int x = 0; x < this.getContents().length; x++) {
                if(getAt(x, y) != null) {
                    s += x + ":" + y + ":" + getAt(x, y).toString() + ";";
                }
        }  
        return s;
    }

    public boolean isOpen() {
        return this.isOpen;
    }
}
