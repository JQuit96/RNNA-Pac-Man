import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

public class PopulationEntry
{
	private int cost; 
	private String pathStr; 
	private List<Point> path; 
	
	public PopulationEntry()
	{
		cost = 0; 
		pathStr = "";
		path = new ArrayList();
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
	
	public void addPoint(Point newPoint)
	{
		this.path.add(newPoint);
	}

	public Point getPoint(int index)
	{
		return path.get(index); 
	}

	public List<Point> getPath(){
		return path;
	}
	
	public void setPath(List<Point> newPath){
		this.path = newPath;
	}
	
	public void printPath(){
		for(Point x : path)
			System.out.print(x + "  ");
		System.out.println();
	}
}
