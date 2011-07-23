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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;

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

    private final PointerByReference eventHandlerReference;

    public CarbonProvider() {
        this.eventHandlerReference = new PointerByReference();
    }

    public void init(ScheduledExecutorService executorService) {
        logger.info("Installing Event Handler");
        EventHandlerProcPtr keyListener = new EventHandler(executorService);

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
    }

    @Override
    public void stop() {
        logger.info("Stopping now");
        super.stop();
        if (eventHandlerReference.getValue() != null) {
            Lib.RemoveEventHandler(eventHandlerReference.getValue());
        }
    }

    public void reset() {
        super.reset();
        resetAll();
    }

    private void resetAll() {
        logger.info("Resetting hotkeys");
        for (OSXHotKey hotKey : hotKeys.values()) {
            int ret = Lib.UnregisterEventHotKey(hotKey.handler.getValue());
            if (ret != 0) {
                logger.warn("Could not unregister hotkey. Error code: " + ret);
            }
        }
        hotKeys.clear();
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
        private final ExecutorService executorService;

        public EventHandler(ExecutorService executorService) {
            this.executorService = executorService;
        }

        public int callback(Pointer inHandlerCallRef, Pointer inEvent, Pointer inUserData) {
            final EventHotKeyID eventHotKeyID = new EventHotKeyID();
            final int ret = Lib.GetEventParameter(inEvent,
                    kEventParamDirectObject,
                    typeEventHotKeyID,
                    null,
                    eventHotKeyID.size(),
                    null,
                    eventHotKeyID);
            try {
                //we should do everything FAST here. Move all listener firing to executor service
                if (!executorService.isShutdown()) {
                    executorService.execute(new Runnable() {
                        public void run() {
                            if (ret == 0) {
                                long eventId = eventHotKeyID.id;
                                logger.info("Received event id: " + eventId);
                                OSXHotKey hotKey = hotKeys.get(eventId);
                                fireEvent(hotKey);
                            } else {
                                logger.warn("Could not get event parameters. Error code: " + ret);
                            }
                        }
                    });
                }
            } catch (RejectedExecutionException e) {
                //executor is shutdown?
            } catch (Exception e) {
                logger.debug("Exception while adding event to executor service", e);
            }
            return 0;
        }
    }
}
