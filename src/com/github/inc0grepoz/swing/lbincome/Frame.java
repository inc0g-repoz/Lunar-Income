package com.github.inc0grepoz.swing.lbincome;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Frame extends JFrame {

    private JTextField jtfBalance = new JTextField("0", 10);
    private JTextField jtfInterest = new JTextField("1.0", 10);
    private JTextField jtfDelay = new JTextField("30", 6);
    private JTextField jtfTime = new JTextField("12", 6);

    private JLabel jlHeaderImage;
    private JLabel jlBalance = new JLabel("Balance:");
    private JLabel jlInterest = new JLabel("Interest:");
    private JLabel jlDelay = new JLabel("Delay (mins):");
    private JLabel jlTime = new JLabel("Time (hours):");

    private JLabel jlResult = new JLabel("$0.00");
    private JButton jbResult = new JButton("Calculate");
    private JCheckBox jcbOnline = new JCheckBox("Online", true);

    protected Frame() {
        setTitle("LunarIncome");
        setSize(300, 370);
        setLocation(500, 300);
        setResizable(false);

        JPanel panel = new JPanel() {
            {
                setLayout(new FlowLayout(FlowLayout.CENTER, 100, 10));

                try {
                    BufferedImage myPicture = ImageIO.read(getClass().getClassLoader()
                            .getResource("assets/ad.png"));
                    jlHeaderImage = new JLabel(new ImageIcon(myPicture));
                    jlHeaderImage.setToolTipText("Click here to copy the command!");
                    add(jlHeaderImage);
                } catch (IOException ioe) {
                }

                
                JPanel borderedPanel = new JPanel() {
                    {
                        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

                        JPanel balancePanel = new JPanel();
                        balancePanel.add(jlBalance);
                        balancePanel.add(jtfBalance);
                        add(balancePanel);

                        JPanel interestPanel = new JPanel();
                        interestPanel.add(jlInterest);
                        interestPanel.add(jtfInterest);
                        add(interestPanel);

                        JPanel delayPanel = new JPanel();
                        delayPanel.add(jlDelay);
                        delayPanel.add(jtfDelay);
                        add(delayPanel);

                        JPanel timePanel = new JPanel();
                        timePanel.add(jlTime);
                        timePanel.add(jtfTime);
                        add(timePanel);
                    }
                };

                add(jcbOnline);
                borderedPanel.setBorder(BorderFactory.createEtchedBorder());
                add(borderedPanel);

                jlResult.setFont(new Font("Serif", Font.PLAIN, 25));
                add(jlResult);
                add(jbResult);
            }
        };

        ActionListener al = (e) -> updateLabel();
        jtfBalance.addActionListener(al);
        jtfInterest.addActionListener(al);
        jtfDelay.addActionListener(al);
        jtfTime.addActionListener(al);
        jbResult.addActionListener(al);

        if (jlHeaderImage != null) {
            jlHeaderImage.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    copyText("lunarblock.minehut.gg");
                    jlResult.setText("Copied!");
                }
            });
        }

        add(panel);
    }

    public void updateLabel() {
        String oldText = jlResult.getText();
        double balance, interest, delay, time;
        try {
            balance = Double.valueOf(jtfBalance.getText());
            interest = Double.valueOf(jtfInterest.getText());
            delay = Double.valueOf(jtfDelay.getText());
            time = Double.valueOf(jtfTime.getText());
        } catch (Exception e) {
            jlResult.setText("Invalid input!");
            jlResult.setForeground(Color.red);
            return;
        }

        if (!jcbOnline.isSelected()) {
            delay *= 2;
        }

        double timeMins = time * 60;
        for (double i = 0; i < timeMins; i += delay) {
            double portion = (balance / 100) * interest;
            balance += portion;
        }

        jlResult.setText("$" + format(balance));
        jlResult.setForeground(Color.black);

        if (!oldText.equals(jlResult.getText())) {
            playSound("assets/kiddpark.wav");
        }
    }

    public void copyText(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void playSound(String resourcePath) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass()
                    .getClassLoader().getResource(resourcePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
        }
    }

    public String format(double time) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        String string = df.format(time);
        if (string.startsWith(".")) {
            string = "0" + string;
        }
        return string;
    }

}
