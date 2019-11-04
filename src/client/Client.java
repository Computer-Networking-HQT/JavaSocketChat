//package client;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.net.SocketAddress;
//import java.util.ArrayList;
//
//import data.Peer;
//import tags.Decode;
//import tags.Encode;
//import tags.Tags;
//
//public class Client {
//
//	public static ArrayList<Peer> clientarray = null;
//	private ClientServer server;
//	private InetAddress IPserver;
//	private int portServer = 8080;
//	private String nameUser = "";
//	private boolean isStop = false;
//	private static int portClient = 10000;
//	private int timeOut = 10000;  //time to each request is 10 seconds.
//	private Socket socketClient;
//	private ObjectInputStream serverInputStream;
//	private ObjectOutputStream serverOutputStream;
//
//
//	public Client(String arg, int arg1, String name, String dataUser) throws Exception {
//		IPserver = InetAddress.getByName(arg);
//		nameUser = name;
//		portClient = arg1;
//		clientarray = Decode.getAllUser(dataUser);
//		new Thread(new Runnable(){
//			@Override
//			public void run() {
//				updateFriend();
//			}
//		}).start();
//		server = new ClientServer(nameUser);
//		(new Request()).start();
//	}
//
//	public static int getPort() {
//		return portClient;
//	}
//
//	public void request() throws Exception {
//		socketClient = new Socket();
//		SocketAddress addressServer = new InetSocketAddress(IPserver, portServer);
//		socketClient.connect(addressServer);
//		String msg = Encode.sendRequest(nameUser);
//		serverOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
//		serverOutputStream.writeObject(msg);
//		serverOutputStream.flush();
//		serverInputStream = new ObjectInputStream(socketClient.getInputStream());
//		msg = (String) serverInputStream.readObject();
//		serverInputStream.close();
//		//		just for test
//		System.out.println("toantoan" + msg); //test server return to user
//		clientarray = Decode.getAllUser(msg);
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				updateFriend();
//			}
//		}).start();
//	}
//
//	public class Request extends Thread {
//		@Override
//		public void run() {
//			super.run();
//			while (!isStop) {
//				try {
//					Thread.sleep(timeOut);
//					request();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	public void intialNewChat(String IP, int host, String guest) throws Exception {
//		final Socket connclient = new Socket(InetAddress.getByName(IP), host);
//		ObjectOutputStream sendrequestChat = new ObjectOutputStream(connclient.getOutputStream());
//		sendrequestChat.writeObject(Encode.sendRequestChat(nameUser));
//		sendrequestChat.flush();
//		ObjectInputStream receivedChat = new ObjectInputStream(connclient.getInputStream());
//		String msg = (String) receivedChat.readObject();
//		if (msg.equals(Tags.CHAT_DENY_TAG)) {
//			MainGui.request("Your friend denied connect with you!", false);
//			connclient.close();
//			return;
//		}
//		//not if
//		new ChatGui(nameUser, guest, connclient, portClient);
//
//	}
//
//	public void exit() throws IOException, ClassNotFoundException {
//		isStop = true;
//		socketClient = new Socket();
//		SocketAddress addressServer = new InetSocketAddress(IPserver, portServer);
//		socketClient.connect(addressServer);
//		String msg = Encode.exit(nameUser);
//		serverOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
//		serverOutputStream.writeObject(msg);
//		serverOutputStream.flush();
//		serverOutputStream.close();
//		server.exit();
//	}
//
//	public void updateFriend(){
//		int size = clientarray.size();
//		MainGui.resetList();
//		//while loop
//		int i = 0;
//		while (i < size) {
//			if (!clientarray.get(i).getName().equals(nameUser))
//				MainGui.updateFriendMainGui(clientarray.get(i).getName());
//			i++;
//		}
//	}
//}
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import data.Peer;
import tags.Decode;
import tags.Encode;
import tags.Tags;

public class Client {

	public static ArrayList<Peer> clientarray = null;
	private ClientServer server;
	private InetAddress IPserver;
	private int portServer = 8080;
	private String nameUser = "";
	private boolean isStop = false;
	private static int portClient = 10000;
	private int timeOut = 10000;  //time to each request is 10 seconds.
	private Socket socketClient;
	private ObjectInputStream serverInputStream;
	private ObjectOutputStream serverOutputStream;


