//import java.awt.*;
import javax.swing.*;

public class ButtonInfo {
	
	int height;									// ��ư�� ���� ����
	int animalnumber;							// ���� ��ȣ
	boolean animalexist; 						// ��ư�� ���� �������ִ°��� ��Ÿ��. true = ���� ���� , false = ���� ���� 
	boolean canput;								// �� ��ư�� ���� ���� �� �ִ°��� ��Ÿ��. true = ���� ���� �� ����, false = ���� ���� �� ����.
	int [] canput_number = new int[2];			// ��ư�� ���� �� �ִ� ������ ��ȣ. canput_number[0]�� ���� ���ʹ�ư�� ����ִ� ���� ��ȣ.
												// 							canput_number[1]�� ���� �����ʹ�ư�� ����ִ� ���� ��ȣ.
	JButton btn;
	
	public ButtonInfo(int height, int animalnumber, boolean animalexist, boolean canput) {
		this.height = height;
		this.animalnumber = animalnumber;
		this.animalexist = animalexist;
		this.canput = canput;
		this.btn = new JButton();
	}
}
