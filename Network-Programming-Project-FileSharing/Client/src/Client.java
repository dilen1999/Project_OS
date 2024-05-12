import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        final File[] fileToSend = new File[1];
        final String[] hostAddress = { "localhost" }; // Default host address

        
        JFrame jFrame = new JFrame("File Sharing System");
        jFrame.setSize(500, 500);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel jlTitle = new JLabel("FILE SHARING SYSTEM");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 30));
        jlTitle.setBorder(new EmptyBorder(20,0,10,0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlTitle.setForeground(Color.BLUE);

        JLabel jlFileName = new JLabel("Choose a file to send.");
        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
        jlFileName.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlFileName.setForeground(Color.RED);

        JPanel jpButton = new JPanel();
        jpButton.setLayout(new BoxLayout(jpButton, BoxLayout.Y_AXIS));
        jpButton.setBorder(new EmptyBorder(5, 0, 5, 0));

        JButton jbChooseFile = new JButton("<html>Choose File<br> &nbsp; &nbsp; &nbsp; &nbsp;  \u25BC</html>");
        jbChooseFile.setPreferredSize(new Dimension(500, 200));
        jbChooseFile.setMaximumSize(new Dimension(Integer.MAX_VALUE, jbChooseFile.getPreferredSize().height));
        jbChooseFile.setFont(new Font("Arial", Font.BOLD, 30));
        jbChooseFile.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton jbSendFile = new JButton("Send File");
        jbSendFile.setPreferredSize(new Dimension(150, 50));
        jbSendFile.setFont(new Font("Arial", Font.BOLD, 12));
        jbSendFile.setAlignmentX(Component.CENTER_ALIGNMENT);
        jbSendFile.setForeground(Color.GREEN); // Change text color to black

        JTextField hostTextField = new JTextField(hostAddress[0], 15); // Text field for entering host IP address
        hostTextField.setPreferredSize(new Dimension(200, 31));

        JButton updateHostButton = new JButton("Update Host"); // Button to update host IP address
        updateHostButton.setPreferredSize(new Dimension(150, 30));
        updateHostButton.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel hostPanel = new JPanel();
        hostPanel.setLayout(new FlowLayout());
        hostPanel.add(hostTextField);
        hostPanel.add(updateHostButton);

        jpButton.setBorder(new EmptyBorder(5, 0, 10, 0));
        jpButton.add(jbChooseFile);
        jpButton.setBorder(new EmptyBorder(50, 0, 10, 0));
        jpButton.add(jbSendFile);

        jpButton.add(hostPanel);

        jbSendFile.setBackground(Color.white);
        jbChooseFile.setBackground(new Color(255, 255, 255));
        jbSendFile.setForeground(Color.WHITE);
        jbChooseFile.setForeground(Color.BLACK);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        jbSendFile.setFont(buttonFont);
        jbChooseFile.setFont(buttonFont);

        jbSendFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jbSendFile.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                jbSendFile.setBackground(new Color(52, 152, 219));
            }
        });

        jbChooseFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jbChooseFile.setBackground(new Color(204, 229, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                jbChooseFile.setBackground(new Color(255, 255, 255));
            }
        });

        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Choose a file to send.");
                if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jlFileName.setText("The file you want to send is: " + fileToSend[0].getName());
                }
            }
        });

        updateHostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hostAddress[0] = hostTextField.getText(); // Update host address
                JOptionPane.showMessageDialog(jFrame, "The Host IP address is updated", "Host IP Updated", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        jbSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    if (fileToSend[0] == null) {
                        jlFileName.setText("Please choose a file to send first!");
                    } else {
                        try {
                            FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                            Socket socket = new Socket(hostAddress[0], 1234); // Use updated host address
                            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                            String fileName = fileToSend[0].getName();
                            byte[] fileNameBytes = fileName.getBytes();
                            byte[] fileBytes = new byte[(int) fileToSend[0].length()];
                            fileInputStream.read(fileBytes);
                            dataOutputStream.writeInt(fileNameBytes.length);
                            dataOutputStream.write(fileNameBytes);
                            dataOutputStream.writeInt(fileBytes.length);
                            dataOutputStream.write(fileBytes);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        jFrame.add(jlTitle);
        jFrame.add(jlFileName);
        jFrame.add(jpButton);
        jFrame.setVisible(true);
    }
}
