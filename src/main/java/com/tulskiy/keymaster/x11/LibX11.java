package com.tulskiy.keymaster.x11;

import com.sun.jna.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/15/11
 */
public class LibX11 {
    static {
        Native.register("X11");
    }

    public static final int AnyModifier = (1 << 15);
    public static final int GrabModeAsync = 1;
    public static final int KeyPress = 2;

    public static final int ShiftMask = (1 << 0);
    public static final int LockMask = (1 << 1);
    public static final int ControlMask = (1 << 2);
    public static final int Mod1Mask = (1 << 3);
    public static final int Mod2Mask = (1 << 4);
    public static final int Mod3Mask = (1 << 5);
    public static final int Mod4Mask = (1 << 6);
    public static final int Mod5Mask = (1 << 7);

    public static final int XF86AUDIO_VOLUP = 176;
    public static final int XF86AUDIO_VOLDN = 174;
    public static final int XF86AUDIO_MEDIA = 129;
    public static final int XF86AUDIO_PLAY = 162;
    public static final int XF86AUDIO_STOP = 164;
    public static final int XF86AUDIO_NEXT = 153;
    public static final int XF86AUDIO_PREV = 144;

    public static native Display XOpenDisplay(String name);

    public static native Window XDefaultRootWindow(Display display);

    public static native byte XKeysymToKeycode(Display display, long keysym);

    public static native int XGrabKey(Display display, int code, int modifiers, Window root, boolean ownerEvents, int pointerMode, int keyBoardMode);

    public static native int XUngrabKey(Display display, int code, int modifiers, Window window);

    public static native int XNextEvent(Display display, XEvent event);

    public static native int XPending(Display display);

    public static native int XCloseDisplay(Display display);

    public static native XErrorHandler XSetErrorHandler(XErrorHandler errorHandler);

    public static native int XGetErrorText(Display display, int code, byte[] buffer, int len);

    public interface XErrorHandler extends Callback {
        int apply(Display display, XErrorEvent errorEvent);

    }

    public static class Display extends PointerType {
    }

    public static class XEvent extends Union {
        public int type;
        public XKeyEvent xkey;
        public NativeLong[] pad = new NativeLong[24];
    }

    public static class XKeyEvent extends Structure {
        public int type;            // of event
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // public Display the event was read from
        public Window window;         // "event" window it is reported relative to
        public Window root;           // root window that the event occurred on
        public Window subwindow;      // child window
        public NativeLong time;     // milliseconds
        public int x, y;            // pointer x, y coordinates in event window
        public int x_root, y_root;  // coordinates relative to root
        public int state;           // key or button mask
        public int keycode;         // detail
        public int same_screen;     // same screen flag
    }

    public static class XErrorEvent extends Structure {
        public int type;
        public Display display;     // Display the event was read from
        public NativeLong resourceid;     // resource id
        public NativeLong serial;   // serial number of failed request
        public byte error_code;     // error code of failed request
        public byte request_code;   // Major op-code of failed request
        public byte minor_code;     // Minor op-code of failed request
    }

    public static class Window extends NativeLong {
        private static final long serialVersionUID = 1L;
        public static final Window None = null;

        public Window() {
        }

        public Window(long id) {
            super(id);
        }

        protected boolean isNone(Object o) {
            return o == null
                    || (o instanceof Number
                    && ((Number) o).longValue() == 0);
        }


        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue))
                return None;
            return new Window(((Number) nativeValue).longValue());
        }
    }
}
