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
	private JPanel scorepanel; // �����г�
	private JPanel pyramidpanel; // ������ ����Ǵ� �г�
	private JPanel animalpanel; // �ڽ��� ���� �ִ� �г�
	private JPanel buttonpanel; // �� ���, �� �ѱ�, ���� ���� ��ư�� �ִ� �г�

	private JLabel MY, user2, user3; // �÷��̾���� �̸��� ��Ÿ���� JLabel
	private JLabel A_Count, B_Count, C_Count; // �÷��̾���� ������ ��Ÿ���� JLabel
	private JLabel turn;
	private JButton distribute, turnskip, gameset; // �� ���, �� �ѱ�, ���� ���� ��ư
	private JTextField distribute_text, turnskip_text, gameset_text;
	private boolean startCheck = false;		// ������ ���۵Ǿ������� ����. false = ������ ���۵��� ���� ����, true = ������ ���۵� ����
	private boolean turnCheck = false;		// �ڽ��� ���������� ���� ����. false = �ڽ��� ���� �ƴ�, true = �ڽ��� ����
	private int MyTurnNumber;	// ���� �� ��ȣ.
	private boolean turnAble = true;		// �ڽ��� �Ͽ� ���� ���� �� �ִ��� �������� ��Ÿ��.
	private boolean B_turnAble = true;		// B�� ���� ���� �� �ִ��� �������� ��Ÿ��.
	private boolean C_turnAble = true;		// C�� ���� ���� �� �ִ��� �������� ��Ÿ��.
	// (��ħ)��Ʈ�߰�
	Font font = new Font("�޸�����ü", Font.BOLD, 15);
	Font treefont = new Font("HY������B", Font.BOLD, 13);
	
	private ArrayList<JButton> myAnimal; 		// �ڽ��� ������ �ִ� ���鿡 ���� 12���ǹ�ư�� ��ҷ� ������ ArrayList
	private ArrayList<Integer> myAnimalNum;		// ������ �־��� �����ĺ���ȣ�� ��ҷ� ������ ArrayList
	
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
	// (��ħ)�̹��� �߰�
	private ImageIcon place = new ImageIcon("image/place.png");
	private ImageIcon turnpass = new ImageIcon("image/turnpass.png");
	private ImageIcon out = new ImageIcon("image/out.png");
	private ImageIcon turnoff = new ImageIcon("image/turnoff.png");
	private ImageIcon turnon = new ImageIcon("image/turnon.png");
	
	private ArrayList<ImageIcon> myanimals = new ArrayList<ImageIcon>();
		
	boolean selected = false;		
	boolean canput = true;
	
	public int choose = -1;	// �ڽ��� �����ǿ� ���� ���� ���� ������ �����ĺ���ȣ
	private int touched = 0;

	ObjectInputStream reader;
	ObjectOutputStream writer;

	Color sand = new Color(200, 200, 150);  // �縷��
	Color brown = new Color(97, 90, 64);  // ����
	Color blue = new Color(25, 25, 112);  // �Ķ���
	Color blue2 = new Color(50,50,150);  // �Ķ���
	Color darkbrown = new Color(68, 48, 23);  // £�� ����
	Color gray = new Color(128, 128, 128);  // £�� ����
	
	ArrayList<ButtonInfo> info;
	
	private int k = 0;
	private int x;		// ���� ��ư�� �߿��� ������ ��ư�� �ε����� �����ϴ� ����.

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
		turn = new JLabel("����");

		// (��ħ) ��ư �ؽ�Ʈ ����
		distribute = new JButton();
		turnskip = new JButton();
		gameset = new JButton();
		
		// (��ħ) �߰��� �͵� ~
		// ��ư ����(���й�, �ϳѱ��, ��������) ũ��, ��ġ
		distribute.setBounds(350, 16, 50, 50);
		turnskip.setBounds(450, 16, 50, 50);
		gameset.setBounds(550, 16, 50, 50);
		// ��ư �����ϰ�
		distribute.setBorderPainted(false);
		distribute.setContentAreaFilled(false);
		distribute.setOpaque(false);
		turnskip.setBorderPainted(false);
		turnskip.setContentAreaFilled(false);
		turnskip.setOpaque(false);
		gameset.setBorderPainted(false);
		gameset.setContentAreaFilled(false);
		gameset.setOpaque(false);	
		// ��ư�� �̹��� �ֱ�
		distribute.setIcon(place);
		turnskip.setIcon(turnpass);
		gameset.setIcon(out);	
		
		// ��ư �ؿ� �ؽ�Ʈ �ֱ�
		distribute_text = new JTextField("�� �й�");
		turnskip_text = new JTextField("�� �ѱ��");
		gameset_text = new JTextField("��������");
		distribute_text.setBounds(351, 68, 60, 30);
		turnskip_text.setBounds(443, 68, 60, 30);
		gameset_text.setBounds(546, 68, 60, 30);
		// �ؽ�Ʈ�� ��� �����ϰ�
		distribute_text.setOpaque(false);
		turnskip_text.setOpaque(false);
		gameset_text.setOpaque(false);
		distribute_text.setBorder(null);
		turnskip_text.setBorder(null);
		gameset_text.setBorder(null);
		// �ؽ�Ʈ ��Ʈ����
		distribute_text.setFont(treefont);
		turnskip_text.setFont(treefont);
		gameset_text.setFont(treefont);
		// �ؽ�Ʈ ���� ����
		distribute_text.setForeground(Color.WHITE);
		turnskip_text.setForeground(Color.WHITE);
		gameset_text.setForeground(Color.WHITE);
		//
		
		info = new ArrayList<>();

		// (��ħ)��ġ ����~
		MY.setBounds(0, 0, 100, 30);
		A_Count.setBounds(90, 0, 30, 30);
		user2.setBounds(0, 30, 100, 30);
		B_Count.setBounds(90, 30, 30, 30);
		user3.setBounds(0, 60, 100, 30);
		C_Count.setBounds(90, 60, 30, 30);
		turnlabel.setBounds(180, 15, 50, 30);
		turn.setBounds(180, 45, 40, 30);			
		
		// ��¥ ���
		MY.setForeground(Color.WHITE);
		A_Count.setForeground(Color.WHITE);
		user2.setForeground(Color.WHITE);
		B_Count.setForeground(Color.WHITE);
		user3.setForeground(Color.WHITE);
		C_Count.setForeground(Color.WHITE);
		turnlabel.setForeground(Color.WHITE);
		turn.setForeground(Color.WHITE);
		// ��Ʈ ����
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
		// scorepanel.setBackground(sand); ���ֵ���
		myAnimalNum = new ArrayList<Integer>();
		
		//�߰��� ����
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
		// �ڽ��� ���� �����ִ� ��ư. ��ư�鿡�� ������ ����
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
							//System.out.println(tmp + "�̹����� ����");
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
		
		// animalpanel�� ��ư ����. ó������ ��Ȱ��ȭ
		for (JButton btn : myAnimal) {
			btn.setPreferredSize(new Dimension(50, 52));
			btn.setEnabled(false);
			animalpanel.add(btn);
		}

		// �Ƕ�̵� ��ư ��ü���� ����
		for (int i = 1; i < 9; i++) {
			for (int j = 0; j < i; j++) {
				info.add(new ButtonInfo(i, -1, false, true));
			}
		}

		// �Ƕ�̵� ��ư�鿡�� �����ʸ� �޾��ش�
		for (int i = 0; i < info.size(); i++) {
			info.get(i).btn.setPreferredSize(new Dimension(50, 52));
			info.get(i).btn.setBorder(BorderFactory.createLoweredBevelBorder());
			info.get(i).btn.addActionListener(new ButtonListener());
			info.get(i).btn.setFocusPainted(false);
			info.get(i).btn.setBackground(blue2);
		}

		// �Ƕ�̵� ��ư���� �гο� ����
		for (int i = 1; i < 9; i++) {
			JPanel panel = new JPanel(new FlowLayout());
			for (int j = 0; j < i; j++) {
				panel.add(info.get(k).btn);
				k++;
			}
			panel.setBackground(blue);
			pyramidpanel.add(panel);
		}

		// (��ħ)~
		buttonpanel.setLayout(null);  // ��ġ������ ����
		buttonpanel.setPreferredSize(new Dimension(70, 110));  // ��ư�г� ������ ����
		buttonpanel.add(distribute);
		buttonpanel.add(turnskip);
		buttonpanel.add(gameset);
		buttonpanel.add(distribute_text);  // �ؽ�Ʈ �߰�
		buttonpanel.add(turnskip_text);
		buttonpanel.add(gameset_text);
		//
		
		distribute.addActionListener(new DistributeListener());
		turnskip.addActionListener(new TurnSkipListener());
		gameset.addActionListener(new QuitListener());

		// (��ħ) ���ھ��г� ��ġ���� ~
		scorepanel.setPreferredSize(new Dimension(50, 50));
		scorepanel.setBounds(40, 10, 290, 200);
		animalpanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		//
		
		totalpanel.add(buttonpanel);
		buttonpanel.add(scorepanel);  // (��ħ) ���ھ��г��� ��ư�гο� �߰���Ŵ
		totalpanel.add(pyramidpanel);
		totalpanel.add(animalpanel);		
		
		scorepanel.setBackground(brown);  // (��ħ) ���� �ٲ�
		pyramidpanel.setBackground(blue);
		animalpanel.setBackground(brown);
		buttonpanel.setBackground(brown);
		totalpanel.setBackground(blue);
		
		add(totalpanel);

		
	}
	

	// �� ��� ��ư�� ������
	public class DistributeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!startCheck) {
				try {
					startCheck = true;
					writer.writeObject(new GameChat(GameChat.MsgType.GAME_START));
					writer.flush();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "�޽��� ������ ������ �߻��Ͽ����ϴ�.");
					e1.printStackTrace();
				}
			} else
				JOptionPane.showMessageDialog(null, "������ �������Դϴ�.");
		}
	}
	
	// �� �ѱ� ��ư�� ������
	public class TurnSkipListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {			// �Ƕ�̵� ��ư�� �ڽ��� ��ư�� �������� �ʾ����Ƿ�, 0 �ε����� ������ȣ -1�� ����
				writer.writeObject(new GameChat(GameChat.MsgType.GAME_INFO, 0, -1, MyScore(), MY.getText(), MyTurnNumber, turnAble));
				writer.flush();
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "�޽��� ������ ������ �߻��Ͽ����ϴ�.");
				e1.printStackTrace();
			}
		}
		
	}
	
	// �ڽ��� �� ��ư�� ���� ������
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
				//�߰�
				selected = true;
				
			} else
				JOptionPane.showMessageDialog(null, "���� �����Դϴ�.");
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
	//�Ƕ�̵� ��ư�鿡 ���� ������
	class ButtonListener implements ActionListener {
		
		private int i;
		@Override
		public void actionPerformed(ActionEvent e) {
			if (choose >= 0) {
				soundplay3();
				selected = false;
				canput = true;
				try {
					// ���� ��ư�� �ε����� �˾Ƴ���
					for (int j = 0; j < info.size(); j++) {
						if (e.getSource() == info.get(j).btn)
							i = j;
					}
					
					if (info.get(i).animalexist == true) {
						canput = false;
						throw new Exception();
					}
					// �� ���� ���� ��ư�� �ƴҶ�
					if (info.get(i).height != 8) {
						// ���� �� ��ư�� ���� �����ִ����� �˻��ؼ�
						// �� ��ư�� ���� �� �����ִ°� �ƴ϶�� ���� ���� �� ���ٰ� �޽��� ���
						if (!(info.get(i + info.get(i).height).animalexist && info.get(i + 1 + info.get(i).height).animalexist))
							throw new Exception();
						// ���� �� ��ư�� �� ���� ������ �ִٸ�,
						else {
							// ȣ���̰� �ִ����� �˻�,
							// ȣ���̰� �ִٸ� ���� ������ ���ٰ� �޽��� ���
							if (info.get(i + info.get(i).height).animalnumber == 5
									|| info.get(i + 1 + info.get(i).height).animalnumber == 5) {
								throw new Exception();
							}
							// ȣ���̰� ���� ���� �� ��� �߿��� ��� �ϳ��� ����� ���� �̹������� �Ѵ�.
							else {
								if (choose == 5) {
									myAnimal.get(x).setEnabled(false);
									writer.writeObject(new GameChat(GameChat.MsgType.GAME_INFO, i, choose, MyScore(), MY.getText(), MyTurnNumber, turnAble));
									writer.flush();
									choose = -1;
								} else if (choose == info.get(i + info.get(i).height).animalnumber
										|| choose == info.get(i + 1 + info.get(i).height).animalnumber) {
									// �Ƕ�̵忡�� ���� ��ư�� �ε���, �ڽ��� ������ ����, ����, �ڽ��� �̸�, �ڽ��� �� �ѹ��� ����.
									myAnimal.get(x).setEnabled(false);
									writer.writeObject(new GameChat(GameChat.MsgType.GAME_INFO, i, choose, MyScore(), MY.getText(), MyTurnNumber, turnAble));
									writer.flush();
									choose = -1;
								}
								// �� ��� ��� ������ �̹����� �ٸ��ٸ� ���� �� ���ٴ� �޽��� ���
								else
									JOptionPane.showMessageDialog(null, "���� �� �ִ� ������ ���� �ƴմϴ�.");
							}
						}
					} else {
						if(canput == true) {

							// �� ���� ���� �ٷ� ���� ���� �� �ִ�.
							myAnimal.get(x).setEnabled(false);
																		// MsgType,  �ε���, ������ȣ , ����(int��),	 ���� �̸�     , ���� �� ��ȣ(int��), ���� ���� ����
							writer.writeObject(new GameChat(GameChat.MsgType.GAME_INFO, i, choose , MyScore(), MY.getText(), MyTurnNumber, turnAble));
							writer.flush();
							choose = -1;
						}
					}
				} 
				catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "�װ����� ���� ���� �� �����ϴ�.");
					//ex.printStackTrace();
				}

			} else
				JOptionPane.showMessageDialog(null, "���� ���� �����Ͻʽÿ�");
		}
	}
	
	// ���� ���� ��ư�� ������
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
	
	// ��� �÷��̾ �� �̻� ���� ���� �� ���� ���������� Ȯ���Ѵ�. ��� �÷��̾ �� �̻� ���� ���� �� ���� ���¶��  ���ڸ� �˷��ִ� �޽����� ����Ѵ�.
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
			JOptionPane.showMessageDialog(null, ("�� �̻� ���� �� �ִ� ���� ���� ������ ����Ǿ����ϴ�. ���ڴ� " + score[0] + "���� " + winner +" �Դϴ�."));
		}
	}
	
	// ���� ������ ����ϴ� �Լ�
	public int MyScore() {
		int score = 0;
		for(JButton btn : myAnimal) {
			if(!btn.isEnabled())
				score++;
		}
		return score;
	}
	
	// ������ ������ �����Ͽ� ǥ���ϴ� �Լ�
	public void ScoreSet(int score, String sender) {
		if(sender.equals(MY.getText()))
			A_Count.setText(Integer.toString(score*10));
		else if(sender.equals(user2.getText()))
			B_Count.setText(Integer.toString(score*10));
		else if(sender.equals(user3.getText()))
			C_Count.setText(Integer.toString(score*10));
	}
	
	// ������ �������� �����ִ� �Լ�
	public void ShowWhosTurn(String thisTurnUser) {
		if(thisTurnUser.equals(MY.getText()))
			turn.setText(MY.getText());
		else if(thisTurnUser.equals(user2.getText()))
			turn.setText(user2.getText());
		else if(thisTurnUser.equals(user3.getText()))
			turn.setText(user3.getText());
	}
	
	// �� ���������� ����ϴ� �Լ�
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
	
	// ������ �־��� �����ĺ���ȣ��� �� ��ȣ�� �´� ���� �̹��� ����
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
	
	// ���� �� ��ȣ ����
	public void setMyTurnNumber(int number) {
		MyTurnNumber = number;
	}

	// ������ ���۵� �������� �ƴ����� ��Ÿ���� startCheck ���� ����
	public void setStartCheck(boolean b) {
		startCheck = b;
	}

	public void soundplay2() { // ���� �÷����ϴ� �Լ�
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
	
	public void soundplay3() { // ���� �÷����ϴ� �Լ�
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
	
	// �Ƕ�̵� ��ư�� ������ ������ �� ��ư�� �̹��� ���ð� ButtonInfo��ü�� ���� �ֽ�ȭ.
	// �߰������� ����
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
	
	// ���� ���ʿ� �����ǿ� ���� �� �ִ� ������ �ִ����� üũ�ϴ� �Լ�
	public void CheckMyAnimals(int turnNum) {
		int count = 0;
		for (int k = 28; k < info.size(); k++) {
			if (!info.get(k).animalexist)
				count++;
		}
		for(int i = 35; i>=0; i--) { // ��� ��ư�� ���� �����ؼ�,
			if(!info.get(i).animalexist) { // ������ �������� ���� ��ư�̶��(������ �����ִ� ��ư�� �н�), 
				if(info.get(i).canput) { // �� ��ư�� ������ ���� �� �ִ� ��ư�̶��(������ ���� �� ���� ��ư�� �н�),
					 for(int j=0; j<myAnimal.size(); j++) { // ���� ��ư���� �����ؼ�,
						 if(myAnimal.get(j).isEnabled()) { // ���� ���� ���� ��ư�߿��� �����ǿ� ���� �� �ִ� ��ư�� �ִ����� �˻��ؼ� �ִٸ� count�� 1 �߰���Ŵ.
							 if(info.get(i).canput_number[0] == myAnimalNum.get(j) || info.get(i).canput_number[1] == myAnimalNum.get(j))
								 count++;
						 }
					 }
				}
			}
		}
		if(count == 0) // ���� �� �ִ� ��ư�� �ϳ��� ���ٸ�,
			turnAble = false;
		else			// ���� ���� �� �ִ� ��ư�� 1���� �ִٸ�,
			turnAble = true;
		
		if(!turnAble && MyTurnNumber == turnNum) // ���� �����ε�, ���� �� �ִ� ��ư�� ���ٸ� �ϳѱ� ��ư ������� �˸� ���
			JOptionPane.showMessageDialog(null, "���� �� �ִ� ������ �����ϴ�. \"�� �ѱ�\" ��ư�� ���� ���� �Ѱ��ּ���.");
	}
	
	// �Ƕ�̵� ��ư���� canput ���� �缳�� �Լ�.
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
