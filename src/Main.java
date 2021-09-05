import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("My Clicker");
        JPanel panel = new JPanel(new FlowLayout());
        Point mousePoint = MouseInfo.getPointerInfo().getLocation();
        AtomicBoolean stop = new AtomicBoolean(true);

        Label mousePositionX = new Label("X = " + mousePoint.x);
        Label mousePositionY = new Label("Y = " + mousePoint.y);
        panel.add(mousePositionX);
        panel.add(mousePositionY);
        final Thread[] thread = {createThread(stop)};
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Button pressed : " + e);
                if (e.getKeyCode() == KeyEvent.VK_F1) {
                    stop.set(false);
                    if (thread[0].isAlive()) {
                        thread[0].start();
                    } else {
                        try {
                            thread[0].interrupt();
                        } catch (Exception exception) {
                            // ignore InterruptedException
                        }
                        thread[0] = createThread(stop);
                        thread[0].start();
                    }
                }

                if (e.getKeyCode() == KeyEvent.VK_F2) {
                    stop.set(true);
                    try {
                        thread[0].interrupt();
                    } catch (Exception exception) {
                        // ignore InterruptedException
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        panel.addMouseListener(createMouseListener(frame, mousePositionX, mousePositionY));
        frame.addMouseListener(createMouseListener(frame, mousePositionX, mousePositionY));

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.setSize(240, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        thread[0].run();
    }

    private static Thread createThread(AtomicBoolean stop) {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point currentPosition = pointerInfo.getLocation();
        AtomicBoolean right = new AtomicBoolean(true);

        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Robot robot = new Robot();
                    while (!stop.get()) {
                        try {
                            sleep(2500);
                            int x = pointerInfo.getLocation().x;
                            int y = pointerInfo.getLocation().y;
                            if (right.get()) {
                                robot.mouseMove(x + 50, y);
                            } else {
                                robot.mouseMove(x - 50, y);
                            }
                            right.set(!right.get());

                            robot.mousePress(MouseEvent.BUTTON3_DOWN_MASK);
                            robot.mouseRelease(MouseEvent.BUTTON3_DOWN_MASK);
                            sleep(800);
                            robot.mousePress(MouseEvent.BUTTON3_DOWN_MASK);
                            robot.mouseRelease(MouseEvent.BUTTON3_DOWN_MASK);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                        pointerInfo.getLocation().setLocation(currentPosition);
                    }
                } catch (AWTException e) {
                    // ignore
                }
            }
        });
    }

    private static MouseListener createMouseListener(JFrame frame, Label mousePositionX, Label mousePositionY) {
        return new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point mousePoint = MouseInfo.getPointerInfo().getLocation();
                JPanel panel = new JPanel(new FlowLayout());
                mousePositionX.setText("X = " + mousePoint.x);
                mousePositionY.setText("Y = " + mousePoint.y);

                panel.add(mousePositionX);
                panel.add(mousePositionY);
                frame.remove(0);
                frame.add(panel);
            }
        };
    }
}
