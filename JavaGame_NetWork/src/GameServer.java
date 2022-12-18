//import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import java.util.*;

//import javax.swing.ImageIcon;
//import javax.swing.JButton;

public class GameServer {
	
	int []nums;						// 각 플레이어들의 턴 번호 저장
	private int x; 					//턴 번호 설정할때 1 더한 값을 저장하는 변수 
	//private String NextUser;
	
	ArrayList<String> userset;
	ArrayList<Integer> Aset;		//첫 번째 플레이어에게 주어질 동물들
	ArrayList<Integer> Bset;		//두 번째 플레이어에게 주어질 동물들
	ArrayList<Integer> Cset;		//세 번째 플레이어에게 주어질 동물들
	ArrayList<ArrayList<Integer>> playeranimals; // 각 플레이어들에게 분배될 동물들의 set
	
	HashMap<Integer, String> TURN = new HashMap<Integer, String>();

	// 접속한 클라이언트의 사용자 이름과 출력 스트림을 해쉬 테이블에 보관
	// 나중에 특정 사용자에게 메시지를 보낼때 사용. 현재 접속해 있는 사용자의 전체 리스트를 구할때도 사용
	HashMap<String, ObjectOutputStream> clientOutputStreams = new HashMap<String, ObjectOutputStream>();

	public static void main(String[] args) {
		new GameServer().go();
	}

	private void go() {
		try {
			ServerSocket serverSock = new ServerSocket(8080); // 채팅을 위한 소켓 포트 8080 사용
			while (true) {
				Socket clientSocket = serverSock.accept(); // 새로운 클라이언트 접속 대기

				// 클라이언트를 위한 입출력 스트림 및 스레드 생성
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				System.out.println("S : 클라이언트 연결 됨"); // 상태를 보기위한 출력 메시지
			}
		} catch (Exception ex) {
			System.out.println("S : 클라이언트  연결 중 이상발생"); // 상태를 보기위한 출력 메시지
			ex.printStackTrace();
		}
	}

	// Client 와 1:1 대응하는 메시지 수신 스레드
	private class ClientHandler implements Runnable {
		Socket sock; // 클라이언트 연결용 소켓
		ObjectInputStream reader; // 클라이언트로 부터 수신하기 위한 스트림
		ObjectOutputStream writer; // 클라이언트로 송신하기 위한 스트림

