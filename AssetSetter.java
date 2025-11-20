package main;

import entity.NPC_Old_Man;
import monster.MON_Cow;
import object.*;

public class AssetSetter {

    GamePanel gp;

    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }

    public void setObject(){
        // Equipment that spawns in the world
        gp.eqp[0] = new OBJ_Sword(gp);
        gp.eqp[0].worldX = gp.tileSize * 15;
        gp.eqp[0].worldY = gp.tileSize * 10;
    }

    public void setItems(){
        // Consumable items that spawn in the world
        gp.obj[0] = new Potion(gp);
        gp.obj[0].worldX = gp.tileSize * 5;
        gp.obj[0].worldY = gp.tileSize * 4;

        // Add more items if needed
        // gp.obj[1] = new potion(gp);
        // gp.obj[1].worldX = gp.tileSize * 8;
        // gp.obj[1].worldY = gp.tileSize * 6;
    }

    public void setNPC(){
        gp.npc[0] = new NPC_Old_Man(gp);
        gp.npc[0].worldX = gp.tileSize * 6;
        gp.npc[0].worldY = gp.tileSize * 3;
    }

    public void setMonster(){
         //Uncomment when ready to add monsters
         gp.monster[0] = new MON_Cow(gp);
         gp.monster[0].worldX = gp.tileSize * 11;
         gp.monster[0].worldY = gp.tileSize * 11;

        // gp.monster[1] = new MON_Cow(gp);
        // gp.monster[1].worldX = gp.tileSize * 12;
        // gp.monster[1].worldY = gp.tileSize * 11;

        // gp.monster[2] = new MON_Cow(gp);
        // gp.monster[2].worldX = gp.tileSize * 13;
        // gp.monster[2].worldY = gp.tileSize * 11;
    }
}