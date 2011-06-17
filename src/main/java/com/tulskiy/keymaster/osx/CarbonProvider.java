package com.tulskiy.keymaster.osx;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.awt.event.ActionListener;

import static com.tulskiy.keymaster.osx.CarbonLib.Lib;
import static com.tulskiy.keymaster.osx.CarbonLib.noErr;

/**
 * Author: Denis Tulskiy
 * Date: 6/17/11
 */
public class CarbonProvider implements Provider {
    private static final int kEventHotKeyPressed = 5;
    private static final int kEventHotKeyReleased = 6;

    private static final int kEventClassKeyboard = OS_TYPE("keyb");
    private static final int typeEventHotKeyID = OS_TYPE("hkid"); /* EventHotKeyID*/
    private static final int kEventParamDirectObject = OS_TYPE("----");

    private static final int cmdKey = 0x0100;
    private static final int shiftKey = 0x0200;
    private static final int optionKey = 0x0800;
    private static final int controlKey = 0x1000;
    public CarbonLib.EventHandlerProcPtr keyListener;


    public void init() {
        // Our events handlers we want to listen to
        PointerByReference fHandlerRef = new PointerByReference();

        keyListener = new EventHandler();

        //EventTypeSpec	kEvents[] = { { kEventClassCommand, kEventCommandUpdateStatus } };
        CarbonLib.EventTypeSpec[] eventTypes = (CarbonLib.EventTypeSpec[]) (new CarbonLib.EventTypeSpec().toArray(1));
        eventTypes[0].eventClass = kEventClassKeyboard;
        eventTypes[0].eventKind = kEventHotKeyPressed;

        // install an event handler to detect clicks on the main window to tell the application which
        // events to track/stop tracking
        //InstallApplicationEventHandler( CmdHandler, GetEventTypeCount( kEvents ), kEvents, 0, &fHandler );
        CarbonLib.OSStatus status = Lib.InstallEventHandler(Lib.GetEventDispatcherTarget(), Lib.NewEventHandlerUPP(keyListener), new CarbonLib.ItemCount(1), eventTypes, null, fHandlerRef); //fHandlerRef
        if (status.intValue() != 0) {
            logger.warning("Could not register event handler, error code: " + status);
        }
    }

    public void stop() {
    }

    public void reset() {
    }

    public void register(KeyStroke keyCode, ActionListener listener) {
    }

    public void register(MediaKey mediaKey, ActionListener listener) {
    }

    private static int OS_TYPE(String osType) {
        byte[] bytes = osType.getBytes();
        return bytes[0] << 24 + bytes[1] << 16 + bytes[2] << 8 + bytes[1];
    }

    private static class EventHandler implements CarbonLib.EventHandlerProcPtr {
        public CarbonLib.OSStatus callback(Pointer inHandlerCallRef, Pointer inEvent, Pointer inUserData) {
            System.out.println("Fuck Yeah");
            return noErr;
        }
    }
}
