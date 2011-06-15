package com.tulskiy.keymaster.osx;

import com.sun.jna.*;
import com.sun.jna.ptr.PointerByReference;

/**
 * Author: Denis Tulskiy
 * Date: 6/15/11
 */

public class CarbonLib {
    static {
        Native.register(NativeLibrary.getInstance("Carbon"));
    }

    public static native Pointer GetApplicationEventTarget();

    /* OSStatus InstallEventHandler(EventTargetRef inTarget, EventHandlerUPP inHandler, ItemCount inNumTypes, const EventTypeSpec* inList, void* inUserData, EventHandlerRef *outRef) */

    public static native OSStatus InstallEventHandler(Pointer inTarget, EventHandlerUPP inHandler, ItemCount inNumTypes, final EventTypeSpec[] inList, Pointer inUserData, PointerByReference outRef);

    public static native OSStatus RegisterEventHotKey(int inHotKeyCode, int inHotKeyModifiers, EventHotKeyID inHotKeyID, Pointer inTarget, int inOptions, PointerByReference outRef);

    public static native EventHandlerUPP NewEventHandlerUPP(EventHandlerProcPtr userRoutine);

    public static native Pointer GetEventMonitorTarget();

    public static native OSStatus RemoveEventHandler(Pointer inHandlerRef);

    /*extern OSStatus AddEventTypesToHandler(
      EventHandlerRef        inHandlerRef,
      ItemCount              inNumTypes,
      const EventTypeSpec *  inList)*/
    public static native OSStatus AddEventTypesToHandler(Pointer inHandlerRef, ItemCount inNumTypes, EventTypeSpec inList);


    /* typedef SInt32 OSStatus */
    public class OSStatus extends IntegerType {
        public OSStatus() {
            this(0);
        }

        public OSStatus(int value) {
            super(4, value);
        }
    }

    OSStatus noErr = new OSStatus(0);
    OSStatus eventAlreadyPostedErr = new OSStatus(-9860);
    OSStatus eventTargetBusyErr = new OSStatus(-9861);
    OSStatus eventClassInvalidErr = new OSStatus(-9862); //Note More on page 213 - Carbon_Event_Manager_Ref.pdf

    /* Don't see defined anywhere, but used where integer is expected. */
    public class ItemCount extends IntegerType {
        public ItemCount() {
            this(0);
        }

        public ItemCount(int value) {
            super(4, value);
        }
    }

    /* EventTypeSpec */ /* struct EventTypeSpec { UInt32 eventClass; UInt32 eventKind; }; typedef struct EventTypeSpec EventTypeSpec */
    public class EventTypeSpec extends Structure {
        public int eventClass;
        public int eventKind;
    }

    /* EventHotKeyID: struct EventHotKeyID { OSType signature; UInt32 id; }; */
    public class EventHotKeyID extends Structure {
        public int signature;
        public int id;
    }


    public interface EventHandlerUPP extends EventHandlerProcPtr {

    }

    /* typedef OSStatus (*EventHandlerProcPtr) ( EventHandlerCallRef inHandlerCallRef, EventRef inEvent, void * inUserData ); */
    interface EventHandlerProcPtr extends Callback {
        public OSStatus callback(Pointer inHandlerCallRef, Pointer inEvent, Pointer inUserData);
        //OSStatus callback(EventHandlerCallRef inHandlerCallRef, EventRef inEvent, Pointer inUserData); }

    }
}