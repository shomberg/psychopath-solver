import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

public class Psychopath_Solver {
	
	static final Color startC = new Color(64,254,64);
	static final Color blankC = new Color(153,204,255);
	static final Color pinkC = new Color(178,178,127);
	static final Color blackC = new Color(76,101,127);
	
	static final int backgroundRedThreshold = 170;
	static final int backgroundGreenThreshold = 210;
	static final int backgroundBlueThreshold = 255;
	
	static boolean doBacktrack;
	static final int topLeftX = 69;
	static final int topLeftY = 276;
	static final int pixelWidth = 355;
	static final int pixelHeight = 355;
	static final int xOffset = 9;
	static final int yOffset = 9;
	
	public enum Action{
		UP, DOWN, LEFT, RIGHT;
	}
	public static int[][] heuristicVals;
	
	public static void main(String[] args) throws AWTException {
		Robot r = new Robot();
		int gridWidth = 0;
		int gridHeight = 0;
		double gridOffset = 0;
		//Identify grid dimensions
		BufferedImage screenCap = r.createScreenCapture(new Rectangle(topLeftX,topLeftY,pixelWidth,pixelHeight));
		int pixelSinceBackground = xOffset;
		boolean isBack = false;
		int lastSwitchBackground = 0;
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		int minOffset = pixelWidth;
		for(int x = 0; x < pixelWidth; x++){
			Color query = new Color(screenCap.getRGB(x, 0));
			boolean isBackNew = isBackground(query);
			if(isBackNew && !isBack){
				offsets.add(pixelSinceBackground);
				minOffset = Math.min(pixelSinceBackground, minOffset);
				pixelSinceBackground = 0;
				lastSwitchBackground = x;
			}
			pixelSinceBackground++;
			isBack = isBackNew;
		}
		for(int i = 0; i < offsets.size(); i++){
			int thisOffset = offsets.get(i);
			if(thisOffset > 1.5*minOffset){
				offsets.set(i, thisOffset-minOffset);
				offsets.add(i,minOffset);
			}
		}
		gridWidth = offsets.size()+1;
		gridOffset = (lastSwitchBackground+xOffset)/(double)offsets.size();
		for(gridHeight=1; gridHeight*gridOffset < pixelHeight && !isBackground(new Color(screenCap.getRGB(0, (int)(gridHeight*gridOffset)))); gridHeight++);
		
		heuristicVals = new int[gridWidth][gridHeight];
//		for(int i = 0; i < gridWidth; i++){
//			for(int j = 0; j < gridHeight; j++){
//				heuristicVals[i][j] = gridWidth*gridHeight;
//			}
//		}
		
		boolean[][] black = new boolean[gridWidth][gridHeight];
		boolean[][] pink = new boolean[gridWidth][gridHeight];
		Coordinate start = new Coordinate(0,0);
		Set<Coordinate> end = new HashSet<Coordinate>();
		delay(500);
		System.out.println("reading board");
		for(int x = 0; x < gridWidth; x++){
			for(int y = 0; y < gridHeight; y++){
				Color c = new Color(screenCap.getRGB((int)(x*gridOffset), (int)(y*gridOffset)));
				if(c.equals(startC)){
					start=new Coordinate(x,y);
				} else if(c.equals(blackC)){
					black[x][y]=true;
				} else if(c.equals(pinkC)){
					pink[x][y]=true;
				} else if(c.equals(blankC)){
					//Do Nothing
				} else{
					end.add(new Coordinate(x,y));
				}
			}
		}

		for(int i = 0; i < gridWidth*gridHeight; i++){
			for(int x = 0; x < gridWidth; x++){
				for(int y = 0; y < gridHeight; y++){
					if(end.contains(new Coordinate(x,y))){
						heuristicVals[x][y]=0;
					}else{
						int hVal = black.length*black[0].length;
						int xOff=1,yOff=0;
						if(inRange(x+xOff,y+yOff,black) && !black[x+xOff][y+yOff]){
							hVal = Math.min(hVal,heuristicVals[x+xOff][y+yOff]);
						}
						xOff = -1;
						if(inRange(x+xOff,y+yOff,black) && !black[x+xOff][y+yOff]){
							hVal = Math.min(hVal,heuristicVals[x+xOff][y+yOff]);
						}
						xOff=0;
						yOff=1;
						if(inRange(x+xOff,y+yOff,black) && !black[x+xOff][y+yOff]){
							hVal = Math.min(hVal,heuristicVals[x+xOff][y+yOff]);
						}
						yOff=-1;
						if(inRange(x+xOff,y+yOff,black) && !black[x+xOff][y+yOff]){
							hVal = Math.min(hVal,heuristicVals[x+xOff][y+yOff]);
						}
					}
				}
			}
		}

		printBoard(black, pink, start, end);
		System.out.println("Beginning search");
		ArrayList<Coordinate> result = findPath(black, pink, start, end, .5);
		System.out.println("Result:");
		System.out.println(result);
		if(result != null){
			try {
				ArrayList<Direction> steps = getSteps(result);
				System.out.println(steps);
				for(Direction d:steps){
					keyType(r, d.getKeycode());
				}
			} catch (Exception e) {
				System.out.println("Error: Path is not continuous");
				System.out.println(e.getMessage());
			}
		}
	}
	

