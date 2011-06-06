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
    public static final int MOD_WIN = 0x000;
    public static final int WM_HOTKEY = 0x0312;

    static {
        BridJ.register();
    }

    public static native boolean RegisterHotKey(Pointer hWnd, int id, int fsModifiers, int vk);
    public static native boolean UnregisterHotKey(Pointer hWnd, int id);
    public static native int GetMessage(Pointer<MSG> lpMsg, Pointer hWnd, int wMsgFilterMin, int wMsgFilterMax);
}

