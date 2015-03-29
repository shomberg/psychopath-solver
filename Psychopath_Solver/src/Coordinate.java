
public class Coordinate{
	public int x, y;
	public Coordinate(int xIn, int yIn){
		x=xIn;
		y=yIn;
	}

	public boolean equals(Object o){
		if(!(o instanceof Coordinate))
			return false;
		Coordinate c = (Coordinate)o;
		return (x==c.x && y==c.y);
	}
	
	public String toString(){
		return "("+x+","+y+")";
	}
}