package com.tulskiy.keymaster.windows;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.jawt.JAWTUtils;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.tulskiy.keymaster.windows.Kernel32.GetCurrentThreadId;
import static com.tulskiy.keymaster.windows.User32.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
public class Test {

    private static final int id = 0x0012;
    private static long threadId;

    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            public void run() {
                if (!RegisterHotKey(null, id, MOD_NOREPEAT, 0xB3)) {
                    System.out.println("Error");
                    return;
                }
                threadId = GetCurrentThreadId();
                Pointer<MSG> msgPointer = Pointer.allocate(MSG.class);

                try {
                    while (GetMessage(msgPointer, null, 0, 0) != 0) {
                        MSG msg = msgPointer.get();
                        System.out.println(msg.message());
                        if (msg.message() == WM_HOTKEY && msg.wParam() == id) {
                            System.out.println("FUCK YEAH");
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    UnregisterHotKey(null, id);
                }
            }
        };

        final Thread thread = new Thread(runnable);
        thread.start();


    }
}
