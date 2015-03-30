import java.util.ArrayList;

public class SearchNode {
	Psychopath_Solver.Action action;
	State state;
	SearchNode parent;
	int actionCost;
	int heuristicCost;
	int cost;
	
	public SearchNode(Psychopath_Solver.Action a, State s, SearchNode p, int aCost, int hCost){
		action = a;
		state = s;
		parent = p;
		actionCost = aCost;
		heuristicCost = hCost;
		if(parent != null){
			cost = aCost + parent.cost;
		} else{
			cost = aCost;
		}
	}
	
	public ArrayList<Coordinate> getPath(){
		ArrayList<Coordinate> parentPath;
		if(parent==null){
			parentPath = new ArrayList<Coordinate>();
		}else{
			parentPath = parent.getPath();
		}
		parentPath.add(state.current);
		return parentPath;
	}
}
