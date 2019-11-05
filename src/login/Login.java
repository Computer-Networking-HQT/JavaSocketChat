package login;

import client.MainGui;
import tags.Encode;
import tags.Tags;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.regex.Pattern;

public class Login {
 private static String NAME_FAILED = "THIS NAME CONTAINS INVALID CHARACTER ";
 private static String NAME_EXSIST = "THIS NAME IS ALREADY USED ";
 private static String RETRY ="PLEASE TRY AGAIN";
 private static String SERVER_NOT_START = "MAKE SURE YOUR SERVER ARE ALWAYS ONLINE";
 private static String TURN_SERVER = "PLEASE RUNNING YOUR SERVER";

 private Pattern checkName = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");

 private JFrame frameLoginForm;
 private JTextField txtPort;
 private JLabel lblError , lblError1 ;
 private String name = "", IP = "";
 private JTextField txtIP;	
 private JTextField txtUsername;
 private JButton btnLogin;
 private JPanel server;

 public static void main(String[] args) {
  EventQueue.invokeLater(new Runnable() {
   public void run() {
    try {
     Login window = new Login();
     window.frameLoginForm.setVisible(true);
    } catch (Exception e) {
     e.printStackTrace();
    }
   }
  });
 }

 public Login() {
  initialize();
 }

 private void initialize() {
  frameLoginForm = new JFrame();
  frameLoginForm.setTitle("<<<LOGIN>>>");
  frameLoginForm.setResizable(true);
  //frameLoginForm.setLocationRelativeTo(null);
  frameLoginForm.setSize(320,600);
  frameLoginForm.setBounds(800, 100, 320, 600);
  frameLoginForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  frameLoginForm.getContentPane().setLayout(null);

  // Label gom text unedit
  JLabel lblWelcome = new JLabel("---CHAT LOGIN---\r\n"); // Dau enter
  lblWelcome.setBackground(Color.BLUE);
  lblWelcome.setForeground(Color.BLUE);
  //lblWelcome.setForeground(UIManager.getColor("RadioButtonMenuItem.selectionBackground"));
  lblWelcome.setFont(new Font("Courier New", Font.BOLD, 30));
  lblWelcome.setBounds(10, 10, 360, 50);
  frameLoginForm.getContentPane().add(lblWelcome);

     JLabel lbavtserver = new JLabel("");
     lbavtserver.setBounds(130, 60, 40, 40);
     lbavtserver.setIcon(new javax.swing.ImageIcon(Login.class.getResource("/image/serverLogin.png")));
     frameLoginForm.getContentPane().add(lbavtserver);


  server = new JPanel();
  server.setBounds(20, 100, 250, 200);
  //JLabel lblHostServer = new JLabel("IP Server");
  server.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "<<<SERVER>>>"));
  frameLoginForm.getContentPane().add(server);
  server.setLayout(null);
  //lblHostServer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  //lblHostServer.setBounds(47, 74, 86, 20);
 // frameLoginForm.getContentPane().add(lblHostServer);

  JLabel lblPortServer = new JLabel("Port Of Server :");
  lblPortServer.setFont(new Font("Segoe UI", Font.BOLD, 15));
  lblPortServer.setBounds(30, 30, 150, 14);
  server.add(lblPortServer);

  txtPort = new JTextField();
  txtPort.setFont(new Font("Courier New", Font.BOLD, 15));
  txtPort.setBackground(Color.WHITE);
  txtPort.setForeground(Color.RED);
  txtPort.setText("8080");
  txtPort.setEditable(false);
  txtPort.setColumns(10);
  txtPort.setBounds(30, 60, 65, 28);
  server.add(txtPort);

  JLabel ipServer = new JLabel("IP Of Server :");
  ipServer.setFont(new Font("Segoe UI", Font.BOLD, 15));
  ipServer.setBounds(30, 100, 150, 30);
  server.add(ipServer);

  txtIP = new JTextField();
  txtIP.setBounds(30, 150, 185, 28);
  server.add(txtIP);
  txtIP.setColumns(10);

     JLabel lbavtclient = new JLabel(""); //empty
  lbavtclient.setBounds(130, 310, 40, 40);
  lbavtclient.setIcon(new javax.swing.ImageIcon(Login.class.getResource("/image/clientLogin.png")));
     frameLoginForm.getContentPane().add(lbavtclient);


     JLabel lblUserName = new JLabel("UserName Of Client:");
  lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 15));
  lblUserName.setBounds(50, 350, 200, 20);
  frameLoginForm.getContentPane().add(lblUserName);
  //lblUserName.setIcon(new javax.swing.ImageIcon(Login.class.getResource("/image/eye.png")));

  txtUsername = new JTextField();
  txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  txtUsername.setColumns(10);
  txtUsername.setBounds(50, 380, 185, 30);
  frameLoginForm.getContentPane().add(txtUsername);


  lblError = new JLabel("");
  lblError.setBounds(10, 420, 399, 20);
  frameLoginForm.getContentPane().add(lblError);

  lblError1 = new JLabel("");
  lblError1.setBounds(10, 450, 399, 20);
  frameLoginForm.getContentPane().add(lblError1);

  btnLogin = new JButton("Connect ");
  btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
  btnLogin.setIcon(new javax.swing.ImageIcon(Login.class.getResource("/image/login1.png")));
  btnLogin.addActionListener(new ActionListener() {

   public void actionPerformed(ActionEvent arg0) {
    name = txtUsername.getText();
    lblError.setVisible(false);
    IP = txtIP.getText();


    //must edit here
    if (checkName.matcher(name).matches() && !IP.equals("")) {
     try {
      Random rd = new Random();
      int portPeer = 10000 + rd.nextInt() % 1000;
      InetAddress ipServer = InetAddress.getByName(IP);
      int portServer = Integer.parseInt("8080");
      Socket socketClient = new Socket(ipServer, portServer);

      String msg = Encode.getCreateAccount(name, Integer.toString(portPeer));
      ObjectOutputStream serverOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
      serverOutputStream.writeObject(msg);
      serverOutputStream.flush();
      ObjectInputStream serverInputStream = new ObjectInputStream(socketClient.getInputStream());
      msg = (String) serverInputStream.readObject();

      socketClient.close(); //
      if (msg.equals(Tags.SESSION_DENY_TAG)) {
       lblError.setText(NAME_EXSIST);
       lblError1.setText(RETRY);
       lblError.setVisible(true);
       lblError1.setVisible(true);
       return;
      }
      new MainGui(IP, portPeer, name, msg);
      frameLoginForm.dispose();
     }
     catch (Exception e) {
      lblError.setText(SERVER_NOT_START);
      lblError1.setText(TURN_SERVER);
      lblError.setVisible(true);
      lblError1.setVisible(true);
      e.printStackTrace();
     }
    }
    else {
     lblError.setText(NAME_FAILED);
     lblError1.setText(RETRY);
     lblError.setVisible(true);
     lblError1.setVisible(true);
    }
   }
  });
  
  btnLogin.setBounds(50, 500, 180, 50);
  frameLoginForm.getContentPane().add(btnLogin);
  lblError.setVisible(false);


 }
}
