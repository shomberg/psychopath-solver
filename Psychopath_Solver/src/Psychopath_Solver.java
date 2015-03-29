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
		for(int x = 0; x < pixelWidth; x++){
			Color query = new Color(screenCap.getRGB(x, 0));
			boolean isBackNew = isBackground(query);
			if(isBackNew && !isBack){
				offsets.add(pixelSinceBackground);
				pixelSinceBackground = 0;
				lastSwitchBackground = x;
			}
			pixelSinceBackground++;
			isBack = isBackNew;
		}
		double minDiff = 100000;
		for(int width = offsets.size(); width < 50; width++){
			double diff = 0;
			double offset = (lastSwitchBackground+xOffset)/(double)width;
			for(Integer actualOffset : offsets){
				diff += Math.abs(offset-actualOffset.intValue());
			}
			if(diff < minDiff){
				gridWidth = width+1;
				gridOffset = offset;
				minDiff = diff;
			}
		}
		for(gridHeight=1; gridHeight*gridOffset < pixelHeight && !isBackground(new Color(screenCap.getRGB(0, (int)(gridHeight*gridOffset)))); gridHeight++);
		
		boolean[][] black = new boolean[gridHeight][gridWidth];
		boolean[][] pink = new boolean[gridHeight][gridWidth];
		Coordinate start = new Coordinate(0,0);
		Coordinate end = new Coordinate(gridWidth-1,gridHeight-1);
		int max = 76;
		int maxTry=76;
		doBacktrack = false;
		delay(500);
		System.out.println("reading board");
		for(int x = 0; x < gridWidth; x++){
			for(int y = 0; y < gridHeight; y++){
				Color c = new Color(screenCap.getRGB((int)(x*gridOffset), (int)(y*gridOffset)));
				if(c.equals(startC)){
					start=new Coordinate(x,y);
				} else if(c.equals(blackC)){
					black[y][x]=true;
				} else if(c.equals(pinkC)){
					pink[y][x]=true;
				} else if(c.equals(blankC)){
					//Do Nothing
				} else{
					end = new Coordinate(x,y);
				}
			}
		}
//		black[9][5]=true;
//		end=new Coordinate(1,17);
		printBoard(black, pink, start, end);
		for(; max <= maxTry; max++){
			System.out.println("Beginning search for: " + max);
			ArrayList<Coordinate> result = findPath(black, pink, start, end, new ArrayList<Coordinate>(), new ArrayList<Coordinate>(), max);
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
				break;
			}
		}
	}
	

	public static ArrayList<Direction> getSteps(ArrayList<Coordinate> path) throws Exception {
		ArrayList<Direction> ret = new ArrayList<Direction>();
		for(int i = 0; i < path.size()-1; i++){
			ret.add(0, new Direction(path.get(i+1), path.get(i)));
		}
		return ret;
	}


	public static ArrayList<Coordinate> findPath(boolean[][] black, boolean[][] pink, Coordinate current, Coordinate target, ArrayList<Coordinate> path, ArrayList<Coordinate> backtrack, int max){
		path.add(0,current);
		if(current.equals(target)){
			return path;
		}
		if(path.size()==max+1){
			path.remove(0);
			return null;
		}
		if(Math.abs(current.x-target.x) + Math.abs(current.y-target.y) > max-path.size()+1){
			path.remove(0);
			return null;
		}
		if(path.size()==1)
			System.out.println("level 1");
		if(path.size()==2)
			System.out.println("level 2");
		if(path.size()==3)
			System.out.println("level 3");
		for(int xOff=-1; xOff<=1; xOff++){
			for(int yOff=-1; yOff<=1; yOff++){
				if((xOff==0&&yOff==0)||(xOff!=0&&yOff!=0)) continue;
				Coordinate next = new Coordinate(current.x+xOff, current.y+yOff);
				try {
					Direction d = new Direction(current, next);
				} catch (Exception e) {
					System.out.println("not adjacent??");
					System.out.println(current);
					System.out.println(next);
					System.out.println("xOff: " + xOff + " yOff: " + yOff);
				}
				if(backtrack.contains(next)) continue;
				if(next.x<0||next.x>=black[0].length||next.y<0||next.y>=black.length) continue;
				if(black[next.y][next.x]) continue;
				ArrayList<Coordinate> backtrackNew = (ArrayList<Coordinate>) backtrack.clone();
				backtrackNew.add(0,current);
				boolean[][] iterate = copy(pink);
				if(pink[next.y][next.x]){
					Coordinate push = new Coordinate(current.x+2*xOff, current.y+2*yOff);
					if(!(push.x<0||push.x>=black[0].length||push.y<0||push.y>=black.length) && !black[push.y][push.x] && !pink[push.y][push.x]){
						iterate[current.y+2*yOff][current.x+2*xOff]=true;
						iterate[next.y][next.x]=false;
						if(doBacktrack){
							for(int i = 0; i < 0; i++){
								if(backtrackNew.size()>0)
									backtrackNew.remove(0);
							}
						}
					}
					else
						continue;
				}
				ArrayList<Coordinate> ret = findPath(black, iterate, next, target, path, backtrackNew, max);
				if(ret!=null){
					return ret;
				}
			}
		}
		path.remove(0);
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
	
	public static void printBoard(boolean[][] black, boolean[][] pink, Coordinate current, Coordinate target){
		for(int i = 0; i < black.length; i++){
			for(int j = 0; j < black[0].length; j++){
				Coordinate at = new Coordinate(j,i);
				if(at.equals(current))
					System.out.print("@");
				else if(at.equals(target))
					System.out.print("T");
				else if(black[i][j])
					System.out.print("B");
				else if(pink[i][j])
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
