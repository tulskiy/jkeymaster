package com.tulskiy.keymaster.osx;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static com.tulskiy.keymaster.osx.CarbonLib.Lib;
import static com.tulskiy.keymaster.osx.CarbonLib.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/17/11
 */
public class CarbonProvider extends Provider {
    private static final int kEventHotKeyPressed = 5;

    private static final int kEventClassKeyboard = OS_TYPE("keyb");
    private static final int typeEventHotKeyID = OS_TYPE("hkid"); /* EventHotKeyID*/
    private static final int kEventParamDirectObject = OS_TYPE("----");

    private static int idSeq = 1;

    private Map<Integer, KeyStroke> idToKeyStroke = new HashMap<Integer, KeyStroke>();
    private Map<Integer, PointerByReference> idToHandler = new HashMap<Integer, PointerByReference>();
    private Map<Integer, ActionListener> idToListener = new HashMap<Integer, ActionListener>();

    public EventHandlerProcPtr keyListener;
    public PointerByReference eventHandlerReference;


    public void init() {
        logger.info("Installing Event Handler");
        eventHandlerReference = new PointerByReference();
        keyListener = new EventHandler();

        CarbonLib.EventTypeSpec[] eventTypes = (CarbonLib.EventTypeSpec[]) (new CarbonLib.EventTypeSpec().toArray(1));
        eventTypes[0].eventClass = kEventClassKeyboard;
        eventTypes[0].eventKind = kEventHotKeyPressed;

        int status = Lib.InstallEventHandler(Lib.GetEventDispatcherTarget(), Lib.NewEventHandlerUPP(keyListener), 1, eventTypes, null, eventHandlerReference); //fHandlerRef
        if (status != 0) {
            logger.warning("Could not register Event Handler, error code: " + status);
        }

        if (eventHandlerReference.getValue() == null) {
            logger.warning("Event Handler reference is null");
        }
    }

    public void stop() {
        if (eventHandlerReference.getValue() != null) {
            Lib.RemoveEventHandler(eventHandlerReference.getValue());
        }
    }

    public void reset() {
        logger.info("Resetting hotkeys");
        for (PointerByReference ptr : idToHandler.values()) {
            int ret = Lib.UnregisterEventHotKey(ptr.getValue());
            if (ret != 0) {
                logger.warning("Could not unregister hotkey. Error code: " + ret);
            }
        }

        idToHandler.clear();
        idToKeyStroke.clear();
        idToListener.clear();
    }

    public void register(KeyStroke keyCode, ActionListener listener) {
        EventHotKeyID.ByValue hotKeyReference = new EventHotKeyID.ByValue();
        int id = idSeq++;
        hotKeyReference.id = id;
        hotKeyReference.signature = OS_TYPE("hk" + String.format("%02d", id));
        PointerByReference gMyHotKeyRef = new PointerByReference();

        int status = Lib.RegisterEventHotKey(KeyMap.getKeyCode(keyCode), KeyMap.getModifier(keyCode), hotKeyReference, Lib.GetEventDispatcherTarget(), 0, gMyHotKeyRef);

        if (status != 0) {
            logger.warning("Could not register HotKey: " + keyCode + ". Error code: " + status);
            return;
        }

        if (gMyHotKeyRef.getValue() == null) {
            logger.warning("HotKey returned null handler reference");
            return;
        }

        logger.info("Registered hotkey: " + keyCode);
        idToHandler.put(id, gMyHotKeyRef);
        idToKeyStroke.put(id, keyCode);
        idToListener.put(id, listener);
    }

    public void register(MediaKey mediaKey, ActionListener listener) {
        logger.warning("Media keys are not supported on this platform");
    }

    private static int OS_TYPE(String osType) {
        byte[] bytes = osType.getBytes();
        return (bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3];
    }

    private class EventHandler implements CarbonLib.EventHandlerProcPtr {
        public int callback(Pointer inHandlerCallRef, Pointer inEvent, Pointer inUserData) {
            EventHotKeyID eventHotKeyID = new EventHotKeyID();
            int ret = Lib.GetEventParameter(inEvent, kEventParamDirectObject, typeEventHotKeyID, null, eventHotKeyID.size(), null, eventHotKeyID);
            if (ret != 0) {
                logger.warning("Could not get event parameters. Error code: " + ret);
            } else {
                int eventId = eventHotKeyID.id;
                logger.info("Received event id: " + eventId);

                ActionListener listener = idToListener.get(eventId);
                if (listener != null) {
                    fireEvent(idToKeyStroke.get(eventId), listener);
                }
            }
            return 0;
        }
    }
}
