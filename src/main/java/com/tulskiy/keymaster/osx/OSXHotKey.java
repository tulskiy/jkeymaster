package com.tulskiy.keymaster.osx;

import com.sun.jna.ptr.PointerByReference;
import com.tulskiy.keymaster.common.HotKey;

import javax.swing.*;

/**
* Author Alex (mq0) Ivanov at 21.07.11 18:54
*/
public class OSXHotKey extends HotKey {
    public final PointerByReference handler;
    public final long id;

    public OSXHotKey(KeyStroke keyStroke, PointerByReference handler, long id) {
        super(keyStroke);
        this.handler = handler;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OSXHotKey)) return false;

        OSXHotKey osxHotKey = (OSXHotKey) o;

        if (id != osxHotKey.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
