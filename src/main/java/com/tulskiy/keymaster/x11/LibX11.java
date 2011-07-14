/*
 * Copyright (c) 2011 Denis Tulskiy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tulskiy.keymaster.x11;

import com.sun.jna.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/15/11
 */
@SuppressWarnings({"UnusedDeclaration"})
public class LibX11 {
    static {
        Native.register("X11");
    }

    public static final int GrabModeAsync = 1;
    public static final int KeyPress = 2;

    public static final int ShiftMask = (1);
    public static final int LockMask = (1 << 1);
    public static final int ControlMask = (1 << 2);
    public static final int Mod1Mask = (1 << 3);
    public static final int Mod2Mask = (1 << 4);
    public static final int Mod3Mask = (1 << 5);
    public static final int Mod4Mask = (1 << 6);
    public static final int Mod5Mask = (1 << 7);

    public static native Pointer XOpenDisplay(String name);

    public static native NativeLong XDefaultRootWindow(Pointer display);

    public static native byte XKeysymToKeycode(Pointer display, long keysym);

    public static native int XGrabKey(Pointer display, int code, int modifiers, NativeLong root, boolean ownerEvents, int pointerMode, int keyBoardMode);

    public static native int XUngrabKey(Pointer display, int code, int modifiers, NativeLong NativeLong);

    public static native int XNextEvent(Pointer display, XEvent event);

    public static native int XPending(Pointer display);

    public static native int XCloseDisplay(Pointer display);

    public static native XErrorHandler XSetErrorHandler(XErrorHandler errorHandler);

    public static native int XGetErrorText(Pointer display, int code, byte[] buffer, int len);

    public interface XErrorHandler extends Callback {
        public int apply(Pointer display, XErrorEvent errorEvent);
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
        public Pointer display;     // public Display the event was read from
        public NativeLong window;         // "event" window it is reported relative to
        public NativeLong root;           // root window that the event occurred on
        public NativeLong subwindow;      // child window
        public NativeLong time;     // milliseconds
        public int x, y;            // pointer x, y coordinates in event window
        public int x_root, y_root;  // coordinates relative to root
        public int state;           // key or button mask
        public int keycode;         // detail
        public int same_screen;     // same screen flag
    }

    public static class XErrorEvent extends Structure {
        public int type;
        public Pointer display;     // Display the event was read from
        public NativeLong resourceid;     // resource id
        public NativeLong serial;   // serial number of failed request
        public byte error_code;     // error code of failed request
        public byte request_code;   // Major op-code of failed request
        public byte minor_code;     // Minor op-code of failed request
    }
}
