package com.finpro.frontend.pools;

import com.finpro.frontend.Portal;

public class PortalPool extends ObjectPool<Portal> {
    @Override
    protected Portal newObject() {
        return new Portal();
    }
}
