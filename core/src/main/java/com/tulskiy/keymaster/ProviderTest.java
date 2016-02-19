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

import javax.swing.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/13/11
 */
public class ProviderTest {
    public static void main(String[] args) {
        final Provider provider = Provider.getCurrentProvider(false);

        provider.register(KeyStroke.getKeyStroke("control alt F"), new HotKeyListener() {
            public void onHotKey(HotKey hotKey) {
                System.out.println(hotKey);
                provider.reset();
                provider.stop();
            }
        });

        HotKeyListener listener = new HotKeyListener() {
            public void onHotKey(HotKey hotKey) {
                System.out.println(hotKey);
            }
        };
        provider.register(KeyStroke.getKeyStroke("control shift 1"), listener);
        provider.register(KeyStroke.getKeyStroke("control shift released 1"), listener);
    }
}
