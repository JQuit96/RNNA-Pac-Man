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
 * RNNA
 * @author Julian Quitian, Ley Widley
 * @version 10/08/2017
 */
public class PacSimRNNA implements PacAction {
   
	private List<Point> path;
	private int simTime;
      
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

			int population = 0, cost = 0; 
			String pathStr = "";
			Point pacManLoc = new Point(); 
			Point currentFood; 
			List<PopulationEntry> populationList = new ArrayList<>();
			// Calculate each step in the algorithm 
			for(int i = 0; i < allFoodPellets.size(); i++)
			{
				System.out.println("Population at step " + (i+1) + ":");
				population = 0; 
				cost = 0;
				
				for (int j = 0; j < allFoodPellets.size(); j++)
				{
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
						pacManLoc = populationList.get(j).getPoint(i-1); 
					}

					// Calculate the cost from pacman's current location to get current food 
					cost = PacUtils.manhattanDistance(pacManLoc, currentFood);

					// Add new food pellet in point format to and set new cost
					populationList.get(j).addPoint(currentFood);

					// Set the cost for that particular population entry 
					populationList.get(j).setCost(populationList.get(j).getCost() + cost);
					// Get the path in a string format
					pathStr = "[("+ (int)currentFood.getX() + "," + (int)currentFood.getY() +
							")," + populationList.get(j).getCost() + "]";
					populationList.get(j).setPathStr(populationList.get(j).getPathStr() + pathStr);
					
					Collections.sort(populationList, new Comparator<PopulationEntry>()
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
					
										
					/*TODO
					 * 1 - Get path in point format
					 * 2 - Find a way to sort entry based on cost 
					 * 3 - Change Pacman location 
					 * 4 - Add logic to handle if the cost is the same with another (Branching)
					 * 
					 */
				}
				
				for (int j = 0 ; j < populationList.size(); j++)
				{
					System.out.println(population + " : cost="+ populationList.get(j).getCost() + " : " +
							populationList.get(j).getPathStr());
					population++;

				}
				
				System.out.println();
			}

			//TODO compute solution path
		}
	
		// TODO Note current position and next step; return NSEW direction in form of PacFace enum
		// Change! Added only for testing purposes.
		Point tgt = PacUtils.nearestFood(pc.getLoc(), grid);
		path = BFSPath.getPath(grid, pc.getLoc(), tgt);
		Point next = path.remove(0);
		PacFace face = PacUtils.direction(pc.getLoc(), next);
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
}
