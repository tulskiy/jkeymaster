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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.tulskiy.keymaster.x11.X11.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/13/11
 */
public class X11Provider extends Provider {
    private final Pointer display;
    private final NativeLong window;
    private final ErrorHandler errorHandler;
    private final Set<X11HotKey> hotKeys = new CopyOnWriteArraySet<X11HotKey>();

    public X11Provider() {
        logger.info("Starting X11 global hotkey provider");
        display = Lib.XOpenDisplay(null);
        errorHandler = new ErrorHandler();
        Lib.XSetErrorHandler(errorHandler);
        window = Lib.XDefaultRootWindow(display);
    }

    protected void init(ScheduledExecutorService executorService) {
        Runnable runnable = new Runnable() {
            public void run() {
                XEvent event = new XEvent();
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
            }
        };
        executorService.scheduleWithFixedDelay(runnable, 0, 300, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        super.stop();
        Lib.XCloseDisplay(display);
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
        resetAll();
    }

    private void resetAll() {
        Set<X11HotKey> keys = new HashSet<X11HotKey>(hotKeys);
        hotKeys.clear();

        for (X11HotKey hotKey : keys) {
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
    }

    private static class ErrorHandler implements XErrorHandler {
        public int apply(Pointer display, XErrorEvent errorEvent) {
            byte[] buf = new byte[1024];
            Lib.XGetErrorText(display, errorEvent.error_code, buf, buf.length);
            int len = 0;
            while (buf[len] != 0 && len < buf.length) {
                len++;
            }

            String errorMessage;
            try {
                errorMessage = new String(buf, 0, len);
            } catch (Exception e) {
                logger.warn("Can't decode error message.");
                errorMessage = "Unknown. Buffer length " + len + " error code " + errorEvent.error_code;
            }

            logger.warn("Error: " + errorMessage);
            return 0;
        }
    }
}
