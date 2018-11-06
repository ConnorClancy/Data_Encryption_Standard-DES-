/*
UI creates and populates Components through which the user interacts with the application.
Takes input from the user in the form of a key and an input file or a manually typed input message.
 */

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class UI extends JFrame{

    private int FLAG; //0 for file input, 1 for message input
    private String MESSAGE_INPUT;
    private File INPUT;
    private File KEY;

    UI(){
        setBounds(45, 50, 400, 650);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        ImageIcon l = new ImageIcon("resources/EncImage.png");
        ImageIcon r = new ImageIcon("resources/DecImage.png");
        ImageIcon backing = new ImageIcon("resources/test.png");

        JPanel label = new JPanel();
        label.setBackground(Color.white);

        JLabel left = new JLabel(l);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        /**
            Encryption button
         */
        JButton EncryptionButton = new JButton(l);
        EncryptionButton.setContentAreaFilled(false);
        EncryptionButton.setBorderPainted(false);
        EncryptionButton.setOpaque(false);
        EncryptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(INPUT != null && KEY != null){
                    int letter;
                    StringBuilder PlainIn = new StringBuilder();
                    try {
                        if(FLAG == 0) { //convert file into hexadecimal
                            BufferedReader fp = new BufferedReader(new FileReader(INPUT));
                            while ((letter = fp.read()) != -1) {
                                String temp = Integer.toHexString(letter);
                                //check for new line and tab characters to prevent distorted output
                                if(temp.equals("a"))
                                    PlainIn.append("a0");
                                else if(temp.equals("9"))
                                    PlainIn.append("90");
                                else
                                    PlainIn.append(Integer.toHexString(letter));
                            }
                        }
                        else {
                            for(char c: MESSAGE_INPUT.toCharArray()){
                                PlainIn.append(Integer.toHexString(c));
                            }
                        }
                        StringBuilder keyBuilder = new StringBuilder();
                        BufferedReader fp = new BufferedReader(new FileReader(KEY));
                        while ((letter = fp.read()) != -1)
                            keyBuilder.append((char) letter);

                        DesAlgorithm D = new DesAlgorithm(PlainIn, keyBuilder.toString());
                        FileWriter fw = new FileWriter("Encryption Output.txt");
                        fw.write(D.encrypt());
                        fw.close();
                        dispose();
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
                else {
                    System.out.println("Invalid Input(s), encryption failed...");
                    dispose();
                }
            }
        });
        left.add(EncryptionButton);

        JLabel right = new JLabel(backing);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        /**
            Decryption button
         */
        JButton DecryptionButton = new JButton(r);
        DecryptionButton.setContentAreaFilled(false);
        DecryptionButton.setBorderPainted(false);
        DecryptionButton.setOpaque(false);
        DecryptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(INPUT != null && KEY != null){
                    int letter;
                    StringBuilder EncIn = new StringBuilder();
                    try {
                        if(FLAG == 0) {
                            BufferedReader fp = new BufferedReader(new FileReader(INPUT));
                            while ((letter = fp.read()) != -1) {
                                EncIn.append((char) letter);
                            }
                        }
                        else {
                            for(char c: MESSAGE_INPUT.toCharArray()){
                                EncIn.append(c);
                            }
                        }
                        StringBuilder keyBuilder = new StringBuilder();

                        BufferedReader fp = new BufferedReader(new FileReader(KEY));
                        while ((letter = fp.read()) != -1)
                            keyBuilder.append((char) letter);

                        DesAlgorithm D = new DesAlgorithm(EncIn, keyBuilder.toString());
                        FileWriter fw = new FileWriter("Decryption Output.txt");
                        fw.write(D.decrypt());
                        fw.close();
                        dispose();
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                else {
                    System.out.println("Invalid Input(s), decryption failed...");
                    dispose();
                }
            }
        });
        right.add(DecryptionButton);

        label.add(left);
        label.add(right);

        add(label, BorderLayout.NORTH);

        JPanel selection = new JPanel();
        selection.setBackground(Color.WHITE);

        selection.add(Box.createRigidArea(new Dimension(400, 160)));
        final JButton key = new JButton(new ImageIcon("resources/key_image.png"));
        final MouseListener KM = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) {
                key.setIcon(new ImageIcon("resources/hover_key_image.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                key.setIcon(new ImageIcon("resources/key_image.png"));
            }
        };
        /**
            Key Input
         */
        key.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser browser = new JFileChooser(FileSystemView.getFileSystemView());
                browser.setDialogTitle("Choose key");
                browser.setAcceptAllFileFilterUsed(false);
                browser.addChoosableFileFilter(new FileNameExtensionFilter(
                        "key file (.key)", "key"));

                if (browser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    KEY = browser.getSelectedFile();
                    key.setIcon(new ImageIcon("resources/inputted_key_image.png"));
                    key.removeActionListener(this);
                    key.removeMouseListener(KM);
                }
            }
        });
        key.addMouseListener(KM);
        key.setBorderPainted(false);
        selection.add(key);
        selection.add(Box.createRigidArea(new Dimension(400, 10)));
        final JButton input = new JButton(new ImageIcon("resources/inputimage.png"));
        input.setBorderPainted(false);
         /**
            File Input
         */
        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser browser = new JFileChooser(FileSystemView.getFileSystemView());
                browser.setDialogTitle("Choose file to encrypt");
                browser.setAcceptAllFileFilterUsed(false);
                browser.addChoosableFileFilter(new FileNameExtensionFilter(
                        "text files (.dat or .txt)", "dat", "txt", "png"));

                if (browser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    INPUT = browser.getSelectedFile();
                    FLAG = 0;
                }
            }
        });
        input.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) {
                input.setIcon(new ImageIcon("resources/hover_input_image.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                input.setIcon(new ImageIcon("resources/inputimage.png"));
            }
        });
        selection.add(input);
        selection.add(Box.createRigidArea(new Dimension(400, 10)));
        final JButton messageInput = new JButton (new ImageIcon("resources/message_image.png"));
        messageInput.setBorderPainted(false);
         /**
            Manual Message Input
         */
        messageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MESSAGE_INPUT = JOptionPane.showInputDialog("Enter message...");
                    FLAG = 1;
                    INPUT = new File("message.txt");
                }catch (Exception E){
                    E.printStackTrace();
                }
            }
        });
        messageInput.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) {
                messageInput.setIcon(new ImageIcon("resources/hover_message_image.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                messageInput.setIcon(new ImageIcon("resources/message_image.png"));
            }
        });

        selection.add(messageInput);

        add(selection);

        JButton finish = new JButton("EXIT");
        finish.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        } );

        add(finish, BorderLayout.AFTER_LAST_LINE);


        setVisible(true);
    }
}
