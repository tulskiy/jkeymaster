package com.tulskiy.keymaster.x11;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.CLong;
import org.bridj.ann.Library;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
@Library("libX11")
public class LibX11 {
    public static final int ShiftMask = (1 << 0);
    public static final int Mod1Mask = (1 << 3);
    public static final int LockMask = (int) (1 << 1);
    public static final int ControlMask = (int) (1 << 2);
    public static final int Mod4Mask = (int) (1 << 6);
    public static final int AnyModifier = (1 << 15);
    public static final int GrabModeAsync = 1;
    public static final int KeyPress = 2;

    public static final int XF86AUDIO_VOLUP = 176;
    public static final int XF86AUDIO_VOLDN = 174;
    public static final int XF86AUDIO_MEDIA = 129;
    public static final int XF86AUDIO_PLAY = 162;
    public static final int XF86AUDIO_STOP = 164;
    public static final int XF86AUDIO_NEXT = 153;
    public static final int XF86AUDIO_PREV = 144;

    static {
        BridJ.addLibraryPath("/usr/lib/i386-linux-gnu");
        BridJ.register();
    }

    public static native Pointer<XDisplay> XOpenDisplay(Pointer<Byte> charPtr1);

    @CLong
    public static native long XDefaultRootWindow(Pointer<XDisplay> DisplayPtr1);

    public static native byte XKeysymToKeycode(Pointer<XDisplay> DisplayPtr1, long keysym);

    @CLong
    public static native long XStringToKeysym(Pointer<Byte> code);

    public static native int XGrabKey(Pointer<XDisplay> DisplayPtr1, int code, int modifiers, long root, boolean ownerEvents, int pointerMode, int keyBoardMode);

    public static native int XUngrabKey(Pointer<XDisplay> DisplayPtr1, int code, int modifiers, long window);

    public static native int XNextEvent(Pointer<XDisplay> DisplayPtr1, Pointer<XEvent> XEventPtr1);

    public static interface XDisplay {
    }
}
