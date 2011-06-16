package com.tulskiy.keymaster.osx;

import com.sun.jna.*;
import com.sun.jna.ptr.PointerByReference;

/**
 * Author: Denis Tulskiy
 * Date: 6/15/11
 */

public interface CarbonLib extends Library {
    public static CarbonLib Lib = (CarbonLib) Native.loadLibrary("Carbon", CarbonLib.class);

    public static final OSStatus noErr = new OSStatus(0);
    public static final OSStatus eventAlreadyPostedErr = new OSStatus(-9860);
    public static final OSStatus eventTargetBusyErr = new OSStatus(-9861);
    public static final OSStatus eventClassInvalidErr = new OSStatus(-9862); //Note More on page 213 - Carbon_Event_Manager_Ref.pdf


    public Pointer GetApplicationEventTarget();

    public Pointer GetEventDispatcherTarget();

    /* OSStatus InstallEventHandler(EventTargetRef inTarget, EventHandlerUPP inHandler, ItemCount inNumTypes, const EventTypeSpec* inList, void* inUserData, EventHandlerRef *outRef) */

    public OSStatus InstallEventHandler(Pointer inTarget, Pointer inHandler, ItemCount inNumTypes, EventTypeSpec[] inList, Pointer inUserData, PointerByReference outRef);

    public OSStatus RegisterEventHotKey(int inHotKeyCode, int inHotKeyModifiers, EventHotKeyID.ByValue inHotKeyID, Pointer inTarget, int inOptions, PointerByReference outRef);

    public Pointer NewEventHandlerUPP(EventHandlerProcPtr userRoutine);

    public Pointer GetEventMonitorTarget();

    public OSStatus RemoveEventHandler(Pointer inHandlerRef);

    /*extern OSStatus AddEventTypesToHandler(
      EventHandlerRef        inHandlerRef,
      ItemCount              inNumTypes,
      const EventTypeSpec *  inList)*/
    public OSStatus AddEventTypesToHandler(Pointer inHandlerRef, ItemCount inNumTypes, EventTypeSpec inList);


    /* typedef SInt32 OSStatus */
    public class OSStatus extends IntegerType {
        public OSStatus() {
            this(0);
        }

        public OSStatus(int value) {
            super(4, value);
        }
    }

    /* Don't see defined anywhere, but used where integer is expected. */
    public  class ItemCount extends IntegerType {
        public ItemCount() {
            this(0);
        }

        public ItemCount(int value) {
            super(4, value);
        }
    }

    /* EventTypeSpec */ /* struct EventTypeSpec { UInt32 eventClass; UInt32 eventKind; }; typedef struct EventTypeSpec EventTypeSpec */
    public  class EventTypeSpec extends Structure {
        public int eventClass;
        public int eventKind;
    }

    /* EventHotKeyID: struct EventHotKeyID { OSType signature; UInt32 id; }; */
    public static class EventHotKeyID extends Structure {
        public int signature;
        public int id;

        public static class ByValue extends EventHotKeyID implements Structure.ByValue {

        }
    }


    public static interface EventHandlerUPP {

    }

    /* typedef OSStatus (*EventHandlerProcPtr) ( EventHandlerCallRef inHandlerCallRef, EventRef inEvent, void * inUserData ); */
    public static interface EventHandlerProcPtr extends Callback {
        public OSStatus callback(Pointer inHandlerCallRef, Pointer inEvent, Pointer inUserData);
        //OSStatus callback(EventHandlerCallRef inHandlerCallRef, EventRef inEvent, Pointer inUserData); }

    }
}