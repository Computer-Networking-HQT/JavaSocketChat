package server;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import javax.swing.*;

import java.awt.TextArea;
import java.awt.Font;

import java.awt.Color;

public class ServerGui {

    public static int port = 8080;
    private JFrame frmServerMangement;
    private JTextField txtIP, txtPort;
    private JLabel lblStatus;
    private static TextArea txtMessage;
    public static JLabel lblUserOnline;
    ServerCore server;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ServerGui window = new ServerGui();
                    window.frmServerMangement.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ServerGui() {
        initialize();
    }

    public static String getLabelUserOnline() {
        return lblUserOnline.getText();
    }
    public static void updateMessage(String msg) {
        txtMessage.append(msg + "\n");
    }

    public static void updateNumberClient() {
        int number = Integer.parseInt(lblUserOnline.getText());
        lblUserOnline.setText(Integer.toString(number + 1));
    }

    public static void decreaseNumberClient() {
        int number = Integer.parseInt(lblUserOnline.getText());
        lblUserOnline.setText(Integer.toString(number - 1));

    }

    private void initialize() {
        frmServerMangement = new JFrame();
        frmServerMangement.setForeground(UIManager.getColor("RadioButtonMenuItem.foreground"));
        frmServerMangement.getContentPane().setFont(new Font("Courier", Font.BOLD, 15));
        frmServerMangement.getContentPane().setForeground(UIManager.getColor("RadioButtonMenuItem.acceleratorSelectionForeground"));
        frmServerMangement.setTitle("<<<ACTIVITY OF SERVER>>>");
        frmServerMangement.setResizable(true);
        frmServerMangement.setBounds(0, 0, 800, 1000);
        frmServerMangement.setAlwaysOnTop(true);
        frmServerMangement.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmServerMangement.getContentPane().setLayout(null);
        frmServerMangement.setBackground(Color.BLUE);

        JLabel lblLogoserver = new JLabel("---ACTIVITY OF SERVER---");
        lblLogoserver.setForeground(new Color(0, 0, 205));
        lblLogoserver.setIcon(new javax.swing.ImageIcon(ServerGui.class.getResource("/image/server3.png")));
        lblLogoserver.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblLogoserver.setBounds(20, 20, 400, 60);
        frmServerMangement.getContentPane().add(lblLogoserver);

        JLabel lblLogoserver1 = new JLabel("");
        lblLogoserver1.setBounds(370, 20, 70, 60);
        lblLogoserver1.setIcon(new javax.swing.ImageIcon(ServerGui.class.getResource("/image/server3.png")));
        frmServerMangement.getContentPane().add(lblLogoserver1);
// ServerPanel
        JPanel serverPanel = new JPanel();
        serverPanel.setBounds(20, 90, 240, 190);
        serverPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "<<<SERVER>>>"));
        frmServerMangement.getContentPane().add(serverPanel);
        serverPanel.setLayout(null);