	public Client(String arg, int arg1, String name, String dataUser) throws Exception {
		IPserver = InetAddress.getByName(arg);
		nameUser = name; // tên của client hiện tại
		portClient = arg1;
		clientarray = Decode.getAllUser(dataUser); // dataUser là các msg server trả về khi client đăng nhập thành công
		// dataUser : <SESSION_ACCEPT><PEER><PEER_NAME>H</PEER_NAME><IP>/169.254.93.72</IP><PORT>10566</PORT></PEER></SESSION_ACCEPT>
		//clientarray là 1 dãy các client
		new Thread(new Runnable(){
			@Override
			public void run() {
				updateFriend();
			}
		}).start();
		server = new ClientServer(nameUser);
		(new Request()).start();
	}

	public static int getPort() {
		return portClient;
	}

	public void request() throws Exception {
		socketClient = new Socket();
		SocketAddress addressServer = new InetSocketAddress(IPserver, portServer);
		socketClient.connect(addressServer);
		String msg = Encode.sendRequest(nameUser);
		serverOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
		serverOutputStream.writeObject(msg);
		serverOutputStream.flush();
		serverInputStream = new ObjectInputStream(socketClient.getInputStream());
		msg = (String) serverInputStream.readObject();
		serverInputStream.close();
		//		just for test
		System.out.println("msg request from server to client: " + msg); //test server return to user
		clientarray = Decode.getAllUser(msg);
		new Thread(new Runnable() {

			@Override
			public void run() {
				updateFriend();
			}
		}).start();
	}

	public class Request extends Thread {
		@Override
		public void run() {
			super.run();
			while (!isStop) {
				try {
					Thread.sleep(timeOut);
					request();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void intialNewChat(String IP, int host, String guest) throws Exception {
		final Socket connclient = new Socket(InetAddress.getByName(IP), host); // tạo 1 socket kết nối đến 1 client có IP và port là host
		ObjectOutputStream sendrequestChat = new ObjectOutputStream(connclient.getOutputStream()); // tạo 1 luồng dữ liệu ra từ client hiện tại đến
		// client kết nối thông qua socket
		sendrequestChat.writeObject(Encode.sendRequestChat(nameUser)); // Gửi đi tin msg đến client khác có dạng
		//<CHAT_REQ><PEER_NAME>nameOfHostClient</PEER_NAME></CHAT_REQ>
		System.out.print("msg new chat from host to guest: " + Encode.sendRequestChat(nameUser)); // test tin nhắn gửi đi từ phía host
		sendrequestChat.flush(); // Đẩy tin nhắn đi

		ObjectInputStream receivedChat = new ObjectInputStream(connclient.getInputStream()); //tạo 1 luồng dữ liệu vào từ client khách đến client hiện tại
		String msg = (String) receivedChat.readObject(); //Đọc tin nhắn đến từ phía guest
		System.out.print("msg accept chat from guest to host :" + msg);
		if (msg.equals(Tags.CHAT_DENY_TAG)) { // <CHAT_DENY />
			MainGui.request("Your friend denied connect with you!", false);
			connclient.close(); //Đóng kết nối khi mà bị từ chối
			return;
		}
		//not if
		new ChatGui(nameUser, guest, connclient, portClient); // Tạo phòng chat riêng tư mới nhé. (conclient: socket kết nối đến client muốn chat)
		//portClient là port của client hiện tại
		//guest tên của client muốn chat
	}

	public void exit() throws IOException, ClassNotFoundException {
		isStop = true;
		socketClient = new Socket();  // tạo socket mới
		SocketAddress addressServer = new InetSocketAddress(IPserver, portServer);
		socketClient.connect(addressServer); // Kết nối socket đến server socket
		String msg = Encode.exit(nameUser); // msg = <SESSION_ACCEPT><PEER_NAME>name</PEER_NAME><STATUS>"STOP"</STATUS></SESSION_ACCEPT>
		serverOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
		serverOutputStream.writeObject(msg);
		serverOutputStream.flush();
		System.out.print("msg from client hiện tại đến client kết nối" + msg);
		serverOutputStream.close();
		server.exit(); // đóng socket server peer hiện tại
	}

	public void updateFriend(){
		int size = clientarray.size();
		MainGui.resetList();
		//while loop
		int i = 0;
		while (i < size) {
			if (!clientarray.get(i).getName().equals(nameUser))
				MainGui.updateFriendMainGui(clientarray.get(i).getName());
			i++;
		}
	}
}