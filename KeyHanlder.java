package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHanlder implements KeyListener {
    GamePanel gp;

    public boolean upPressed, downPressed,leftPressed, rightPressed, enterPressed, shotKeyPressed;
    public boolean showDebug = false;
    public boolean harvestKeyPressed;

    public KeyHanlder(GamePanel gp){
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();
        // TITLE STATE
        if(gp.gameState == gp.titleState){
            titleState(code);
        }
        // PLAY STATE
        else if(gp.gameState == gp.playState){
            playState(code);
        }
        // PAUSE STATE
        else if(gp.gameState == gp.pauseState){
            pauseState(code);
        }
        // DIALOGUE STATE
        else if(gp.gameState == gp.dialogueState){
            dialogueState(code);
        }
        // Character State
        else if (gp.gameState == gp.characterState){
            characterState(code);
        }

    }
    public void titleState(int code){
        if(code == KeyEvent.VK_W){
            gp.ui.commandNum--;
            if(gp.ui.commandNum < 0){
                gp.ui.commandNum = 2;
            }

        }
        if(code == KeyEvent.VK_S){
            gp.ui.commandNum++;
            if(gp.ui.commandNum > 2){
                gp.ui.commandNum = 0;
            }
        }
        if(code == KeyEvent.VK_ENTER){
            if(gp.ui.commandNum == 0){
                gp.gameState = gp.playState;
            }
            if(gp.ui.commandNum == 1){
                // Load
            }
            if(gp.ui.commandNum == 2){
                System.exit(0);
            }
        }
    }
    public void playState(int code){
        if(code == KeyEvent.VK_W){
            upPressed = true;
        }
        if(code == KeyEvent.VK_S){
            downPressed = true;
        }
        if(code == KeyEvent.VK_A){
            leftPressed = true;
        }
        if(code == KeyEvent.VK_D){
            rightPressed = true;
        }
        if(code == KeyEvent.VK_T){
            if(showDebug){
                showDebug = false;
            }
            else{
                showDebug = true;
            }
        }
        if(code == KeyEvent.VK_P){
            gp.gameState = gp.pauseState;
        }
        if(code == KeyEvent.VK_C){
            gp.gameState = gp.characterState;
        }
        if(code == KeyEvent.VK_ENTER){
            enterPressed = true;
        }
        if(code == KeyEvent.VK_F){
            //shotKeyPressed = true;
        }
        if(code == KeyEvent.VK_H) {
            harvestKeyPressed = true;
            gp.player.harvestCrop();
        }
    }
    public void pauseState(int code){
        if(code == KeyEvent.VK_P){
            gp.gameState = gp.playState;
        }
    }
    public void characterState(int code){
        if(code == KeyEvent.VK_C){
            gp.gameState = gp.playState;
            gp.ui.inventoryIndex = 0;
            gp.ui.slotRow = 0;
            gp.ui.slotCol = 0;
        }
        if(code == KeyEvent.VK_RIGHT){
            if(gp.ui.inventoryIndex == 2 ){
                gp.ui.inventoryIndex = 0;
            }
            else{
                gp.ui.inventoryIndex++;
            }
        }
        else if(code == KeyEvent.VK_LEFT){
            if(gp.ui.inventoryIndex == 0){
                gp.ui.inventoryIndex = 2;
            }
            else{
                gp.ui.inventoryIndex--;
            }
        }
        if(code == KeyEvent.VK_W){
            if(gp.ui.slotRow != 0){
                gp.ui.slotRow--;
                gp.playSE(9);
            }
        }
        if(code == KeyEvent.VK_S){
            if(gp.ui.slotRow != 5){
                gp.ui.slotRow++;
                gp.playSE(9);
            }
        }
        if(code == KeyEvent.VK_A){
            if(gp.ui.slotCol != 0){
                gp.ui.slotCol--;
                gp.playSE(9);
            }
        }
        if(code == KeyEvent.VK_D){
            if(gp.ui.slotCol != 5){
                gp.ui.slotCol++;
                gp.playSE(9);
            }
        }
        if(code == KeyEvent.VK_ENTER){
            if(gp.ui.inventoryIndex == 0){
                gp.player.selectItem(0);
            }
            if(gp.ui.inventoryIndex == 1){
                gp.player.selectItem(1);
            }
        }

    }
    public void dialogueState(int code){
        if(code == KeyEvent.VK_ENTER){
            gp.gameState = gp.playState;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W){
            upPressed = false;
        }
        if(code == KeyEvent.VK_S){
            downPressed = false;
        }
        if(code == KeyEvent.VK_A){
            leftPressed = false;
        }
        if(code == KeyEvent.VK_D){
            rightPressed = false;
        }
        if(code == KeyEvent.VK_F){
            shotKeyPressed = false;
        }
        if(code == KeyEvent.VK_H) { harvestKeyPressed = false; }

    }
}

