//import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import java.util.*;

//import javax.swing.ImageIcon;
//import javax.swing.JButton;

public class GameServer {
	
	int []nums;						// �� �÷��̾���� �� ��ȣ ����
	private int x; 					//�� ��ȣ �����Ҷ� 1 ���� ���� �����ϴ� ���� 
	//private String NextUser;
	
	ArrayList<String> userset;
	ArrayList<Integer> Aset;		//ù ��° �÷��̾�� �־��� ������
	ArrayList<Integer> Bset;		//�� ��° �÷��̾�� �־��� ������
	ArrayList<Integer> Cset;		//�� ��° �÷��̾�� �־��� ������
	ArrayList<ArrayList<Integer>> playeranimals; // �� �÷��̾�鿡�� �й�� �������� set
	
	HashMap<Integer, String> TURN = new HashMap<Integer, String>();

	// ������ Ŭ���̾�Ʈ�� ����� �̸��� ��� ��Ʈ���� �ؽ� ���̺� ����
	// ���߿� Ư�� ����ڿ��� �޽����� ������ ���. ���� ������ �ִ� ������� ��ü ����Ʈ�� ���Ҷ��� ���
	HashMap<String, ObjectOutputStream> clientOutputStreams = new HashMap<String, ObjectOutputStream>();

	public static void main(String[] args) {
		new GameServer().go();
	}

	private void go() {
		try {
			ServerSocket serverSock = new ServerSocket(8080); // ä���� ���� ���� ��Ʈ 8080 ���
			while (true) {
				Socket clientSocket = serverSock.accept(); // ���ο� Ŭ���̾�Ʈ ���� ���

				// Ŭ���̾�Ʈ�� ���� ����� ��Ʈ�� �� ������ ����
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				System.out.println("S : Ŭ���̾�Ʈ ���� ��"); // ���¸� �������� ��� �޽���
			}
		} catch (Exception ex) {
			System.out.println("S : Ŭ���̾�Ʈ  ���� �� �̻�߻�"); // ���¸� �������� ��� �޽���
			ex.printStackTrace();
		}
	}

	// Client �� 1:1 �����ϴ� �޽��� ���� ������
	private class ClientHandler implements Runnable {
		Socket sock; // Ŭ���̾�Ʈ ����� ����
		ObjectInputStream reader; // Ŭ���̾�Ʈ�� ���� �����ϱ� ���� ��Ʈ��
		ObjectOutputStream writer; // Ŭ���̾�Ʈ�� �۽��ϱ� ���� ��Ʈ��

