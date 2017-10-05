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
      new PacSimRNNA( args[ 0 ] );
   }

	// Use this method to reset any variables that must be re-initialized before runs
   @Override
   public void init() {
      simTime = 0;
      path = null;
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
		
		// Compute RNNA solution path only when method is first called
		if(path == null){
			path = new ArrayList();
		}
   }
}
