package com.tulskiy.keymaster.osx;

import org.bridj.Pointer;

import javax.swing.*;

import static com.tulskiy.keymaster.osx.Carbon.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/16/11
 */
public class Test2 {
    public static final int kEventHotKeyPressed = 5;
    public static final int kEventHotKeyReleased = 6;
    public static final int kEventClassKeyboard = ((int) 'k' << 24) + ((int) 'e' << 16) + ((int) 'y' << 8) + (int) 'b';

    static final int cmdKey = 0x0100;
    static final int shiftKey = 0x0200;
    static final int optionKey = 0x0800;
    static final int controlKey = 0x1000;

    public static void main(String[] args) {
        EventHandlerProcPtr handler = new EventHandlerProcPtr() {
            @Override
            public int apply(EventHandlerCallRef inHandlerCallRef, EventRef inEvent, Pointer<?> inUserData) {
                System.out.println("Fuck yeah");
                return 0;
            }
        };

        EventTypeSpec eventTypeSpec = new EventTypeSpec();
        eventTypeSpec.eventClass(kEventClassKeyboard).eventKind(kEventHotKeyPressed);
        Pointer<EventHandlerRef> fHandlerRef = Pointer.allocate(EventHandlerRef.class);

        int ret = InstallEventHandler(GetEventDispatcherTarget(), handler, 1, Pointer.pointerTo(eventTypeSpec), null, fHandlerRef);
        System.out.println("InstallEventHandler return: " + ret);

        EventHotKeyID gMyHotKeyID = new EventHotKeyID();
        gMyHotKeyID.id(1).signature(((int) 'h' << 24) + ((int) 't' << 16) + ((int) 'k' << 8) + '1');

        Pointer<EventHotKeyRef> eventHotKeyRefs = Pointer.allocate(EventHotKeyRef.class);
        ret = RegisterEventHotKey(9, controlKey | shiftKey, gMyHotKeyID, GetEventDispatcherTarget(), 0, eventHotKeyRefs);

        System.out.println("RegisterEventHotKey return: " + ret);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(100, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