	public static ArrayList<Direction> getSteps(ArrayList<Coordinate> path) throws Exception {
		ArrayList<Direction> ret = new ArrayList<Direction>();
		/*ArrayDeque<Coordinate> copy = path.clone();
		while(copy.size()>=2){
			ret.add(new Direction(copy.removeFirst(),copy.peekFirst()));
		}*/
		for(int i = 0; i < path.size()-1; i++){
			ret.add(new Direction(path.get(i), path.get(i+1)));
		}
		return ret;
	}

	public static int getHeuristic(int x, int y, boolean[][] black, Set<Coordinate> target){
		/*if(heuristicVals[x][y] < black.length*black[0].length){
			return heuristicVals[x][y];
		}
		if(target.contains(new Coordinate(x,y))){
			heuristicVals[x][y] = 0;
			return heuristicVals[x][y];
		}
		int hVal = black.length*black[0].length;
		int xOff=1,yOff=0;
		if(inRange(x+xOff,y+yOff,black) && !black[x+xOff][y+yOff]){
			boolean[][] blackCopy = copy(black);
			blackCopy[x][y] = true;
			hVal = Math.min(hVal,getHeuristic(x+xOff,y+yOff,blackCopy, target));
		}
		xOff = -1;
		if(inRange(x+xOff,y+yOff,black) && !black[x+xOff][y+yOff]){
			boolean[][] blackCopy = copy(black);
			blackCopy[x][y] = true;
			hVal = Math.min(hVal,getHeuristic(x+xOff,y+yOff,blackCopy, target));
		}
		xOff=0;
		yOff=1;
		if(inRange(x+xOff,y+yOff,black) && !black[x+xOff][y+yOff]){
			boolean[][] blackCopy = copy(black);
			blackCopy[x][y] = true;
			hVal = Math.min(hVal,getHeuristic(x+xOff,y+yOff,blackCopy, target));
		}
		yOff=-1;
		if(inRange(x+xOff,y+yOff,black) && !black[x+xOff][y+yOff]){
			boolean[][] blackCopy = copy(black);
			blackCopy[x][y] = true;
			hVal = Math.min(hVal,getHeuristic(x+xOff,y+yOff,blackCopy, target));
		}*/
		return heuristicVals[x][y];
	}
	
	public static ArrayList<Action> actions(State s, boolean[][] black){
		int x = s.current.x, y = s.current.y;
		ArrayList<Action> ret = new ArrayList<Action>();
		if(inRange(x,y-1,black) && !black[x][y-1] && (!s.pinks[x][y-1] || (inRange(x,y-2,black) && !(s.pinks[x][y-2] || black[x][y-2])))){
			ret.add(Action.UP);
		}
		if(inRange(x,y+1,black) && !black[x][y+1] && (!s.pinks[x][y+1] || (inRange(x,y+2,black) && !(s.pinks[x][y+2] || black[x][y+2])))){
			ret.add(Action.DOWN);
		}
		if(inRange(x-1,y,black) && !black[x-1][y] && (!s.pinks[x-1][y] || (inRange(x-2,y,black) && !(s.pinks[x-2][y] || black[x-2][y])))){
			ret.add(Action.LEFT);
		}
		if(inRange(x+1,y,black) && !black[x+1][y] && (!s.pinks[x+1][y] || (inRange(x+2,y,black) && !(s.pinks[x+2][y] || black[x+2][y])))){
			ret.add(Action.RIGHT);
		}
		return ret;
	}
	
