package com.finpro.frontend.pools;

import com.finpro.frontend.Projectile;

public class ProjectilePool extends ObjectPool<Projectile> {
    @Override
    protected Projectile newObject() {
        return new Projectile();
    }
}
