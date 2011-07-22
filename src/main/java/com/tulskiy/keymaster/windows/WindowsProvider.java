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

package com.tulskiy.keymaster.windows;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tulskiy.keymaster.windows.User32.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/12/11
 */
public class WindowsProvider extends Provider {
    private static final AtomicInteger idSeq = new AtomicInteger(0);

    private boolean listen;
    private Boolean reset = false;
    private final Object lock = new Object();
    private Thread thread;

    private final Map<Integer, HotKey> hotKeys = new ConcurrentHashMap<Integer, HotKey>();

    public void init() {
        Runnable runnable = new Runnable() {
            public void run() {
                logger.info("Starting Windows global hotkey provider");
                MSG msg = new MSG();
                listen = true;
                while (listen) {
                    while (PeekMessage(msg, null, 0, 0, PM_REMOVE)) {
                        if (msg.message == WM_HOTKEY) {
                            int id = msg.wParam;
                            HotKey hotKey = hotKeys.get(id);

                            if (hotKey != null) {
                                fireEvent(hotKey);
                            }
                        }
                    }

                    synchronized (lock) {
                        if (reset) {
                            logger.info("Reset hotkeys");
                            for (Integer id : hotKeys.keySet()) {
                                UnregisterHotKey(null, id);
                            }

                            hotKeys.clear();
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
                logger.info("Exit listening thread");
            }
        };

        thread = new Thread(runnable);
        thread.start();
    }

    private static int registerHotKey(HotKey hotKey) {
        int id = idSeq.incrementAndGet();
        int code = KeyMap.getCode(hotKey);
        if (RegisterHotKey(null, id, KeyMap.getModifiers(hotKey.keyStroke), code)) {
            logger.info("Registering hotkey: " + hotKey);
            return id;
        } else {
            throw new RuntimeException("Could not register hotkey: " + hotKey);
        }
    }

    public void register(KeyStroke keyCode, HotKeyListener listener) {
        registerAll(listener, new HotKey(keyCode));
    }

    public void register(MediaKey mediaKey, HotKeyListener listener) {
        registerAll(listener, new HotKey(mediaKey));
    }

    private void registerAll(HotKeyListener listener, HotKey key) {
        addListener(key, listener);
        int id = registerHotKey(key);
        hotKeys.put(id, key);
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

    @Override
    public void stop() {
        listen = false;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.stop();
    }
}
