package com.tulskiy.keymaster.osx;

import com.sun.jna.ptr.PointerByReference;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tulskiy.keymaster.osx.Carbon.Lib;

/**
 * Author Alex (mq0) Ivanov at 21.07.11 18:54
 */
public class OSXHelper {
    private static final AtomicInteger idSeq = new AtomicInteger(1);

    public static synchronized OSXHotKey registerKeyStroke(KeyStroke hotKey) {
        Carbon.EventHotKeyID.ByValue hotKeyReference = new Carbon.EventHotKeyID.ByValue();
        int id = idSeq.incrementAndGet();
        hotKeyReference.id = id;
        hotKeyReference.signature = OS_TYPE("hk" + String.format("%02d", id));
        PointerByReference gMyHotKeyRef = new PointerByReference();

        int status = Lib.RegisterEventHotKey(
                KeyMap.getKeyCode(hotKey),
                KeyMap.getModifier(hotKey),
                hotKeyReference,
                Lib.GetEventDispatcherTarget(),
                0,
                gMyHotKeyRef);

        if (status != 0) {
            throw new RuntimeException("Could not registerKeyStroke HotKey: " + hotKey + ". Error code: " + status);
        }

        if (gMyHotKeyRef.getValue() == null) {
            throw new RuntimeException("HotKey returned null handler reference");
        }

        OSXHotKey osXHotkey = new OSXHotKey(hotKey, gMyHotKeyRef, id);
        Provider.logger.info("Registered hotkey: " + hotKey);
        return osXHotkey;
    }

    public static int OS_TYPE(String osType) {
        byte[] bytes = osType.getBytes();
        return (bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3];
    }
}
