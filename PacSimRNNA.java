import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import java.util.Collections;

import pacsim.BFSPath;
import pacsim.PacAction;
import pacsim.PacCell;
import pacsim.PacFace;
import pacsim.PacSim;
import pacsim.PacUtils;
import pacsim.PacmanCell;

/**
 * University of Central Florida 
 * CAP4630 - Fall 2017
 * RNNA
 * @author Julian Quitian, Ley Widley
 * @version 10/08/2017
 */
public class PacSimRNNA implements PacAction {
   
	private List<Point> path;
	private int simTime;
	int solutionMoves = 0; 
      
	public PacSimRNNA( String fname ) {
		PacSim sim = new PacSim( fname );
		sim.init(this);
	}
   
	public static void main( String[] args ) {
		System.out.println("\nTSP using RNNA agent by Julian Quitian & Ley Widley:");
		System.out.println("\nMaze : " + args[ 0 ] + "\n" );
		new PacSimRNNA( args[ 0 ] );
	}

	// Use this method to reset any variables that must be re-initialized before runs
	@Override
	public void init() {
		simTime = 0;
		solutionMoves = 0; 
		path = new ArrayList();
	}
   
	/*
	 * Inputs:
	 *		- Object that will be 2D array of PacCell cells.
	 * Requirements:
	 * 	1) Compute RNNA solution path through input maze exactly one time
	 *			a) Extract Pac-Man's starting location
	 *			b) Extract location of all food dots
	 *			c) Determine valid successors of a location by taking into account which cells are walls and which are not
	 *		2) Determine Direction to move Pac-Man for next move
	 */
	@Override
	public PacFace action( Object state ) {
		
		PacCell[][] grid = (PacCell[][]) state;
		PacmanCell pc = PacUtils.findPacman(grid);

		// Make sure Pac-Man is in the game
		if (pc == null) 
			return null;

		// Step 1: Compute RNNA solution path only when method is first called
		if(path.isEmpty()){

			// Generate cost table
			int[][] costTable = generateCostTable(grid, pc);

			// Generate the Food array
			List<Point> allFoodPellets = generateFoodArray(grid);

			int population = 0, cost = 0, previousCost =0; 
			String pathStr = "";
			Point pacManLoc = new Point(); 
			Point currentFood; 
			List<PopulationEntry> populationList = new ArrayList<>();
			List<Point> previousPath = new ArrayList<>();
			ArrayList<Point> closestPellets = new ArrayList<Point>();

			List<PopulationEntry> popListCopy = new ArrayList<>();

			long start = System.currentTimeMillis();  
			// Calculate each step in the algorithm 
			for(int i = 0; i < allFoodPellets.size(); i++)
			{
				System.out.println("Population at step " + (i+1) + ":");
				population = 0; 
				cost = 0;
				
				int capacity = (populationList.size() > allFoodPellets.size()) ? populationList.size() : allFoodPellets.size();
				for (int j = 0; j < capacity; j++)
				{
					String previousPathStr = "";	
					// If we're in the first step, then instantiate 
					// each population entry and add them to the list
					
					if (i == 0)
					{
						PopulationEntry p = new PopulationEntry(); 
						populationList.add(p); 
						pacManLoc = pc.getLoc(); 
						currentFood = allFoodPellets.get(j);
					}
					else
					{

						closestPellets.clear();

						// System.out.println("i: " + i + "  j: " + j);
						pacManLoc = populationList.get(j).getPoint(i-1);
						
						// Get available food pellets closest to last simulated PacMan location. Branch if more than one available
						closestPellets = getClosestPellets(populationList.get(j), allFoodPellets, costTable, pacManLoc);
						currentFood = closestPellets.get(0);
	
					}
					previousPathStr = populationList.get(j).getPathStr();
					previousPath = populationList.get(j).getPath();
					previousCost = populationList.get(j).getCost();
					// Calculate the cost from pacman's current location to get current food 
					cost = BFSPath.getPath(grid, pacManLoc, currentFood).size();						
					
					// Set the cost for that particular population entry 
					populationList.get(j).setCost(populationList.get(j).getCost() + cost);
					
					//System.out.println("Distance between " + pacManLoc + " and " + currentFood + " is " + populationList.get(j).getCost());

					// Add new food pellet in point format to and set new cost
					populationList.get(j).addPoint(currentFood);

					// Get the path in a string format
					pathStr = "[("+ (int)currentFood.getX() + "," + (int)currentFood.getY() +
							")," + cost + "]";
					populationList.get(j).setPathStr(previousPathStr + pathStr);

				}

				popListCopy = populationList; 
				Collections.sort(popListCopy, new Comparator<PopulationEntry>()
				{

					@Override
					public int compare(PopulationEntry o1, PopulationEntry o2) {
						if(o1.getCost() > o2.getCost())
						{
							return 1;
						}
						else if (o1.getCost() == o2.getCost())
						{
							return 0; 
						}
						else
						{
							return -1; 
						}
					}
					
				});
				
				for (int j = 0 ; j < popListCopy.size(); j++)
				{
					System.out.println(population + " : cost="+ popListCopy.get(j).getCost() + " : " +
							popListCopy.get(j).getPathStr());
					population++;
				}
				// System.out.println(populationList.size() - 1);	
			}
			List<Point> stepsToTake = null; 
			path.add(popListCopy.get(0).getPath().get(0));
			for (int i = 0; i < popListCopy.get(0).getPath().size() -1; i++)
			{
				stepsToTake = BFSPath.getPath(grid, popListCopy.get(0).getPath().get(i), popListCopy.get(0).getPath().get(i+1));
				for(int j = 0; j < stepsToTake.size(); j++)
				{
					path.add(stepsToTake.get(j));
				}
				
			}
			
			long end = System.currentTimeMillis();  
				
			System.out.println("\nTime to generate plan: " + (int)(end - start) + " ms\n");
			System.out.println("Solution moves\n");
		}
	
	
	//	Point tgt = PacUtils.nearestFood(pc.getLoc(), grid);
	//	path = BFSPath.getPath(grid, pc.getLoc(), tgt);
		Point next = path.remove(0);
		PacFace face = PacUtils.direction(pc.getLoc(), next);
		System.out.println(solutionMoves + " : From [  " + (int)pc.getLoc().getX()+ ",  " +
				(int)pc.getLoc().getY() +" ] go "+ face);
		solutionMoves++; 
		return face;
   }

