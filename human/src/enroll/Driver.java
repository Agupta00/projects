package enroll;

import java.io.File;

public class Driver {

	public static void main(String[] args) {
 
		// enroll person from folder
		File folder = new File(System.getProperty("user.home"), "Desktop/recognize/");

		//FacialRecognition.enrollFolder(folder, "Himanshu");

		// recognize
		 FacialRecognition.recognizeFolder(folder);
		// Wrapper.recognizeFile(file);
		//FacialRecognition.enrollFolder(folder, "JJ");
/*
		for (File fileEntry : folder.listFiles()) {

			if (fileEntry.getName().toLowerCase().endsWith(".jpg")
					|| fileEntry.getName().toLowerCase().endsWith(".jpeg")) {
				System.out.println(fileEntry.getName());
				//FacialRecognition.recognizeFile(fileEntry);
				FacialRecognition.enrollFile(fileEntry, "sun");
			}

		}
*/		
		
/*
 * // setpose
			System.out.println("_____________-________________");
			int leftEyeCenterX = (int) faceobj.getDouble("leftEyeCenterX");
			int leftEyeCenterY = (int) faceobj.getDouble("leftEyeCenterY");
			int rightEyeCenterX = (int) faceobj.getDouble("rightEyeCenterX");
			int rightEyeCenterY = (int) faceobj.getDouble("rightEyeCenterY");

			System.out.println(height);
			System.out.println(leftEyeCenterX);
			System.out.println(leftEyeCenterY);
			System.out.println(rightEyeCenterX);
			System.out.println(rightEyeCenterY);
			System.out.println(topLeftX);
			System.out.println(topLeftY);
			
			System.out.println(height);
					System.out.println(image_id);
					System.out.println(topLeftX);
					System.out.println(topLeftY);
					System.out.println(width);
 */
	}
}
