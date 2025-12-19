package com.finpro.frontend.pools;

import com.finpro.frontend.Bullet;

public class BulletPool extends ObjectPool<Bullet> {
    @Override
    protected Bullet newObject() {
        return new Bullet();
    }
}