////// Vi tri lbl Port
        JLabel lblPortServer = new JLabel("PORT OF SERVER :");
        lblPortServer.setFont(new Font("Courier New", Font.BOLD, 15));
        lblPortServer.setBounds(30, 30, 150, 14);
        serverPanel.add(lblPortServer);

        txtPort = new JTextField();
        txtPort.setFont(new Font("Courier New", Font.BOLD, 15));
        txtPort.setBackground(Color.WHITE);
        txtPort.setForeground(Color.RED);
        txtPort.setText("8080");
        txtPort.setEditable(false);
        txtPort.setColumns(10);
        txtPort.setBounds(30, 60, 65, 28);
        serverPanel.add(txtPort);

        ////// Vi tri lbl IP
        JLabel ipServer = new JLabel("IP OF SERVER :");
        ipServer.setFont(new Font("Courier", Font.BOLD, 15));
        ipServer.setBounds(30, 100, 150, 30);
        serverPanel.add(ipServer);

        txtIP = new JTextField();
        txtIP.setBounds(30, 150, 185, 28);
        serverPanel.add(txtIP);
        txtIP.setColumns(10);
        try {
            txtIP.setText(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

//button Start
        JButton btnStart = new JButton("RUN");
        btnStart.setBackground(UIManager.getColor("RadioButtonMenuItem.selectionBackground"));
        btnStart.setFont(new Font("Courier", Font.BOLD, 15));

        btnStart.setBounds(200, 300, 150, 30);			/////// Vi tri button START
        frmServerMangement.getContentPane().add(btnStart);
//        btnStart.setIcon(new javax.swing.ImageIcon(ServerGui.class.getResource("/image/start1.png")));

//		BufferedImage img = null;
//		try {
//		    img = ImageIO.read(new File(ServerGui.class.getResource("/image/serverManager.png").getFile()));
//		} catch (IOException e) {
//		    e.printStackTrace();
//		}
//		Image dimg = img.getScaledInstance(64, 64,
//		        Image.SCALE_SMOOTH);
//		ImageIcon imageIcon = new ImageIcon(dimg);


        txtMessage = new TextArea();
        txtMessage.setBackground(Color.BLUE);
        txtMessage.setForeground(Color.YELLOW);
        txtMessage.setFont(new Font("Consolas", Font.ITALIC, 13));
        txtMessage.setEditable(false);
        txtMessage.setBounds(0, 360, 714, 358);		////// Vi tri textArea
        frmServerMangement.getContentPane().add(txtMessage);

        JButton btnStop = new JButton("DISABLE");
        btnStop.setFont(new Font("Courier New", Font.BOLD, 15));
        btnStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                lblUserOnline.setText("0");
                try {
                    server.stopserver();
                    ServerGui.updateMessage("DISABLE SERVER");
                    lblStatus.setText("<html><font color='green'>OFF</font></html>");
                } catch (Exception e) {
                    e.printStackTrace();
                    ServerGui.updateMessage("DISABLE SERVER");
                    lblStatus.setText("<html><font color='green'>OFF</font></html>");
                }
            }
        });
        btnStop.setBounds(20, 300, 150, 30);						//// Vi tri button Stop
        frmServerMangement.getContentPane().add(btnStop);
//        btnStop.setIcon(new javax.swing.ImageIcon(ServerGui.class.getResource("/image/stop1.png")));

        JLabel lblnew111 = new JLabel("STATUS OF SERVER: ");
        lblnew111.setFont(new Font("Courier New", Font.BOLD, 15));
        lblnew111.setBounds(300, 120, 200, 16);
        frmServerMangement.getContentPane().add(lblnew111);

        lblStatus = new JLabel("New label");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblStatus.setBounds(500, 120, 98, 16);


        frmServerMangement.getContentPane().add(lblStatus);
        lblStatus.setText("<html><font color='blue'>OFF</font></html>");

        JLabel lblRecord = new JLabel("LOG OF SERVER :");
        lblRecord.setFont(new Font("Courier New", Font.BOLD, 15));
        lblRecord.setBounds(20, 340, 200, 16);
        frmServerMangement.getContentPane().add(lblRecord);

        JLabel lbllabelUserOnline = new JLabel("USER ONLINE");
        lbllabelUserOnline.setFont(new Font("Courier New", Font.BOLD, 15));
        lbllabelUserOnline.setBounds(300, 190, 150, 16);
        frmServerMangement.getContentPane().add(lbllabelUserOnline);

        lblUserOnline = new JLabel("0");
        lblUserOnline.setForeground(Color.GREEN);
        lblUserOnline.setFont(new Font("Courier New", Font.BOLD, 15));
        lblUserOnline.setBounds(500, 190, 56, 16);
        frmServerMangement.getContentPane().add(lblUserOnline);

        JMenuBar menuBar = new JMenuBar();
        frmServerMangement.setJMenuBar(menuBar);

        JMenu mnNewMenu = new JMenu("Let's run or disable your server !!!");
        menuBar.add(mnNewMenu);

        JMenuItem mntmMe = new JMenuItem("Us");
        mnNewMenu.add(mntmMe);

        JMenuItem mntmSoftware = new JMenuItem("Software");
        mnNewMenu.add(mntmSoftware);
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    server = new ServerCore(8080);
                    ServerGui.updateMessage("START SERVER");
                    lblStatus.setText("<html><font color='RED'>RUNNING...</font></html>");
                } catch (Exception e) {
                    ServerGui.updateMessage("START ERROR");
                    e.printStackTrace();
                }
            }
        });
    }
}
