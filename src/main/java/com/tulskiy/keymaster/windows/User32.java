package com.tulskiy.keymaster.windows;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
@Library("user32")
public class User32 {
    public static final int MOD_ALT = 0x0001;
    public static final int MOD_CONTROL = 0x0002;
    public static final int MOD_NOREPEAT = 0x4000;
    public static final int MOD_SHIFT = 0x0004;
    public static final int MOD_WIN = 0x0008;
    public static final int WM_HOTKEY = 0x0312;
    public static final int WM_QUIT = 0x0012;
    public static final int VK_MEDIA_NEXT_TRACK = 0xB0;
    public static final int VK_MEDIA_PREV_TRACK = 0xB1;
    public static final int VK_MEDIA_STOP = 0xB2;
    public static final int VK_MEDIA_PLAY_PAUSE = 0xB3;
    public static final int WM_KEYFIRST = 0x0100;
    public static final int WM_KEYLAST = 0x0109;
    public static final int PM_REMOVE = 0x0001;

    static {
        BridJ.register();
    }

    public static native boolean RegisterHotKey(Pointer hWnd, int id, int fsModifiers, int vk);

    public static native boolean UnregisterHotKey(Pointer hWnd, int id);

    public static native int GetMessage(Pointer<MSG> lpMsg, Pointer hWnd, int wMsgFilterMin, int wMsgFilterMax);

    public static native int PeekMessage(Pointer<MSG> lpMsg, Pointer hWnd, int wMsgFilterMin, int wMsgFilterMax, int wRemoveMsg);
}

