package com.finpro.frontend.factories;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.finpro.frontend.obstacles.Wall;

public class LevelFactory {
    // 32 px per Square
    private final float TILE_SIZE = 32f;
    private Vector2 startPosition;

    // Map Legends (Lebar 40 karakter, Tinggi 22 baris)
    // # = Wall
    // - = Air
    // S = Spawn
    // E = Exit
    private String[] mapLayout = {
            "########################################",
            "#----#----------#------#--------------E#",
            "#----#----------#------#--------------E#",
            "#----#----------#------#--------------E#",
            "#----#----------#------#--------------##",
            "#----#----------#------#---------#######",
            "######----------#------#---------#-----#",
            "#---------------#------#---------#-----#",
            "#---------------########---------#-----#",
            "#--------------------------------#-----#",
            "#--------------------------------#-----#",
            "#--------------------------------#-----#",
            "#--------------------------------#-----#",
            "#-----#------#-------------------#-----#",
            "#-----#------#-------------------#-----#",
            "#-----#------#-------------------#-----#",
            "#-----#------#-------#######-----#-----#",
            "#-----#--S---#-------#-----#-----#-----#",
            "#-----#------#-------#-----#-----#-----#",
            "#-----########-------#-----#-----#-----#",
            "#-----#------#-------#-----#-----#-----#",
            "########################################"
    };

    public Array<Wall> createLevel() {
        Array<Wall> walls = new Array<>();
        startPosition = new Vector2(100, 100);

        for (int row = 0; row < mapLayout.length; row++) {
            String line = mapLayout[row];
            int cols = line.length();

            for (int col = 0; col < cols; col++) {
                char code = line.charAt(col);

                float x = col * TILE_SIZE;
                float y = (mapLayout.length - 1 - row) * TILE_SIZE;

                switch (code) {
                    case '#': // Wall
                        walls.add(new Wall(x, y, TILE_SIZE, false));
                        break;
                    case 'S': // Spawn
                        startPosition = new Vector2(x, y);
                        break;
                    case 'E': // Exit
                        walls.add(new Wall(x, y, TILE_SIZE, true));
                        break;
                    case '-':
                    default:
                        break;
                }
            }
        }
        return walls;
    }

    public Vector2 getStartPosition() {
        return startPosition;
    }
}
