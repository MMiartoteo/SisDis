package roundword;


public class Player {

	String nickName;
	
	int points;
	
	//IP Address, or other information to contact him
	
	public Player(String nickname) {
		this.nickName = nickname;
		points = 0;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public int getPoints() {
		return points;
	}
	
	@Override
	public String toString() {
		return nickName;
	}
	
}
