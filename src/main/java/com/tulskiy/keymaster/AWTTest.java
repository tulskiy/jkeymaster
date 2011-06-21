package com.tulskiy.keymaster;

import com.sun.jna.Platform;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;
import com.tulskiy.keymaster.osx.CarbonProvider;
import com.tulskiy.keymaster.windows.WindowsProvider;
import com.tulskiy.keymaster.x11.X11Provider;

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
        final Provider provider = Provider.getCurrentProvider();

        provider.init();
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

        JButton grabMedia = new JButton("Grab media keys");
        grabMedia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                provider.register(MediaKey.MEDIA_NEXT_TRACK, new MediaListener(MediaKey.MEDIA_NEXT_TRACK));
                provider.register(MediaKey.MEDIA_PLAY_PAUSE, new MediaListener(MediaKey.MEDIA_PLAY_PAUSE));
                provider.register(MediaKey.MEDIA_PREV_TRACK, new MediaListener(MediaKey.MEDIA_PREV_TRACK));
                provider.register(MediaKey.MEDIA_STOP, new MediaListener(MediaKey.MEDIA_STOP));
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

    private static class MediaListener implements ActionListener {
        private MediaKey key;

        private MediaListener(MediaKey key) {
            this.key = key;
        }

        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(null, "Hooray, media key: " + key);
                }
            });
        }
    }
}
