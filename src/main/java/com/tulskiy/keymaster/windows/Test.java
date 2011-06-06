package com.tulskiy.keymaster.windows;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.jawt.JAWTUtils;

import javax.swing.*;

import static com.tulskiy.keymaster.windows.User32.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
public class Test {

    private static final int id = 0x0012;

    public static void main(String[] args) {
        if (!RegisterHotKey(null, id, MOD_ALT | MOD_NOREPEAT, 0x42)) {
            System.out.println("Error");
            return;
        }

        Pointer<MSG> msgPointer = Pointer.allocate(MSG.class);

        try {
            while (GetMessage(msgPointer, null, 0, 0) != 0) {
                MSG msg = msgPointer.get();
                if (msg.message() == WM_HOTKEY && msg.wParam() == id) {
                    System.out.println("FUCK YEAH");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            UnregisterHotKey(null, id);
        }
    }
}
