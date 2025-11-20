package main;

import entity.Entity;
import object.OBJ_Heart;
import object.Others;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class UI {

    GamePanel gp;
    Graphics2D g2;
    Font bitcell, graphicpixel;

    BufferedImage heart_full, heart_half, heart_blank, bgImage;
    public UtilityTool uTool = new UtilityTool();
    public boolean messageOn = false;
    public boolean gameFinished = false;
    public String currentDialogue = "";
    public int commandNum = 0;
    ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();
    // VAR for cursor
    public int slotCol;
    public int slotRow;
    // For inventory sht
    public int inventoryIndex = 0;
    // Title screen things
    private final Color shadowText = new Color(60, 60, 60);
    private final Color mainText = new Color(255, 255, 255);

    public UI(GamePanel gp){
        this.gp = gp;

        try {
            InputStream is = getClass().getResourceAsStream("/font/bitcell_memesbruh03.ttf");
            bitcell = Font.createFont(Font.TRUETYPE_FONT, is);
            is = getClass().getResourceAsStream("/font/graphicpixel.ttf");
            graphicpixel = Font.createFont(Font.TRUETYPE_FONT, is);
            bgImage = ImageIO.read(getClass().getResourceAsStream("/ui/backgroundImage.png"));
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Create HUD
        Entity heart = new OBJ_Heart(gp);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;
    }
    public void addMessage(String text){
        message.add(text);
        messageCounter.add(0);
    }
    public void draw(Graphics2D g2){
        this.g2 = g2;
        g2.setFont(graphicpixel);
        g2.setColor(Color.WHITE);
        // Title State
        if(gp.gameState == gp.titleState){
            drawTitleScreen();
        }
        // Play State
        if(gp.gameState == gp.playState){
            drawPlayerLife();
            drawMessage();
        }
        // Pause State
        if(gp.gameState == gp.pauseState){
            drawPlayerLife();
            drawPausedScreen();
        }
        // Dialogue State
        if(gp.gameState == gp.dialogueState){
            drawPlayerLife();
            drawDialogueScreen();
        }
        // Character State
        if(gp.gameState == gp.characterState){
            drawCharacterScreen();
        }

    }
    public void drawMessage(){
        int messageX = gp.tileSize;
        int messageY = gp.tileSize * 4;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
        for(int i = 0; i < message.size(); i++){
            if(message.get(i) != null){
                g2.setColor(Color.black);
                g2.drawString(message.get(i), messageX + 2, messageY + 2);
                g2.setColor(Color.white);
                g2.drawString(message.get(i), messageX, messageY);

                int counter = messageCounter.get(i) + 1; // message counter++ but for arraylist
                messageCounter.set(i, counter); // set the counter to the array
                messageY += 50;

                if(messageCounter.get(i) > 180){
                    message.remove(i);
                    messageCounter.remove(i);
                }
            }
        }
    }
    public void drawPausedScreen(){
        String text = "PAUSED";
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,80));
        int x = getXForCenteredText(text);
        int y = gp.screenHeight / 2;

        g2.drawString(text, x, y);

    }
    public int getXForCenteredText(String text){
        int x;
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth(); // Super useful for creating text at the very center for x-axis
        x = gp.screenWidth / 2 - length / 2;
        return x;
    }
    public void drawDialogueScreen(){
        // Window
        int x = gp.tileSize * 2; // Placement
        int y = gp.tileSize / 2; // Placement
        int width = gp.screenWidth - (gp.tileSize * 4); // Dimension
        int height = gp.tileSize * 4; // Dimension
        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        x += gp.tileSize;
        y += gp.tileSize;
        for(String line: currentDialogue.split("\n")){
            g2.drawString(line, x, y);
            y += 40;
        }



    }
    public void drawSubWindow(int x, int y, int width, int height){
        Color c = new Color(0,0,0, 190);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 25, 25);

        c = new Color(255,255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5,y + 5,width - 10,height - 10,25,25);
    }
    public void drawTitleScreen() {
        // BGImage
        if(bgImage != null) {  // Check if it loaded successfully
            BufferedImage newBG = uTool.scaleImage(bgImage, gp.tileSize * 16, gp.tileSize * 12);
            g2.drawImage(newBG, 0, 0, null);  // Also changed 1,1 to 0,0 for top-left corner
        }
        drawTitleText();

    }
    public void drawPlayerLife(){
        int x = gp.tileSize / 2;
        int y = gp.tileSize / 2;
        int i = 0;
        // Draw max life
        while(i < gp.player.maxLife / 2){
            g2.drawImage(heart_blank, x, y, null);
            i++;
            x += gp.tileSize;
        }
        // Reset
        x = gp.tileSize / 2;
        y = gp.tileSize / 2;
        i = 0;
        // Draw current life
        while(i < gp.player.life){
            g2.drawImage(heart_half, x, y, null);
            i++;
            if(i < gp.player.life){
                g2.drawImage(heart_full, x, y, null);
            }
            i++;
            x += gp.tileSize;
        }
    }
    public void drawCharacterScreen(){
        if(inventoryIndex == 0){
            drawEquipment();
        }
        else if(inventoryIndex == 1){
            drawInventory();
        }
        else if(inventoryIndex == 2){
            drawStats();
        }
//        // create frame
//        final int frameX = gp.tileSize;
//        final int frameY = gp.tileSize;
//        final int frameWidth = gp.tileSize * 5;
//        final int frameHeight = gp.tileSize * 10 + (gp.tileSize / 2);
//        drawSubWindow(frameX, frameY, frameWidth, frameHeight);
//
//        // display text
//        g2.setColor(Color.white);
//        g2.setFont(g2.getFont().deriveFont(32F));
//
//        int textX = frameX + 20;
//        int textY = frameY + gp.tileSize;
//        final int lineHeight = 43;
//
//        // names
//        g2.drawString("Level", textX, textY);
//        textY += lineHeight;
//        g2.drawString("Life", textX, textY);
//        textY += lineHeight;
//        g2.drawString("Strength", textX, textY);
//        textY += lineHeight;
//        g2.drawString("Dexterity", textX, textY);
//        textY += lineHeight;
//        g2.drawString("Attack", textX, textY);
//        textY += lineHeight;
//        g2.drawString("Defense", textX, textY);
//        textY += lineHeight;
//        g2.drawString("Exp", textX, textY);
//        textY += lineHeight;
//        g2.drawString("Next Lvl", textX, textY);
//        textY += lineHeight;
//        g2.drawString("Coin", textX, textY);
//        textY += lineHeight;
//        g2.drawString("Weapon", textX, textY);
//        textY += lineHeight;
//        g2.drawString("OffHand", textX, textY);
//        textY += lineHeight;
//
//        // values
//        int tailX = (frameX + frameWidth) - 30;
//        textY = frameY + gp.tileSize;
//        String value;
//
//        value = String.valueOf(gp.player.level);
//        textX = getXForAlignToRightText(value, tailX);
//        g2.drawString(value, textX, textY);
//        textY += lineHeight;
//
//        value = String.valueOf(gp.player.life + "/" + gp.player.maxLife);
//        textX = getXForAlignToRightText(value, tailX);
//        g2.drawString(value, textX, textY);
//        textY += lineHeight;
//
//        value = String.valueOf(gp.player.strength);
//        textX = getXForAlignToRightText(value, tailX);
//        g2.drawString(value, textX, textY);
//        textY += lineHeight;
//
//        value = String.valueOf(gp.player.dexterity);
//        textX = getXForAlignToRightText(value, tailX);
//        g2.drawString(value, textX, textY);
//        textY += lineHeight;
//
//        value = String.valueOf(gp.player.attack);
//        textX = getXForAlignToRightText(value, tailX);
//        g2.drawString(value, textX, textY);
//        textY += lineHeight;
//
//        value = String.valueOf(gp.player.defense);
//        textX = getXForAlignToRightText(value, tailX);
//        g2.drawString(value, textX, textY);
//        textY += lineHeight;
//
//        value = String.valueOf(gp.player.exp);
//        textX = getXForAlignToRightText(value, tailX);
//        g2.drawString(value, textX, textY);
//        textY += lineHeight;
//
//        value = String.valueOf(gp.player.nextLevelExp);
//        textX = getXForAlignToRightText(value, tailX);
//        g2.drawString(value, textX, textY);
//        textY += lineHeight;
//
//        value = String.valueOf(gp.player.coin);
//        textX = getXForAlignToRightText(value, tailX);
//        g2.drawString(value, textX, textY);
//        textY += lineHeight - 30;
//
//        g2.drawImage(gp.player.currentWeapon.down1, tailX - (gp.tileSize - 8),textY, null);
//        textY += lineHeight;
//        g2.drawImage(gp.player.currentOffHand.down1, tailX - (gp.tileSize - 8),textY, null);
    }
    public int getItemIndex(){
        return slotCol + (slotRow * 6);
    }
    public int getXForAlignToRightText(String text, int tailX){
        int x;
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth(); // Super useful for creating text at the very center for x-axis
        x = tailX - length;
        return x;
    }
    public void drawTitleText(){
        Font font = bitcell.deriveFont(Font.BOLD, 100);
        g2.setFont(font);
        int titleX = getXForCenteredText("STARDEW PEAKS");
        int titleY = gp.tileSize * 3;

        g2.setColor(shadowText);
        g2.drawString("STARDEW PEAKS",titleX + 5, titleY + 5 );

        g2.setColor(mainText);
        g2.drawString("STARDEW PEAKS",titleX, titleY );


        if(commandNum == 0){
            Font menuFont = getTextSize(70);
            g2.setFont(menuFont);
            int menuX = getXForCenteredText("New Game");
            int menuY = titleY + gp.tileSize * 5;
            g2.setColor(shadowText);
            g2.drawString("New Game",menuX + 5, menuY + 5);
            g2.setColor(mainText);
            g2.drawString("New Game",menuX, menuY);
            menuY += gp.tileSize + (gp.tileSize / 4);

            menuFont = getTextSize(50);
            g2.setFont(menuFont);
            menuX = getXForCenteredText("Load Game");
            g2.setColor(shadowText);
            g2.drawString("Load Game",menuX + 5, menuY + 5);
            g2.setColor(mainText);
            g2.drawString("Load Game",menuX, menuY);
            menuY += gp.tileSize;

            menuX = getXForCenteredText("Quit Game");
            g2.setColor(shadowText);
            g2.drawString("Quit Game",menuX + 5, menuY + 5);
            g2.setColor(mainText);
            g2.drawString("Quit Game",menuX, menuY);
        }
        if(commandNum == 1){
            Font menuFont = getTextSize(50);
            g2.setFont(menuFont);
            int menuX = getXForCenteredText("New Game");
            int menuY = titleY + gp.tileSize * 5;
            g2.setColor(shadowText);
            g2.drawString("New Game",menuX + 5, menuY + 5);
            g2.setColor(mainText);
            g2.drawString("New Game",menuX, menuY);
            menuY += gp.tileSize + (gp.tileSize / 4);

            menuFont = getTextSize(70);
            g2.setFont(menuFont);
            menuX = getXForCenteredText("Load Game");
            g2.setColor(shadowText);
            g2.drawString("Load Game",menuX + 5, menuY + 5);
            g2.setColor(mainText);
            g2.drawString("Load Game",menuX, menuY);
            menuY += gp.tileSize;

            menuFont = getTextSize(50);
            g2.setFont(menuFont);
            menuX = getXForCenteredText("Quit Game");
            g2.setColor(shadowText);
            g2.drawString("Quit Game",menuX + 5, menuY + 5);
            g2.setColor(mainText);
            g2.drawString("Quit Game",menuX, menuY);
        }
        if(commandNum == 2){
            Font menuFont = getTextSize(50);
            g2.setFont(menuFont);
            int menuX = getXForCenteredText("New Game");
            int menuY = titleY + gp.tileSize * 5;
            g2.setColor(shadowText);
            g2.drawString("New Game",menuX + 5, menuY + 5);
            g2.setColor(mainText);
            g2.drawString("New Game",menuX, menuY);
            menuY += gp.tileSize + (gp.tileSize / 4);

            menuX = getXForCenteredText("Load Game");
            g2.setColor(shadowText);
            g2.drawString("Load Game",menuX + 5, menuY + 5);
            g2.setColor(mainText);
            g2.drawString("Load Game",menuX, menuY);
            menuY += gp.tileSize;

            menuFont = getTextSize(70);
            g2.setFont(menuFont);
            menuX = getXForCenteredText("Quit Game");
            g2.setColor(shadowText);
            g2.drawString("Quit Game",menuX + 5, menuY + 5);
            g2.setColor(mainText);
            g2.drawString("Quit Game",menuX, menuY);
        }

    }
    public Font getTextSize(int size){
        return bitcell.deriveFont(Font.BOLD, size);
    }
    public void drawEquipment() {
        // Calculate 90% of screen dimensions
        int uiWidth = (int)(gp.screenWidth * 0.9);
        int uiHeight = (int)(gp.screenHeight * 0.9);

        // Center the UI on screen
        int uiX = (gp.screenWidth - uiWidth) / 2;
        int uiY = (gp.screenHeight - uiHeight) / 2;

        // Ancient theme color palette
        Color bgColor = new Color(40, 35, 30);           // Dark brown
        Color borderColor = new Color(139, 115, 85);     // Light brown/bronze
        Color slotBgColor = new Color(60, 50, 40);       // Slightly lighter brown
        Color slotBorderColor = new Color(180, 150, 100); // Gold/bronze
        Color labelColor = new Color(220, 200, 150);     // Light gold

        g2.setColor(bgColor);
        g2.fillRect(uiX,uiY, uiWidth, uiHeight);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(5f));
        g2.drawRect(uiX,uiY,uiWidth,uiHeight);

        int slotWidth = uiWidth - gp.tileSize;
        int slotHeight = uiHeight - gp.tileSize * 3;
        int slotX = uiX + 24;
        int slotY = uiY + gp.tileSize ;
        g2.setColor(slotBgColor);
        g2.fillRect(slotX, slotY, slotWidth, slotHeight);

        g2.setColor(slotBorderColor);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(slotX, slotY, slotWidth, slotHeight);

        g2.drawRect(slotX,slotY,gp.tileSize * 5, slotHeight);

        int boxHeight = gp.tileSize;
        int boxWidth = gp.tileSize;

        // Head slot
        g2.setColor(slotBgColor);
        g2.fillRect(slotX + (gp.tileSize * 2),slotY + 10, boxWidth,boxHeight);
        g2.setColor(labelColor);
        g2.drawRect(slotX + (gp.tileSize * 2),slotY + 10, boxWidth,boxHeight);
        if(gp.player.headGear != null && gp.player.headGear.down1 != null){
            g2.drawImage(gp.player.headGear.down1, slotX + (gp.tileSize * 2), slotY + 10, boxWidth, boxHeight, null);
        }

        // Left hand slot (offhand)
        g2.setColor(slotBgColor);
        g2.fillRect(slotX + (gp.tileSize ) - 10,(slotY + gp.tileSize) + 10 + 10, boxWidth,boxHeight);
        g2.setColor(labelColor);
        g2.drawRect(slotX + (gp.tileSize) - 10,(slotY + gp.tileSize) + 10 + 10, boxWidth,boxHeight);
        if(gp.player.mainHand != null && gp.player.mainHand.down1 != null){
            g2.drawImage(gp.player.mainHand.down1, slotX + (gp.tileSize) - 10, (slotY + gp.tileSize) + 20, boxWidth, boxHeight, null);
        }

        // Chest slot
        g2.setColor(slotBgColor);
        g2.fillRect(slotX + (gp.tileSize * 2),(slotY + gp.tileSize) + 10 + 10, boxWidth,boxHeight);
        g2.setColor(labelColor);
        g2.drawRect(slotX + (gp.tileSize * 2),(slotY + gp.tileSize) + 10 + 10, boxWidth,boxHeight);
        if(gp.player.chestGear != null && gp.player.chestGear.down1 != null){
            g2.drawImage(gp.player.chestGear.down1, slotX + (gp.tileSize * 2), (slotY + gp.tileSize) + 20, boxWidth, boxHeight, null);
        }

        // Right hand slot (main hand)
        g2.setColor(slotBgColor);
        g2.fillRect(slotX + (gp.tileSize * 3) + 10,(slotY + gp.tileSize) + 10 + 10, boxWidth,boxHeight);
        g2.setColor(labelColor);
        g2.drawRect(slotX + (gp.tileSize * 3) + 10,(slotY + gp.tileSize) + 10 + 10, boxWidth,boxHeight);
        if(gp.player.offHand != null && gp.player.offHand.down1 != null){
            g2.drawImage(gp.player.offHand.down1, slotX + (gp.tileSize * 3) + 10, (slotY + gp.tileSize) + 20, boxWidth, boxHeight, null);
        }

        // Legs slot
        g2.setColor(slotBgColor);
        g2.fillRect(slotX + (gp.tileSize * 2),(slotY + gp.tileSize * 2) + 20 + 10, boxWidth,boxHeight);
        g2.setColor(labelColor);
        g2.drawRect(slotX + (gp.tileSize * 2),(slotY + gp.tileSize * 2) + 20 + 10, boxWidth,boxHeight);
        if(gp.player.legGear != null && gp.player.legGear.down1 != null){
            g2.drawImage(gp.player.legGear.down1, slotX + (gp.tileSize * 2), (slotY + gp.tileSize * 2) + 30, boxWidth, boxHeight, null);
        }

        // Boots slot
        g2.setColor(slotBgColor);
        g2.fillRect(slotX + (gp.tileSize * 2),(slotY + gp.tileSize * 3) + 30 + 10, boxWidth,boxHeight);
        g2.setColor(labelColor);
        g2.drawRect(slotX + (gp.tileSize * 2),(slotY + gp.tileSize * 3) + 30 + 10, boxWidth,boxHeight);
        if(gp.player.bootsGear != null && gp.player.bootsGear.down1 != null){
            g2.drawImage(gp.player.bootsGear.down1, slotX + (gp.tileSize * 2), (slotY + gp.tileSize * 3) + 40, boxWidth, boxHeight, null);
        }

        // Description box at the bottom of the equipment boxes
        int descX = slotX + 10;
        int descY = slotY + (gp.tileSize * 4) + 60;
        int descWidth = (gp.tileSize * 5) - 20;
        int descHeight = slotHeight - (gp.tileSize * 4) - 80;

        // Draw description background
        g2.setColor(slotBgColor);
        g2.fillRect(descX, descY, descWidth, descHeight);
        g2.setColor(labelColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(descX, descY, descWidth, descHeight);

        String coinText = "Coins: " + gp.player.coin;
        int coinX = descX + 2;
        int coinY = descY - 10;
        g2.setFont(bitcell.deriveFont(Font.BOLD, 20F));
        g2.setColor(new Color(255, 215, 0));
        g2.drawString(coinText,coinX,coinY);
        // Get the currently selected item from equipment grid
        int itemIndex = getItemIndex();
        if(itemIndex < gp.player.equipment.size()){
            Entity selectedItem = gp.player.equipment.get(itemIndex);

            // Draw description text
            int textX = descX + 10;
            int textY = descY + 30;

            g2.setColor(labelColor);
            g2.setFont(bitcell.deriveFont(32F));
            if(selectedItem.itemDescription != null){
                for(String line: selectedItem.itemDescription.split("\n")){
                    g2.drawString(line, textX, textY);
                    textY += 28;
                }
            }
        }

        drawSlot(slotX, slotY, slotBorderColor, "equipment");

        Font font = getTextSize(30);
        g2.setFont(font);
        String label = "Inventory";
        int labelX = getXForCenteredText(label);
        int labelY = (uiY + uiHeight) - (gp.tileSize);
        g2.drawString(label,labelX,labelY);

        font = getTextSize(45);
        g2.setFont(font);
        label = "Equipment";
        labelX = labelX - uiWidth / 3;
        g2.drawString(label,labelX,labelY);

        font = getTextSize(30);
        g2.setFont(font);
        label = "Status";
        labelX = labelX + ((uiWidth / 3) * 2);
        g2.drawString(label,labelX,labelY);
    }
    public void drawSlot(int x, int y, Color color, String array){
        int slotX = x + (gp.tileSize * 5) + 28;
        int slotY = y + 18;
        int slotW = gp.tileSize;
        int slotH = gp.tileSize;
        int spacing = 10;
        g2.setColor(color);

        for(int row = 0; row < 6; row++){
            for(int col = 0; col < 6; col++){
                int currentX = slotX + (col * (slotW + spacing));
                int currentY = slotY + (row * (slotH + spacing));
                g2.drawRect(currentX, currentY, slotW, slotH);
            }
        }

        int cursorX = slotX + (slotCol * (slotW + spacing));
        int cursorY = slotY + (slotRow * (slotH + spacing));
        int cursorDimension = gp.tileSize;
        g2.setStroke(new BasicStroke(5));
        g2.setColor(new Color(34, 139, 34));
        g2.drawRect(cursorX, cursorY, cursorDimension, cursorDimension);
        g2.setColor(color);

        if (array.equals("inventory")) {

            for (int i = 0; i < gp.player.inventory.size(); i++) {

                int row = i / 6;   // 6 columns
                int col = i % 6;

                int itemX = slotX + (col * (slotW + spacing));
                int itemY = slotY + (row * (slotH + spacing));

                // Draw the image inside the slot box
                g2.drawImage(
                        gp.player.inventory.get(i).itemImage,
                        itemX, itemY,
                        slotW, slotH,
                        null
                );
            }

        }
        if (array.equals("equipment")) {

            for (int i = 0; i < gp.player.equipment.size(); i++) {

                int row = i / 6;   // 6 columns
                int col = i % 6;

                int itemX = slotX + (col * (slotW + spacing));
                int itemY = slotY + (row * (slotH + spacing));

                Entity currentItem = gp.player.equipment.get(i);

                // Check if this item is currently equipped
                boolean isEquipped = (currentItem == gp.player.headGear ||
                        currentItem == gp.player.chestGear ||
                        currentItem == gp.player.legGear ||
                        currentItem == gp.player.bootsGear ||
                        currentItem == gp.player.mainHand ||
                        currentItem == gp.player.offHand);

                // Draw colored background if equipped
                if(isEquipped){
                    g2.setColor(new Color(240, 190, 90)); // Gold/orange color for equipped items
                    g2.fillRect(itemX, itemY, slotW, slotH);
                }

                // Draw the image inside the slot box
                g2.drawImage(
                        currentItem.down1,
                        itemX, itemY,
                        slotW, slotH,
                        null
                );
                g2.setColor(color);
            }
        }
    }
    public void drawInventory(){
        // Calculate 90% of screen dimensions
        int uiWidth = (int)(gp.screenWidth * 0.9);
        int uiHeight = (int)(gp.screenHeight * 0.9);

        // Center the UI on screen
        int uiX = (gp.screenWidth - uiWidth) / 2;
        int uiY = (gp.screenHeight - uiHeight) / 2;

        // Ancient theme color palette
        Color bgColor = new Color(40, 35, 30);           // Dark brown
        Color borderColor = new Color(139, 115, 85);     // Light brown/bronze
        Color slotBgColor = new Color(60, 50, 40);       // Slightly lighter brown
        Color slotBorderColor = new Color(180, 150, 100); // Gold/bronze
        Color labelColor = new Color(220, 200, 150);     // Light gold

        g2.setColor(bgColor);
        g2.fillRect(uiX,uiY, uiWidth, uiHeight);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(5f));
        g2.drawRect(uiX,uiY,uiWidth,uiHeight);

        int slotWidth = uiWidth - gp.tileSize;
        int slotHeight = uiHeight - gp.tileSize * 3;
        int slotX = uiX + 24;
        int slotY = uiY + gp.tileSize ;
        g2.setColor(slotBgColor);
        g2.fillRect(slotX, slotY, slotWidth, slotHeight);

        g2.setColor(slotBorderColor);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(slotX, slotY, slotWidth, slotHeight);

        g2.drawRect(slotX,slotY,gp.tileSize * 5, slotHeight);

        // Item preview box
        int itemX = slotX + gp.tileSize+(gp.tileSize/2);
        int itemY = slotY + 18;
        int itemW = gp.tileSize * 2;
        int itemH = gp.tileSize * 2;

        g2.setColor(slotBgColor);
        g2.fillRect(itemX,itemY,itemW,itemH);
        g2.setColor(labelColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(itemX,itemY,itemW,itemH);

        // Get the currently selected item
        int itemIndex = getItemIndex();
        if(itemIndex < gp.player.inventory.size()){
            Others selectedItem = gp.player.inventory.get(itemIndex);

            // Draw the item image in the preview box
            g2.drawImage(selectedItem.itemImage, itemX, itemY, itemW, itemH, null);

            // Description box below the preview
            int descX = slotX + 20;
            int descY = itemY + itemH + 20;
            int descWidth = (gp.tileSize * 5) - 40;
            int descHeight = slotHeight - itemH - 60;

            // Draw description background
            g2.setColor(slotBgColor);
            g2.fillRect(descX, descY, descWidth, descHeight);
            g2.setColor(labelColor);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(descX, descY, descWidth, descHeight);

            // Draw description text
            g2.setFont(g2.getFont().deriveFont(24F));
            g2.setColor(labelColor);
            int textX = descX + 10;
            int textY = descY + 30;

            if(selectedItem.description != null){  // Changed from itemDescription to description
                for(String line: selectedItem.description.split("\n")){
                    g2.drawString(line, textX, textY);
                    textY += 28;
                }
            }
        }

        drawSlot(slotX, slotY, slotBorderColor, "inventory");

        Font font = getTextSize(45);
        g2.setFont(font);
        String label = "Inventory";
        int labelX = getXForCenteredText(label);
        int labelY = (uiY + uiHeight) - (gp.tileSize);
        g2.drawString(label,labelX,labelY);

        font = getTextSize(30);
        g2.setFont(font);
        label = "Equipment";
        labelX = labelX - uiWidth / 3;
        g2.drawString(label,labelX,labelY);

        font = getTextSize(30);
        g2.setFont(font);
        label = "Status";
        labelX = labelX + ((uiWidth / 3) * 2);
        g2.drawString(label,labelX,labelY);
    }
    public void drawStats(){
        // Calculate 90% of screen dimensions
        int uiWidth = (int)(gp.screenWidth * 0.9);
        int uiHeight = (int)(gp.screenHeight * 0.9);

        // Center the UI on screen
        int uiX = (gp.screenWidth - uiWidth) / 2;
        int uiY = (gp.screenHeight - uiHeight) / 2;

        // Ancient theme color palette
        Color bgColor = new Color(40, 35, 30);           // Dark brown
        Color borderColor = new Color(139, 115, 85);     // Light brown/bronze
        Color slotBgColor = new Color(60, 50, 40);       // Slightly lighter brown
        Color slotBorderColor = new Color(180, 150, 100); // Gold/bronze
        Color labelColor = new Color(220, 200, 150);     // Light gold

        g2.setColor(bgColor);
        g2.fillRect(uiX,uiY, uiWidth, uiHeight);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(5f));
        g2.drawRect(uiX,uiY,uiWidth,uiHeight);

        int slotWidth = uiWidth - gp.tileSize;
        int slotHeight = uiHeight - gp.tileSize * 3;
        int slotX = uiX + 24;
        int slotY = uiY + gp.tileSize ;
        g2.setColor(slotBgColor);
        g2.fillRect(slotX, slotY, slotWidth, slotHeight);

        g2.setColor(slotBorderColor);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(slotX, slotY, slotWidth, slotHeight);

        // Draw section labels at bottom
        g2.setColor(slotBorderColor);
        Font font = getTextSize(30);
        g2.setFont(font);
        String label = "Inventory";
        int labelX = getXForCenteredText(label);
        int labelY = (uiY + uiHeight) - (gp.tileSize);
        g2.drawString(label,labelX,labelY);

        font = getTextSize(30);
        g2.setFont(font);
        label = "Equipment";
        labelX = labelX - uiWidth / 3;
        g2.drawString(label,labelX,labelY);

        font = getTextSize(45);
        g2.setFont(font);
        label = "Status";
        labelX = labelX + ((uiWidth / 3) * 2);
        g2.drawString(label,labelX,labelY);

        // DRAW PLAYER STATS - Fill the whole slot area
        int statusX = slotX + 10;
        int statusY = slotY + 30;
        int lineHeight = 60;

        g2.setColor(labelColor);
        font = getTextSize(36);
        g2.setFont(font);

        // Level
        g2.drawString("Level: " + gp.player.level, statusX, statusY);
        statusY += lineHeight;

        // Life
        g2.drawString("Life: " + gp.player.life + "/" + gp.player.maxLife, statusX, statusY);
        statusY += lineHeight;

        // Attack
        g2.drawString("Attack: " + gp.player.getAttackValue(), statusX, statusY);
        statusY += lineHeight;

        // Defense
        g2.drawString("Defense: " + gp.player.getDefenseValue(), statusX, statusY);
        statusY += lineHeight;

        // Next Level Exp
        g2.drawString("Next Level: " + gp.player.nextLevelExp, statusX, statusY);
        statusY += lineHeight;

        // Speed
        g2.drawString("Speed: " + gp.player.getDefenseValue(), statusX, statusY);
    }
}
