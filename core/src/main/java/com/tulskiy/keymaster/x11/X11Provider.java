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

import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.Window;
import com.sun.jna.platform.unix.X11.XErrorEvent;
import com.sun.jna.platform.unix.X11.XErrorHandler;
import com.sun.jna.platform.unix.X11.XEvent;
import com.sun.jna.platform.unix.X11.XKeyEvent;
import com.sun.jna.ptr.IntByReference;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.KeyStroke;

/**
 * Author: Denis Tulskiy
 * Date: 6/13/11
 */
public class X11Provider extends Provider {
    private static final Logger LOGGER = LoggerFactory.getLogger(X11Provider.class);
    private Display display;
    private Window window;
    private boolean listening;
    private Thread thread;
    private boolean reset;
    private ErrorHandler errorHandler;
    private final Object lock = new Object();
    private Queue<X11HotKey> registerQueue = new LinkedList<X11HotKey>();
    private List<X11HotKey> hotKeys = new ArrayList<X11HotKey>();

    public void init() {
        Runnable runnable = new Runnable() {
            public void run() {
                LOGGER.info("Starting X11 global hotkey provider");
                display = X11.INSTANCE.XOpenDisplay(null);
                errorHandler = new ErrorHandler();
                X11.INSTANCE.XSetErrorHandler(errorHandler);
                window = X11.INSTANCE.XDefaultRootWindow(display);
                listening = true;
                XEvent event = new XEvent();

                IntByReference supported_rtrn = new IntByReference();
                X11Ext.Lib.XkbSetDetectableAutoRepeat(display, true, supported_rtrn);
                if (supported_rtrn.getValue() != 1) {
                    LOGGER.warn("auto repeat detection not supported");
                }

                while (listening) {
                    while (X11.INSTANCE.XPending(display) > 0) {
                        X11.INSTANCE.XNextEvent(display, event);
                        if (event.type == X11.KeyPress || event.type == X11.KeyRelease) {
                            processEvent(event);
                        }
                    }

                    synchronized (lock) {
                        if (reset) {
                            LOGGER.info("Reset hotkeys");
                            resetAll();
                            reset = false;
                            lock.notify();
                        }

                        while (!registerQueue.isEmpty()) {
                            X11HotKey hotKey = registerQueue.poll();
                            LOGGER.info("Registering hotkey: " + hotKey);
                            if (hotKey.isMedia()) {
                                registerMedia(hotKey);
                            } else {
                                register(hotKey);
                            }
                            hotKeys.add(hotKey);
                        }

                        try {
                            lock.wait(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }

                LOGGER.info("Thread - stop listening");
            }

            private void processEvent(XEvent event) {
                XKeyEvent xkey = (XKeyEvent) event.readField("xkey");
                for (X11HotKey hotKey : hotKeys) {
                    int state = xkey.state & (X11.ShiftMask | X11.ControlMask | X11.Mod1Mask | X11.Mod4Mask);
                    int eventType = hotKey.keyStroke.isOnKeyRelease() ? X11.KeyRelease : X11.KeyPress;

                    if (hotKey.code == (byte) xkey.keycode
                            && hotKey.modifiers == state
                            && event.type == eventType) {
                        LOGGER.info("Received event for hotkey: " + hotKey);
                        fireEvent(hotKey);
                        break;
                    }
                }
            }
        };

        thread = new Thread(runnable);
        thread.start();
    }

    private void register(X11HotKey hotKey) {
        byte code = KeyMap.getCode(hotKey.keyStroke, display);
        if (code == 0) {
            LOGGER.warn("Could not find mapping for " + hotKey.keyStroke);
            return;
        }
        int modifiers = KeyMap.getModifiers(hotKey.keyStroke);
        hotKey.code = code;
        hotKey.modifiers = modifiers;
        for (int i = 0; i < 16; i++) {
            int flags = correctModifiers(modifiers, i);

            X11.INSTANCE.XGrabKey(display, code, flags, window, 1, X11.GrabModeAsync, X11.GrabModeAsync);
        }
    }

    private void registerMedia(X11HotKey hotKey) {
        byte keyCode = KeyMap.getMediaCode(hotKey.mediaKey, display);
        hotKey.modifiers = 0;
        hotKey.code = keyCode;
        X11.INSTANCE.XGrabKey(display, keyCode, 0, window, 1, X11.GrabModeAsync, X11.GrabModeAsync);
    }

    private void resetAll() {
        for (X11HotKey hotKey : hotKeys) {
            if (!hotKey.isMedia()) {
                int modifiers = hotKey.modifiers;
                for (int i = 0; i < 16; i++) {
                    int flags = correctModifiers(modifiers, i);

                    X11.INSTANCE.XUngrabKey(display, hotKey.code, flags, window);
                }
            } else {
                X11.INSTANCE.XUngrabKey(display, hotKey.code, 0, window);
            }
        }

        hotKeys.clear();
    }

    private int correctModifiers(int modifiers, int flags) {
        int ret = modifiers;
        if ((flags & 1) != 0)
            ret |= X11.LockMask;
        if ((flags & 2) != 0)
            ret |= X11.Mod2Mask;
        if ((flags & 4) != 0)
            ret |= X11.Mod3Mask;
        if ((flags & 8) != 0)
            ret |= X11.Mod5Mask;
        return ret;
    }

    @Override
    public void stop() {
        if (thread != null && thread.isAlive()) {
            listening = false;
            try {
                thread.join();
                X11.INSTANCE.XCloseDisplay(display);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.stop();
    }

    public void register(KeyStroke keyCode, HotKeyListener listener) {
        synchronized (lock) {
            registerQueue.add(new X11HotKey(keyCode, listener));
        }
    }

    public void register(MediaKey mediaKey, HotKeyListener listener) {
        synchronized (lock) {
            registerQueue.add(new X11HotKey(mediaKey, listener));
        }
    }

    public void reset() {
        synchronized (lock) {
            if (thread.isAlive()) {
                reset = true;
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ErrorHandler implements XErrorHandler {
        public int apply(Display display, XErrorEvent errorEvent) {
            byte[] buf = new byte[1024];
            X11.INSTANCE.XGetErrorText(display, errorEvent.error_code, buf, buf.length);
            int len = 0;
            while (buf[len] != 0) len++;
            LOGGER.warn("Error: " + new String(buf, 0, len));
            return 0;
        }
    }

    class X11HotKey extends HotKey {
        byte code;
        int modifiers;

        X11HotKey(KeyStroke keyStroke, HotKeyListener listener) {
            super(keyStroke, listener);
        }

        X11HotKey(MediaKey mediaKey, HotKeyListener listener) {
            super(mediaKey, listener);
        }
    }
}
