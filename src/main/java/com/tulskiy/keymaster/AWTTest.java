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
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
public class AWTTest {
    public static final List<Integer> MODIFIERS = Arrays.asList(KeyEvent.VK_ALT, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_META);

    public static void main(String[] args) {
        final JFrame frame = new JFrame();
        final Provider provider = Provider.getCurrentProvider(true);

        final JTextField textField = new JTextField();
        textField.setEditable(false);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (MODIFIERS.contains(e.getKeyCode()))
                    textField.setText("");
                else
                    textField.setText(KeyStroke.getKeyStrokeForEvent(e).toString().replaceAll("pressed ", ""));
            }
        });
        frame.add(textField, BorderLayout.CENTER);
        JPanel box = new JPanel(new GridLayout(2, 1));
        JButton grab = new JButton("Grab");
        box.add(grab);
        final HotKeyListener listener = new HotKeyListener() {
            public void onHotKey(final HotKey hotKey) {
                JOptionPane.showMessageDialog(frame, "Hooray: " + hotKey);
            }
        };
        grab.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                if (text != null && text.length() > 0) {
                    provider.reset();
                    provider.register(KeyStroke.getKeyStroke(text), listener);
                }
            }
        });

        JButton grabMedia = new JButton("Grab media keys");
        grabMedia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                provider.register(MediaKey.MEDIA_NEXT_TRACK, listener);
                provider.register(MediaKey.MEDIA_PLAY_PAUSE, listener);
                provider.register(MediaKey.MEDIA_PREV_TRACK, listener);
                provider.register(MediaKey.MEDIA_STOP, listener);
            }
        });
        box.add(grabMedia);


        frame.add(box, BorderLayout.PAGE_END);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                provider.reset();
                provider.stop();
                System.exit(0);
            }
        });

        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
