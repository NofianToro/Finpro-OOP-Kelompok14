package com.finpro.frontend.observers;

import com.badlogic.gdx.math.Vector2;

public interface PortalEventListener {
    void onPortalSpawned(Vector2 position, String color);

    void onLinkEstablished();
}
