package opencv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class reader {
	public static void main(String[] args) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		/*
		 * ObjectOutputStream out = new ObjectOutputStream(new
		 * FileOutputStream("test.ser")); out.writeObject(new int[] {0, 1, 2, 3,
		 * 4, 5, 6, 7, 8, 9}); out.flush(); out.close();
		 */
		// Deserialize the int[]
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				"test.ser"));
		int[][] array = (int[][]) in.readObject();
		in.close();

		// Print out contents of deserialized int[]
		// System.out.println("It is " +fac (array instanceof Serializable) +
		// " that int[] implements Serializable");
		// System.out.print("Deserialized array: " + array[0]);

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				System.out.print(array[i][j]);
			}
			System.out.println();
		}

	}
}
