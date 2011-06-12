package com.tulskiy.keymaster.windows;

import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;
import org.bridj.Pointer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;

import static com.tulskiy.keymaster.windows.Kernel32.GetCurrentThreadId;
import static com.tulskiy.keymaster.windows.User32.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/12/11
 */
public class WindowsProvider implements Provider {
    private ActionListener mediaListener;
    private boolean initialized = false;
    private static volatile int idSeq = 0;
    private EnumMap<MediaKey, Integer> mediaIds = new EnumMap<MediaKey, Integer>(MediaKey.class);

    public void init() {
        Runnable runnable = new Runnable() {
            public void run() {
                Pointer<MSG> msgPointer = Pointer.allocate(MSG.class);

                while (GetMessage(msgPointer, null, 0, 0) != 0) {
                    MSG msg = msgPointer.get();
                    System.out.println(msg.message());
                    if (msg.message() == WM_HOTKEY) {
                        switch (msg.wParam()) {
                            case VK_MEDIA_NEXT_TRACK:
                                mediaListener.actionPerformed(new ActionEvent(MediaKey.MEDIA_NEXT_TRACK, 0, ""));
                                break;
                            case VK_MEDIA_PREV_TRACK:
                                mediaListener.actionPerformed(new ActionEvent(MediaKey.MEDIA_PREV_TRACK, 0, ""));
                                break;
                            case VK_MEDIA_PLAY_PAUSE:
                                mediaListener.actionPerformed(new ActionEvent(MediaKey.MEDIA_PLAY_PAUSE, 0, ""));
                                break;
                            case VK_MEDIA_STOP:
                                mediaListener.actionPerformed(new ActionEvent(MediaKey.MEDIA_STOP, 0, ""));
                                break;
                        }
                    }
                }
            }
        };

        final Thread thread = new Thread(runnable);
        thread.start();
    }

    public void registerMediaKeyListener(ActionListener listener) {
        mediaListener = listener;
        registerMediaKeys();
    }

    private void registerMediaKeys() {
        int id;
        id = idSeq++;
        if (RegisterHotKey(null, id, 0, VK_MEDIA_NEXT_TRACK)) {
            mediaIds.put(MediaKey.MEDIA_NEXT_TRACK, id);
        }
        id = idSeq++;
        if (RegisterHotKey(null, id, 0, VK_MEDIA_PREV_TRACK)) {
            mediaIds.put(MediaKey.MEDIA_PREV_TRACK, id);
        }
        id = idSeq++;
        if (RegisterHotKey(null, id, 0, VK_MEDIA_PLAY_PAUSE)) {
            mediaIds.put(MediaKey.MEDIA_PLAY_PAUSE, id);
        }
        id = idSeq++;
        if (RegisterHotKey(null, id, 0, VK_MEDIA_STOP)) {
            mediaIds.put(MediaKey.MEDIA_STOP, id);
        }
    }

    public boolean register(String keyCode, ActionListener listener) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode);
        return false;
    }

    public boolean unregisterAll() {
        return false;
    }

    public boolean stop() {
        return false;
    }
}
