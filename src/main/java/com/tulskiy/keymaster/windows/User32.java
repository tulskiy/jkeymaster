package com.tulskiy.keymaster.windows;

import com.sun.jna.*;
import com.sun.jna.win32.W32APIOptions;

/**
 * Author: Denis Tulskiy
 * Date: 6/15/11
 */
public class User32 {
    static {
        Native.register(NativeLibrary.getInstance("user32", W32APIOptions.DEFAULT_OPTIONS));
    }

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

    public static native boolean RegisterHotKey(Pointer hWnd, int id, int fsModifiers, int vk);

    public static native boolean UnregisterHotKey(Pointer hWnd, int id);

    public static native boolean PeekMessage(MSG lpMsg, Pointer hWnd, int wMsgFilterMin, int wMsgFilterMax, int wRemoveMsg);

    /**
     * Defines the x- and y-coordinates of a point.
     */
    public static class POINT extends Structure {
        public int x, y;

        public POINT() {
        }

        public POINT(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class MSG extends Structure {
        public HWND hWnd;
        public int message;
        public WPARAM wParam;
        public LPARAM lParam;
        public int time;
        public POINT pt;
    }

    /**
     * Handle to a window.
     */
    public static class HWND extends PointerType {
    }

    /**
     * Unsigned INT_PTR.
     */
    public static class UINT_PTR extends IntegerType {
        public UINT_PTR() {
            super(Pointer.SIZE);
        }

        public UINT_PTR(long value) {
            super(Pointer.SIZE, value);
        }

        public Pointer toPointer() {
            return Pointer.createConstant(longValue());
        }
    }

    /**
     * Message parameter.
     */
    public static class WPARAM extends UINT_PTR {
        public WPARAM() {
            this(0);
        }

        public WPARAM(long value) {
            super(value);
        }
    }

    /**
     * Signed long type for pointer precision.
     * Use when casting a pointer to a long to perform pointer arithmetic.
     */
    public static class LONG_PTR extends IntegerType {
        public LONG_PTR() {
            this(0);
        }

        public LONG_PTR(long value) {
            super(Pointer.SIZE, value);
        }
    }

    /**
	 * Message parameter.
	 */
	public static class LPARAM extends LONG_PTR {
		public LPARAM() {
			this(0);
		}

		public LPARAM(long value) {
			super(value);
		}
	}

}
