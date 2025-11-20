package main;

import object.Plant;
import java.util.ArrayList;
import java.util.Iterator;

public class PlantManager {

    GamePanel gp;
    public ArrayList<Plant> plantList;

    public PlantManager(GamePanel gp) {
        this.gp = gp;
        this.plantList = new ArrayList<>();
    }

    public void update() {
        // Update all plants
        for(Plant plant : plantList) {
            plant.update();
        }
    }

    public void addPlant(int col, int row, int originalPlotType) {  // ← FIXED: Added 3rd parameter
        // Check if there's already a plant at this location
        if(!isPlantAt(col, row)) {
            Plant newPlant = new Plant(gp, col, row, originalPlotType);  // ← FIXED: Pass originalPlotType
            plantList.add(newPlant);

            // Immediately set tile based on plot type
            int seedTile = 0;
            if(originalPlotType == 13) {  // plot_top
                seedTile = 53;  // plot_top_seed
            } else if(originalPlotType == 14) {  // plot (middle)
                seedTile = 57;  // plot_seed
            } else if(originalPlotType == 15) {  // plot_bottom
                seedTile = 61;  // plot_bottom_seed
            }

            gp.tileM.mapTileNumber[gp.currentMap][col][row] = seedTile;
        }
    }

    public boolean isPlantAt(int col, int row) {
        for(Plant plant : plantList) {
            if(plant.col == col && plant.row == row) {
                return true;
            }
        }
        return false;
    }

    public Plant getPlantAt(int col, int row) {
        for(Plant plant : plantList) {
            if(plant.col == col && plant.row == row) {
                return plant;
            }
        }
        return null;
    }

    public void removePlant(int col, int row) {
        Iterator<Plant> iterator = plantList.iterator();
        while(iterator.hasNext()) {
            Plant plant = iterator.next();
            if(plant.col == col && plant.row == row) {
                iterator.remove();
                break;
            }
        }
    }
}