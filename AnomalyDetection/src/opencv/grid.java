package opencv;


public class grid  {
	public static void main(String[] args) {

		int x=3;
		int y = 4;
		int answ = x/y;
		//System.out.println(answ);
		learn(710,4);
		
	}
	
	public static void learn(int x, int y){
		int gridLegnth = 20;
		final int width= 720;
		final int legnth = 1280;
		int[][] grid = new int[width/gridLegnth][legnth/gridLegnth];
		
		grid[x/gridLegnth][y/gridLegnth] = grid[x/gridLegnth][y/gridLegnth] + 1;
		System.out.println(grid[35][0]);
//makes a grid with the value representing the number of "hits" in the grid
		
		
		
		
	
	}
	
	

}
