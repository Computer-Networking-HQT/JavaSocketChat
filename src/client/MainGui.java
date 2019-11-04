package client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;

import javax.swing.DefaultListModel;
import javax.swing.JButton;

import tags.Tags;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

import java.awt.Color;
import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainGui {

	private Client clientNode;
	private static String IPClient = "", nameUser = "", dataUser = "";
	private static int portClient = 0;
	private JFrame frameMainGui;
	private JTextField txtNameFriend;
	private JButton btnChat, btnExit;
	private JLabel lblLogo, lblLogo1, find;
	private JLabel lblActiveNow;
	private static JList<String> listActive;
	
	static DefaultListModel<String> model = new DefaultListModel<>();
	private JLabel lblUsername;


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGui window = new MainGui();
					window.frameMainGui.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainGui(String arg, int arg1, String name, String msg) throws Exception {
		IPClient = arg;
		portClient = arg1;
		nameUser = name;
		dataUser = msg;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGui window = new MainGui();
					window.frameMainGui.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainGui() throws Exception {
		initialize();
		clientNode = new Client(IPClient, portClient, nameUser, dataUser);
	}

	public static void updateFriendMainGui(String msg) {
		model.addElement(msg);
	}

	public static void resetList() {
		model.clear();
	} 
	
	private void initialize() {
		frameMainGui = new JFrame();
		frameMainGui.setTitle("<<<LIST OF ONLINE USER>>>");
		frameMainGui.setResizable(true);
		frameMainGui.setBounds(100, 100, 300, 700);
		frameMainGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameMainGui.getContentPane().setLayout(null);

		lblLogo = new JLabel("*MAIN CHAT*");
		lblLogo.setForeground(new Color(0, 0, 205));
		lblLogo.setIcon(new javax.swing.ImageIcon(MainGui.class.getResource("/image/logoMain.png")));
		lblLogo.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblLogo.setBounds(20, 13, 200, 38);
		frameMainGui.getContentPane().add(lblLogo);

		lblLogo1 = new JLabel("");
		lblLogo1.setBounds(200, 13, 40, 40);
		lblLogo1.setIcon(new javax.swing.ImageIcon(MainGui.class.getResource("/image/logoMain.png")));
		frameMainGui.getContentPane().add(lblLogo1);
// welcome

		JLabel lblHello = new JLabel("Hello you, this is your account: ");
		lblHello.setFont(new Font("Courier New", Font.ITALIC, 14));
		lblHello.setBounds(10, 82, 300, 16);
		frameMainGui.getContentPane().add(lblHello);
// TextLabel for my user
		lblUsername = new JLabel(nameUser);
		lblUsername.setForeground(Color.PINK);
		lblUsername.setFont(new Font("Courier New", Font.BOLD, 15));
		lblUsername.setBounds(10, 100, 156, 28);
		frameMainGui.getContentPane().add(lblUsername);

		// buttion exit
		btnExit = new JButton("OFFLINE");
		btnExit.setFont(new Font("Courier New", Font.BOLD, 15));
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int result = Tags.show(frameMainGui, "Do you want to offline ?", true);
				if (result == 0) {
					try {
						clientNode.exit();
						frameMainGui.dispose();
					} catch (Exception e) {
						frameMainGui.dispose();
					}
				}
			}
		});
		btnExit.setBounds(10, 135, 150, 30);
		btnExit.setIcon(new javax.swing.ImageIcon(MainGui.class.getResource("/image/stop1.png")));
		frameMainGui.getContentPane().add(btnExit);


//find friend
		JLabel lblFriendsName = new JLabel("Find your friend by guest user name: ");
		lblFriendsName.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblFriendsName.setBounds(10, 180, 330, 20);
		frameMainGui.getContentPane().add(lblFriendsName);

		find = new JLabel("");
		find.setBounds(10, 210, 40, 30);
		find.setIcon(new javax.swing.ImageIcon(MainGui.class.getResource("/image/find.png")));
		frameMainGui.getContentPane().add(find);
// Textfileid of namefiremd
		txtNameFriend = new JTextField("");
		txtNameFriend.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		txtNameFriend.setColumns(10);
		txtNameFriend.setBounds(50, 210, 200, 28);
		frameMainGui.getContentPane().add(txtNameFriend);
//button chat
		btnChat = new JButton("CONNECT");
		btnChat.setFont(new Font("Courier New", Font.BOLD, 15));

		btnChat.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String name = txtNameFriend.getText();
				if (name.equals("") || Client.clientarray == null) {
					Tags.show(frameMainGui, "Username isn't valid", false);
					return;
				}
				if (name.equals(nameUser)) {
					Tags.show(frameMainGui, "App doesn't support chat yourself/recusive function", false);
					return;
				}
				int size = Client.clientarray.size();
				for (int i = 0; i < size; i++) {
					if (name.equals(Client.clientarray.get(i).getName())) { // duyệt lần lượng các client trong mảng và kiểm tra tên
						try {
							clientNode.intialNewChat(Client.clientarray.get(i).getHost(),Client.clientarray.get(i).getPort(), name);// tạo ra chat riêng tư mới
							//với client thứ i (tạo ra 1 socket riêng để kết nối với client thứ i )
							return;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				Tags.show(frameMainGui, "Friend can maybe offline. Please wait to update your list online user", false);
			}
		});
		btnChat.setBounds(10, 310, 150, 30);//
		frameMainGui.getContentPane().add(btnChat);
		btnChat.setIcon(new javax.swing.ImageIcon(MainGui.class.getResource("/image/chat1.png")));

//list online
		lblActiveNow = new JLabel("List of Online Users :");
		lblActiveNow.setForeground(new Color(100, 149, 237));
		lblActiveNow.setFont(new Font("Courier New", Font.BOLD, 15));
		lblActiveNow.setBounds(10, 290, 300, 16);
		frameMainGui.getContentPane().add(lblActiveNow);
		
		listActive = new JList<>(model);
		listActive.setFont(new Font("Courier New", Font.BOLD, 15));
		listActive.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String value = (String)listActive.getModel().getElementAt(listActive.locationToIndex(arg0.getPoint()));
				txtNameFriend.setText(value);
			}
		});
		listActive.setBounds(10, 360, 240, 240);
		frameMainGui.getContentPane().add(listActive);
		

			
	}
		

	public static int request(String msg, boolean type) {
		JFrame frameMessage = new JFrame();
		return Tags.show(frameMessage, msg, type);
	}
}
