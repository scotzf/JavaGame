package main;

import entity.Entity;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp){
        this.gp =gp;
    }

    public void checkTile(Entity entity){

        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX/gp.tileSize;
        int entityRightCol = entityRightWorldX/gp.tileSize;
        int entityTopRow = entityTopWorldY/gp.tileSize;
        int entityBottomRow = entityBottomWorldY/gp.tileSize;

        int tileNum1, tileNum2;

        switch(entity.direction){
            case "up" -> {
                entityTopRow = (entityTopWorldY - entity.speed)/gp.tileSize;
                if(entityLeftCol < 0 || entityLeftCol >= gp.tileM.mapTileNumber[gp.currentMap].length ||
                        entityRightCol < 0 || entityRightCol >= gp.tileM.mapTileNumber[gp.currentMap].length ||
                        entityTopRow < 0 || entityTopRow >= gp.tileM.mapTileNumber[gp.currentMap][0].length) {  // FIXED
                    entity.collisionOn = true;
                    return;
                }
                tileNum1 = gp.tileM.mapTileNumber[gp.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNumber[gp.currentMap][entityRightCol][entityTopRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                    entity.collisionOn = true;
                }
            }
            case "down" -> {
                entityBottomRow = (entityBottomWorldY + entity.speed)/gp.tileSize;
                if(entityLeftCol < 0 || entityLeftCol >= gp.tileM.mapTileNumber[gp.currentMap].length ||
                        entityRightCol < 0 || entityRightCol >= gp.tileM.mapTileNumber[gp.currentMap].length ||
                        entityBottomRow < 0 || entityBottomRow >= gp.tileM.mapTileNumber[gp.currentMap][0].length) {  // FIXED
                    entity.collisionOn = true;
                    return;
                }
                tileNum1 = gp.tileM.mapTileNumber[gp.currentMap][entityLeftCol][entityBottomRow];  // FIXED
                tileNum2 = gp.tileM.mapTileNumber[gp.currentMap][entityRightCol][entityBottomRow];  // FIXED
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                    entity.collisionOn = true;
                }
            }
            case "left" -> {
                entityLeftCol = (entityLeftWorldX - entity.speed)/gp.tileSize;
                if(entityLeftCol < 0 || entityLeftCol >= gp.tileM.mapTileNumber[gp.currentMap].length ||
                        entityTopRow < 0 || entityTopRow >= gp.tileM.mapTileNumber[gp.currentMap][0].length ||  // FIXED
                        entityBottomRow < 0 || entityBottomRow >= gp.tileM.mapTileNumber[gp.currentMap][0].length) {  // FIXED
                    entity.collisionOn = true;
                    return;
                }
                tileNum1 = gp.tileM.mapTileNumber[gp.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNumber[gp.currentMap][entityLeftCol][entityBottomRow];  // FIXED
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                    entity.collisionOn = true;
                }
            }
            case "right" -> {
                entityRightCol = (entityRightWorldX + entity.speed)/gp.tileSize;
                if(entityRightCol < 0 || entityRightCol >= gp.tileM.mapTileNumber[gp.currentMap].length ||
                        entityTopRow < 0 || entityTopRow >= gp.tileM.mapTileNumber[gp.currentMap][0].length ||  // FIXED
                        entityBottomRow < 0 || entityBottomRow >= gp.tileM.mapTileNumber[gp.currentMap][0].length) {  // FIXED
                    entity.collisionOn = true;
                    return;
                }
                tileNum1 = gp.tileM.mapTileNumber[gp.currentMap][entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNumber[gp.currentMap][entityRightCol][entityBottomRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                    entity.collisionOn = true;
                }
            }
        }
    }

    // Check equipment objects (like swords, shields on ground)
    public int checkObject(Entity entity, boolean player){
        int index = 999;

        for(int i = 0; i < gp.eqp.length; i++){
            if (gp.eqp[i] != null) {

                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                gp.eqp[i].solidArea.x = gp.eqp[i].worldX + gp.eqp[i].solidArea.x;
                gp.eqp[i].solidArea.y = gp.eqp[i].worldY + gp.eqp[i].solidArea.y;

                switch(entity.direction){
                    case "up" -> entity.solidArea.y -= entity.speed;
                    case "down" -> entity.solidArea.y += entity.speed;
                    case "right" -> entity.solidArea.x += entity.speed;
                    case "left" -> entity.solidArea.x -= entity.speed;
                }

                if(entity.solidArea.intersects(gp.eqp[i].solidArea)){
                    if(gp.eqp[i].collision){
                        entity.collisionOn = true;
                    }
                    if(player){
                        index = i;
                    }
                }

                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.eqp[i].solidArea.x = gp.eqp[i].solidAreaDefaultX;
                gp.eqp[i].solidArea.y = gp.eqp[i].solidAreaDefaultY;
            }
        }

        return index;
    }

    // NEW: Check consumable items (like potions on ground)
    public int checkItem(Entity entity, boolean player){
        int index = 999;

        for(int i = 0; i < gp.obj.length; i++){
            if (gp.obj[i] != null) {

                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                gp.obj[i].solidArea.x = gp.obj[i].worldX + gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].worldY + gp.obj[i].solidArea.y;

                switch(entity.direction){
                    case "up" -> entity.solidArea.y -= entity.speed;
                    case "down" -> entity.solidArea.y += entity.speed;
                    case "right" -> entity.solidArea.x += entity.speed;
                    case "left" -> entity.solidArea.x -= entity.speed;
                }

                if(entity.solidArea.intersects(gp.obj[i].solidArea)){
                    if(gp.obj[i].collision){
                        entity.collisionOn = true;
                    }
                    if(player){
                        index = i;
                    }
                }

                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }

        return index;
    }

    // NPC or Monster Collision
    public int checkEntity(Entity entity, Entity[] target){
        int index = 999;

        for(int i = 0; i < target.length; i++){
            if (target[i] != null) {

                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;
                target[i].solidArea.x = target[i].worldX + target[i].solidArea.x;
                target[i].solidArea.y = target[i].worldY + target[i].solidArea.y;

                switch(entity.direction){
                    case "up" -> entity.solidArea.y -= entity.speed;
                    case "down" -> entity.solidArea.y += entity.speed;
                    case "right" -> entity.solidArea.x += entity.speed;
                    case "left" -> entity.solidArea.x -= entity.speed;
                }

                if(entity.solidArea.intersects(target[i].solidArea)){
                    if(target[i] != entity){
                        entity.collisionOn = true;
                        index = i;
                    }
                }

                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                target[i].solidArea.x = target[i].solidAreaDefaultX;
                target[i].solidArea.y = target[i].solidAreaDefaultY;
            }
        }

        return index;
    }

    public boolean checkPlayer(Entity entity){
        boolean contactPlayer = false;

        entity.solidArea.x = entity.worldX + entity.solidArea.x;
        entity.solidArea.y = entity.worldY + entity.solidArea.y;

        gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

        switch(entity.direction){
            case "up" -> entity.solidArea.y -= entity.speed;
            case "down" -> entity.solidArea.y += entity.speed;
            case "right" -> entity.solidArea.x += entity.speed;
            case "left" -> entity.solidArea.x -= entity.speed;
        }

        if(entity.solidArea.intersects(gp.player.solidArea)){
            entity.collisionOn = true;
            contactPlayer = true;
        }

        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;

        return contactPlayer;
    }
}