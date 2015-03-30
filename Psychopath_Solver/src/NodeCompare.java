import java.util.Comparator;

public class NodeCompare implements Comparator<SearchNode> {
	double costWeight, heuristicWeight;
	
	public NodeCompare(double cWeight, double hWeight){
		costWeight = cWeight;
		heuristicWeight = hWeight;
	}
	
	public int compare(SearchNode o1, SearchNode o2) {
		double ret = (o1.cost*costWeight+o1.heuristicCost*heuristicWeight)-(o2.cost*costWeight+o2.heuristicCost*heuristicWeight);
		if(ret>0){
			return 1;
		} else if(ret<0){
			return -1;
		} else{
			return 0;
		}
	}

}
