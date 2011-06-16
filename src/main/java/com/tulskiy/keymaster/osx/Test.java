package com.tulskiy.keymaster.osx;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import static com.tulskiy.keymaster.osx.CarbonLib.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/16/11
 */
public class Test {
    static final int kEventHotKeyPressed = 5;
    static final int kEventHotKeyReleased = 6;

    static final int kEventClassKeyboard = ((int) 'k' << 24) + ((int) 'e' << 16) + ((int) 'y' << 8) + (int) 'b';

    static final int cmdKey     = 0x0100;
    static final int shiftKey   = 0x0200;
    static final int optionKey  = 0x0800;
    static final int controlKey = 0x1000;

    public static void main(String[] args) {
        try
        {
            // Our events handlers we want to listen to
            //EventHandlerRef		sHandler, fHandler;
            PointerByReference fHandlerRef;
            PointerByReference gMyHotKeyRef;
            fHandlerRef  = new PointerByReference();
            gMyHotKeyRef = new PointerByReference();

            EventHandlerUPP myKeyListener = new EventHandlerUPP() {
                public OSStatus callback(Pointer inHandlerCallRef, Pointer inEvent, Pointer inUserData) {
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
            CarbonLib.OSStatus status = Lib.InstallEventHandler(Lib.GetApplicationEventTarget(), myKeyListener, new CarbonLib.ItemCount(1), eventTypes, null, fHandlerRef); //fHandlerRef

            System.out.println("InstallEventHandler: "+status);

            System.out.println(fHandlerRef.getValue()==null?"2. NULL":"2. Not NULL");
            System.out.println(fHandlerRef.getValue());

            EventHotKeyID gMyHotKeyID = new EventHotKeyID();
            gMyHotKeyID.id = 1;
            gMyHotKeyID.signature = ((int)'h'<< 24) +((int)'t' << 16) +((int)'k' << 8) +'1';


            // extern OSStatus RegisterEventHotKey(UInt32 inHotKeyCode, UInt32 inHotKeyModifiers, EventHotKeyID inHotKeyID,
            //                 EventTargetRef inTarget, OptionBits inOptions, EventHotKeyRef *  outRef)
            // HotKey = cmdKey+controlKey+SPACE
            status = Lib.RegisterEventHotKey(49, cmdKey+controlKey, gMyHotKeyID, Lib.GetApplicationEventTarget(), 0, gMyHotKeyRef);

            System.out.println("RegisterHotKey: "+status);
            System.out.println(gMyHotKeyRef.getValue());

            System.out.println("Event handers installed");

        }
        catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}
