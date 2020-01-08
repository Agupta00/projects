//-----------------------------------------------------------------------------
//Akul Gupta, ID:1686664
// ChessBoard.java
// Creates a chessboard represented by a 2d array which through recursive calls iterates through different queen
// placements to find if it is possible to put n-queens on the nxn board so that no queen attacks another queen
//-----------------------------------------------------------------------------
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;

public class ChessBoard {
	public int n;

	boolean[][] board;
	// the current queens on the board
	static ArrayList<Point> boardList;

	public ChessBoard(int n) {
		this.n = n;
		board = new boolean[n][n];
		boardList = new ArrayList<Point>();
		// numQueens=n;
	}

	// sets up the board so (1,1) is the first element in the board rather than
	// (0,0)
	public void setBoard(int r, int c, boolean state) {

		// System.out.println(r + " " + c);
		board[r - 1][c - 1] = state;
	}

	// sets up the board so (1,1) is the first element in the board rather than
	// (0,0)
	public boolean getBoard(int r, int c) {
		return board[r - 1][c - 1];
	}

	// updates open spaces on the board by marking up attacking spaces as true
	public void updateBoard(Point point) {

		// marks top-down spots
		for (int r = 1; r <= n; r++) {
			// System.err.println(r+""+point.x);
			setBoard(r, point.x, true);
		}

		// marks left-right spots
		for (int c = 1; c <= n; c++) {
			// System.out.println(point.y+""+c);
			setBoard(point.y, c, true);
		}

		// marks top-down diagonal
		for (int c = 1; c <= n; c++) {
			if (inRange((point.y - (point.x - c)), c)) {
				setBoard((point.y - (point.x - c)), c, true);
			}

		}
		// marks bottom-top diagonal
		for (int c = 1; c <= n; c++) {
			if (inRange(point.y + (point.x - c), c)) {
				setBoard((point.y + (point.x - c)), c, true);
			}

		}

	}

	// returns true if the point is in the range of the chessboard
	private boolean inRange(int r, int c) {
		if (r > 0 && r <= n && c > 0 && c <= n) {
			return true;
		}
		return false;
	}

	// generates an new board based on current stack
	public void updateBoardMultiple() {
		// clears chessboard
		board = new boolean[n][n];

		for (Point p : boardList) {
			updateBoard(p);
		}
	}

	// returns an ArrayList of points that is all open points on the chessboard
	// after placing the passed point
	public ArrayList<Point> genPoints() {
		ArrayList<Point> points = new ArrayList<Point>();
		for (int r = 1; r <= n; r++) {
			for (int c = 1; c <= n; c++) {
				if (!getBoard(r, c)) {
					points.add(new Point(c, r));
				}
			}
		}
		return points;
	}

	
	public boolean nQueens(int init, Stack<Point> st) {
		//keeps track of how far in the stack refers to the previous points possibilities
		//so that if all combination of possibilities fail then the previous point can be removed too
		Stack<Integer> countStack = new Stack<Integer>();
		int count;
		//want to stop when the initial queens provided provides no possible solution
		while (st.size()>=init) {
			//System.err.println(countStack.toString());
			Point point = st.peek();
			boardList.add(point);
			updateBoard(point);

			// System.out.println(stack.toString());
			ArrayList<Point> possiblities = genPoints();
			// System.err.println(possiblities.toString());

			if (boardList.size() == n) {
				// no more queens to be placed, will work its way up to the
				// first nQueens call and return true
				return true;
				// no more possible points to place queens or there are more
				// queens than possible open points left
			} else if (possiblities.size() == 0
					|| (n - boardList.size()) > possiblities.size()) {

				// no more possible places to put remaining queens
				count = countStack.pop();
				boardList.remove(boardList.size() - 1);
				updateBoardMultiple();
				st.pop();
				countStack.push(--count);
				
			} else {
				 count =0;
				for (Point p : possiblities) {
					st.add(p);
					count++;
				}
				countStack.push(count);
				continue;
			}
			//ensures that continuous countStacks can be popped of at once
			while(countStack.size()>1){
				count = countStack.peek();
				if(count==0){
					countStack.pop();
					count=countStack.pop();
					countStack.push(--count);
					st.pop();
					boardList.remove(boardList.size() - 1);
					updateBoardMultiple();
				}else{
					break;
				}
			}
			count = countStack.peek();
			if(count==0){
				countStack.pop();
				st.pop();
				boardList.remove(boardList.size() - 1);
				updateBoardMultiple();
			}

		}
		return false;
	}

	// helper method to remove the commas in the result of stack.toString
	private String formatOutputString(String input) {
		input = input.replace("[", "");
		input = input.replace("]", " ");

		input = input.replace(",", "");
		return input;
	}

	// takes an input as an array of three integers which will be read from the
	// input file and checks if there is a solution for that line
	public static String solveNQueens(int[] data) {
		ChessBoard chessboard = new ChessBoard(data[0]);
		Stack<Point> st = new Stack<Point>();
		for(int i=1; i< data.length-1;){
			
			st.add(new Point(data[i],data[i+1]));
			i=i+2;
		}
		
		//checks to make sure the multiple queens provided in the input do not attack each other
		for(int i=1; i< data.length-1;){
			ArrayList<Point> possiblities = chessboard.genPoints();
			boolean containsPoint=false;
			for(Point p:possiblities){
				if(p.equals(new Point(data[i],data[i+1]))){
					containsPoint=true;
					break;
				}
			}
			if(containsPoint){
				boardList.add(new Point(data[i],data[i+1]));
				chessboard.updateBoard(new Point(data[i],data[i+1]));
				i=i+2;
			}else{
				return "No solution";

			}
		}
		//removes the last queen because nQueens takes the first queen of the stack and adds it to the boardList
		boardList.remove(boardList.size()-1);
		//passes the number of initial queens, and the stack with those initial queens already placed
		if (chessboard.nQueens((data.length-1)/2, st)) {
			// sorts the list of points if there is a solution
			Collections.sort(chessboard.boardList);
			// System.out.println(chessboard.stack.toString());
			return chessboard.formatOutputString(chessboard.boardList
					.toString());
		} else {
			return "No solution";
		}
	}

	public static void main(String[] args) throws IOException {
//		args = new String[2];
//		args[0]="more-input.txt";
//		args[1]="out.txt";
		// check if an input and output file have been provided
		if (args.length < 2) {
			System.out
					.println("Usage: java –jar nQueens.jar <input file> <output file>");
			System.exit(1);

		}

		// Creates file reader and writer objects
		Scanner in = new Scanner(new File(args[0]));
		PrintWriter out = new PrintWriter(args[1]);

		// Iterates through each line of the input file
		while (in.hasNextLine()) {

			// Calls solveNQueens for each line in the input file
			String line = in.nextLine().trim() + " ";
			String[] tokens = line.split("\\s+");
			
			
			int[] data = new int[tokens.length];
			for (int i = 0; i < tokens.length; i++) {
				data[i] = Integer.parseInt(tokens[i]);
			}

			String result = solveNQueens(data);
			out.println(result);
			System.out.println(result);

		}

		// closes read and write objects
		in.close();
		out.close();

	}

}
