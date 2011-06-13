package com.tulskiy.keymaster;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
public class AWTTest {
    public static final List<Integer> MODIFIERS = Arrays.asList(KeyEvent.VK_ALT, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_META);

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JTextField textField = new JTextField();
        textField.setEditable(false);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (MODIFIERS.contains(e.getKeyCode()))
                    textField.setText("");
                else
                    textField.setText(KeyEvent.getKeyModifiersText(e.getModifiers()) + " " +
                            KeyEvent.getKeyText(e.getKeyCode()));
            }
        });
        frame.add(textField);

        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
