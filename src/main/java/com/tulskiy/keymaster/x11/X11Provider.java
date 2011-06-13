package com.tulskiy.keymaster.x11;

import com.tulskiy.keymaster.common.Provider;
import org.bridj.Pointer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.tulskiy.keymaster.x11.LibX11.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/13/11
 */
public class X11Provider implements Provider {
    private Map<KeyStroke, ActionListener> listeners = Collections.synchronizedMap(new HashMap<KeyStroke, ActionListener>());
    private Pointer<LibX11.XDisplay> display;
    private long window;
    private boolean initialized = false;
    public boolean listening;
    public Thread thread;

    public void init() {
        if (!initialized) {
            logger.info("Loading X11 global hotkey provider");
            display = XOpenDisplay(null);
            XSetErrorHandler(Pointer.pointerTo(new ErrorHandler()));
            window = XDefaultRootWindow(display);
            initialized = true;
        }

        Runnable runnable = new Runnable() {
            public void run() {
                listening = true;
                Pointer<XEvent> ptr = Pointer.allocate(XEvent.class);

                while (listening) {
                    while (XPending(display) > 0) {
                        XNextEvent(display, ptr);

                        XEvent event = ptr.get();
                        if (event.type() == KeyPress) {
                            for (Map.Entry<KeyStroke, ActionListener> entry : listeners.entrySet()) {
                                KeyStroke keyStroke = entry.getKey();
                                int state = event.xkey().state() & (ShiftMask|ControlMask|Mod1Mask|Mod4Mask);
                                if (Converter.getCode(keyStroke, display) == event.xkey().keycode() &&
                                        state == Converter.getModifiers(keyStroke)) {
                                    logger.info("Received event for KeyCode: " + keyStroke.toString());
                                    entry.getValue().actionPerformed(new ActionEvent(keyStroke, 0, ""));
                                }
                            }
                        }
                    }

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                logger.info("Thread - stop listening");
            }
        };

        thread = new Thread(runnable);
        thread.start();
    }

    public void stop() {
        if (thread != null) {
            listening = false;
            try {
                thread.join();
                XCloseDisplay(display);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerMediaKeyListener(ActionListener listener) {
    }

    public void register(KeyStroke keyCode, ActionListener listener) {
        logger.info("Registering hotkey: " + keyCode.toString());
        byte code = Converter.getCode(keyCode, display);
        if (code == 0) {
            return;
        }
        int modifiers = Converter.getModifiers(keyCode);
        for (int i = 0; i < 16; i++) {
            int flags = modifiers;
            if ((i & 1) != 0)
                flags |= LockMask;
            if ((i & 2) != 0)
                flags |= Mod2Mask;
            if ((i & 4) != 0)
                flags |= Mod3Mask;
            if ((i & 8) != 0)
                flags |= Mod5Mask;

            XGrabKey(display, code, flags, window, true, GrabModeAsync, GrabModeAsync);
        }
        listeners.put(keyCode, listener);
    }

    public void reset() {
        logger.info("Reset hotkeys");
        for (KeyStroke keyStroke : listeners.keySet()) {
            int modifiers = Converter.getModifiers(keyStroke);
            byte code = Converter.getCode(keyStroke, display);
            for (int i = 0; i < 16; i++) {
                int flags = modifiers;
                if ((i & 1) != 0)
                    flags |= LockMask;
                if ((i & 2) != 0)
                    flags |= Mod2Mask;
                if ((i & 4) != 0)
                    flags |= Mod3Mask;
                if ((i & 8) != 0)
                    flags |= Mod5Mask;

                XUngrabKey(display, code, flags, window);
            }
        }
    }

    class ErrorHandler extends XErrorHandler {
        @Override
        public int apply(Pointer<XDisplay> DisplayPtr1, Pointer<XErrorEvent> XErrorEventPtr1) {
            Pointer<Byte> ret = Pointer.allocateBytes(1024);
            XGetErrorText(DisplayPtr1, XErrorEventPtr1.get().error_code(), ret, 1024);
            logger.warning("Error: " + ret.getString(Pointer.StringType.C));
            return 0;
        }
    }
}
