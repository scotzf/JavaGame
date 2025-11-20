package main;

import java.awt.*;

public class EventHandler {
    GamePanel gp;
    EventRect[][] eventRect;

    int previousX, previousY;
    boolean canTouchEvent = true;


    public EventHandler(GamePanel gp){
        this.gp = gp;
        eventRect = new EventRect[gp.maxWorldCol][gp.maxWorldRow];

        int col= 0;
        int row = 0;

        while(col < gp.maxWorldCol && row < gp.maxWorldRow){
            eventRect[col][row] = new EventRect();
            eventRect[col][row].x = 23;
            eventRect[col][row].y = 23;
            eventRect[col][row].width = 2;
            eventRect[col][row].height = 2;
            eventRect[col][row].eventRectDefaultX = eventRect[col][row].x;
            eventRect[col][row].eventRectDefaultY = eventRect[col][row].y;

            col++;
            if(col == gp.maxWorldCol){
                row++;
                col = 0;
            }
        }
    }
    public void checkEvent(){
        int playerCol = (gp.player.worldX + gp.player.solidArea.x) / gp.tileSize;
        int playerRow = (gp.player.worldY + gp.player.solidArea.y) / gp.tileSize;

        // Map 0 events
        if(gp.currentMap == 0){
            if(playerCol == 10 && playerRow == 15){
                changeMap(1, 5, 8);  // Enter house
            }
        }

        // Map 1 events
        else if(gp.currentMap == 1){
            if(playerCol == 5 && playerRow == 8){
                changeMap(0, 10, 16);  // Exit house
            }
        }
    }
    public boolean hit(int col, int row, String reqDirection){
        boolean hit = false;
        gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;
        eventRect[col][row].x = col * gp.tileSize + eventRect[col][row].x;
        eventRect[col][row].y = row * gp.tileSize + eventRect[col][row].y;

        if(gp.player.solidArea.intersects(eventRect[col][row]) && eventRect[col][row].eventDone == false){
            if(gp.player.direction.contentEquals(reqDirection) || reqDirection.contentEquals("any")){
                hit = true;

                previousX = gp.player.worldX;
                previousY = gp.player.worldY;
            }
        }
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;
        eventRect[col][row].x = eventRect[col][row].eventRectDefaultX;
        eventRect[col][row].y = eventRect[col][row].eventRectDefaultY;
        return hit;
    }
    public void changeMap(int targetMap, int targetCol, int targetRow) {
        gp.currentMap = targetMap;
        gp.player.worldX = gp.tileSize * targetCol;
        gp.player.worldY = gp.tileSize * targetRow;
    }
}
