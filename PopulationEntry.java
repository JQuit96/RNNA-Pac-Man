import java.awt.Point;
import java.util.List;

public class PopulationEntry
{
	private int cost; 
	private String pathStr; 
	private List<Point> path; 
	
	public PopulationEntry()
	{
		cost = 0; 
		pathStr = ""; 
	}
	
	public void setCost(int cost)
	{
		this.cost = cost; 
	}
	
	public int getCost()
	{
		return this.cost; 
	}
	
	public void setPathStr(String pathStr)
	{
		this.pathStr = pathStr;
	}
	
	public String getPathStr()
	{
		return this.pathStr;
	}
}
