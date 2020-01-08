import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;

public class Driver {

	public static ArrayList<Pair>[] generateData() throws FileNotFoundException {
		// Creates file reader and writer objects
		
		int totalWords=0;
		Scanner in = new Scanner(new File("shakespeare.txt"));

		Hashtable<String, Integer> hashTable = new Hashtable<String, Integer>();
		// builds hashtable with word frequency values (word, frequency)
		while (in.hasNextLine()) {

			// Calls solveNQueens for each line in the input file
			String line = in.nextLine().trim() + " ";
			//line = line.replaceAll("[-|?|,|.|!|:|;|\\[|\\]]", " ");
			String[] tokens = line.split("[\\s?,.!:;\\[\\]]+");
			// tolowercase
			for (String token : tokens) {
				totalWords++;
				token = token.toLowerCase();
				// increments the value of the word if it exists otherwise it
				// creates it
				if (hashTable.containsKey(token)) {
					hashTable.put(token, hashTable.get(token) + 1);
				} else {
					hashTable.put(token, 1);
				}

			}

		}
		System.out.println("unique words " + hashTable.size());
		System.out.println("total words"+totalWords);
		// list that holds arraylists that hold the different pair objects
		// the index in the list represents the length of word
		ArrayList<Pair>[] list = new ArrayList[40];
		Set<String> keys = hashTable.keySet();
		for (String key : keys) {
			int length = key.length();
			if (list[length] == null) {
				list[length] = new ArrayList<Pair>();
				list[length].add(new Pair(key, hashTable.get(key)));
			} else {
				list[length].add(new Pair(key, hashTable.get(key)));
			}

		}

		for (int i = 0; i < list.length; i++) {
			if (list[i] != null) {
				Collections.sort(list[i]);

			}
		}

		in.close();

		return list;
	}

	public static void main(String[] args) throws IOException {
//		args = new String[2];
//		args[0] = "more-input.txt";
//		args[1] = "out.txt";
		// check if an input and output file have been provided
		if (args.length < 2) {
			System.out
					.println("Usage: java –jar nQueens.jar <input file> <output file>");
			System.exit(1);

		}
		
		ArrayList<Pair>[] data = generateData();
		Scanner in = new Scanner(new File(args[0]));
		PrintWriter out = new PrintWriter(args[1]);

		while (in.hasNextLine()) {

			String line = in.nextLine().trim() + " ";
			String[] tokens = line.split("\\s+");
			int length=Integer.parseInt(tokens[0]);
			int frequent = Integer.parseInt(tokens[1]);

			if(data[length]==null||data[length].size()-1<frequent||data[length].get(frequent)==null){
				System.out.println("-");
				out.println("-");
			}else{
				String result = data[length].get(frequent).key;
				out.println(result);
				System.out.println(result);
			}
			

		}
		in.close();
		out.close();
	}

}