	// Given the grid, get all the food location 
	// from the grid, and then print them out. 
	public List<Point> generateFoodArray(PacCell[][] G)
	{
		List<Point> allFood = PacUtils.findFood(G); 
		System.out.println("Food Array\n");
		for (int i = 0; i < allFood.size(); i++)
		{
			System.out.println(i + " : (" + (int)allFood.get(i).getX() + "," + (int)allFood.get(i).getY() + ")");
		}
		System.out.println();
		
		return allFood; 
	}

	// Prints and returns 2D adjacency matrix where Pacman is first row/column and
	// all other entries are food pellets.
	private int[][] generateCostTable(PacCell[][] G, PacmanCell pc){
		
		List<Point> foodPellets = PacUtils.findFood(G);
		
		// Cost table size is n+1 by n+1
		int tableSize = foodPellets.size() + 1;
		int[][] costTable = new int[tableSize][tableSize];
	
		for(int x = 1; x < tableSize; x++){
			int cost = BFSPath.getPath(G, pc.getLoc(), foodPellets.get(x - 1)).size();
			costTable[0][x] = cost;
			costTable[x][0] = cost;
		}

		for(int x = 1; x < tableSize; x++){
			for(int y = 1; y < tableSize; y++){
				costTable[x][y] = BFSPath.getPath(G	, foodPellets.get(x - 1), foodPellets.get(y - 1)).size();
				}
		}
		
		// Print cost table
		System.out.println("Cost table:\n");
		for(int x = 0; x < tableSize; x++){
			for(int y = 0; y < tableSize; y++){
				//System.out.print(" " + costTable[x][y]);
				System.out.printf("%-3d", costTable[x][y]);
			}
			System.out.println();
		}
		
		System.out.println("\n");

		return costTable;
	}
	
	// Returns Point array list representing food pellet closest to last entry in foodPellets
	private ArrayList<Point> getClosestPellets(PopulationEntry currentPath, List<Point> foodPellets, int[][] costTable, Point pacmanLoc){
		
		//System.out.println("Looking at: (" + pacmanLoc.getX() + ", " + pacmanLoc.getY() + ")");
		int minDistance = Integer.MAX_VALUE;

		// Figure out pacmanLoc index
		int indexOfPacman = foodPellets.indexOf(pacmanLoc) + 1;
		
		// Keep track of points with least distance
		ArrayList<Point> chosenPoints = new ArrayList<Point>();

		for(int x = 0; x < foodPellets.size(); x++){
			//if((int)pacmanLoc.getX() == 3)
			//	System.out.println("Checking Food: (" + foodPellets.get(x).getX() + ", " + foodPellets.get(x).getY() + ")");
			if(!(currentPath.getPath().contains(foodPellets.get(x)))){
				//System.out.println("Distance to pellet " + x + " is " + costTable[x + 1][indexOfPacman]);
				if((x + 1) != indexOfPacman && costTable[x + 1][indexOfPacman] < minDistance){
					chosenPoints.clear();
					chosenPoints.add(foodPellets.get(x));
					minDistance = costTable[x + 1][indexOfPacman];
				}else if(costTable[x + 1][indexOfPacman] == minDistance){
					chosenPoints.add(foodPellets.get(x));
				}
			} 
		}
		//System.out.println(chosenPoint.getX() + ", " + chosenPoint.getY());
		return chosenPoints;
	}

}