		// 구성자. 클라이언트와의 소켓에서 읽기와 쓰기 스트림 만들어 냄
		// 스트림을 만들때 InputStream을 먼저 만들면 Hang함. 그래서 OutputStream먼저 만들었음.
		// Bug 인지... 어떤 이유가 있는 것인지 나중에 찾아보기로 함
		public ClientHandler(Socket clientSocket) {
			try {
				sock = clientSocket;
				writer = new ObjectOutputStream(clientSocket.getOutputStream());
				reader = new ObjectInputStream(clientSocket.getInputStream());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// 클라이언트에서 받은 메시지에 따라 상응하는 작업을 수행
		public void run() {
			GameChat message;
			GameChat.MsgType type;
			try {
				while (true) {
					// 읽은 메시지의 종류에 따라 각각 할일이 정해져 있음
					message = (GameChat) reader.readObject(); // 클라이언트의 전송 메시지 받음
					type = message.getType();
					if (type == GameChat.MsgType.LOGIN) { // 클라이언트 로그인 요청
						handleLogin(message.getSender(), writer); // 클아이언트 이름과 그에게 메시지를
																	// 보낼 스트림을 등록
					} else if (type == GameChat.MsgType.LOGOUT) { // 클라이언트 로그아웃 요청
						handleLogout(message.getSender()); // 등록된 이름 및 이와 연결된 스트림 삭제
						writer.close();
						reader.close();
						sock.close(); // 이 클라이언트와 관련된 스트림들 닫기
						return; // 스레드 종료
					} else if (type == GameChat.MsgType.CLIENT_MSG) {
						handleMessage(message.getSender(), message.getContents());
					} else if (type == GameChat.MsgType.NO_ACT) {
						// 무시해도 되는 메시지
						continue;
					} else if (type == GameChat.MsgType.GAME_INFO) {
						broadcastMessage(new GameChat(GameChat.MsgType.GAME_INFO, message.i, message.animalNum, message.score, message.getSender(), 
								(message.turnNum+1)%3, TURN.get((message.turnNum+1)%3), message.turnAble));

					} else if (type == GameChat.MsgType.GAME_START) {
						distributeMessage(writer);
					} else {
						// 정체가 확인되지 않는 이상한 메시지?
						throw new Exception("S : 클라이언트에서 알수 없는 메시지 도착했음");
					}
				}
			} catch (Exception ex) {
				System.out.println("S : 클라이언트 접속 종료"); // 연결된 클라이언트 종료되면 예외발생
														// 이를 이용해 스레드 종료시킴
			}
		} // close run
	} // close inner class

	// 사용자 이름과 클라이언트로의 출력 스트림과 연관지어 해쉬 테이블에 넣어줌.
	// 이미 동일한 이름의 사용자가 있다면, 현재의 로그인은 실패 한것으로 클라이언트에게 알림
	// 해쉬 테이블의 접근에서는 경쟁조건 생기면 곤란 (not Thread-Safe. Synchronized로 상호배제 함.
	private synchronized void handleLogin(String user, ObjectOutputStream writer) {
		try {
			// 이미 동일한 이름의 사용자가 있다면, 현재의 로그인은 실패 한것으로 클라이언트에게 알림
			if (clientOutputStreams.containsKey(user)) {
				writer.writeObject(new GameChat(GameChat.MsgType.LOGIN_FAILURE, "", "사용자 이미 있음"));
				return;
			}
		} catch (Exception ex) {
			System.out.println("S : 서버에서 송신 중 이상 발생");
			ex.printStackTrace();
		}
		// 해쉬테이블에 사용자-전송스트림 페어를 추가하고 새로운 로그인 리스트를 모두에게 알림
		clientOutputStreams.put(user, writer);
		// 새로운 로그인 리스트를 전체에게 보내 줌
		broadcastMessage(new GameChat(GameChat.MsgType.LOGIN_LIST, "", makeClientList()));
	} // close handleLogin

	// 주어진 사용자를 해쉬테이블에서 제거 (출력 스트림도 제거)
	// 그리고 업데이트된 접속자 리스트를 모든 접속자에게 보내줌
	private synchronized void handleLogout(String user) {
		clientOutputStreams.remove(user);
		// 새로운 로그인 리스트를 전체에게 보내 줌
		broadcastMessage(new GameChat(GameChat.MsgType.LOGIN_LIST, "", makeClientList()));
	} // close handleLogout

	// 클라이언트가 대화 상대방에게 보내는 메시지. "전체"에게 보내 주어야 함
	private synchronized void handleMessage(String sender, String contents) {
		broadcastMessage(new GameChat(GameChat.MsgType.SERVER_MSG, sender, contents));
	} // close handleIncomingMessage

	// 해쉬맵에 있는 모든 접속자들에게 주어진 메시지를 보내는 메소드.
	// 반드시 synchronized 된 메소드에서만 호출하기로 함
	private void broadcastMessage(GameChat message) {
		Set<String> s = clientOutputStreams.keySet(); // 먼저 등록된 사용자들을 추출하고 하나하나에 메시지 보냄
														// 그러기 위해서 먼저 사용자 리스트만 추출
		Iterator<String> it = s.iterator();
		String user;
		while (it.hasNext()) {
			user = it.next();
			try {
				ObjectOutputStream writer = clientOutputStreams.get(user); // 대상 사용자와의 스트림 추출
				writer.writeObject(message); // 그 스트림에 출력
				writer.flush();
			} catch (Exception ex) {
				System.out.println("S : 서버에서 송신 중 이상 발생");
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
					ObjectOutputStream writer = clientOutputStreams.get(userset.get(i)); // 대상 사용자와의 스트림 추출
					writer.writeObject(new GameChat(GameChat.MsgType.GAME_START, playeranimals.get(i), nums[i], 0, TURN.get(0)));
					writer.flush();
				}
			} else
				writer1.writeObject(new GameChat(GameChat.MsgType.NOT_ENOUGH_USER, "", "인원이 충분하지 않음"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 각 플레이어들의 턴 번호를 만들고 해시맵에 넣어줌.
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
	
	//다음 차례의 턴 번호를 설정.
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
		Set<String> s = clientOutputStreams.keySet(); // 먼저 등록된 사용자들을 추출
		Iterator<String> it = s.iterator();
		String userList = "";
		while (it.hasNext()) {
			userList += it.next() + "/"; // 스트링 리스트에 추가하고 구분자 명시
		} // end while
		return userList;
	} // makeClientList

}
