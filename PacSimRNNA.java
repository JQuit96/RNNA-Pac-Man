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

   @Override
   public void init() {
      simTime = 0;
      path = new ArrayList();	
   }
   
   @Override
   public PacFace action( Object state ) {
	
   }
}
