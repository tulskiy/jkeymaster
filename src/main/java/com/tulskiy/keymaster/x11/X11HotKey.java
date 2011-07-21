package com.tulskiy.keymaster.x11;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.MediaKey;

import javax.swing.*;

/**
* Author: Alex (mq0) Ivanov at 21.07.11 18:14
*/
public class X11HotKey extends HotKey {
    public final byte code;
    public final int modifiers;

    public X11HotKey(KeyStroke keyStroke, byte code, int modifiers) {
        super(keyStroke);
        this.code = code;
        this.modifiers = modifiers;
    }

    public X11HotKey(MediaKey mediaKey, byte code) {
        super(mediaKey);
        this.modifiers = 0;
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof X11HotKey)) return false;

        X11HotKey x11HotKey = (X11HotKey) o;

        if (code != x11HotKey.code) return false;
        if (modifiers != x11HotKey.modifiers) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 31 * code + modifiers;
    }
}
