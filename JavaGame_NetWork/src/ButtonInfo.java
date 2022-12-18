//import java.awt.*;
import javax.swing.*;

public class ButtonInfo {
	
	int height;									// 버튼의 높이 정보
	int animalnumber;							// 동물 번호
	boolean animalexist; 						// 버튼에 말이 놓여져있는가를 나타냄. true = 말이 있음 , false = 말이 없음 
	boolean canput;								// 이 버튼에 말을 놓을 수 있는가를 나타냄. true = 말을 놓을 수 있음, false = 말을 놓을 수 없음.
	int [] canput_number = new int[2];			// 버튼에 놓을 수 있는 동물의 번호. canput_number[0]은 밑의 왼쪽버튼에 들어있는 동물 번호.
												// 							canput_number[1]은 밑의 오룬쪽버튼에 들어있는 동물 번호.
	JButton btn;
	
	public ButtonInfo(int height, int animalnumber, boolean animalexist, boolean canput) {
		this.height = height;
		this.animalnumber = animalnumber;
		this.animalexist = animalexist;
		this.canput = canput;
		this.btn = new JButton();
	}
}
