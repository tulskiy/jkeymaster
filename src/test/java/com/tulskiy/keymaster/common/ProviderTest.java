package com.tulskiy.keymaster.common;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Ignored test because it's success depeneds on current system setup
 *
 * Author: Denis Tulskiy
 * Date: 7/25/11
 */
@Ignore
public class ProviderTest {
    private Robot robot;
    private final Object lock = new Object();

    @Before
    public void setUp() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test() throws InterruptedException {
        Provider provider = Provider.getCurrentProvider(false);

        assertNotNull(provider);

        final ArrayList<KeyStroke> strokes = new ArrayList<KeyStroke>();

        for (int code = KeyEvent.VK_A; code <= KeyEvent.VK_Z; code++) {
            strokes.add(KeyStroke.getKeyStroke(code, randomModifiers()));
        }

        for (final KeyStroke stroke : strokes) {
            provider.register(stroke, new HotKeyListener() {
                public void onHotKey(HotKey hotKey) {
                    assertFalse(SwingUtilities.isEventDispatchThread());

                    assertFalse(hotKey.isMedia());
                    assertEquals(stroke, hotKey.keyStroke);
                    synchronized (lock) {
                        strokes.remove(stroke);
                    }
                }
            });
        }
        robot.delay(3000);

        synchronized (lock) {
            long start = System.currentTimeMillis();
            for (KeyStroke stroke : strokes) {
                pressHotKey(stroke);
                robot.delay(100);
            }

            while (!strokes.isEmpty()) {
                if (System.currentTimeMillis() - start > 10000) {
                    provider.reset();
                    provider.stop();
                    fail("Timeout. Perhaps some hotkeys failed to be registered");
                }

                lock.wait(300);
            }
        }
        provider.reset();
        provider.stop();
    }

    private int randomModifiers() {
        int modifiers = 0;

        if (Math.random() > 0.5) {
            modifiers |= InputEvent.SHIFT_DOWN_MASK;
        }
        if (Math.random() > 0.5) {
            modifiers |= InputEvent.ALT_DOWN_MASK;
        }
        if (Math.random() > 0.5 || modifiers == 0) {
            modifiers |= InputEvent.CTRL_DOWN_MASK;
        }

        return modifiers;
    }

    private void pressHotKey(KeyStroke key) {
        ArrayList<Integer> keys = new ArrayList<Integer>();

        int modifiers = key.getModifiers();
        if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
            keys.add(KeyEvent.VK_SHIFT);
        }
        if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
            keys.add(KeyEvent.VK_CONTROL);
        }
        if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
            keys.add(KeyEvent.VK_META);
        }
        if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
            keys.add(KeyEvent.VK_ALT);
        }
        keys.add(key.getKeyCode());

        for (int code : keys) {
            robot.keyPress(code);
        }

        robot.delay(100);

        for (int code : keys) {
            robot.keyRelease(code);
        }
    }
}
