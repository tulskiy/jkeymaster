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

import com.tulskiy.keymaster.*;
import com.tulskiy.keymaster.HotKey;
import com.tulskiy.keymaster.HotKeyListener;
import com.tulskiy.keymaster.MediaKey;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tulskiy.keymaster.windows.User32.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/12/11
 */
public class WindowsProvider extends Provider {
    private static final AtomicInteger idSeq = new AtomicInteger(0);

    private final Map<Integer, HotKey> hotKeys = new ConcurrentHashMap<Integer, HotKey>();

    protected void init(ScheduledExecutorService executorService) {
        Runnable runnable = new Runnable() {
            public void run() {
                logger.info("Starting Windows global hotkey provider");
                MSG msg = new MSG();
                while (PeekMessage(msg, null, 0, 0, PM_REMOVE)) {
                    if (msg.message == WM_HOTKEY) {
                        int id = msg.wParam;
                        HotKey hotKey = hotKeys.get(id);

                        if (hotKey != null) {
                            fireEvent(hotKey);
                        }
                    }
                }
            }
        };
        executorService.scheduleWithFixedDelay(runnable, 0, 300, TimeUnit.MILLISECONDS);
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
        Map<Integer, HotKey> keys = new HashMap<Integer, HotKey>(hotKeys);
        hotKeys.clear();

        for (Integer id : keys.keySet()) {
            UnregisterHotKey(null, id);
        }
    }

    @Override
    public void stop() {
        reset();
        super.stop();
    }
}
