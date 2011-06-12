package com.tulskiy.keymaster;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Author: Denis Tulskiy
 * Date: 6/6/11
 */
public class AWTTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JTextField textField = new JTextField();
        textField.setEditable(false);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    Class cl = Class.forName("java.awt.AWTEvent");
                    Field f = cl.getDeclaredField("bdata");
                    f.setAccessible(true);
                    byte[] result = (byte[]) f.get(e);
                    System.out.println(Arrays.toString(result));
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (NoSuchFieldException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
                textField.setText(KeyEvent.getModifiersExText(e.getModifiersEx()) + " " +
                        KeyEvent.getKeyText(e.getKeyCode()));
            }
        });
        frame.add(textField);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
