package com.olav.logolicious.util.cacher;

import android.graphics.Bitmap;
import android.util.LruCache;

public class PhotoCache extends LruCache<String, Bitmap> {
    public PhotoCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getByteCount();
    }
}
