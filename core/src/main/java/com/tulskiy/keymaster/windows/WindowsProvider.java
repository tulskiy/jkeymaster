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

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Author: Denis Tulskiy
 * Date: 6/12/11
 */
public class WindowsProvider extends Provider {
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsProvider.class);
    private static volatile int idSeq = 0;

    private volatile boolean listen = true;
    private volatile boolean reset = false;
    private final Object lock = new Object();
    private Thread thread;
    private int threadId;

    private final Map<Integer, HotKey> hotKeys = new HashMap<>();
    private final Queue<HotKey> registerQueue = new LinkedList<>();

    @Override
    public void init() {
        Runnable runnable = new Runnable() {
            public void run() {
                LOGGER.info("Starting Windows global hotkey provider");
                WinUser.MSG msg = new WinUser.MSG();
                synchronized (lock) {
                    threadId = Kernel32.INSTANCE.GetCurrentThreadId();
                    // Run once in this thread to start message queue
                    // (also causes it to go through the loop once)
                    unblock();
                }
                while (listen || reset) {
                    if (listen) {
                        int result = User32.INSTANCE.GetMessage(msg, null, 0, 0);
                        if (result == -1) {
                            LOGGER.warn("Error getting message: "+Kernel32.INSTANCE.GetLastError());
                            listen = false;
                        } else {
                            if (msg.message == WinUser.WM_HOTKEY) {
                                int id = msg.wParam.intValue();
                                HotKey hotKey = hotKeys.get(id);

                                if (hotKey != null) {
                                    fireEvent(hotKey);
                                }
                            }
                        }
                    }
                    synchronized (lock) {
                        if (reset) {
                            LOGGER.info("Reset hotkeys");
                            for (Integer id : hotKeys.keySet()) {
                                User32.INSTANCE.UnregisterHotKey(null, id);
                            }
                            hotKeys.clear();
                            reset = false;
                        }

                        while (!registerQueue.isEmpty()) {
                            HotKey hotKey = registerQueue.poll();
                            if (hotKey.isUnregister()) {
                                unregister(hotKey);
                            } else {
                                register(hotKey);
                            }
                        }
                    }
                }
                LOGGER.info("Exit Windows global hotkey thread");
                // Shouldn't try to send messages to this thread anymore
                synchronized (lock) {
                    threadId = 0;
                }
            }
        };

        thread = new Thread(runnable, "JKeyMaster-Windows");
        thread.start();
    }

    private void register(HotKey hotKey) {
        int id = idSeq++;
        int code = KeyMap.getCode(hotKey);
        if (User32.INSTANCE.RegisterHotKey(null, id, KeyMap.getModifiers(hotKey.keyStroke), code)) {
            LOGGER.info(String.format("Registered hotkey: %s (%2$d/0x%2$X) [%3$d]",
                    hotKey, code, id));
            hotKeys.put(id, hotKey);
        } else {
            LOGGER.warn("Could not register hotkey: " + hotKey);
        }
    }

    @Override
    public void register(KeyStroke keyCode, HotKeyListener listener) {
        synchronized (lock) {
            registerQueue.add(new HotKey(keyCode, listener));
            unblock();
        }
    }
    
    @Override
    public void register(MediaKey mediaKey, HotKeyListener listener) {
        synchronized (lock) {
            registerQueue.add(new HotKey(mediaKey, listener));
            unblock();
        }
    }

    private void unregister(HotKey hotKey) {
        hotKeys.entrySet().removeIf(regHotKey -> {
            boolean matches = hotKey.hasSameTrigger(regHotKey.getValue());
            if (matches) {
                if (User32.INSTANCE.UnregisterHotKey(null, regHotKey.getKey())) {
                    LOGGER.info("Unregistered hotkey: " + hotKey);
                } else {
                    LOGGER.warn("Could not unregister hotkey: " + hotKey);
                }
            }
            return matches;
        });
    }

    @Override
    public void unregister(KeyStroke keyCode) {
        synchronized (lock) {
            registerQueue.add(new HotKey(keyCode, null));
            unblock();
        }
    }

    @Override
    public void unregister(MediaKey mediaKey) {
        synchronized (lock) {
            registerQueue.add(new HotKey(mediaKey, null));
            unblock();
        }
    }

    @Override
    public void reset() {
        if (isRunning()) {
            synchronized (lock) {
                reset = true;
                registerQueue.clear();
                unblock();
            }
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            synchronized (lock) {
                listen = false;
                unblock();
            }
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.stop();
    }
    
    @Override
    public boolean isRunning() {
        return thread != null && thread.isAlive();
    }
    
    /**
     * Send a message to unblock the GetMessage() call for one loop.
     */
    private void unblock() {
        if (threadId != 0) {
            if (User32.INSTANCE.PostThreadMessage(threadId, WinUser.WM_USER + 1, null, null) == 0) {
                LOGGER.warn("Posting unblock message failed (thread " + threadId + "): " + Kernel32.INSTANCE.GetLastError());
            }
        }
    }
    
}
