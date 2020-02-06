package enroll;

import java.io.File;

public class Enlist {

	public static void main(String[] args) {

		File folder = new File(System.getProperty("user.home"),
				"FI9831W_00626E5275F2/snap/");

		File file = new File(System.getProperty("user.home"),
				"FI9831W_00626E5275F2/snap/IMG_3445.jpg");
		FacialRecognition.enrollFile(file, "");
		//Enroll.enrollFolder(folder);

	}
}
