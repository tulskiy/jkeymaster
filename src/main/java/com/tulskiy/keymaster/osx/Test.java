package com.tulskiy.keymaster.osx;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.PointerByReference;

import javax.swing.*;

import static com.tulskiy.keymaster.osx.CarbonLib.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/16/11
 */
public class Test {
    static final int kEventHotKeyPressed = 5;
    static final int kEventHotKeyReleased = 6;

    private static final int kEventClassKeyboard = OS_TYPE("keyb");
    private static final int typeEventHotKeyID = OS_TYPE("hkid"); /* EventHotKeyID*/
    private static final int kEventParamDirectObject = OS_TYPE("----");

    static final int cmdKey = 0x0100;
    static final int shiftKey = 0x0200;
    static final int optionKey = 0x0800;
    static final int controlKey = 0x1000;

    private static int OS_TYPE(String osType) {
        byte[] bytes = osType.getBytes();
        return bytes[0] << 24 + bytes[1] << 16 + bytes[2] << 8 + bytes[1];
    }

    public static void main(String[] args) {
        try {
            // Our events handlers we want to listen to
            //EventHandlerRef		sHandler, fHandler;
            PointerByReference fHandlerRef;
            PointerByReference gMyHotKeyRef;
            fHandlerRef = new PointerByReference();
            gMyHotKeyRef = new PointerByReference();

            EventHandlerProcPtr myKeyListener = new EventHandlerProcPtr() {
                public OSStatus callback(Pointer inHandlerCallRef, Pointer inEvent, Pointer inUserData) {
                    EventHotKeyID eventHotKeyID = new EventHotKeyID();
                    Lib.GetEventParameter(inEvent, kEventParamDirectObject, typeEventHotKeyID, null, eventHotKeyID.size(), null, eventHotKeyID);
                    System.out.println("Received event with id: " + eventHotKeyID.id);
                    return noErr;
                }
            };


            // use an event that isn't monitored just so we have a valid EventTypeSpec to install
            //EventTypeSpec	kEvents[] = { { kEventClassCommand, kEventCommandUpdateStatus } };
            CarbonLib.EventTypeSpec[] eventTypes = (CarbonLib.EventTypeSpec[]) (new CarbonLib.EventTypeSpec().toArray(1));
            eventTypes[0].eventClass = kEventClassKeyboard;
            eventTypes[0].eventKind = kEventHotKeyPressed;

            // install an event handler to detect clicks on the main window to tell the application which
            // events to track/stop tracking
            //InstallApplicationEventHandler( CmdHandler, GetEventTypeCount( kEvents ), kEvents, 0, &fHandler );
            CarbonLib.OSStatus status = Lib.InstallEventHandler(Lib.GetEventDispatcherTarget(), Lib.NewEventHandlerUPP(myKeyListener), new CarbonLib.ItemCount(1), eventTypes, null, fHandlerRef); //fHandlerRef

            System.out.println("InstallEventHandler: " + status);

            System.out.println(fHandlerRef.getValue() == null ? "2. NULL" : "2. Not NULL");
            System.out.println(fHandlerRef.getValue());

            EventHotKeyID.ByValue gMyHotKeyID = new EventHotKeyID.ByValue();
            gMyHotKeyID.id = 1;
            gMyHotKeyID.signature = ((int) 'h' << 24) + ((int) 't' << 16) + ((int) 'k' << 8) + '1';


            // extern OSStatus RegisterEventHotKey(UInt32 inHotKeyCode, UInt32 inHotKeyModifiers, EventHotKeyID inHotKeyID,
            //                 EventTargetRef inTarget, OptionBits inOptions, EventHotKeyRef *  outRef)
            // HotKey = cmdKey+controlKey+SPACE
            status = Lib.RegisterEventHotKey(9, shiftKey + controlKey, gMyHotKeyID, Lib.GetEventDispatcherTarget(), 0, gMyHotKeyRef);

            System.out.println("RegisterHotKey: " + status);
            System.out.println(gMyHotKeyRef.getValue());

            System.out.println("Event handers installed");

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(100, 100);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });

        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}
