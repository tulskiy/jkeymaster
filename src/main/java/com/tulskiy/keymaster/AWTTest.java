package com.tulskiy.keymaster;

import com.tulskiy.keymaster.common.Provider;
import com.tulskiy.keymaster.windows.WindowsProvider;
import com.tulskiy.keymaster.x11.X11Provider;
import org.bridj.Platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
        final JFrame frame = new JFrame();
        final Provider provider;
        if (Platform.isUnix()) {
            provider = new X11Provider();
        } else if (Platform.isWindows()) {
            provider = new WindowsProvider();
        } else {
            System.out.println("No suitable provider!");
            return;
        }
        provider.init();
        final JTextField textField = new JTextField();
        textField.setEditable(false);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (MODIFIERS.contains(e.getKeyCode()))
                    textField.setText("");
                else
                    textField.setText(KeyStroke.getKeyStrokeForEvent(e).toString());
            }
        });
        frame.add(textField, BorderLayout.CENTER);
        JButton grab = new JButton("Grab");
        frame.add(grab, BorderLayout.PAGE_END);
        grab.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                if (text != null && !text.isEmpty()) {
                    provider.reset();
                    provider.register(KeyStroke.getKeyStroke(text), new ActionListener() {
                        public void actionPerformed(final ActionEvent e) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    JOptionPane.showMessageDialog(frame, "Hooray: " + e.getSource());
                                }
                            });
                        }
                    });
                }
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                provider.reset();
                provider.stop();
                System.exit(0);
            }
        });

        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
