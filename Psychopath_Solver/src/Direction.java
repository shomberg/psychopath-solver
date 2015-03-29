import java.awt.event.KeyEvent;

public class Direction {
	public int dir;
	public Direction(Coordinate start, Coordinate end) throws Exception{
		int xOff = end.x-start.x;
		int yOff = end.y-start.y;
		if((xOff==0 && yOff==0)||(xOff!=0 && yOff!=0)){
			throw new Exception("coordinates " + start + " and " + end + " not adjacent");
		}
		if(yOff==-1){
			dir=0; //Up
		}
		if(xOff==1){
			dir = 1; //Right
		}
		if(yOff==1){
			dir=2; //Down
		}
		if(xOff==-1){
			dir=3; //Left
		}
	}
	
	public String toString(){
		String ret;
		switch(dir){
		case 0: ret="Up"; break;
		case 1: ret="Right"; break;
		case 2: ret="Down"; break;
		case 3: ret="Left"; break;
		default: ret="Undefined";
		}
		return ret;
	}
	
	public int getKeycode(){
		int ret=KeyEvent.VK_R;
		switch(dir){
		case 0: ret=KeyEvent.VK_UP; break;
		case 1: ret=KeyEvent.VK_RIGHT; break;
		case 2: ret=KeyEvent.VK_DOWN; break;
		case 3: ret=KeyEvent.VK_LEFT; break;
		}
		return ret;
	}
}
