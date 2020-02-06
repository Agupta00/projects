package opencv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bytedeco.javacv.FrameGrabber.Exception;


public class Hello {

	

    public static void main(String[] args) throws Exception, FileNotFoundException, IOException, ClassNotFoundException {
    	 // Serialize an int[]
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("test.ser"));
        out.writeObject(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        out.flush();
        out.close();

       

    }
}

