package com.tulskiy.keymaster.windows;

import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.tulskiy.keymaster.windows.User32.*;
import static java.awt.event.KeyEvent.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/12/11
 */
public class WindowsProvider extends Provider {
    private static final Map<Integer, Integer> codeExceptions = new HashMap<Integer, Integer>() {{
        put(VK_INSERT, 0x2D);
        put(VK_DELETE, 0x2E);
        put(VK_ENTER, 0x0D);
        put(VK_COMMA, 0xBC);
        put(VK_PERIOD, 0xBE);
        put(VK_PLUS, 0xBB);
        put(VK_MINUS, 0xBD);
        put(VK_SLASH, 0xBF);
        put(VK_SEMICOLON, 0xBA);
    }};

    private static volatile int idSeq = 0;

    private Map<Integer, ActionListener> idToListener = Collections.synchronizedMap(new HashMap<Integer, ActionListener>());
    private Map<Integer, KeyStroke> idToKeyStroke = Collections.synchronizedMap(new HashMap<Integer, KeyStroke>());
    private final Map<KeyStroke, ActionListener> toRegister = new HashMap<KeyStroke, ActionListener>();
    private boolean listen;
    private Boolean reset = false;
    private final Object lock = new Object();
    private Thread thread;

    public void init() {
        Runnable runnable = new Runnable() {
            public void run() {
                MSG msg = new MSG();
                listen = true;
                while (listen) {
                    synchronized (lock) {
                        if (reset) {
                            for (Integer id : idToListener.keySet()) {
                                UnregisterHotKey(null, id);
                            }

                            idToListener.clear();
                            idToKeyStroke.clear();
                            reset = false;
                            lock.notify();
                        }

                        for (Map.Entry<KeyStroke, ActionListener> entry : toRegister.entrySet()) {
                            int id = idSeq++;
                            KeyStroke keyCode = entry.getKey();
                            ActionListener listener = entry.getValue();
                            int code = keyCode.getKeyCode();
                            if (codeExceptions.containsKey(code)) {
                                code = codeExceptions.get(code);
                            }
                            if (RegisterHotKey(null, id, getModifiers(keyCode), code)) {
                                logger.info("Registering hotkey: " + keyCode);
                                idToListener.put(id, listener);
                                idToKeyStroke.put(id, keyCode);
                            } else {
                                logger.warning("Could not register hotkey: " + keyCode);
                            }
                        }
                        toRegister.clear();
                    }

                    while (PeekMessage(msg, null, 0, 0, PM_REMOVE)) {
                        if (msg.message == WM_HOTKEY) {
                            int id = msg.wParam.intValue();
                            ActionListener listener = idToListener.get(id);

                            if (listener != null) {
                                fireEvent(idToKeyStroke.get(id), listener);
                            }
                        }
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread = new Thread(runnable);
        thread.start();
    }

    public void register(KeyStroke keyCode, ActionListener listener) {
        synchronized (lock) {
            toRegister.put(keyCode, listener);
        }
    }

    public void register(MediaKey mediaKey, ActionListener listener) {
        int code = 0;
        switch (mediaKey) {
            case MEDIA_NEXT_TRACK:
                code = VK_MEDIA_NEXT_TRACK;
                break;
            case MEDIA_PLAY_PAUSE:
                code = VK_MEDIA_PLAY_PAUSE;
                break;
            case MEDIA_PREV_TRACK:
                code = VK_MEDIA_PREV_TRACK;
                break;
            case MEDIA_STOP:
                code = VK_MEDIA_STOP;
                break;
        }

        register(KeyStroke.getKeyStroke(code, 0), listener);
    }

    public void reset() {
        synchronized (lock) {
            reset = true;
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        listen = false;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getModifiers(KeyStroke keyCode) {
        int modifiers = 0;
        if ((keyCode.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0) {
            modifiers |= MOD_SHIFT;
        }
        if ((keyCode.getModifiers() & InputEvent.CTRL_DOWN_MASK) != 0) {
            modifiers |= MOD_CONTROL;
        }
        if ((keyCode.getModifiers() & InputEvent.META_DOWN_MASK) != 0) {
            modifiers |= MOD_WIN;
        }
        if ((keyCode.getModifiers() & InputEvent.ALT_DOWN_MASK) != 0) {
            modifiers |= MOD_ALT;
        }
        return modifiers;
    }
}
