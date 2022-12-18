import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class GameMain extends JPanel {

	private JPanel totalpanel;
	private JPanel scorepanel; // 점수패널
	private JPanel pyramidpanel; // 게임이 진행되는 패널
	private JPanel animalpanel; // 자신의 말이 있는 패널
	private JPanel buttonpanel; // 말 배분, 턴 넘김, 게임 종료 버튼이 있는 패널

	private JLabel MY, user2, user3; // 플레이어들의 이름을 나타내는 JLabel
	private JLabel A_Count, B_Count, C_Count; // 플레이어들의 점수를 나타내는 JLabel
	private JLabel turn;
	private JButton distribute, turnskip, gameset; // 말 배분, 턴 넘김, 게임 종료 버튼
	private JTextField distribute_text, turnskip_text, gameset_text;
	private boolean startCheck = false;		// 게임이 시작되었는지의 여부. false = 게임이 시작되지 않은 상태, true = 게임이 시작된 상태
	private boolean turnCheck = false;		// 자신의 차례인지에 대한 변수. false = 자신의 차례 아님, true = 자신의 차례
	private int MyTurnNumber;	// 나의 턴 번호.
	private boolean turnAble = true;		// 자신의 턴에 말을 놓을 수 있는지 없는지를 나타냄.
	private boolean B_turnAble = true;		// B가 말을 놓을 수 있는지 없는지를 나타냄.
	private boolean C_turnAble = true;		// C가 말을 놓을 수 있는지 없는지를 나타냄.
	// (고침)폰트추가
	Font font = new Font("휴먼편지체", Font.BOLD, 15);
	Font treefont = new Font("HY목각파임B", Font.BOLD, 13);
	
	private ArrayList<JButton> myAnimal; 		// 자신이 가지고 있는 말들에 대한 12개의버튼을 요소로 가지는 ArrayList
	private ArrayList<Integer> myAnimalNum;		// 나에게 주어진 동물식별번호를 요소로 가지는 ArrayList
	
	private ArrayList<ImageIcon> myAnimalTouch = new ArrayList<ImageIcon>();
	private ArrayList<ImageIcon> myAnimals_img = new ArrayList<ImageIcon>();

	private ImageIcon slime4 = new ImageIcon("image/slime4.png");
	private ImageIcon mushroom4 = new ImageIcon("image/mushroom4.png");
	private ImageIcon octopus4 = new ImageIcon("image/octopus4.png");
	private ImageIcon penguin4 = new ImageIcon("image/penguin4.png");
	private ImageIcon pinkbean4 = new ImageIcon("image/pinkbean4.png");
	private ImageIcon balrog4 = new ImageIcon("image/balrog4.png");
	private ImageIcon slime3 = new ImageIcon("image/slime3.png");
	private ImageIcon mushroom3 = new ImageIcon("image/mushroom3.png");
	private ImageIcon octopus3 = new ImageIcon("image/octopus3.png");
	private ImageIcon penguin3 = new ImageIcon("image/penguin3.png");
	private ImageIcon pinkbean3 = new ImageIcon("image/pinkbean3.png");
	private ImageIcon balrog3 = new ImageIcon("image/balrog3.png");
	private ImageIcon slime2 = new ImageIcon("image/slime2.png");
	private ImageIcon mushroom2 = new ImageIcon("image/mushroom2.png");
	private ImageIcon octopus2 = new ImageIcon("image/octopus2.png");
	private ImageIcon penguin2 = new ImageIcon("image/penguin2.png");
	private ImageIcon pinkbean2 = new ImageIcon("image/pinkbean2.png");
	private ImageIcon balrog2 = new ImageIcon("image/balrog2.png");
	private ImageIcon slime1 = new ImageIcon("image/slime1.png");
	private ImageIcon mushroom1 = new ImageIcon("image/mushroom1.png");
	private ImageIcon octopus1 = new ImageIcon("image/octopus1.png");
	private ImageIcon penguin1 = new ImageIcon("image/penguin1.png");
	private ImageIcon pinkbean1 = new ImageIcon("image/pinkbean1.png");
	private ImageIcon balrog1 = new ImageIcon("image/balrog1.png");
	private ImageIcon bg_pryamid = new ImageIcon("image/pryamid_background.png");
	// (고침)이미지 추가
	private ImageIcon place = new ImageIcon("image/place.png");
	private ImageIcon turnpass = new ImageIcon("image/turnpass.png");
	private ImageIcon out = new ImageIcon("image/out.png");
	private ImageIcon turnoff = new ImageIcon("image/turnoff.png");
	private ImageIcon turnon = new ImageIcon("image/turnon.png");
	
	private ArrayList<ImageIcon> myanimals = new ArrayList<ImageIcon>();
		
	boolean selected = false;		
	boolean canput = true;
	
	public int choose = -1;	// 자신이 게임판에 말을 놓기 위해 선택한 동물식별번호
	private int touched = 0;

	ObjectInputStream reader;
	ObjectOutputStream writer;

	Color sand = new Color(200, 200, 150);  // 사막색
	Color brown = new Color(97, 90, 64);  // 갈색
	Color blue = new Color(25, 25, 112);  // 파란색
	Color blue2 = new Color(50,50,150);  // 파란색
	Color darkbrown = new Color(68, 48, 23);  // 짙은 갈색
	Color gray = new Color(128, 128, 128);  // 짙은 갈색
	
	ArrayList<ButtonInfo> info;
	
	private int k = 0;
	private int x;		// 나의 버튼들 중에서 선택한 버튼의 인덱스를 저장하는 변수.

	public GameMain() {
		totalpanel = new JPanel();
		totalpanel.setLayout(new BoxLayout(totalpanel, BoxLayout.Y_AXIS));
		
		scorepanel = new JPanel();
		scorepanel.setLayout(null);
		pyramidpanel = new JPanel();
		pyramidpanel.setLayout(new BoxLayout(pyramidpanel, BoxLayout.Y_AXIS));
		animalpanel = new JPanel();
		buttonpanel = new JPanel();

		MY = new JLabel();
		user2 = new JLabel();
		user3 = new JLabel();

		A_Count = new JLabel("0");
		B_Count = new JLabel("0");
		C_Count = new JLabel("0");
		JLabel turnlabel = new JLabel("Turn");
		turn = new JLabel("없음");

		// (고침) 버튼 텍스트 없앰
		distribute = new JButton();
		turnskip = new JButton();
		gameset = new JButton();
		
		// (고침) 추가된 것들 ~
		// 버튼 세개(말분배, 턴넘기기, 게임종료) 크기, 위치
		distribute.setBounds(350, 16, 50, 50);
		turnskip.setBounds(450, 16, 50, 50);
		gameset.setBounds(550, 16, 50, 50);
		// 버튼 투명하게
		distribute.setBorderPainted(false);
		distribute.setContentAreaFilled(false);
		distribute.setOpaque(false);
		turnskip.setBorderPainted(false);
		turnskip.setContentAreaFilled(false);
		turnskip.setOpaque(false);
		gameset.setBorderPainted(false);
		gameset.setContentAreaFilled(false);
		gameset.setOpaque(false);	
		// 버튼에 이미지 넣기
		distribute.setIcon(place);
		turnskip.setIcon(turnpass);
		gameset.setIcon(out);	
		
		// 버튼 밑에 텍스트 넣기
		distribute_text = new JTextField("말 분배");
		turnskip_text = new JTextField("턴 넘기기");
		gameset_text = new JTextField("게임종료");
		distribute_text.setBounds(351, 68, 60, 30);
		turnskip_text.setBounds(443, 68, 60, 30);
		gameset_text.setBounds(546, 68, 60, 30);
		// 텍스트들 배경 투명하게
		distribute_text.setOpaque(false);
		turnskip_text.setOpaque(false);
		gameset_text.setOpaque(false);
		distribute_text.setBorder(null);
		turnskip_text.setBorder(null);
		gameset_text.setBorder(null);
		// 텍스트 폰트지정
		distribute_text.setFont(treefont);
		turnskip_text.setFont(treefont);
		gameset_text.setFont(treefont);
		// 텍스트 색깔 지정
		distribute_text.setForeground(Color.WHITE);
		turnskip_text.setForeground(Color.WHITE);
		gameset_text.setForeground(Color.WHITE);
		//
		
		info = new ArrayList<>();

		// (고침)위치 조정~
		MY.setBounds(0, 0, 100, 30);
		A_Count.setBounds(90, 0, 30, 30);
		user2.setBounds(0, 30, 100, 30);
		B_Count.setBounds(90, 30, 30, 30);
		user3.setBounds(0, 60, 100, 30);
		C_Count.setBounds(90, 60, 30, 30);
		turnlabel.setBounds(180, 15, 50, 30);
		turn.setBounds(180, 45, 40, 30);			
		
		// 글짜 모양
		MY.setForeground(Color.WHITE);
		A_Count.setForeground(Color.WHITE);
		user2.setForeground(Color.WHITE);
		B_Count.setForeground(Color.WHITE);
		user3.setForeground(Color.WHITE);
		C_Count.setForeground(Color.WHITE);
		turnlabel.setForeground(Color.WHITE);
		turn.setForeground(Color.WHITE);
		// 폰트 적용
		MY.setFont(font);
		A_Count.setFont(font);
		user2.setFont(font);
		B_Count.setFont(font);
		user3.setFont(font);
		user2.setFont(font);
		C_Count.setFont(font);
		turnlabel.setFont(font);
		turn.setFont(font);
		//
		
		scorepanel.add(MY);
		scorepanel.add(A_Count);
		scorepanel.add(user2);
		scorepanel.add(B_Count);
		scorepanel.add(user3);
		scorepanel.add(C_Count);
		scorepanel.add(turnlabel);
		scorepanel.add(turn);
		// scorepanel.setBackground(sand); 없애도됨
		myAnimalNum = new ArrayList<Integer>();
		
		//추가된 구문
		myAnimalTouch.add(slime3);
		myAnimalTouch.add(mushroom3);
		myAnimalTouch.add(pinkbean3);
		myAnimalTouch.add(penguin3);
		myAnimalTouch.add(octopus3);
		myAnimalTouch.add(balrog3);
		
		myAnimals_img.add(slime4);
		myAnimals_img.add(mushroom4);
		myAnimals_img.add(pinkbean4);
		myAnimals_img.add(penguin4);
		myAnimals_img.add(octopus4);
		myAnimals_img.add(balrog4);
		
		myanimals.add(slime1);
		myanimals.add(slime2);
		myanimals.add(slime3);
		myanimals.add(slime4);
		myanimals.add(mushroom1);
		myanimals.add(mushroom2);
		myanimals.add(mushroom3);
		myanimals.add(mushroom4);
		myanimals.add(pinkbean1);
		myanimals.add(pinkbean2);
		myanimals.add(pinkbean3);
		myanimals.add(pinkbean4);
		myanimals.add(penguin1);
		myanimals.add(penguin2);
		myanimals.add(penguin3);
		myanimals.add(penguin4);
		myanimals.add(octopus1);
		myanimals.add(octopus2);
		myanimals.add(octopus3);
		myanimals.add(octopus4);
		myanimals.add(balrog1);
		myanimals.add(balrog2);
		myanimals.add(balrog3);
		myanimals.add(balrog4);
		// 자신의 말을 보여주는 버튼. 버튼들에게 리스너 장착
		myAnimal = new ArrayList<JButton>();
		for (int i = 0; i < 12; i++) {
			int x = i;			
			myAnimal.add(new JButton());
			myAnimal.get(i).addActionListener(new ChooseListener());
			myAnimal.get(i).addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					if (selected == false && myAnimal.get(0).getIcon() != null )
						myAnimal.get(x).setIcon(myAnimals_img.get(touched));
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {					
					// TODO Auto-generated method stub
					if (selected == false) {
						int tmp = 0;					
						for (int i = 0; i < myAnimal.size(); i++) {
							if (e.getSource() == myAnimal.get(i))
								tmp = i;
						}
						if(myAnimal.get(tmp).getIcon() != null) {
							//System.out.println(tmp + "이미지가 있음");
							touched = myAnimalNum.get(tmp);				
							myAnimal.get(x).setIcon(myAnimalTouch.get(touched));
						}										
					}
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			myAnimal.get(i).setBackground(darkbrown);
		}
		
		// animalpanel에 버튼 장착. 처음에는 비활성화
		for (JButton btn : myAnimal) {
			btn.setPreferredSize(new Dimension(50, 52));
			btn.setEnabled(false);
			animalpanel.add(btn);
		}

		// 피라미드 버튼 객체들의 생성
		for (int i = 1; i < 9; i++) {
			for (int j = 0; j < i; j++) {
				info.add(new ButtonInfo(i, -1, false, true));
			}
		}

		// 피라미드 버튼들에게 리스너를 달아준다
		for (int i = 0; i < info.size(); i++) {
			info.get(i).btn.setPreferredSize(new Dimension(50, 52));
			info.get(i).btn.setBorder(BorderFactory.createLoweredBevelBorder());
			info.get(i).btn.addActionListener(new ButtonListener());
			info.get(i).btn.setFocusPainted(false);
			info.get(i).btn.setBackground(blue2);
		}

		// 피라미드 버튼들을 패널에 부착
		for (int i = 1; i < 9; i++) {
			JPanel panel = new JPanel(new FlowLayout());
			for (int j = 0; j < i; j++) {
				panel.add(info.get(k).btn);
				k++;
			}
			panel.setBackground(blue);
			pyramidpanel.add(panel);
		}

		// (고침)~
		buttonpanel.setLayout(null);  // 배치관리자 제거
		buttonpanel.setPreferredSize(new Dimension(70, 110));  // 버튼패널 사이즈 조정
		buttonpanel.add(distribute);
		buttonpanel.add(turnskip);
		buttonpanel.add(gameset);
		buttonpanel.add(distribute_text);  // 텍스트 추가
		buttonpanel.add(turnskip_text);
		buttonpanel.add(gameset_text);
		//
		
		distribute.addActionListener(new DistributeListener());
		turnskip.addActionListener(new TurnSkipListener());
		gameset.addActionListener(new QuitListener());

		// (고침) 스코어패널 위치조정 ~
		scorepanel.setPreferredSize(new Dimension(50, 50));
		scorepanel.setBounds(40, 10, 290, 200);
		animalpanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//
		
		totalpanel.add(buttonpanel);
		buttonpanel.add(scorepanel);  // (고침) 스코어패널을 버튼패널에 추가시킴
		totalpanel.add(pyramidpanel);
		totalpanel.add(animalpanel);		
		
		scorepanel.setBackground(brown);  // (고침) 색깔 바꿈
		pyramidpanel.setBackground(blue);
		animalpanel.setBackground(brown);
		buttonpanel.setBackground(brown);
		totalpanel.setBackground(blue);
		
		add(totalpanel);

		
	}
	

	// 말 배분 버튼의 리스너
	public class DistributeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!startCheck) {
				try {
					startCheck = true;
					writer.writeObject(new GameChat(GameChat.MsgType.GAME_START));
					writer.flush();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "메시지 전송중 문제가 발생하였습니다.");
					e1.printStackTrace();
				}
			} else
				JOptionPane.showMessageDialog(null, "게임이 진행중입니다.");
		}
	}
	
	// 턴 넘김 버튼의 리스너
	public class TurnSkipListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {			// 피라미드 버튼과 자신의 버튼을 선택하지 않았으므로, 0 인덱스와 동물번호 -1을 전송
				writer.writeObject(new GameChat(GameChat.MsgType.GAME_INFO, 0, -1, MyScore(), MY.getText(), MyTurnNumber, turnAble));
				writer.flush();
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "메시지 전송중 문제가 발생하였습니다.");
				e1.printStackTrace();
			}
		}
		
	}
	
	// 자신의 말 버튼에 대한 리스너
	class ChooseListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (turnCheck) {
				for (int i = 0; i < myAnimal.size(); i++) {
					if (e.getSource() == myAnimal.get(i)) {
						x = i;
					}
				}
				choose = myAnimalNum.get(x);
				//추가
				selected = true;
				
			} else
				JOptionPane.showMessageDialog(null, "상대방 차례입니다.");
		}

	}
	
	public class Buttonaction implements Runnable {
		int btn_info;
		int temp_choose;
		Buttonaction(int btn_info, int choose){
			this.btn_info = btn_info;
			this.temp_choose = choose;
		}
		@Override
		public void run() {
			try {
				temp_choose = choose;
				if (temp_choose != -1) {
					for(int k =0; k< 4; k++) {
						info.get(temp_choose).btn.setIcon(myanimals.get((temp_choose*4)+k));
						Thread.sleep(200);
					}
				}
				
			}
			catch(InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	//피라미드 버튼들에 대한 리스너
	class ButtonListener implements ActionListener {
		
		private int i;
		@Override
		public void actionPerformed(ActionEvent e) {
			if (choose >= 0) {
				soundplay3();
				selected = false;
				canput = true;
				try {
					// 눌린 버튼의 인덱스를 알아낸다
					for (int j = 0; j < info.size(); j++) {
						if (e.getSource() == info.get(j).btn)
							i = j;
					}
					
					if (info.get(i).animalexist == true) {
						canput = false;
						throw new Exception();
					}
					// 맨 밑의 층의 버튼이 아닐때
					if (info.get(i).height != 8) {
						// 밑의 두 버튼에 말이 놓여있는지를 검사해서
						// 두 버튼에 말이 다 놓여있는게 아니라면 말을 놓을 수 없다고 메시지 출력
						if (!(info.get(i + info.get(i).height).animalexist && info.get(i + 1 + info.get(i).height).animalexist))
							throw new Exception();
						// 밑의 두 버튼에 다 말이 놓여져 있다면,
						else {
							// 호랑이가 있는지를 검사,
							// 호랑이가 있다면 말을 놓을수 없다고 메시지 출력
							if (info.get(i + info.get(i).height).animalnumber == 5
									|| info.get(i + 1 + info.get(i).height).animalnumber == 5) {
								throw new Exception();
							}
							// 호랑이가 없고 밑의 두 블록 중에서 적어도 하나의 블록이 같은 이미지여야 한다.
							else {
								if (choose == 5) {
									myAnimal.get(x).setEnabled(false);
									writer.writeObject(new GameChat(GameChat.MsgType.GAME_INFO, i, choose, MyScore(), MY.getText(), MyTurnNumber, turnAble));
									writer.flush();
									choose = -1;
								} else if (choose == info.get(i + info.get(i).height).animalnumber
										|| choose == info.get(i + 1 + info.get(i).height).animalnumber) {
									// 피라미드에서 누른 버튼의 인덱스, 자신이 선택한 동물, 점수, 자신의 이름, 자신의 턴 넘버를 전송.
									myAnimal.get(x).setEnabled(false);
									writer.writeObject(new GameChat(GameChat.MsgType.GAME_INFO, i, choose, MyScore(), MY.getText(), MyTurnNumber, turnAble));
									writer.flush();
									choose = -1;
								}
								// 두 블록 모두 선택한 이미지와 다르다면 놓을 수 없다는 메시지 출력
								else
									JOptionPane.showMessageDialog(null, "놓을 수 있는 종류의 말이 아닙니다.");
							}
						}
					} else {
						if(canput == true) {

							// 맨 밑의 층은 바로 말을 놓을 수 있다.
							myAnimal.get(x).setEnabled(false);
																		// MsgType,  인덱스, 동물번호 , 점수(int형),	 나의 이름     , 나의 턴 번호(int형), 진행 가능 여부
							writer.writeObject(new GameChat(GameChat.MsgType.GAME_INFO, i, choose , MyScore(), MY.getText(), MyTurnNumber, turnAble));
							writer.flush();
							choose = -1;
						}
					}
				} 
				catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "그곳에는 말을 놓을 수 없습니다.");
					//ex.printStackTrace();
				}

			} else
				JOptionPane.showMessageDialog(null, "놓을 말을 선택하십시오");
		}
	}
	
	// 게임 종료 버튼의 리스너
	class QuitListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
				System.exit(0);
		}
	}
	
	public void gameSet(String my, String you1, String you2) {
		MY.setText(my);
		user2.setText(you1);
		user3.setText(you2);
		for (JButton btn : myAnimal) {
			btn.setEnabled(true);
		}
	}
	
	// 모든 플레이어가 더 이상 말을 놓을 수 없는 상태인지를 확인한다. 모든 플레이어가 더 이상 말을 놓을 수 없는 상태라면  승자를 알려주는 메시지를 출력한다.
	public void CheckGameEnd(String sender, boolean enable) {
		if(sender.equals(user2.getText()))
			B_turnAble = enable;
		else if(sender.equals(user3.getText()))
			C_turnAble = enable;
		
		if(!turnAble && !B_turnAble && !C_turnAble) {
			int [] score = new int[3];
			score[0] = Integer.parseInt(A_Count.getText());
			score[1] = Integer.parseInt(B_Count.getText());
			score[2] = Integer.parseInt(C_Count.getText());
			for(int i = 0; i<3; i++) {
				for(int j = i+1; j<3; j++) {
					if(score[i]<score[j]) {
						int n = score[i];
						score[i] = score[j];
						score[j] = n;
					}
				}
			}
			String winner = " , ";
			if(score[0] == Integer.parseInt(A_Count.getText()))
				winner = winner + MY.getText();
			if(score[0] == Integer.parseInt(B_Count.getText()))
				winner = winner + user2.getText();
			if(score[0] == Integer.parseInt(C_Count.getText()))
				winner = winner + user3.getText();
			JOptionPane.showMessageDialog(null, ("더 이상 놓을 수 있는 말이 없어 게임이 종료되었습니다. 승자는 " + score[0] + "점의 " + winner +" 입니다."));
		}
	}
	
	// 나의 점수를 계산하는 함수
	public int MyScore() {
		int score = 0;
		for(JButton btn : myAnimal) {
			if(!btn.isEnabled())
				score++;
		}
		return score;
	}
	
	// 상대방의 점수를 갱신하여 표시하는 함수
	public void ScoreSet(int score, String sender) {
		if(sender.equals(MY.getText()))
			A_Count.setText(Integer.toString(score*10));
		else if(sender.equals(user2.getText()))
			B_Count.setText(Integer.toString(score*10));
		else if(sender.equals(user3.getText()))
			C_Count.setText(Integer.toString(score*10));
	}
	
	// 누구의 차례인지 보여주는 함수
	public void ShowWhosTurn(String thisTurnUser) {
		if(thisTurnUser.equals(MY.getText()))
			turn.setText(MY.getText());
		else if(thisTurnUser.equals(user2.getText()))
			turn.setText(user2.getText());
		else if(thisTurnUser.equals(user3.getText()))
			turn.setText(user3.getText());
	}
	
	// 내 차례인지를 계산하는 함수
	public void CheckMyTurn(int i) {
		if(MyTurnNumber == i)
			turnCheck = true;
		else
			turnCheck = false;
	}

	
	public void initializeGame() {
		for(ButtonInfo b : info) {
			b.btn.setIcon(null);
		}
		for(JButton btn : myAnimal) {
			btn.setIcon(null);
		}
		 distribute.setEnabled(false);
	}
	
	// 나에게 주어진 동물식별번호들로 각 번호에 맞는 동물 이미지 세팅
	public void setAnimals(ArrayList<Integer> AnimalNum) {
		myAnimalNum = AnimalNum;
		for(int i = 0; i<myAnimal.size(); i++) {
			switch(myAnimalNum.get(i)) {			
			case 0:
				myAnimal.get(i).setIcon(slime4);
				break;
			case 1:
				myAnimal.get(i).setIcon(mushroom4);					
				break;
			case 2:
				myAnimal.get(i).setIcon(pinkbean4);				
				break;
			case 3:
				myAnimal.get(i).setIcon(penguin4);			
				break;
			case 4:
				myAnimal.get(i).setIcon(octopus4);				
				break;
			case 5:
				myAnimal.get(i).setIcon(balrog4);			
				break;
			}
		}
	}
	
	// 나의 턴 번호 세팅
	public void setMyTurnNumber(int number) {
		MyTurnNumber = number;
	}

	// 게임이 시작된 상태인지 아닌지를 나타내는 startCheck 변수 세팅
	public void setStartCheck(boolean b) {
		startCheck = b;
	}

	public void soundplay2() { // 사운드 플레이하는 함수
		File file1 = new File("sound/button2.wav");
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
	
	public void soundplay3() { // 사운드 플레이하는 함수
		File file1 = new File("sound/button3.wav");
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
	
	// 피라미드 버튼에 동물이 놓였을 때 버튼의 이미지 세팅과 ButtonInfo객체의 정보 최신화.
	// 추가적으로 동물
	public void BoardSet(int i, int number) {
		if (number != -1) {
			for(int k =0; k< 4; k++) {
				info.get(i).btn.setIcon(myanimals.get((number*4)+k));
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(int k=2; k == 0; k--) {
				info.get(i).btn.setIcon(myanimals.get((number*4)+k));
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		info.get(i).animalexist = true;
		info.get(i).animalnumber = number;
		if(number == 5) {
			info.get(i).canput = false;
		}
		
	}
	
	// 나의 차례에 게임판에 놓을 수 있는 동물이 있는지를 체크하는 함수
	public void CheckMyAnimals(int turnNum) {
		int count = 0;
		for (int k = 28; k < info.size(); k++) {
			if (!info.get(k).animalexist)
				count++;
		}
		for(int i = 35; i>=0; i--) { // 모든 버튼에 대해 조사해서,
			if(!info.get(i).animalexist) { // 동물이 놓여있지 않은 버튼이라면(동물이 놓여있는 버튼은 패스), 
				if(info.get(i).canput) { // 그 버튼이 동물을 놓을 수 있는 버튼이라면(동물을 놓을 수 없는 버튼은 패스),
					 for(int j=0; j<myAnimal.size(); j++) { // 나의 버튼들을 조사해서,
						 if(myAnimal.get(j).isEnabled()) { // 아직 놓지 않은 버튼중에서 게임판에 놓을 수 있는 버튼이 있는지를 검사해서 있다면 count를 1 중가시킴.
							 if(info.get(i).canput_number[0] == myAnimalNum.get(j) || info.get(i).canput_number[1] == myAnimalNum.get(j))
								 count++;
						 }
					 }
				}
			}
		}
		if(count == 0) // 놓을 수 있는 버튼이 하나도 없다면,
			turnAble = false;
		else			// 아직 놓을 수 있는 버튼이 1개라도 있다면,
			turnAble = true;
		
		if(!turnAble && MyTurnNumber == turnNum) // 나의 차례인데, 놓을 수 있는 버튼이 없다면 턴넘김 버튼 누르라는 알림 출력
			JOptionPane.showMessageDialog(null, "놓을 수 있는 동물이 없습니다. \"턴 넘김\" 버튼을 눌러 턴을 넘겨주세요.");
	}
	
	// 피라미드 버튼들의 canput 변수 재설정 함수.
	public void ChangeButtonInformation() {
		for (int i = 27; i >= 0; i--) {
			if (info.get(i + info.get(i).height).canput && info.get(i + 1 + info.get(i).height).canput) {
				if (info.get(i).animalnumber == 5)
					info.get(i).canput = false;
				else {
					if (info.get(i + info.get(i).height).animalexist
							&& info.get(i + 1 + info.get(i).height).animalexist) {
						info.get(i).canput = true;
						info.get(i).canput_number[0] = info.get(i + info.get(i).height).animalnumber;
						info.get(i).canput_number[1] = info.get(i + 1 + info.get(i).height).animalnumber;
					} else
						info.get(i).canput = false;
				}
			} else
				info.get(i).canput = false;
		}
	}
	
}
