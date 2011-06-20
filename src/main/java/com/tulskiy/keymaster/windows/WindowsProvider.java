package com.tulskiy.keymaster.windows;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.*;

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

    private boolean listen;
    private Boolean reset = false;
    private final Object lock = new Object();
    private Thread thread;

    private Map<Integer, HotKey> hotKeys = new HashMap<Integer, HotKey>();
    private List<HotKey> registerQueue = new ArrayList<HotKey>();

    public void init() {
        Runnable runnable = new Runnable() {
            public void run() {
                MSG msg = new MSG();
                listen = true;
                while (listen) {
                    synchronized (lock) {
                        if (reset) {
                            for (Integer id : hotKeys.keySet()) {
                                UnregisterHotKey(null, id);
                            }

                            hotKeys.clear();
                            reset = false;
                            lock.notify();
                        }

                        if (!registerQueue.isEmpty()) {
                            for (HotKey entry : registerQueue) {
                                register(entry);
                            }
                            registerQueue.clear();
                        }
                    }

                    while (PeekMessage(msg, null, 0, 0, PM_REMOVE)) {
                        if (msg.message == WM_HOTKEY) {
                            int id = msg.wParam.intValue();
                            HotKey hotKey = hotKeys.get(id);

                            if (hotKey != null) {
                                fireEvent(hotKey);
                            }
                        }
                    }
                    try {
                        lock.wait(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread = new Thread(runnable);
        thread.start();
    }

    private void register(HotKey entry) {
        int id = idSeq++;
        KeyStroke keyCode = entry.keyStroke;
        int code = keyCode.getKeyCode();
        if (codeExceptions.containsKey(code)) {
            code = codeExceptions.get(code);
        }
        if (RegisterHotKey(null, id, getModifiers(keyCode), code)) {
            logger.info("Registering hotkey: " + keyCode);
            hotKeys.put(id, entry);
        } else {
            logger.warning("Could not register hotkey: " + keyCode);
        }
    }

    public void register(KeyStroke keyCode, ActionListener listener) {
        synchronized (lock) {
            registerQueue.add(new HotKey(keyCode, listener));
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
