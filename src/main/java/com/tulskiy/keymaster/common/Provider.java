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

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Author: Denis Tulskiy
 * Date: 6/12/11
 */
public abstract class Provider {
    public static final Logger logger = Logger.getLogger(Provider.class.getName());

    public static Provider getCurrentProvider() {
        if (Platform.isX11()) {
            return new X11Provider();
        } else if (Platform.isWindows()) {
            return new WindowsProvider();
        } else if (Platform.isMac()) {
            return new CarbonProvider();
        } else {
            logger.warning("No suitable provider for " + System.getProperty("os.name"));
            return null;
        }
    }

    private ExecutorService eventQueue = Executors.newSingleThreadExecutor();

    public abstract void init();

    public abstract void stop();

    public abstract void reset();

    public abstract void register(KeyStroke keyCode, HotKeyListener listener);

    public abstract void register(MediaKey mediaKey, HotKeyListener listener);

    protected void fireEvent(HotKey hotKey) {
        eventQueue.execute(new HotKeyEvent(hotKey));
    }

    private class HotKeyEvent implements Runnable {
        private HotKey hotKey;

        private HotKeyEvent(HotKey hotKey) {
            this.hotKey = hotKey;
        }

        public void run() {
            hotKey.listener.onHotKey(hotKey);
        }
    }

}
