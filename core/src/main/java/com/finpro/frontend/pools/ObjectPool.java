package com.finpro.frontend.pools;

import com.badlogic.gdx.utils.Array;

public abstract class ObjectPool<T> {
    private final Array<T> freeObjects;

    public ObjectPool() {
        this.freeObjects = new Array<>();
    }

    protected abstract T newObject();

    public T obtain() {
        return (freeObjects.size == 0) ? newObject() : freeObjects.pop();
    }

    public void free(T object) {
        if (object == null)
            throw new IllegalArgumentException("object cannot be null.");
        freeObjects.add(object);
    }

    public void clear() {
        freeObjects.clear();
    }
}
