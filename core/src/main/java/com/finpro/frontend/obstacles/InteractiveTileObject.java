package com.finpro.frontend.obstacles;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.finpro.frontend.utils.Constants;
import com.badlogic.gdx.physics.box2d.Fixture;

public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;

    protected Fixture fixture;

    public InteractiveTileObject(World world, TiledMap map, Rectangle bounds) {
        this.world = world;
        this.map = map;
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / Constants.PPM,
                (bounds.getY() + bounds.getHeight() / 2) / Constants.PPM);

        body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth() / 2 / Constants.PPM,
                bounds.getHeight() / 2 / Constants.PPM);
        fdef.shape = shape;

        // UBAH BARIS INI (Simpan hasilnya ke variabel fixture)
        // Dari: body.createFixture(fdef);
        // Menjadi:
        this.fixture = body.createFixture(fdef);
    }
}
