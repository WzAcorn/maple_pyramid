import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
//import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.text.AttributeSet.ColorAttribute;

public class GameClient {

	JFrame frame;
	String frameTitle = "사용자 없음";
	JTextArea outgoing; // 보낼 메시지를 작성하는 곳
	JTextArea incoming; // 받은 메시지를 출력하는 곳
	JButton logButton; // 로그인과 로그아웃을 하는 버튼
	ObjectInputStream reader; // 수신용 스트림
	ObjectOutputStream writer; // 송신용 스트림
	Socket sock; // 서버 연결용 소켓
	String user; // 이 클라이언트로 로그인 한 유저의 이름
	JScrollPane qScroller;
	Font font = new Font("휴먼편지체", Font.BOLD, 15);
	Font treefont = new Font("HY목각파임B", Font.BOLD, 13);
	// (고친것)이미지 추가
	ImageIcon logoImg = new ImageIcon("image/logo.png");
	ImageIcon loginImg = new ImageIcon("image/login.png");
	ImageIcon logoutImg = new ImageIcon("image/logout.png");
	ImageIcon sendImg = new ImageIcon("image/send.png");
	
	private int i;		// i = 피라미드 버튼의 인덱스 번호
	private int animalNum;	// 동물 식별 번호
	private boolean login = true;  // (고침) 추가 - 현재 로그인상태인가를 확인하는 변수
	GameMain gm;
	
	Color sand = new Color(200, 200, 150);  // 사막색
	Color maroon = new Color(128, 0, 0);	// 갈색2
	Color gray = new Color(80, 80, 80);	// 갈색2
	public static void main(String[] args) {
		new GameClient().go();
	}
	
