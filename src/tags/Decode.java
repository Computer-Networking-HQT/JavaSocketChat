package tags;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.Peer;

public class Decode {
	// Định nghĩa biểu thức chính quy cho tài khoản được tạo có dạng :"<SESSION_REQ><PEER_NAME>NameUser<PEER_NAME><PORT>PortNumber<PORT><SESSION_REQ>"
	private static Pattern createAccount = Pattern
			.compile(Tags.SESSION_OPEN_TAG + Tags.PEER_NAME_OPEN_TAG + ".*"
					+ Tags.PEER_NAME_CLOSE_TAG + Tags.PORT_OPEN_TAG + ".*"
					+ Tags.PORT_CLOSE_TAG + Tags.SESSION_CLOSE_TAG);

	private static Pattern users = Pattern.compile(Tags.SESSION_ACCEPT_OPEN_TAG
			+ "(" + Tags.PEER_OPEN_TAG + Tags.PEER_NAME_OPEN_TAG + ".+"
			+ Tags.PEER_NAME_CLOSE_TAG + Tags.IP_OPEN_TAG + ".+"
			+ Tags.IP_CLOSE_TAG + Tags.PORT_OPEN_TAG + "[0-9]+"
			+ Tags.PORT_CLOSE_TAG + Tags.PEER_CLOSE_TAG + ")*"
			+ Tags.SESSION_ACCEPT_CLOSE_TAG);
	//request do client gửi lên 10s 1 lần gồm tên và trạng thái
	private static Pattern request = Pattern      //Khuôn mẫu cho request = <SESSION_KEEP_ALIVE><PEER_NAME>...</PEER_NAME><STATUS>(RUNNING|STOP)</STATUS></SESSION_KEEP_ALIVE>
			.compile(Tags.SESSION_KEEP_ALIVE_OPEN_TAG + Tags.PEER_NAME_OPEN_TAG
					+ "[^<>]+" + Tags.PEER_NAME_CLOSE_TAG
					+ Tags.STATUS_OPEN_TAG + "(" + Tags.SERVER_ONLINE + "|"
					+ Tags.SERVER_OFFLINE + ")" + Tags.STATUS_CLOSE_TAG
					+ Tags.SESSION_KEEP_ALIVE_CLOSE_TAG);

	private static Pattern message = Pattern.compile(Tags.CHAT_MSG_OPEN_TAG
			+ ".*" + Tags.CHAT_MSG_CLOSE_TAG);

	private static Pattern checkNameFile = Pattern
			.compile(Tags.FILE_REQ_OPEN_TAG + ".*" + Tags.FILE_REQ_CLOSE_TAG);

	private static Pattern feedBack = Pattern
			.compile(Tags.FILE_REQ_ACK_OPEN_TAG + ".*"
					+ Tags.FILE_REQ_ACK_CLOSE_TAG);

	public static ArrayList<String> getUser(String msg) {
		ArrayList<String> user = new ArrayList<String>();
		if (createAccount.matcher(msg).matches()) {
			// Định nghĩa biểu thức chính quy cho user name
			Pattern findName = Pattern.compile(Tags.PEER_NAME_OPEN_TAG + ".*"
					+ Tags.PEER_NAME_CLOSE_TAG);
			//Định nghĩa biểu thức chính quy để lấy port user
			Pattern findPort = Pattern.compile(Tags.PORT_OPEN_TAG + "[0-9]*"
					+ Tags.PORT_CLOSE_TAG);
			Matcher find = findName.matcher(msg); // lấy ra chuỗi biểu thức có chức username: <PEER_NAME>NameUser<PEER_NAME>
			if (find.find()) {
				String name = find.group(0); // trả về chuỗi con phù hợp "<PEER_NAME>NameUser<PEER_NAME>"
				user.add(name.substring(11, name.length() - 12)); // add vào array list user chuỗi con có index đầu và cuối là 11 và ... :"NameUser"
				find = findPort.matcher(msg);
				if (find.find()) { // tìm biểu thức khớp theo mẫu -> trả về true
					String port = find.group(0);
					user.add(port.substring(6, port.length() - 7)); // add chuỗi con portnumber vào list user
				} else
					return null;
			} else
				return null;
		} else
			return null;
		return user;
	}

