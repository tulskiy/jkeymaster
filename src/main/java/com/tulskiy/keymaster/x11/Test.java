package com.tulskiy.keymaster.x11;


import com.sun.org.apache.xerces.internal.impl.dv.xs.AnyURIDV;
import com.tulskiy.keymaster.x11.LibX11;
import com.tulskiy.keymaster.x11.XEvent;
import org.bridj.Pointer;

import java.awt.*;

import static com.tulskiy.keymaster.x11.LibX11.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
public class Test {
    public static void main(String[] args) {
        Pointer<LibX11.XDisplay> display = XOpenDisplay(null);
        long window = XDefaultRootWindow(display);

        Pointer<Byte> pString = Pointer.pointerToCString("XF86AudioPlay");
        byte keyCode = XKeysymToKeycode(display, XStringToKeysym(pString));

        int modifiers = 0;
        XGrabKey(display, 176, modifiers, window, true, GrabModeAsync, GrabModeAsync);

        Pointer<XEvent> ptr = Pointer.allocate(XEvent.class);
        while (true) {
            XNextEvent(display, ptr);

            XEvent event = ptr.get();
            if (event.type() == KeyPress
                    && event.xkey().keycode() == keyCode
                    && event.xkey().state() == modifiers) {
                System.out.println("Fuck yeah");
                XUngrabKey(display, keyCode, modifiers, window);
                break;
            }
        }
    }
}
