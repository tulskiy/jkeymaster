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

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.tulskiy.keymaster.x11.X11.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/13/11
 */
public class X11Provider extends Provider {
    private Pointer display;
    private NativeLong window;
    private boolean listening;
    private Thread thread;
    private boolean reset;
    private ErrorHandler errorHandler;
    private final Object lock = new Object();
    private final Set<X11HotKey> hotKeys = new CopyOnWriteArraySet<X11HotKey>();

    public void init() {
        Runnable runnable = new Runnable() {
            public void run() {
                logger.info("Starting X11 global hotkey provider");
                display = Lib.XOpenDisplay(null);
                errorHandler = new ErrorHandler();
                Lib.XSetErrorHandler(errorHandler);
                window = Lib.XDefaultRootWindow(display);
                listening = true;
                XEvent event = new XEvent();

                while (listening) {
                    while (Lib.XPending(display) > 0) {
                        Lib.XNextEvent(display, event);
                        if (event.type == KeyPress) {
                            XKeyEvent xkey = (XKeyEvent) event.readField("xkey");
                            int state = xkey.state & (ShiftMask | ControlMask | Mod1Mask | Mod4Mask);
                            byte code = (byte) xkey.keycode;

                            X11HotKey hotKey = new X11HotKey(KeyStroke.getKeyStroke(code, state), code, state);
                            logger.info("Received event for hotkey: " + hotKey);
                            fireEvent(hotKey);
                        }
                    }

                    synchronized (lock) {
                        if (reset) {
                            logger.info("Reset hotkeys");
                            resetAll();
                            reset = false;
                            lock.notify();
                        }

                        try {
                            lock.wait(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }

                logger.info("Thread - stop listening");
            }
        };

        thread = new Thread(runnable);
        thread.start();
    }

    private void resetAll() {
        for (X11HotKey hotKey : hotKeys) {
            if (!hotKey.isMedia()) {
                int modifiers = hotKey.modifiers;
                for (int i = 0; i < 16; i++) {
                    int flags = X11Helper.createX11Modifiers(modifiers, i);

                    Lib.XUngrabKey(display, hotKey.code, flags, window);
                }
            } else {
                Lib.XUngrabKey(display, hotKey.code, 0, window);
            }
        }

        hotKeys.clear();
    }

    @Override
    public void stop() {
        if (thread != null) {
            listening = false;
            try {
                thread.join();
                Lib.XCloseDisplay(display);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.stop();
    }

    public void register(KeyStroke key, HotKeyListener listener) {
        addListener(new HotKey(key), listener);
        byte code = X11Helper.createX11CodeForKeyStroke(key, display);
        X11HotKey hotKey = new X11HotKey(key, code, KeyMap.getModifiers(key));
        hotKeys.add(hotKey);
        X11Helper.registerKeyStroke(display, window, hotKey);
    }

    public void register(MediaKey mediaKey, HotKeyListener listener) {
        addListener(new HotKey(mediaKey), listener);
        X11HotKey hotKey = new X11HotKey(mediaKey, KeyMap.getMediaCode(mediaKey, display));
        hotKeys.add(hotKey);
        X11Helper.registerMedia(display, window, hotKey);
    }

    public void reset() {
        super.reset();
        synchronized (lock) {
            reset = true;
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ErrorHandler implements XErrorHandler {
        public int apply(Pointer display, XErrorEvent errorEvent) {
            byte[] buf = new byte[1024];
            Lib.XGetErrorText(display, errorEvent.error_code, buf, buf.length);
            int len = 0;
            while (buf[len] != 0) len++;
            logger.warning("Error: " + new String(buf, 0, len));
            return 0;
        }
    }
}
