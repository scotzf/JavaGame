package main;

import entity.Entity;
import entity.Player;
import object.Others;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class GamePanel extends JPanel implements Runnable {

    //SCREEN SETTINGS
    public final int originalTileSize = 16; // 16x16 tile
    public final int scale = 3;
    long drawStart = 0;




    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 16;    // Size of how many tiles is shown vertically
    public final int maxScreenRow = 12;     // Size of how many tiles is shown horizontally
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // WORLD SETTINGS
    public final int  maxWorldCol = 30;
    public final int maxWorldRow = 30;
    public int currentMap = 0;



    //FPS
    int fps = 60;

    //Instantiation
    public TileManager tileM = new TileManager(this);
    Thread gameThread;
    public KeyHanlder keyH = new KeyHanlder(this);
    Sound se = new Sound();
    Sound music =  new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public EventHandler eHandler = new EventHandler(this);
    public PlantManager plantManager = new PlantManager(this);

    // Entity and Objects
    public Player player = new Player(this, keyH);
    public Entity[] eqp = new Entity[100];
    public Others[] obj = new Others[100];
    public Entity[] npc = new Entity[100];
    public ArrayList<Entity> entityList = new ArrayList<>();
    public Entity[] monster = new Entity[10];
    public ArrayList<Entity> projectileList = new ArrayList<>();
    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int characterState = 4;



    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }
    public void setupGame(){
        aSetter.setObject();
        aSetter.setNPC();
        aSetter.setMonster();
        aSetter.setItems();
        playMusic(0);
        gameState = titleState;
    }
    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }
    // Automatically run when GamePanel.java is called
    @Override
    public void run() {

        double drawInterval = 1000000000.00/fps; //0.0166 per seconds
        double nextDrawTime = System.nanoTime() + drawInterval;


        while(gameThread != null){

            // Update
            update();
            //Draw
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime/1000000;

                if(remainingTime < 0){
                    remainingTime = 0;
                }

                Thread.sleep((long)remainingTime);

                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
    public void update(){
        if(gameState == playState){
            // Player
            player.update();
            plantManager.update();
            // Monster update
            for(int i = 0; i < monster.length; i++){
                if(monster[i] != null){
                    if(monster[i].alive && !monster[i].dying){
                        monster[i].update();
                    }
                    if(!monster[i].alive){
                        monster[i] = null;
                    }
                }
            }
            // projectile
            for(int i = 0; i < projectileList.size(); i++){
                if(projectileList.get(i) != null){
                    if(projectileList.get(i).alive){
                        projectileList.get(i).update();
                    }
                    if(!projectileList.get(i).alive){
                        projectileList.remove(i);
                        i--;
                    }
                }
            }
            // NPC
            for(int i = 0; i < npc.length; i++){
                if(npc[i] != null){
                    npc[i].update();
                }
            }
            // UPDATE ENTITY LIST HERE
            entityList.clear();
            entityList.add(player);

            for(int i = 0; i < npc.length; i++){
                if(npc[i] != null){
                    entityList.add(npc[i]);
                }
            }
            for(int i = 0; i < eqp.length; i++){
                if(eqp[i] != null){
                    entityList.add(eqp[i]);
                }
            }
            for(int i = 0; i < monster.length; i++){
                if(monster[i] != null){
                    entityList.add(monster[i]);
                }
            }
            for(int i = 0; i < obj.length; i++){
                if(obj[i] != null){
                    entityList.add(obj[i]);
                }
            }
            for(int i = 0; i < projectileList.size(); i++){
                if(projectileList.get(i) != null){
                    entityList.add(projectileList.get(i));
                }
            }
            // Sort by Y position for proper rendering depth
            entityList.sort(Comparator.comparingInt(e -> e.worldY));
        }
        if(gameState == pauseState){

        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        if(keyH.showDebug){
            drawStart = System.nanoTime();
        }
        //  TITLE SCREEN
        if(gameState == titleState){
            ui.draw(g2);
        }
        // OTHERS
        else{
            // Tile
            tileM.draw(g2);

            // Draw Entity 1 by 1
            for(int i = 0; i < entityList.size(); i++){
                entityList.get(i).draw(g2);
            }

            // Dialogues and UI stuff
            ui.draw(g2);

            // Debug stuff
            if(keyH.showDebug){
                long drawEnd = System.nanoTime() - drawStart;
                double drawTimeMS = drawEnd/1000000.0;
                g2.setFont(new Font("Arial", Font.PLAIN, 20));
                g2.setColor(Color.WHITE);
                int x = 10;
                int y = 400;
                int lineHeight = 20;
                g2.drawString("WorldX: "+player.worldX, x,y);y += lineHeight;
                g2.drawString("WorldY: "+player.worldY, x,y); y += lineHeight;
                g2.drawString("Col: "+(player.worldX + player.solidArea.x)/tileSize, x,y); y += lineHeight;
                g2.drawString("Row: "+(player.worldY + player.solidArea.y)/tileSize, x,y); y += lineHeight;
                g2.drawString(String.format("Draw Time: %.2f ms", drawTimeMS), x, y);
                System.out.printf("Draw Time: %.2fms%n", drawTimeMS);
            }


            g2.dispose();
        }
    }
    public void playMusic(int i){
        music.setFile(i);
        music.play();
        music.loop();
    }
    public void stopMusic(){
        music.stop();

    }
    public void playSE(int i){
        se.setFile(i);
        se.play();

    }
}