		// ������. Ŭ���̾�Ʈ���� ���Ͽ��� �б�� ���� ��Ʈ�� ����� ��
		// ��Ʈ���� ���鶧 InputStream�� ���� ����� Hang��. �׷��� OutputStream���� �������.
		// Bug ����... � ������ �ִ� ������ ���߿� ã�ƺ���� ��
		public ClientHandler(Socket clientSocket) {
			try {
				sock = clientSocket;
				writer = new ObjectOutputStream(clientSocket.getOutputStream());
				reader = new ObjectInputStream(clientSocket.getInputStream());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// Ŭ���̾�Ʈ���� ���� �޽����� ���� �����ϴ� �۾��� ����
		public void run() {
			GameChat message;
			GameChat.MsgType type;
			try {
				while (true) {
					// ���� �޽����� ������ ���� ���� ������ ������ ����
					message = (GameChat) reader.readObject(); // Ŭ���̾�Ʈ�� ���� �޽��� ����
					type = message.getType();
					if (type == GameChat.MsgType.LOGIN) { // Ŭ���̾�Ʈ �α��� ��û
						handleLogin(message.getSender(), writer); // Ŭ���̾�Ʈ �̸��� �׿��� �޽�����
																	// ���� ��Ʈ���� ���
					} else if (type == GameChat.MsgType.LOGOUT) { // Ŭ���̾�Ʈ �α׾ƿ� ��û
						handleLogout(message.getSender()); // ��ϵ� �̸� �� �̿� ����� ��Ʈ�� ����
						writer.close();
						reader.close();
						sock.close(); // �� Ŭ���̾�Ʈ�� ���õ� ��Ʈ���� �ݱ�
						return; // ������ ����
					} else if (type == GameChat.MsgType.CLIENT_MSG) {
						handleMessage(message.getSender(), message.getContents());
					} else if (type == GameChat.MsgType.NO_ACT) {
						// �����ص� �Ǵ� �޽���
						continue;
					} else if (type == GameChat.MsgType.GAME_INFO) {
						broadcastMessage(new GameChat(GameChat.MsgType.GAME_INFO, message.i, message.animalNum, message.score, message.getSender(), 
								(message.turnNum+1)%3, TURN.get((message.turnNum+1)%3), message.turnAble));

					} else if (type == GameChat.MsgType.GAME_START) {
						distributeMessage(writer);
					} else {
						// ��ü�� Ȯ�ε��� �ʴ� �̻��� �޽���?
						throw new Exception("S : Ŭ���̾�Ʈ���� �˼� ���� �޽��� ��������");
					}
				}
			} catch (Exception ex) {
				System.out.println("S : Ŭ���̾�Ʈ ���� ����"); // ����� Ŭ���̾�Ʈ ����Ǹ� ���ܹ߻�
														// �̸� �̿��� ������ �����Ŵ
			}
		} // close run
	} // close inner class

	// ����� �̸��� Ŭ���̾�Ʈ���� ��� ��Ʈ���� �������� �ؽ� ���̺� �־���.
	// �̹� ������ �̸��� ����ڰ� �ִٸ�, ������ �α����� ���� �Ѱ����� Ŭ���̾�Ʈ���� �˸�
	// �ؽ� ���̺��� ���ٿ����� �������� ����� ��� (not Thread-Safe. Synchronized�� ��ȣ���� ��.
	private synchronized void handleLogin(String user, ObjectOutputStream writer) {
		try {
			// �̹� ������ �̸��� ����ڰ� �ִٸ�, ������ �α����� ���� �Ѱ����� Ŭ���̾�Ʈ���� �˸�
			if (clientOutputStreams.containsKey(user)) {
				writer.writeObject(new GameChat(GameChat.MsgType.LOGIN_FAILURE, "", "����� �̹� ����"));
				return;
			}
		} catch (Exception ex) {
			System.out.println("S : �������� �۽� �� �̻� �߻�");
			ex.printStackTrace();
		}
		// �ؽ����̺� �����-���۽�Ʈ�� �� �߰��ϰ� ���ο� �α��� ����Ʈ�� ��ο��� �˸�
		clientOutputStreams.put(user, writer);
		// ���ο� �α��� ����Ʈ�� ��ü���� ���� ��
		broadcastMessage(new GameChat(GameChat.MsgType.LOGIN_LIST, "", makeClientList()));
	} // close handleLogin

	// �־��� ����ڸ� �ؽ����̺��� ���� (��� ��Ʈ���� ����)
	// �׸��� ������Ʈ�� ������ ����Ʈ�� ��� �����ڿ��� ������
	private synchronized void handleLogout(String user) {
		clientOutputStreams.remove(user);
		// ���ο� �α��� ����Ʈ�� ��ü���� ���� ��
		broadcastMessage(new GameChat(GameChat.MsgType.LOGIN_LIST, "", makeClientList()));
	} // close handleLogout

	// Ŭ���̾�Ʈ�� ��ȭ ���濡�� ������ �޽���. "��ü"���� ���� �־�� ��
	private synchronized void handleMessage(String sender, String contents) {
		broadcastMessage(new GameChat(GameChat.MsgType.SERVER_MSG, sender, contents));
	} // close handleIncomingMessage

	// �ؽ��ʿ� �ִ� ��� �����ڵ鿡�� �־��� �޽����� ������ �޼ҵ�.
	// �ݵ�� synchronized �� �޼ҵ忡���� ȣ���ϱ�� ��
	private void broadcastMessage(GameChat message) {
		Set<String> s = clientOutputStreams.keySet(); // ���� ��ϵ� ����ڵ��� �����ϰ� �ϳ��ϳ��� �޽��� ����
														// �׷��� ���ؼ� ���� ����� ����Ʈ�� ����
		Iterator<String> it = s.iterator();
		String user;
		while (it.hasNext()) {
			user = it.next();
			try {
				ObjectOutputStream writer = clientOutputStreams.get(user); // ��� ����ڿ��� ��Ʈ�� ����
				writer.writeObject(message); // �� ��Ʈ���� ���
				writer.flush();
			} catch (Exception ex) {
				System.out.println("S : �������� �۽� �� �̻� �߻�");
				ex.printStackTrace();
			}
		} // end while
	} // end broadcastMessage
	
	private void distributeMessage(ObjectOutputStream writer1) {
		Set<String> s = clientOutputStreams.keySet();
		userset = new ArrayList<String>(s);
		//String user;
		try {
			if (userset.size() == 3) {
				SetTurnNumber();
				InsertAnimals();
				for (int i = 0; i < userset.size(); i++) {
					ObjectOutputStream writer = clientOutputStreams.get(userset.get(i)); // ��� ����ڿ��� ��Ʈ�� ����
					writer.writeObject(new GameChat(GameChat.MsgType.GAME_START, playeranimals.get(i), nums[i], 0, TURN.get(0)));
					writer.flush();
				}
			} else
				writer1.writeObject(new GameChat(GameChat.MsgType.NOT_ENOUGH_USER, "", "�ο��� ������� ����"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// �� �÷��̾���� �� ��ȣ�� ����� �ؽøʿ� �־���.
	public void SetTurnNumber() {
		nums = new int[3];
		
		for(int i = 0; i<3; i++) {
			nums[i] = (int)(Math.random() * 3);
			for(int j = 0; j<i; j++) {
				if(nums[i] == nums[j]) {
					i--;
					break;
				}
			}
		}
		
		Set<String> s = clientOutputStreams.keySet();
		userset = new ArrayList<String>(s);
		TURN.put(nums[0],userset.get(0));
		TURN.put(nums[1],userset.get(1));
		TURN.put(nums[2],userset.get(2));
	}
	
	//���� ������ �� ��ȣ�� ����.
	public int SetNextTurnNumber(int num) {
		x = num+1;
		return x % 3;
	}

	public void InsertAnimals() {
		int percent, k, i;
		int countSheep = 7;
		int countDog = 7;
		int countCow = 7;
		int countRabbit = 7;
		int countChicken = 7;
		
		Aset = new ArrayList<Integer>();
		Bset = new ArrayList<Integer>();
		Cset = new ArrayList<Integer>();
		
		playeranimals = new ArrayList<ArrayList<Integer>>();
		k = (int) (Math.random() * 3);
		switch(k) {
		case 0:
			Aset.add(5);
			break;
		case 1:
			Bset.add(5);
			break;
		case 2:
			Cset.add(5);
			break;
		}
		
		for(;!(Aset.size()==12);) {
			i = (int)(Math.random() * 5);
			switch(i) {
			case 0: if(countSheep > 0) {Aset.add(0); countSheep--;} break;
			case 1: if(countDog> 0) {Aset.add(1); countDog--;} break;
			case 2: if(countCow > 0) {Aset.add(2); countCow--;} break;
			case 3: if(countRabbit > 0) {Aset.add(3); countRabbit--;} break;
			case 4: if(countChicken > 0) {Aset.add(4); countChicken--;} break;
			}
		}
			
		for(;!(Bset.size()==12);) {
			
			i = (int)(Math.random() * 5);
			switch(i) {
			case 0: if(countSheep > 0) {Bset.add(0); countSheep--;} break;
			case 1: if(countDog> 0) {Bset.add(1); countDog--;} break;
			case 2: if(countCow > 0) {Bset.add(2); countCow--;} break;
			case 3: if(countRabbit > 0) {Bset.add(3); countRabbit--;} break;
			case 4: if(countChicken > 0) {Bset.add(4); countChicken--;} break;
			}	
		}
				
		for(;!(Cset.size()==12);) {
			
			i = (int)(Math.random() * 5);
			switch(i) {
			case 0: if(countSheep > 0) {Cset.add(0); countSheep--;} break;
			case 1: if(countDog> 0) {Cset.add(1); countDog--;} break;
			case 2: if(countCow > 0) {Cset.add(2); countCow--;} break;
			case 3: if(countRabbit > 0) {Cset.add(3); countRabbit--;} break;
			case 4: if(countChicken > 0) {Cset.add(4); countChicken--;} break;
			}	
		}
		
		Collections.sort(Aset, Collections.reverseOrder());
		Collections.sort(Bset, Collections.reverseOrder());
		Collections.sort(Cset, Collections.reverseOrder());
		playeranimals.add(Aset);
		playeranimals.add(Bset);
		playeranimals.add(Cset);

	}

	private String makeClientList() {
		Set<String> s = clientOutputStreams.keySet(); // ���� ��ϵ� ����ڵ��� ����
		Iterator<String> it = s.iterator();
		String userList = "";
		while (it.hasNext()) {
			userList += it.next() + "/"; // ��Ʈ�� ����Ʈ�� �߰��ϰ� ������ ���
		} // end while
		return userList;
	} // makeClientList

}