	public static State successor(State s, Action a){
		int x = s.current.x, y = s.current.y;
		boolean[][] newPinks = copy(s.pinks);
		switch(a){
		case UP:
			if(newPinks[x][y-1]){
				newPinks[x][y-1]=false;
				newPinks[x][y-2]=true;
			}
			return new State(new Coordinate(x,y-1),newPinks);
		case DOWN:
			if(newPinks[x][y+1]){
				newPinks[x][y+1]=false;
				newPinks[x][y+2]=true;
			}
			return new State(new Coordinate(x,y+1),newPinks);
		case LEFT:
			if(newPinks[x-1][y]){
				newPinks[x-1][y]=false;
				newPinks[x-2][y]=true;
			}
			return new State(new Coordinate(x-1,y),newPinks);
		case RIGHT:
			if(newPinks[x+1][y]){
				newPinks[x+1][y]=false;
				newPinks[x+2][y]=true;
			}
			return new State(new Coordinate(x+1,y),newPinks);
		default:
			return null;
		}
	}

	public static ArrayList<Coordinate> findPath(boolean[][] black, boolean[][] pink, Coordinate current, Set<Coordinate> targets, double greedy){
		PriorityQueue<SearchNode> open = new PriorityQueue<SearchNode>(1,new NodeCompare(1-greedy,greedy));
		Set<State> closed = new HashSet<State>();
		open.add(new SearchNode(null, new State(current, copy(pink)), null, 0, getHeuristic(current.x, current.y, black, targets)));
		while(!open.isEmpty()){
			SearchNode expand = open.remove();
			if(closed.contains(expand.state)){
				continue;
			}
			closed.add(expand.state);
			if(targets.contains(expand.state.current)){
				return expand.getPath();
			}
			ArrayList<Action> possibleActions = actions(expand.state,black);
			for(Action action : possibleActions){
				State newState = successor(expand.state, action);
				if(!closed.contains(newState)){
					SearchNode add = new SearchNode(action, newState, expand, 1, getHeuristic(newState.current.x, newState.current.y,black, targets));
					open.add(add);
				}
			}
		}
		return null;
	}
	
	public static boolean isBackground(Color c){
		return c.getRed()>=backgroundRedThreshold && c.getGreen()>=backgroundGreenThreshold && c.getBlue()>=backgroundBlueThreshold;
	}
	
	public static boolean[][] copy(boolean[][] in){
		boolean[][] ret = new boolean[in.length][in[0].length];
		for(int i = 0; i < in.length; i++){
			for(int j = 0; j < in[0].length; j++){
				ret[i][j] = in[i][j];
			}
		}
		return ret;
	}
	
	public static boolean inRange(int x, int y, boolean[][] arr){
		return x >= 0 && x < arr.length && y >= 0 && y < arr[0].length;
	}
	
	public static void printBoard(boolean[][] black, boolean[][] pink, Coordinate current, Set<Coordinate> target){
		for(int y = 0; y < black[0].length; y++){
			for(int x = 0; x < black.length; x++){
				Coordinate at = new Coordinate(x,y);
				if(at.equals(current))
					System.out.print("@");
				else if(target.contains(at))
					System.out.print("T");
				else if(black[x][y])
					System.out.print("B");
				else if(pink[x][y])
					System.out.print("P");
				else
					System.out.print("-");
			}
			System.out.println();
		}
	}

	private static void keyType(Robot r, int keycode){
		r.keyPress(keycode);
		delay(10);
		r.keyRelease(keycode) ;
		delay(10);
	}

	private static void delay(int ms){
		try{
			Thread.sleep(ms);
		}
		catch(InterruptedException e){
		}
	}
}
