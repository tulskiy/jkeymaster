package com.tulskiy.keymaster.x11;

import org.bridj.Pointer;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import static com.tulskiy.keymaster.x11.KeySymDef.*;
import static com.tulskiy.keymaster.x11.LibX11.*;
import static java.awt.event.KeyEvent.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/13/11
 */
public class Converter {
    private static final HashMap<Integer, Integer> common = new HashMap<Integer, Integer>() {{
        put(VK_ESCAPE, XK_Escape);
        put(VK_BACK_SPACE, XK_BackSpace);
        put(VK_TAB, XK_Tab);
        put(VK_ENTER, XK_Return);
        put(VK_PAUSE, XK_Pause);
        put(VK_SCROLL_LOCK, XK_Scroll_Lock);
        put(VK_DELETE, XK_Delete);
    }};

    public static byte getCode(KeyStroke keyStroke, Pointer<XDisplay> display) {
        int code = keyStroke.getKeyCode();

        int ret = -1;
        if ((code >= VK_0 && code <= VK_9) || (code >= VK_A && code <= VK_Z)) {
            ret = code;
        } else if (code >= VK_F1 && code <= VK_F12) {
            ret = code - (VK_F1 - XK_F1);
        } else if (code >= VK_NUMPAD0 && code <= VK_NUMPAD9) {
            ret = code - (VK_NUMPAD0 - XK_KP_0);
        } else {
            Integer i = common.get(code);
            if (i != null) {
                ret = i;
            }
        }

        if (ret != -1) {
            return XKeysymToKeycode(display, ret);
        } else {
            return 0;
        }
    }

    public static int getModifiers(KeyStroke keyCode) {
        int modifiers = 0;
        if ((keyCode.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0) {
            modifiers |= ShiftMask;
        }
        if ((keyCode.getModifiers() & InputEvent.CTRL_DOWN_MASK) != 0) {
            modifiers |= ControlMask;
        }
        if ((keyCode.getModifiers() & InputEvent.META_DOWN_MASK) != 0) {
            modifiers |= Mod4Mask;
        }
        if ((keyCode.getModifiers() & InputEvent.ALT_DOWN_MASK) != 0) {
            modifiers |= Mod1Mask;
        }
        return modifiers;
    }
}
