package opencv;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Graph {

	public static int legnth = 1280;
	public static int width = 720;
	public static int pixelSize = 10;
	public static int strictness = 1;
	public static int intensity = 0;



	public static class Grid extends JPanel {

		public List<Point> fillCells;

		public Grid() {
			fillCells = new ArrayList<>(25);
		} 

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			for(int i=0;i<fillCells.size();i++) {
				Point fillCell = fillCells.get(i);
				int cellX = pixelSize + (fillCell.x * pixelSize);
				int cellY = pixelSize + (fillCell.y * pixelSize);
				//g.setColor(Color.GREEN);
				//System.out.println(intensity);
				
	    			g.setColor(colors.get(i));
	           
				g.fillRect(cellX, cellY, pixelSize, pixelSize);
			}
//			for (Point fillCell : fillCells) {
//
//			}
			 
			g.setColor(Color.BLACK);
			g.drawRect(pixelSize, pixelSize, legnth, width);

			
			//draws black lines
			for (int i = pixelSize; i <= legnth; i += pixelSize) {
				g.drawLine(i, pixelSize, i, width+pixelSize);
			}

			for (int i = pixelSize; i <= width; i += pixelSize) {
				g.drawLine(pixelSize, i, legnth+pixelSize, i);
			}


		}
		public void fillCell(int x, int y, Color c) {
			colors.add(c);
			fillCells.add(new Point(x, y));
			repaint();
		}

	}
	
	public static ArrayList<Color> colors;

	public static void main(String[] a) throws FileNotFoundException, IOException, ClassNotFoundException {
		
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				"data.ser"));
		int[][] array = (int[][]) in.readObject();
		in.close();
		colors = new ArrayList<Color>();
		// Print out contents of deserialized int[]
		// System.out.println("It is " +fac (array instanceof Serializable) +
		// " that int[] implements Serializable");
		// System.out.print("Deserialized array: " + array[0]);

		
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException ex) {
		}

		Grid grid = new Grid();
		JFrame window = new JFrame();
		window.setSize(legnth+20, width+40);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(grid);
		window.setVisible(true);
		
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				//System.out.print(array[i][j]);
				
				if(array[i][j]>strictness){
					intensity = array[i][j];
					System.err.println(intensity);
					Color c = Color.WHITE;
					if (array[i][j]>100) {
						//c=Color.RED;
						c= new Color(255, 0, 0);
					}else if(array[i][j]>80){
						c= new Color(225, 0, 0);
					}else if(array[i][j]>70){
						c= new Color(200, 0, 0);

					}else if(array[i][j]>60){
						c= new Color(180, 0, 0);

					}else if(array[i][j]>50){
						c= new Color(160, 0, 0);

					}else if(array[i][j]>40){
						c= new Color(140, 0, 0);

					}else if(array[i][j]>30){
						c= new Color(120, 0, 0);

					}else if(array[i][j]>20){
						c= new Color(100, 0, 0);

					}else if(array[i][j]>10){
						c= new Color(60, 0, 0);

					}else {
						c= new Color(40, 0, 0);
						System.out.println("here");

					}
					
					System.out.println(c);

					grid.fillCell(j, i, c);
	


				}
				

			}
			System.out.println();
		}

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				System.out.println(array[i][j]);
			}
		}
		
	}

}
