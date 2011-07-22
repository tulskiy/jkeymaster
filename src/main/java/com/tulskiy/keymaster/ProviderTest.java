/*
 * Copyright (c) 2011 Denis Tulskiy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tulskiy.keymaster;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/13/11
 */
public class ProviderTest {
    private static final Logger log = LoggerFactory.getLogger(ProviderTest.class);

    public static void main(String[] args) throws InterruptedException {
        final Provider provider = Provider.getCurrentProvider();


        final Object lock = new Object();
        register(provider, KeyStroke.getKeyStroke("control alt D"), new HotKeyListener() {
            public void onHotKey(HotKey hotKey) {
                System.out.println(hotKey);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        });

        HotKeyListener listener = new HotKeyListener() {
            public void onHotKey(HotKey hotKey) {
                System.out.println("Hey, I found new event! : " + hotKey);
            }
        };
        register(provider, KeyStroke.getKeyStroke("control shift 0"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift PLUS"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift INSERT"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift ESCAPE"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift BACK_QUOTE"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift SLASH"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift BACK_SLASH"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift DIVIDE"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift MULTIPLY"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift ENTER"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift MINUS"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift BACK_QUOTE"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift UP"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift INSERT"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift DELETE"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift ADD"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift SUBTRACT"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift COMMA"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift PERIOD"), listener);
        register(provider, KeyStroke.getKeyStroke("control shift SEMICOLON"), listener);
        register(provider, KeyStroke.getKeyStroke("control alt HOME"), listener);
        register(provider, KeyStroke.getKeyStroke("control alt PAGE_UP"), listener);
        register(provider, KeyStroke.getKeyStroke("control alt NUMPAD0"), listener);
        synchronized (lock) {
            lock.wait();
        }
        provider.stop();
    }

    private static void register(Provider provider, KeyStroke keyStroke, HotKeyListener listener) {
        try {
            provider.register(keyStroke, listener);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }
}
