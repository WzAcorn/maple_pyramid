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
	String frameTitle = "����� ����";
	JTextArea outgoing; // ���� �޽����� �ۼ��ϴ� ��
	JTextArea incoming; // ���� �޽����� ����ϴ� ��
	JButton logButton; // �α��ΰ� �α׾ƿ��� �ϴ� ��ư
	ObjectInputStream reader; // ���ſ� ��Ʈ��
	ObjectOutputStream writer; // �۽ſ� ��Ʈ��
	Socket sock; // ���� ����� ����
	String user; // �� Ŭ���̾�Ʈ�� �α��� �� ������ �̸�
	JScrollPane qScroller;
	Font font = new Font("�޸�����ü", Font.BOLD, 15);
	Font treefont = new Font("HY������B", Font.BOLD, 13);
	// (��ģ��)�̹��� �߰�
	ImageIcon logoImg = new ImageIcon("image/logo.png");
	ImageIcon loginImg = new ImageIcon("image/login.png");
	ImageIcon logoutImg = new ImageIcon("image/logout.png");
	ImageIcon sendImg = new ImageIcon("image/send.png");
	
	private int i;		// i = �Ƕ�̵� ��ư�� �ε��� ��ȣ
	private int animalNum;	// ���� �ĺ� ��ȣ
	private boolean login = true;  // (��ħ) �߰� - ���� �α��λ����ΰ��� Ȯ���ϴ� ����
	GameMain gm;
	
	Color sand = new Color(200, 200, 150);  // �縷��
	Color maroon = new Color(128, 0, 0);	// ����2
	Color gray = new Color(80, 80, 80);	// ����2
	public static void main(String[] args) {
		new GameClient().go();
	}
	
	private static void background_soundplay() { // ���� �÷����ϴ� �Լ�
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
		// ��ü �޽��� â
		incoming = new JTextArea(22, 15);  // (��ħ) ũ�� �ٲ�
		incoming.setBackground(new Color(153,153,153));
		incoming.setForeground(Color.WHITE);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// ���� �ۼ��ϴ� �޽��� â
		outgoing = new JTextArea(5, 15);  // (��ħ) ũ�� �ٲ�
		outgoing.setBackground(new Color(153,153,153));
		outgoing.setForeground(Color.WHITE);
		outgoing.addKeyListener(new EnterKeyListener());
		outgoing.setLineWrap(true);
		outgoing.setWrapStyleWord(true);
		outgoing.setEditable(true);
		JScrollPane oScroller = new JScrollPane(outgoing);
		oScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		oScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// (��ħ) ~
		// Send��ư�� �α� ��/�ƿ� ��ư 
		JButton sendbutton = new JButton();
		sendbutton.setFont(new Font("���", Font.ITALIC, 0));
		sendbutton.setPreferredSize(new Dimension(10, 28));
		sendbutton.addActionListener(new SendButtonListener());
		logButton = new JButton();
		logButton.setFont(new Font("���", Font.ITALIC, 0));
		logButton.setPreferredSize(new Dimension(10, 28));
		logButton.addActionListener(new LogButtonListener());
				
		// (��ħ)��ư �����ϰ�
		sendbutton.setBorderPainted(false);
		sendbutton.setContentAreaFilled(false);
		sendbutton.setOpaque(false);
		logButton.setBorderPainted(false);
		logButton.setContentAreaFilled(false);
		logButton.setOpaque(false);
		// ��ư�� �̹��� �ֱ�
		sendbutton.setIcon(sendImg);
		logButton.setIcon(loginImg);
		//
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
		upperPanel.setBorder(BorderFactory.createEmptyBorder(18, 5, 5, 5));  // (��ħ) ũ�� ����

		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.X_AXIS));
		lowerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

		JPanel sendPanel = new JPanel();
		sendPanel.setLayout(new BorderLayout());

		JLabel textLabel1 = new JLabel("�޽����� �Է��ϼ���");
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

		// (��ħ) �߰� - ������ ��� �ΰ� ����
		JPanel logoPanel = new JPanel();
		JLabel llogo = new JLabel(logoImg);
		logoPanel.setSize(100, 200);
		logoPanel.setBackground(gray);
		logoPanel.add(llogo);
		mainPanel.add(logoPanel);
		logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
		//
		
		JLabel textLabel2 = new JLabel("��ü �޽��� â");
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
		inputPanel.setBackground(gray);  // �޽����� �Է��Ͻÿ� �κ�
		gm.setBackground(sand);
		

		frame.getContentPane().add(BorderLayout.EAST, mainPanel);
		frame.getContentPane().add(BorderLayout.CENTER, gm);
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1030, 730);  // (��ħ)������ ����		
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
			JOptionPane.showMessageDialog(null, "�������ӿ� �����Ͽ����ϴ�. ������ �����մϴ�.");
			
			ex.printStackTrace();
			frame.dispose(); // ��Ʈ��ũ�� �ʱ� ���� �ȵǸ� Ŭ���̾�Ʈ ���� ����
		}
	}

	// Login�� Logout�� ����ϴ� ��ư�� ������
	private class LogButtonListener implements ActionListener {
		@Override
		// (��ħ) �ؽ�Ʈ�� �α��εǾ����� ���� -> login ���� �ϳ� ����� ����
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
	
	private void soundplay1() { // ���� �÷����ϴ� �Լ�
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
		user = JOptionPane.showInputDialog("�̸��� �Է��ϼ���.");
		try {
			writer.writeObject(new GameChat(GameChat.MsgType.LOGIN, user, ""));
			writer.flush();
			frameTitle = "���� ����� :";
			frame.setTitle(frameTitle + user);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "�α��� �� �������ӿ� ������ �߻��Ͽ����ϴ�.");
			ex.printStackTrace();
		}
	}

	private void processLogout() {
		int choice = JOptionPane.showConfirmDialog(null, "Logout�մϴ�");
		if (choice == JOptionPane.YES_OPTION) {
			try {
				writer.writeObject(new GameChat(GameChat.MsgType.LOGOUT, user, ""));
				writer.flush();
				// ����� ��� ��Ʈ���� ������ �ݰ� ���α׷��� ���� ��
				writer.close();
				reader.close();
				sock.close();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "�α׾ƿ� �� �������ӿ� ������ �߻��Ͽ����ϴ�. ���������մϴ�");
				ex.printStackTrace();
			} finally {
				System.exit(100); // Ŭ���̾�Ʈ ���� ����
			}
		}
	}
	
	// �޽��� ������ ��ư�� ������
	public class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			try {
				soundplay1();
				incoming.append(user + " : " + outgoing.getText() + "\n"); // ���� �޽��� â�� ���̱�
				writer.writeObject(new GameChat(GameChat.MsgType.CLIENT_MSG, user, outgoing.getText()));
				writer.flush();
				outgoing.setText("");
				outgoing.requestFocus();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "�޽��� ������ ������ �߻��Ͽ����ϴ�.");
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
						incoming.append(user + " : " + outgoing.getText() + "\n"); // ���� �޽��� â�� ���̱�
						incoming.setSelectionStart(incoming.getText().length());
						qScroller.getVerticalScrollBar().setValue(qScroller.getVerticalScrollBar().getMaximum());
						writer.writeObject(new GameChat(GameChat.MsgType.CLIENT_MSG, user, outgoing.getText()));
						writer.flush();
						outgoing.setText("");
						outgoing.requestFocus();
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, "�޽��� ������ ������ �߻��Ͽ����ϴ�.");
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
					message = (GameChat) reader.readObject(); // �����α� ������ �޽��� ���
					type = message.getType();
					if (type == GameChat.MsgType.LOGIN_FAILURE) { // �α����� ������ �����
						JOptionPane.showMessageDialog(null, "Login�� �����Ͽ����ϴ�. �ٽ� �α����ϼ���");
						frameTitle = "����� ����: ";
						frame.setTitle(frameTitle);
						logButton.setText("Login");
					} else if (type == GameChat.MsgType.SERVER_MSG) { // �޽����� �޾Ҵٸ� ������
						if (message.getSender().equals(user))
							continue; // ���� ���� ������ ���� �ʿ� ����
						incoming.append(message.getSender() + " : " + message.getContents() + "\n");
						qScroller.getVerticalScrollBar().setValue(qScroller.getVerticalScrollBar().getMaximum());
					} else if (type == GameChat.MsgType.LOGIN_LIST) {
						// ���� ���� (""�� ����� ���� �� ����Ʈ �� �տ� ���� ��)
						users = message.getContents().split("/");
						for (int i = 0; i < users.length; i++) {
							if (user.equals(users[i]))
								users[i] = "";
						}
						users = sortUsers(users); // ���� ����� ���� �� �� �ֵ��� �����ؼ� ����
						users[0] = GameChat.ALL; // ����Ʈ �� �տ� "��ü"�� ������ ��
					} else if (type == GameChat.MsgType.NO_ACT) {
						// �ƹ� �׼��� �ʿ���� �޽���. �׳� ��ŵ
					} else if (type == GameChat.MsgType.NOT_ENOUGH_USER) {
						gm.setStartCheck(false);
						JOptionPane.showMessageDialog(null, "���� �ο��� ������� �ʽ��ϴ�.");
					} else if (type == GameChat.MsgType.GAME_INFO) {
						gm.soundplay3();
						i = message.i;								// �Ƕ�̵� ��ư�� �ε���
						animalNum = message.animalNum;				// ������ ������ �ĺ���ȣ
						gm.BoardSet(i, animalNum);					// �ش� �ε����� ��ư�� �������� ������ ������ �ٲ���.
						gm.ChangeButtonInformation();				// �Ƕ�̵� ��ư���� canput�� �缳��.
						gm.ScoreSet(message.score, message.getSender());	// ������ ������ �ֽ�ȭ.
						gm.ShowWhosTurn(message.NextUser);			// ������ �������� ǥ��.
						gm.CheckMyTurn(message.turnNum);			// ���� �ڽ��� �������� Ȯ��.
						gm.CheckMyAnimals(message.turnNum);			// �ڽ��� ���� ���� �� �ִ����� Ȯ��.
						gm.CheckGameEnd(message.getSender(), message.turnAble);
					} else if (type == GameChat.MsgType.GAME_START) {	// �����κ��� �� GameChat Ÿ���� GAME_START���, 
						String you1 = "", you2 = "";	// ��� 1, ��� 2 �� �̸��� �����ϱ� ���� ����
						gm.soundplay2();
						for (int i = 0; i < users.length; i++) {	// ���� ����� �Ⱦ
							if (user.equals(users[i])) ;			// �ڽ��� �̸��� �н�,
							else if (users[1].equals(users[i]))
								you1 = users[i];					// ���1 �� �̸��� you1�� ����,
							else if (users[2].equals(users[i]))
								you2 = users[i];					// ���2 �� �̸��� you1�� ����.
						}
						if (users.length > 1) {
							gm.initializeGame();					// �Ƕ�̵� ��ư�� �ڽ��� �� ��ư�� icon�� null�� ����, �� ��� ��ư ��Ȱ��ȭ.
							gm.gameSet(user, you1, you2);			// �ڽŰ� ���� �̸��� ǥ���ϰ�, �ڽ��� �� ��ư�� Ȱ��ȭ
							gm.setAnimals(message.icon);			// �����κ��� ���� �ڽ��� �����ĺ���ȣ��� �ڽ��� �� ��ư�� ������ ����.
							gm.setMyTurnNumber(message.turnNum);	// �ڽ��� ���� ��ȣ ����.
							gm.ShowWhosTurn(message.NextUser);		// ������ �������� ǥ��.
							gm.CheckMyTurn(message.firstTurnNum);	// ���� �ڽ��� �������� Ȯ��.

						} else {
							JOptionPane.showMessageDialog(null, "���� ��밡 �����ϴ�.");
							gm.setStartCheck(false);
						}
					} else { // ��ü�� Ȯ�ε��� �ʴ� �̻��� �޽���
						throw new Exception("�������� �� �� ���� �޽��� ��������");
					}
				} // close while
			} catch (Exception ex) {
				System.out.println("Ŭ���̾�Ʈ ������ ����"); // �������� ����� ��� �̸� ���� ������ ����
			}
		} // close run

		// �־��� String �迭�� ������ ���ο� �迭 ����
		private String[] sortUsers(String[] users) {
			String[] outList = new String[users.length];
			ArrayList<String> list = new ArrayList<String>();
			for (String s : users) {
				list.add(s);
			}
			Collections.sort(list); // Collections.sort�� ����� �ѹ濡 ����
			for (int i = 0; i < users.length; i++) {
				outList[i] = list.get(i);
			}
			return outList;
		}
	} // close inner class

}
