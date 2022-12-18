//import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.*;

//import javax.swing.*;


public class GameChat implements Serializable {
	// �޽��� Ÿ�� ����
		// 1���� �޽��� ���� �ʵ�� 3���� String�� �ʵ�.
		// NO_ACT�� ������ �� �ִ� Dummy �޽���. ������ ������ ����ϱ� ���� ����� ����
		// (1) Ŭ���̾�Ʈ�� ������ �޽��� ����
		//	- LOGIN  : CLIENT �α���.
		//		�޽��� ���� : LOGIN, "�۽���", "", ""
		//	- LOGOUT : CLIENT �α׾ƿ�.
		//		�޽��� ���� : LOGOUT, "�۽���", "", ""
		// 	- CLIENT_MSG : �������� ������  ��ȭ .
		// 		�޽�������  : CLIENT_MSG, "�۽���", "������", "����"
		// (2) ������ ������ �޽��� ����
		// 	- LOGIN_FAILURE  : �α��� ����
		//		�޽��� ���� : LOGIN_FAILURE, "", "", "�α��� ���� ����"
		// 	- SERVER_MSG : Ŭ���̾�Ʈ���� �������� ������ ��ȭ 
		//		�޽�������  : SERVER_MSG, "�۽���", "", "����" 
		// 	- LOGIN_LIST : ���� �α����� ����� ����Ʈ.
		//		�޽��� ���� : LOGIN_LIST, "", "", "/�� ���е� ����� ����Ʈ"
		public enum MsgType {NO_ACT, LOGIN, LOGOUT, CLIENT_MSG, LOGIN_FAILURE, SERVER_MSG, LOGIN_LIST, GAME_INFO, GAME_START, NOT_ENOUGH_USER};
		public static final String ALL = "��ü";	 // ����� �� �� �ڽ��� ������ ��� �α��εǾ� �ִ�
												 // ����ڸ� ��Ÿ���� �ĺ���
		private MsgType type;
		private String sender;
		private String receiver;
		private String contents;
		
		public ArrayList<Integer> icon = new ArrayList<Integer>();
		public int animalNum; 		// ���� �ĺ� ��ȣ
		public int i,score, turnNum, firstTurnNum;// i=�Ƕ�̵� ��ư �ε��� , score = ���� , turnNum = �� ���� , firstTurnNum = ù��° ������ �� ����
		public String NextUser;		// ���� ���ʿ� ������ ���� �̸�
		public boolean userCheck;	// �ڱ� �������� Ȯ���ϴ� ����
		public boolean turnAble;	// false = ���� ���� �Ұ�, true = ���� ���� ����
		
		public GameChat() {
			this(MsgType.NO_ACT, "", "");
		}
		
		public GameChat(MsgType t){
			type = t;
		}
		
		public GameChat(MsgType t, String sID, String mesg) {
			type = t;
			sender = sID;
			contents = mesg;
		}
		
		public GameChat(MsgType t, int turnNum, int firstTurnNum){
			type = t;
			this.turnNum = turnNum;
			this.firstTurnNum = firstTurnNum;
		}
		public GameChat(MsgType t, int i, int animalNum, int score, String sender, int turnNum, boolean turnAble) {
			type = t;
			this.i = i;
			this.animalNum = animalNum;
			this.score = score;
			this.sender = sender;
			this.turnNum = turnNum;
			this.turnAble = turnAble;
		}
		
		public GameChat(MsgType t, int i, int animalNum, int score, String sender, int turnNum, String NextUser, boolean turnAble) {
			type = t;
			this.i = i;
			this.animalNum = animalNum;
			this.score = score;
			this.sender = sender;
			this.turnNum = turnNum;
			this.NextUser = NextUser;
			this.turnAble = turnAble;
		}
		
		public GameChat(MsgType t, ArrayList<Integer> icon, int turnNum, int firstTurnNum, String NextUser){
			type = t;
			this.icon = icon;
			this.turnNum = turnNum;
			this.firstTurnNum = firstTurnNum;
			this.NextUser = NextUser;
		}
		
		public void setType (MsgType t) {
			type = t;
		}
		public MsgType getType() {
			return type;
		}

		public void setSender (String id) {
			sender = id;
		}
		public String getSender() {
			return sender;
		}
		
		public void setReceiver (String id) {
			receiver = id;
		}
		public String getReceiver() {
			return receiver;
		}
		
		public void setContents (String mesg) {
			contents = mesg;
		}
		public String getContents() {
			return contents;
		}
		
		public String toString() {
			return ("�޽��� ���� : " + type + "\n" +
					"�۽���         : " + sender + "\n" +
					"������         : " + receiver + "\n" +
					"�޽��� ���� : " + contents + "\n");
		}
}
