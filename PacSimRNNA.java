import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
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
		//new PacSimRNNA( args[ 0 ] );
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
			// Generate the Food array
			List<Point> allFoodPellets = generateFoodArray(grid);
			int population = 0, cost = 0; 
			// Calculate each step in the algorithm 
			for(int i = 0; i < allFoodPellets.size(); i++)
			{
				System.out.println("Population at step " + i + ":");
				population = 0; 
				cost = 0;
				for (int j = 0; j < allFoodPellets.size(); j++)
				{
					Point currentFood = allFoodPellets.get(j);
					cost = PacUtils.manhattanDistance(pc.loc, currentFood);
					System.out.println(population + " : cost="+ cost + " : [( "+
					currentFood.getX() + "," + currentFood.getY() + ")," +
							cost + "]");
					population++;
				}
			}

			//TODO compute solution path
		}

		// TODO Note current position and next step; return NSEW direction in form of PacFace enum
		
   }
	// Given the grid, get all the food location 
	// from the grid, and then print them out. 
	public List<Point> generateFoodArray(PacCell[][] G)
	{
		List<Point> allFood = PacUtils.findFood(G); 
		System.out.println("Food Array\n");
		for (int i = 0; i < allFood.size(); i++)
		{
			System.out.println(i + " : (" + allFood.get(i).getX() + "," + allFood.get(i).getY() + ")");
		}
		
		return allFood; 
	}
}