	public static ArrayList<Peer> getAllUser(String msg) {
		ArrayList<Peer> user = new ArrayList<Peer>();
		Pattern findPeer = Pattern.compile(Tags.PEER_OPEN_TAG
				+ Tags.PEER_NAME_OPEN_TAG + "[^<>]*" + Tags.PEER_NAME_CLOSE_TAG
				+ Tags.IP_OPEN_TAG + "[^<>]*" + Tags.IP_CLOSE_TAG
				+ Tags.PORT_OPEN_TAG + "[0-9]*" + Tags.PORT_CLOSE_TAG
				+ Tags.PEER_CLOSE_TAG);
		Pattern findName = Pattern.compile(Tags.PEER_NAME_OPEN_TAG + ".*"
				+ Tags.PEER_NAME_CLOSE_TAG);
		Pattern findPort = Pattern.compile(Tags.PORT_OPEN_TAG + "[0-9]*"
				+ Tags.PORT_CLOSE_TAG);
		Pattern findIP = Pattern.compile(Tags.IP_OPEN_TAG + ".+"
				+ Tags.IP_CLOSE_TAG);
		if (users.matcher(msg).matches()) {
			Matcher find = findPeer.matcher(msg);
			while (find.find()) {
				String peer = find.group(0);
				String data = "";
				Peer dataPeer = new Peer();
				Matcher findInfo = findName.matcher(peer);
				if (findInfo.find()) {
					data = findInfo.group(0);
					dataPeer.setName(data.substring(11, data.length() - 12));
				}
				findInfo = findIP.matcher(peer);
				if (findInfo.find()) {
					data = findInfo.group(0);
					dataPeer.setHost(findInfo.group(0).substring(5,
							data.length() - 5));
				}
				findInfo = findPort.matcher(peer);
				if (findInfo.find()) {
					data = findInfo.group(0);
					dataPeer.setPort(Integer.parseInt(data.substring(6,
							data.length() - 7)));
				}
				user.add(dataPeer);
			}
		} else
			return null;
		return user;
	}

	public static ArrayList<Peer> updatePeerOnline(
			ArrayList<Peer> peerList, String msg) {
		Pattern alive = Pattern.compile(Tags.STATUS_OPEN_TAG
				+ Tags.SERVER_ONLINE + Tags.STATUS_CLOSE_TAG); //Mẫu alive = "<STATUS>RUNNING</STATUS>"
		Pattern killUser = Pattern.compile(Tags.PEER_NAME_OPEN_TAG + "[^<>]*"
				+ Tags.PEER_NAME_CLOSE_TAG); // Biểu thức chính quy killUser = "<PEER_NAME>Bất kì kí tự nào trừ "<>"</PEER_NAME>
		if (request.matcher(msg).matches()) { // Kiểm tra xem msg có khớp với mẫu request không
			Matcher findState = alive.matcher(msg); // chuỗi findState trả về trạng thái của server: "<STATUS>RUNNING</STATUS>"
			if (findState.find())  // nếu tìm được chuỗi (tiếp theo) khớp mẫu, tức là chuỗi <STATUS>RUNNING</STATUS>
				return peerList;   // trả về list client hiện tại
			findState = killUser.matcher(msg); // chuỗi findState trả về msg khớp với mẫu : <PEER_NAME>...</PEER_NAME>
			if (findState.find()) { // nếu tìm được chuỗi khớp mẫu
				String findPeer = findState.group(0);  // Lấy ra chuỗi <PEER_NAME>...</PEER_NAME> hợp lệ đầu tiên
				int size = peerList.size();
				String name = findPeer.substring(11, findPeer.length() - 12); // lấy ra tên client từ <PEER_NAME>...</PEER_NAME>
				for (int i = 0; i < size; i++)
					if (name.equals(peerList.get(i).getName())) { // lần kiểm tra tên có trùng không
						peerList.remove(i); // Nếu trùng tên sẽ remove client đó
						break;
					}
			}
		}
		return peerList;
	}

	public static String getMessage(String msg) {
//		System.out.print("Ham getMessage o decode.java duoc goi");
//		System.out.print(msg);
		if (message.matcher(msg).matches()) {
			int begin = Tags.CHAT_MSG_OPEN_TAG.length();
			int end = msg.length() - Tags.CHAT_MSG_CLOSE_TAG.length();
			System.out.println(begin + " "+ end);
			String message = msg.substring(begin, end);
			return message;
		}
		return null;
	}

	public static String getNameRequestChat(String msg) {
		Pattern checkRequest = Pattern.compile(Tags.CHAT_REQ_OPEN_TAG
				+ Tags.PEER_NAME_OPEN_TAG + "[^<>]*" + Tags.PEER_NAME_CLOSE_TAG
				+ Tags.CHAT_REQ_CLOSE_TAG);
		if (checkRequest.matcher(msg).matches()) {
			int lenght = msg.length();
			String name = msg
					.substring(
							(Tags.CHAT_REQ_OPEN_TAG + Tags.PEER_NAME_OPEN_TAG)
									.length(),
							lenght
									- (Tags.PEER_NAME_CLOSE_TAG + Tags.CHAT_REQ_CLOSE_TAG)
											.length());
			return name;
		}
		return null;
	}

	public static boolean checkFile(String name) {
		if (checkNameFile.matcher(name).matches())
			return true;
		return false;
	}

	public static boolean checkFeedBack(String msg) {
		if (feedBack.matcher(msg).matches())
			return true;
		return false;
	}
}

