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

package com.tulskiy.keymaster.common;

import com.sun.jna.Platform;
import com.tulskiy.keymaster.osx.CarbonProvider;
import com.tulskiy.keymaster.windows.WindowsProvider;
import com.tulskiy.keymaster.x11.X11Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * Main interface to global hotkey providers
 * <p/>
 * Author: Denis Tulskiy
 * Date: 6/12/11
 */
public abstract class Provider {
    public static final Logger logger = LoggerFactory.getLogger(Provider.class);

    private final ConcurrentMap<HotKey, List<HotKeyEvent>> listeners = new ConcurrentHashMap<HotKey, List<HotKeyEvent>>();

    /**
     * Get global hotkey provider for current platform
     *
     * @return new instance of Provider, or null if platform is not supported
     * @see X11Provider
     * @see WindowsProvider
     * @see CarbonProvider
     */
    public static Provider getCurrentProvider() {
        if (Platform.isX11()) {
            return new X11Provider();
        } else if (Platform.isWindows()) {
            return new WindowsProvider();
        } else if (Platform.isMac()) {
            return new CarbonProvider();
        } else {
            logger.warn("No suitable provider for " + System.getProperty("os.name"));
            return null;
        }
    }

    private ExecutorService eventQueue = Executors.newSingleThreadExecutor();

    /**
     * Initialize provider. Starts main thread that will listen to hotkey events
     */
    public abstract void init();

    /**
     * Stop the provider. Stops main thread and frees any resources.
     * </br>
     * all hotkeys should be reset before calling this method
     *
     * @see Provider#reset()
     */
    public void stop() {
        eventQueue.shutdown();
    }

    /**
     * Reset all hotkey listeners
     */
    public void reset() {
        listeners.clear();
    }

    /**
     * Register a global hotkey. Only keyCode and modifiers fields are respected
     *
     * @param keyCode  KeyStroke to register
     * @param listener listener to be notified of hotkey events
     * @see KeyStroke
     */
    public abstract void register(KeyStroke keyCode, HotKeyListener listener);

    /**
     * Register a media hotkey. Currently supported media keys are:
     * <p/>
     * <ul>
     * <li>Play/Pause</li>
     * <li>Stop</li>
     * <li>Next track</li>
     * <li>Previous Track</li>
     * </ul>
     *
     * @param mediaKey media key to register
     * @param listener listener to be notified of hotkey events
     * @see MediaKey
     */
    public abstract void register(MediaKey mediaKey, HotKeyListener listener);


    protected void addListener(HotKey key, HotKeyListener listener) {
        List<HotKeyEvent> listenersList = listeners.get(key);
        if (listenersList == null) {
            listeners.putIfAbsent(key, new CopyOnWriteArrayList<HotKeyEvent>());
        }
        listenersList = listeners.get(key);
        listenersList.add(new HotKeyEvent(listener, key));
    }

    /**
     * Helper method fro providers to fire hotkey event in a separate thread
     *
     * @param hotKey hotkey to fire
     */
    protected void fireEvent(HotKey hotKey) {
        List<HotKeyEvent> listenerList = listeners.get(hotKey);
        if (listenerList != null) {
            for (HotKeyEvent event : listenerList) {
                eventQueue.execute(event);
            }
        }
    }

    private static class HotKeyEvent implements Runnable {
        private final HotKey hotKey;
        private final HotKeyListener listener;

        private HotKeyEvent(HotKeyListener listener, HotKey hotKey) {
            this.listener = listener;
            this.hotKey = hotKey;
        }

        public void run() {
            listener.onHotKey(hotKey);
        }
    }

}
