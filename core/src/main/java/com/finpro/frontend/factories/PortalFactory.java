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

        // Portal direction pointing OUT of the wall
        // (velocityX > 0)  LEFT (-1, 0)
        // (velocityY < 0)  UP (0, 1)
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
