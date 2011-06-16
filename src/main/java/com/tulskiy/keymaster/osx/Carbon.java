package com.tulskiy.keymaster.osx;

import com.sun.jna.ptr.PointerByReference;
import org.bridj.BridJ;
import org.bridj.Callback;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;

/**
 * Author: Denis Tulskiy
 * Date: 6/16/11
 */
@Library("Carbon")
public class Carbon {
    static {
        BridJ.register();
    }

    public static native Pointer<EventTargetRef> GetEventDispatcherTarget();

    public static native int InstallEventHandler(Pointer<EventTargetRef> inTarget, EventHandlerProcPtr inHandler, int inNumTypes, Pointer<EventTypeSpec> inList, Pointer<?> inUserData, Pointer<EventHandlerRef> outRef);

    public static native int RegisterEventHotKey(int inHotKeyCode, int inHotKeyModifiers, EventHotKeyID inHotKeyID, Pointer<EventTargetRef> inTarget, int inOptions, Pointer<EventHotKeyRef> outRef);

    public static abstract class EventHandlerProcPtr extends Callback<EventHandlerProcPtr> {
        public abstract int apply(EventHandlerCallRef inHandlerCallRef, EventRef inEvent, Pointer<?> inUserData);
    }

    public static interface EventHotKeyRef {
    }

    public static interface EventHandlerRef {
    }

    public static interface EventRef {
    }

    public static interface EventHandlerCallRef {
    }

    public static interface EventTargetRef {
    }

    public static class EventHotKeyID extends StructObject {
        public EventHotKeyID() {
            super();
        }

        public EventHotKeyID(Pointer pointer) {
            super(pointer);
        }

        @Field(0)
        public int signature() {
            return this.io.getIntField(this, 0);
        }

        @Field(0)
        public EventHotKeyID signature(int signature) {
            this.io.setIntField(this, 0, signature);
            return this;
        }

        @Field(1)
        public int id() {
            return this.io.getIntField(this, 1);
        }

        @Field(1)
        public EventHotKeyID id(int id) {
            this.io.setIntField(this, 1, id);
            return this;
        }
    }

    public static class EventTypeSpec extends StructObject {
        public EventTypeSpec() {
            super();
        }

        public EventTypeSpec(Pointer pointer) {
            super(pointer);
        }

        @Field(0)
        public int eventClass() {
            return this.io.getIntField(this, 0);
        }

        @Field(0)
        public EventTypeSpec eventClass(int eventClass) {
            this.io.setIntField(this, 0, eventClass);
            return this;
        }

        @Field(1)
        public int eventKind() {
            return this.io.getIntField(this, 1);
        }

        @Field(1)
        public EventTypeSpec eventKind(int eventKind) {
            this.io.setIntField(this, 1, eventKind);
            return this;
        }
    }

}
