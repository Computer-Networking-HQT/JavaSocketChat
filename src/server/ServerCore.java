package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import data.Peer; // thông tin của client
import tags.Decode; //Giải mã các phương thức để lấy thông tin user, port hay ip...
import tags.Tags; // định nghĩa các protocol trong ứng dụng

public class ServerCore {

	private ArrayList<Peer> dataPeer = null; // danh sách user đăng kí hệ thống
	private ServerSocket server;						
	private Socket connection;			
	private ObjectOutputStream obOutputClient; // luồng dữ liệu ra server -> client
	private ObjectInputStream obInputStream; // luồng dữ liệu vào client -> server
	public boolean isStop = false, isExit = false;		
	
	//Intial server socket
	public ServerCore(int port) throws Exception { //contructor của ServerCore
		server = new ServerSocket(port);   // Mở một Server Socket tại port
		dataPeer = new ArrayList<Peer>(); // Tạo một 1 list user rỗng
		(new WaitForConnect()).start();	 //
	}
	
	//	show status of state
	private String sendSessionAccept() throws Exception {
		String msg = Tags.SESSION_ACCEPT_OPEN_TAG; // "<SESSION_ACCEPT>": Chấp nhận người dùng kết nối tới server, sau khi ng dùng login
		int size = dataPeer.size();		// số client
		for (int i = 0; i < size; i++) {		
			Peer peer = dataPeer.get(i); //Lấy user thứ i
			msg += Tags.PEER_OPEN_TAG;	// "<PEER>"
			msg += Tags.PEER_NAME_OPEN_TAG; //"<PEER_NAME>"
			msg += peer.getName();
			msg += Tags.PEER_NAME_CLOSE_TAG; //"</PEER_NAME>"
			msg += Tags.IP_OPEN_TAG; //"<IP>"
			msg += peer.getHost(); // Địa chỉ ip của client
			msg += Tags.IP_CLOSE_TAG; //  "</IP>"
			msg += Tags.PORT_OPEN_TAG;  // "<PORT>"
			msg += peer.getPort(); //Port của client
			msg += Tags.PORT_CLOSE_TAG; //"<PORT>"
			msg += Tags.PEER_CLOSE_TAG;	 // "<PEER>"
		}
		msg += Tags.SESSION_ACCEPT_CLOSE_TAG;	// "<SESSION_ACCEPT>"
		return msg;
	}
	
	//	close server
	public void stopserver() throws Exception {
		isStop = true;
		server.close();	// Đóng ServerSocket
		connection.close();	// Đóng socket kêt nối tới client
	}
	
	//client connect to server
	private boolean waitForConnection() throws Exception {
		connection = server.accept();		// Chấp nhận yêu cầu kết nối từ client
		obInputStream = new ObjectInputStream(connection.getInputStream()); // Lấy luồng dữ liệu từ client
		String msg = (String) obInputStream.readObject(); //Đọc một đối tượng từ input stream.
		ArrayList<String> getData = Decode.getUser(msg); // Lấy thông tin client kết nối đến server, hàm trả về 1 array list với 2 thành phần đó là Tên và Portnumber
		ServerGui.updateMessage(msg); // Hiển thị thông báo msg đến TextArea
		if (getData != null) {
			if (!isExsistName(getData.get(0))) { // kiểm tra tên user có trùng không
				saveNewPeer(getData.get(0), connection.getInetAddress().toString(), Integer.parseInt(getData.get(1)));
				ServerGui.updateMessage(getData.get(0)); // hiển thị tên client đăng kí trên Text Area
				ServerGui.updateNumberClient(); // Cập nhật số lượng client
			} else // từ chối kết nối
				return false;
		} else {  // nếu thông tin client kết nối đến server getData == null,
			int size = dataPeer.size(); // Kiểm tra số lượng client đang kết nối đến server
			Decode.updatePeerOnline(dataPeer, msg);	// trả về dataPeer đang kết nối với server
			if (size != dataPeer.size()) {					
				isExit = true;	//
				ServerGui.decreaseNumberClient(); // giảm số lượng client đi 1
			}
		}
		return true;
	}
	
	
	private void saveNewPeer(String user, String ip, int port) throws Exception {
		Peer newPeer = new Peer();		
		if (dataPeer.size() == 0)	// nếu dataPeer có size  == 0
			dataPeer = new ArrayList<Peer>(); // tạo một array list client
		newPeer.setPeer(user, ip, port);
		dataPeer.add(newPeer); //add vào array list client
	}
	
	
	private boolean isExsistName(String name) throws Exception { // check xem tên client đã tồn tại chưa
		if (dataPeer == null)
			return false;
		int size = dataPeer.size();
		for (int i = 0; i < size; i++) {
			Peer peer = dataPeer.get(i);
			if (peer.getName().equals(name))
				return true;
		}
		return false;
	}
	
	
	
	
	public class WaitForConnect extends Thread {

		@Override
		public void run() {
			super.run();
			try {
				while (!isStop) { //khi ko dừng server
					if (waitForConnection()) {
						if (isExit) {
							isExit = false; // set isExit đang true set về false
						} else {
							obOutputClient = new ObjectOutputStream(connection.getOutputStream()); // Tạo luồng dữ liệu đầu ra tại server gửi đến client
							obOutputClient.writeObject(sendSessionAccept()); // dữ liệu đầu ra bao gồm name,ip,port của client
							obOutputClient.flush(); //Đẩy dữ liệu đi
							obOutputClient.close(); //Đóng luồng dữ liệu
						}
					} else { // nếu kết nối ko chấp chận
						obOutputClient = new ObjectOutputStream(connection.getOutputStream());
						obOutputClient.writeObject(Tags.SESSION_DENY_TAG); //dữ liệu đầu ra từ chối kết nối <SESSION_DENY/>
						obOutputClient.flush();
						obOutputClient.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

