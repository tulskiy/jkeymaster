package com.tulskiy.keymaster.x11;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

import javax.swing.*;

import static com.tulskiy.keymaster.x11.X11.*;

/**
 * Author Alex (mq0) Ivanov at 21.07.11 18:13
 */
public class X11Helper {
    public static synchronized void registerKeyStroke(Pointer display, NativeLong window, X11HotKey hotKey) {
        for (int i = 0; i < 16; i++) {
            int flags = createX11Modifiers(hotKey.modifiers, i);
            Lib.XGrabKey(display, hotKey.code, flags, window, 1, GrabModeAsync, GrabModeAsync);
        }
    }

    public static synchronized void registerMedia(Pointer display, NativeLong window, X11HotKey hotKey) {
        Lib.XGrabKey(display, hotKey.code, 0, window, 1, GrabModeAsync, GrabModeAsync);
    }

    public static int createX11Modifiers(int modifiers, int flags) {
        int ret = modifiers;
        if ((flags & 1) != 0)
            ret |= LockMask;
        if ((flags & 2) != 0)
            ret |= Mod2Mask;
        if ((flags & 4) != 0)
            ret |= Mod3Mask;
        if ((flags & 8) != 0)
            ret |= Mod5Mask;
        return ret;
    }

    public static byte createX11CodeForKeyStroke(KeyStroke keyStroke, Pointer display) {
        byte code = KeyMap.getCode(keyStroke, display);
        if (code == 0) {
            throw new IllegalArgumentException("Can't map keyStroke " + keyStroke + " to valid X11 code");
        }
        return code;
    }
}
