import java.util.Arrays;

public class State {
	public boolean[][] pinks;
	public Coordinate current;
	
	public State(Coordinate c, boolean[][] p){
		pinks = p;
		current = c;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((current == null) ? 0 : current.hashCode());
		result = prime * result + Arrays.deepHashCode(pinks);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (current == null) {
			if (other.current != null)
				return false;
		} else if (!current.equals(other.current))
			return false;
		if (!Arrays.deepEquals(pinks, other.pinks))
			return false;
		return true;
	}
}
