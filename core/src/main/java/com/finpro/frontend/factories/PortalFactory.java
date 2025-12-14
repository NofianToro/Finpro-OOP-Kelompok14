package com.finpro.frontend.factories;

import com.finpro.frontend.Portal;
import com.finpro.frontend.pools.PortalPool;

public class PortalFactory {
    private PortalPool portalPool;

    public PortalFactory(PortalPool portalPool) {
        this.portalPool = portalPool;
    }

    public Portal createPortal(float x, float y, String type, boolean isHorizontal, float velocityX, float velocityY) {
        Portal portal = portalPool.obtain();

        // Calculate Normal Vector (Direction pointing OUT of the wall)
        // If projectile hit wall moving RIGHT (velX > 0), normal should be LEFT (-1, 0)
        // If projectile hit floor moving DOWN (velY < 0), normal should be UP (0, 1)
        float nx = 0, ny = 0;

        if (isHorizontal) {
            ny = velocityY > 0 ? -1 : 1;
        } else {
            nx = velocityX > 0 ? -1 : 1;
        }

        portal.init(x, y, type, isHorizontal ? Portal.Orientation.HORIZONTAL : Portal.Orientation.VERTICAL,
                new com.badlogic.gdx.math.Vector2(nx, ny));
        return portal;
    }
}