	private static void background_soundplay() { // 사운드 플레이하는 함수
		File file = new File("sound/Newtro_Ludibrium.wav");
		System.out.println(file.exists());
		try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();
        } catch(Exception e) {   
            e.printStackTrace();
        }
	}
	

	private void go() {
		frame = new JFrame(frameTitle);
		gm = new GameMain();
		background_soundplay();
		// 전체 메시지 창
		incoming = new JTextArea(22, 15);  // (고침) 크기 바꿈
		incoming.setBackground(new Color(153,153,153));
		incoming.setForeground(Color.WHITE);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// 내가 작성하는 메시지 창
		outgoing = new JTextArea(5, 15);  // (고침) 크기 바꿈
		outgoing.setBackground(new Color(153,153,153));
		outgoing.setForeground(Color.WHITE);
		outgoing.addKeyListener(new EnterKeyListener());
		outgoing.setLineWrap(true);
		outgoing.setWrapStyleWord(true);
		outgoing.setEditable(true);
		JScrollPane oScroller = new JScrollPane(outgoing);
		oScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		oScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// (고침) ~
		// Send버튼와 로그 인/아웃 버튼 
		JButton sendbutton = new JButton();
		sendbutton.setFont(new Font("고딕", Font.ITALIC, 0));
		sendbutton.setPreferredSize(new Dimension(10, 28));
		sendbutton.addActionListener(new SendButtonListener());
		logButton = new JButton();
		logButton.setFont(new Font("고딕", Font.ITALIC, 0));
		logButton.setPreferredSize(new Dimension(10, 28));
		logButton.addActionListener(new LogButtonListener());
				
		// (고침)버튼 투명하게
		sendbutton.setBorderPainted(false);
		sendbutton.setContentAreaFilled(false);
		sendbutton.setOpaque(false);
		logButton.setBorderPainted(false);
		logButton.setContentAreaFilled(false);
		logButton.setOpaque(false);
		// 버튼에 이미지 넣기
		sendbutton.setIcon(sendImg);
		logButton.setIcon(loginImg);
		//
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
		upperPanel.setBorder(BorderFactory.createEmptyBorder(18, 5, 5, 5));  // (고침) 크기 조정

		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.X_AXIS));
		lowerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

		JPanel sendPanel = new JPanel();
		sendPanel.setLayout(new BorderLayout());

		JLabel textLabel1 = new JLabel("메시지를 입력하세요");
		textLabel1.setForeground(Color.WHITE);
		inputPanel.add(textLabel1);
		inputPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		inputPanel.add(oScroller);

		buttonPanel.add(logButton);
		buttonPanel.add(sendbutton);

		sendPanel.add(BorderLayout.CENTER, inputPanel);
		sendPanel.add(BorderLayout.SOUTH, buttonPanel);

		lowerPanel.add(sendPanel);
		lowerPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		// (고침) 추가 - 오른쪽 상단 로고 띄우기
		JPanel logoPanel = new JPanel();
		JLabel llogo = new JLabel(logoImg);
		logoPanel.setSize(100, 200);
		logoPanel.setBackground(gray);
		logoPanel.add(llogo);
		mainPanel.add(logoPanel);
		logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
		//
		
		JLabel textLabel2 = new JLabel("전체 메시지 창");
		textLabel2.setForeground(Color.WHITE);
		upperPanel.add(textLabel2);
		upperPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		upperPanel.add(qScroller);

		mainPanel.add(upperPanel);
		mainPanel.add(lowerPanel);

		SetUpNetWorking();
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
		
		sendbutton.setFont(font);
		logButton.setFont(font);
		textLabel1.setFont(font);
		textLabel2.setFont(font);
		incoming.setFont(treefont);
		outgoing.setFont(treefont);
		
		upperPanel.setBackground(gray);
		lowerPanel.setBackground(gray);
		
		mainPanel.setBackground(sand);
		sendPanel.setBackground(sand);
		buttonPanel.setBackground(gray);
		inputPanel.setBackground(gray);  // 메시지를 입력하시오 부분
		gm.setBackground(sand);
		

		frame.getContentPane().add(BorderLayout.EAST, mainPanel);
		frame.getContentPane().add(BorderLayout.CENTER, gm);
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1030, 730);  // (고침)사이즈 조절		
		frame.setVisible(true);
		frame.setResizable(false);		
	}
	
	
	private void SetUpNetWorking() {
		try {
			sock = new Socket("220.69.208.119", 8080);
			reader = new ObjectInputStream(sock.getInputStream());
			writer = new ObjectOutputStream(sock.getOutputStream());
			gm.writer = writer;
			gm.reader = reader;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "서버접속에 실패하였습니다. 접속을 종료합니다.");
			
			ex.printStackTrace();
			frame.dispose(); // 네트워크가 초기 연결 안되면 클라이언트 강제 종료
		}
	}

	// Login과 Logout을 담당하는 버튼의 리스너
	private class LogButtonListener implements ActionListener {
		@Override
		// (고침) 텍스트로 로그인되었는지 구분 -> login 변수 하나 만들어 구분
		public void actionPerformed(ActionEvent e) {
			if (login == true) {
				processLogin();
				soundplay1();
				login = false;
				logButton.setIcon(logoutImg);
			} else
				processLogout();
		}
	}
	
	private void soundplay1() { // 사운드 플레이하는 함수
		File file1 = new File("sound/button1.wav");
		System.out.println(file1.exists());
		try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file1);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();
        } catch(Exception e) {
            
            e.printStackTrace();
        }
	}
	

	

	

	private void processLogin() {
		user = JOptionPane.showInputDialog("이름을 입력하세요.");
		try {
			writer.writeObject(new GameChat(GameChat.MsgType.LOGIN, user, ""));
			writer.flush();
			frameTitle = "현재 사용자 :";
			frame.setTitle(frameTitle + user);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "로그인 중 서버접속에 문제가 발생하였습니다.");
			ex.printStackTrace();
		}
	}

	private void processLogout() {
		int choice = JOptionPane.showConfirmDialog(null, "Logout합니다");
		if (choice == JOptionPane.YES_OPTION) {
			try {
				writer.writeObject(new GameChat(GameChat.MsgType.LOGOUT, user, ""));
				writer.flush();
				// 연결된 모든 스트림과 소켓을 닫고 프로그램을 종료 함
				writer.close();
				reader.close();
				sock.close();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "로그아웃 중 서버접속에 문제가 발생하였습니다. 강제종료합니다");
				ex.printStackTrace();
			} finally {
				System.exit(100); // 클라이언트 완전 종료
			}
		}
	}
	
	// 메시지 보내는 버튼의 리스너
	public class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			try {
				soundplay1();
				incoming.append(user + " : " + outgoing.getText() + "\n"); // 나의 메시지 창에 보이기
				writer.writeObject(new GameChat(GameChat.MsgType.CLIENT_MSG, user, outgoing.getText()));
				writer.flush();
				outgoing.setText("");
				outgoing.requestFocus();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "메시지 전송중 문제가 발생하였습니다.");
				ex.printStackTrace();
			}
		}
	} // close SendButtonListener inner class

	public class EnterKeyListener implements KeyListener {
		boolean presscheck = false;

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				presscheck = true;
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (presscheck == true) {
					String str = outgoing.getText() + "\r\n";
					outgoing.setText(str);
					presscheck = false;
				} else {
					e.consume();
					presscheck = false;
					try {
						incoming.append(user + " : " + outgoing.getText() + "\n"); // 나의 메시지 창에 보이기
						incoming.setSelectionStart(incoming.getText().length());
						qScroller.getVerticalScrollBar().setValue(qScroller.getVerticalScrollBar().getMaximum());
						writer.writeObject(new GameChat(GameChat.MsgType.CLIENT_MSG, user, outgoing.getText()));
						writer.flush();
						outgoing.setText("");
						outgoing.requestFocus();
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, "메시지 전송중 문제가 발생하였습니다.");
						ex.printStackTrace();
					}
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				presscheck = false;
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}

	public class IncomingReader implements Runnable {
		public void run() {
			GameChat message;
			GameChat.MsgType type;
			String[] users = {};
			try {
				while (true) {
					message = (GameChat) reader.readObject(); // 서버로기 부터의 메시지 대기
					type = message.getType();
					if (type == GameChat.MsgType.LOGIN_FAILURE) { // 로그인이 실패한 경우라면
						JOptionPane.showMessageDialog(null, "Login이 실패하였습니다. 다시 로그인하세요");
						frameTitle = "사용자 없음: ";
						frame.setTitle(frameTitle);
						logButton.setText("Login");
					} else if (type == GameChat.MsgType.SERVER_MSG) { // 메시지를 받았다면 보여줌
						if (message.getSender().equals(user))
							continue; // 내가 보낸 편지면 보일 필요 없음
						incoming.append(message.getSender() + " : " + message.getContents() + "\n");
						qScroller.getVerticalScrollBar().setValue(qScroller.getVerticalScrollBar().getMaximum());
					} else if (type == GameChat.MsgType.LOGIN_LIST) {
						// 나는 빼고 (""로 만들어 정렬 후 리스트 맨 앞에 오게 함)
						users = message.getContents().split("/");
						for (int i = 0; i < users.length; i++) {
							if (user.equals(users[i]))
								users[i] = "";
						}
						users = sortUsers(users); // 유저 목록을 쉽게 볼 수 있도록 정렬해서 제공
						users[0] = GameChat.ALL; // 리스트 맨 앞에 "전체"가 들어가도록 함
					} else if (type == GameChat.MsgType.NO_ACT) {
						// 아무 액션이 필요없는 메시지. 그냥 스킵
					} else if (type == GameChat.MsgType.NOT_ENOUGH_USER) {
						gm.setStartCheck(false);
						JOptionPane.showMessageDialog(null, "게임 인원이 충분하지 않습니다.");
					} else if (type == GameChat.MsgType.GAME_INFO) {
						gm.soundplay3();
						i = message.i;								// 피라미드 버튼의 인덱스
						animalNum = message.animalNum;				// 선택한 동물의 식별번호
						gm.BoardSet(i, animalNum);					// 해당 인덱스의 버튼의 아이콘을 선택한 동물로 바꿔줌.
						gm.ChangeButtonInformation();				// 피라미드 버튼들의 canput을 재설정.
						gm.ScoreSet(message.score, message.getSender());	// 각자의 점수판 최신화.
						gm.ShowWhosTurn(message.NextUser);			// 누구의 차례인지 표시.
						gm.CheckMyTurn(message.turnNum);			// 현재 자신의 차례인지 확인.
						gm.CheckMyAnimals(message.turnNum);			// 자신이 말을 놓을 수 있는지를 확인.
						gm.CheckGameEnd(message.getSender(), message.turnAble);
					} else if (type == GameChat.MsgType.GAME_START) {	// 서버로부터 온 GameChat 타입이 GAME_START라면, 
						String you1 = "", you2 = "";	// 상대 1, 상대 2 의 이름을 저장하기 위한 변수
						gm.soundplay2();
						for (int i = 0; i < users.length; i++) {	// 유저 목록을 훑어서
							if (user.equals(users[i])) ;			// 자신의 이름은 패스,
							else if (users[1].equals(users[i]))
								you1 = users[i];					// 상대1 의 이름을 you1에 저장,
							else if (users[2].equals(users[i]))
								you2 = users[i];					// 상대2 의 이름을 you1에 저장.
						}
						if (users.length > 1) {
							gm.initializeGame();					// 피라미드 버튼과 자신의 말 버튼의 icon을 null로 설정, 말 배분 버튼 비활성화.
							gm.gameSet(user, you1, you2);			// 자신과 유저 이름을 표시하고, 자신의 말 버튼을 활성화
							gm.setAnimals(message.icon);			// 서버로부터 받은 자신의 동물식별번호들로 자신의 말 버튼의 아이콘 세팅.
							gm.setMyTurnNumber(message.turnNum);	// 자신의 차례 번호 세팅.
							gm.ShowWhosTurn(message.NextUser);		// 누구의 차례인지 표시.
							gm.CheckMyTurn(message.firstTurnNum);	// 현재 자신의 차례인지 확인.

						} else {
							JOptionPane.showMessageDialog(null, "게임 상대가 없습니다.");
							gm.setStartCheck(false);
						}
					} else { // 정체가 확인되지 않는 이상한 메시지
						throw new Exception("서버에서 알 수 없는 메시지 도착했음");
					}
				} // close while
			} catch (Exception ex) {
				System.out.println("클라이언트 스레드 종료"); // 프레임이 종료될 경우 이를 통해 스레드 종료
			}
		} // close run

		// 주어진 String 배열을 정렬한 새로운 배열 리턴
		private String[] sortUsers(String[] users) {
			String[] outList = new String[users.length];
			ArrayList<String> list = new ArrayList<String>();
			for (String s : users) {
				list.add(s);
			}
			Collections.sort(list); // Collections.sort를 사용해 한방에 정렬
			for (int i = 0; i < users.length; i++) {
				outList[i] = list.get(i);
			}
			return outList;
		}
	} // close inner class

}
