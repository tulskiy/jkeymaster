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

package com.tulskiy.keymaster.osx;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tulskiy.keymaster.osx.Carbon.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/17/11
 */
public class CarbonProvider extends Provider {
    private static final int kEventHotKeyPressed = 5;

    private static final int kEventClassKeyboard = OSXHelper.OS_TYPE("keyb");
    private static final int typeEventHotKeyID = OSXHelper.OS_TYPE("hkid");
    private static final int kEventParamDirectObject = OSXHelper.OS_TYPE("----");

    private final Map<Long, OSXHotKey> hotKeys = new ConcurrentHashMap<Long, OSXHotKey>();
    private final Object lock = new Object();
    private boolean listen;
    private boolean reset;

    private EventHandlerProcPtr keyListener;
    private PointerByReference eventHandlerReference;
    public Thread thread;


    public void init() {
        logger.info("Installing Event Handler");
        eventHandlerReference = new PointerByReference();
        keyListener = new EventHandler();

        EventTypeSpec[] eventTypes = (EventTypeSpec[]) (new EventTypeSpec().toArray(1));
        eventTypes[0].eventClass = kEventClassKeyboard;
        eventTypes[0].eventKind = kEventHotKeyPressed;

        int status = Lib.InstallEventHandler(
                Lib.GetEventDispatcherTarget(),
                keyListener,
                1,
                eventTypes,
                null,
                eventHandlerReference); //fHandlerRef

        if (status != 0) {
            throw new RuntimeException("Could not register Event Handler, error code: " + status);
        }

        if (eventHandlerReference.getValue() == null) {
            throw new RuntimeException("Event Handler reference is null");
        }

        thread = new Thread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    listen = true;
                    while (listen) {
                        if (reset) {
                            resetAll();
                            reset = false;
                            lock.notify();
                        }

                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        thread.start();
    }

    private void resetAll() {
        logger.info("Resetting hotkeys");
        for (OSXHotKey hotKey : hotKeys.values()) {
            int ret = Lib.UnregisterEventHotKey(hotKey.handler.getValue());
            if (ret != 0) {
                logger.warning("Could not unregister hotkey. Error code: " + ret);
            }
        }
        hotKeys.clear();
    }

    @Override
    public void stop() {
        logger.info("Stopping now");
        try {
            synchronized (lock) {
                listen = false;
                lock.notify();
            }
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (eventHandlerReference.getValue() != null) {
            Lib.RemoveEventHandler(eventHandlerReference.getValue());
        }
        super.stop();
    }

    public void reset() {
        super.reset();
        synchronized (lock) {
            reset = true;
            lock.notify();
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void register(KeyStroke keyCode, HotKeyListener listener) {
        OSXHotKey osxHotKey = OSXHelper.registerKeyStroke(keyCode);
        addListener(osxHotKey, listener);
        hotKeys.put(osxHotKey.id, osxHotKey);
    }

    public void register(MediaKey mediaKey, HotKeyListener listener) {
        throw new UnsupportedOperationException("Media keys are not yet supported on this platform");
    }

    private class EventHandler implements Carbon.EventHandlerProcPtr {
        public int callback(Pointer inHandlerCallRef, Pointer inEvent, Pointer inUserData) {
            EventHotKeyID eventHotKeyID = new EventHotKeyID();
            int ret = Lib.GetEventParameter(inEvent, kEventParamDirectObject, typeEventHotKeyID, null, eventHotKeyID.size(), null, eventHotKeyID);
            if (ret != 0) {
                logger.warning("Could not get event parameters. Error code: " + ret);
            } else {
                long eventId = eventHotKeyID.id;
                logger.info("Received event id: " + eventId);
                OSXHotKey hotKey = hotKeys.get(eventId);
                if (hotKey != null) {
                    fireEvent(hotKey);
                }
            }
            return 0;
        }
    }
}
