package com.finpro.frontend.factories;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.finpro.frontend.obstacles.Wall;
import com.finpro.frontend.obstacles.Turret;
import com.finpro.frontend.pools.BulletPool;

public class LevelFactory {
    private TiledMap map;

    public LevelFactory(TiledMap map) {
        this.map = map;
    }

    public Array<Wall> parseWalls() {
        Array<Wall> walls = new Array<>();

        // Parse Common Wall
        String[] normalLayers = { "Wall", "Ground" };
        for (String layerName : normalLayers) {
            MapLayer layer = map.getLayers().get(layerName);
            if (layer != null) {
                for (MapObject object : layer.getObjects()) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        walls.add(new Wall(rect.x, rect.y, rect.width, rect.height));
                    }
                }
            }
        }

        // Parse Exit
        MapLayer exitLayer = map.getLayers().get("Exit");
        if (exitLayer != null) {
            for (MapObject object : exitLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    Wall exitWall = new Wall(rect.x, rect.y, rect.width, rect.height);
                    exitWall.setExit(true);
                    walls.add(exitWall);
                }
            }
        } else {
            System.out.println("WARNING: Layer 'Exit' tidak ditemukan di Map!");
        }

        // Parse BlackwWall (Cannot Spawn Portal)
        MapLayer blackWallLayer = map.getLayers().get("BlackWall");
        if (blackWallLayer != null) {
            for (MapObject object : blackWallLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();

                    Wall bw = new Wall(rect.x, rect.y, rect.width, rect.height);
                    bw.setBlackWall(true);
                    walls.add(bw);
                }
            }
        }

        return walls;
    }

    public Vector2 getPlayerSpawnPoint(TiledMap map) {
        MapLayer spawnLayer = map.getLayers().get("Spawn");
        if (spawnLayer != null && spawnLayer.getObjects().getCount() > 0) {
            MapObject obj = spawnLayer.getObjects().get(0);
            if (obj instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                return new Vector2(rect.x, rect.y);
            }
        }
        return new Vector2(100, 200);
    }

    public Array<Turret> parseTurrets(BulletPool pool) {
        Array<Turret> turrets = new Array<>();
        MapLayer layer = map.getLayers().get("Turret"); // Assuming layer name is "Turret"
        if (layer != null) {
            for (MapObject object : layer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    turrets.add(new Turret(rect.x, rect.y, pool));
                }
            }
        }
        return turrets;
    }
}
