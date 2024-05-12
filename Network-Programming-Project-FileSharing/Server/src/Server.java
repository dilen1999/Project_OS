import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    static ArrayList<MyFile> myFiles = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        JFrame jFrame = new JFrame("FILE SHARING SERVER");
        jFrame.setSize(600, 600);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jlTitle = new JLabel("FILE RECEIVER");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 30));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        jFrame.add(jlTitle, BorderLayout.NORTH);
        jFrame.add(jScrollPane, BorderLayout.CENTER);
        jFrame.setVisible(true);

        ServerSocket serverSocket = new ServerSocket(1234);

        int fileId = 0;

        while (true) {
            Socket socket = serverSocket.accept();
            Thread clientThread = new Thread(new ClientHandler(socket, jPanel, fileId));
            clientThread.start();
            fileId++;
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private JPanel jPanel;
        private int fileId;

        public ClientHandler(Socket clientSocket, JPanel jPanel, int fileId) {
            this.clientSocket = clientSocket;
            this.jPanel = jPanel;
            this.fileId = fileId;
        }

        @Override
        public void run() {
            try {
                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                String clientAddress = clientSocket.getInetAddress().getHostAddress();

                int fileNameLength = dataInputStream.readInt();
                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength];
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);
                    int fileContentLength = dataInputStream.readInt();
                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);
                        JPanel jpFileRow = new JPanel();
                        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.X_AXIS));
                        JLabel jlFileName = new JLabel(fileName + " from " + clientAddress);
                        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                        jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));
                        if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                            jpFileRow.setName((String.valueOf(fileId)));
                            jpFileRow.addMouseListener(getMyMouseListener());
                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);
                            jPanel.getParent().validate();
                        } else {
                            jpFileRow.setName((String.valueOf(fileId)));
                            jpFileRow.addMouseListener(getMyMouseListener());
                            jpFileRow.add(jlFileName);
                            JButton downloadButton = new JButton("Download");
                            downloadButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    MyFile myFile = myFiles.get(Integer.parseInt(jpFileRow.getName()));
                                    JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                                    jfPreview.setVisible(true);
                                }
                            });
                            jpFileRow.add(downloadButton);
                            jPanel.add(jpFileRow);
                            jPanel.getParent().validate();
                        }

                        myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getFileExtension(String fileName) {
            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                return fileName.substring(i + 1);
            } else {
                return "No extension found.";
            }
        }

        public MouseListener getMyMouseListener() {
            return new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JPanel jPanel = (JPanel) e.getSource();
                    int fileId = Integer.parseInt(jPanel.getName());
                    for (MyFile myFile : myFiles) {
                        if (myFile.getId() == fileId) {
                            JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                            jfPreview.setVisible(true);
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            };
        }

        public JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {
            JFrame jFrame = new JFrame("FILE DOWNLOADER");
            jFrame.setSize(600, 400);
            jFrame.setLocationRelativeTo(null);

            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());

            JLabel jlTitle = new JLabel("FILE DOWNLOADER");
            jlTitle.setHorizontalAlignment(SwingConstants.CENTER);
            jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
            jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));

            JLabel jlPrompt = new JLabel("Are you sure you want to download " + fileName + "?");
            jlPrompt.setFont(new Font("Arial", Font.BOLD, 20));
            jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
            jlPrompt.setHorizontalAlignment(SwingConstants.CENTER);

            JButton jbYes = new JButton("Yes");
            jbYes.setFont(new Font("Arial", Font.BOLD, 20));

            JButton jbNo = new JButton("No");
            jbNo.setFont(new Font("Arial", Font.BOLD, 20));

            JLabel jlFileContent = new JLabel();
            jlFileContent.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel jpButtons = new JPanel();
            jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));
            jpButtons.add(jbYes);
            jpButtons.add(jbNo);

            if (fileExtension.equalsIgnoreCase("txt")) {
                jlFileContent.setText("<html>" + new String(fileData) + "</html>");
            } else {
                ImageIcon icon = new ImageIcon(fileData);
                Image image = icon.getImage();
                Image scaledImage = image.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                jlFileContent.setIcon(scaledIcon);
            }

            jbYes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    File fileToDownload = new File(fileName);
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                        fileOutputStream.write(fileData);
                        fileOutputStream.close();
                        jFrame.dispose();

                        // Show notification window
                        showNotification("File Downloaded Successfully");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            });

            jbNo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jFrame.dispose();
                }
            });

            jPanel.add(jlTitle, BorderLayout.NORTH);
            jPanel.add(jlPrompt, BorderLayout.CENTER);
            jPanel.add(jlFileContent, BorderLayout.CENTER);
            jPanel.add(jpButtons, BorderLayout.SOUTH);

            jFrame.add(jPanel);

            return jFrame;
        }
    }

    // Method to show the notification window
    private static void showNotification(String message) {
        JFrame notificationFrame = new JFrame("Notification");
        JLabel notificationLabel = new JLabel(message);
        notificationLabel.setFont(new Font("Arial", Font.BOLD, 20));
        notificationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        notificationFrame.add(notificationLabel);
        notificationFrame.setSize(400, 100);
        notificationFrame.setLocationRelativeTo(null);
        notificationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        notificationFrame.setVisible(true);
    }

}