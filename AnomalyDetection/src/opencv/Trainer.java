package opencv;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import org.bytedeco.javacpp.*; 
import org.bytedeco.javacv.*; 

import static org.bytedeco.javacpp.opencv_core.*; 
import static org.bytedeco.javacpp.opencv_imgproc.*; 
 
public class Trainer { 
	static int pixelSize = 10;
	static final int width= 720;
	static final int legnth = 1280;
	static int[][] grid = new int[width/pixelSize][legnth/pixelSize];
	
	
    public static void main(String[] args) throws Exception { 
    	 
    	
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0); 
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage(); 
        grabber.start(); 
 
        IplImage frame = converter.convert(grabber.grab()); 
        IplImage image = null; 
        IplImage prevImage = null; 
        IplImage diff = null; 
 
        CanvasFrame canvasFrame = new CanvasFrame("Camera"); 
        canvasFrame.setCanvasSize(frame.width(), frame.height()); 
 
        CvMemStorage storage = CvMemStorage.create(); 
 
        while (canvasFrame.isVisible() && (frame = converter.convert(grabber.grab())) != null) {
        	//while the screen is still open/video feed still working
            cvClearMemStorage(storage); 
 
            cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2); 
            if (image == null) { 
                image = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1); 
                cvCvtColor(frame, image, CV_RGB2GRAY); 
            } else { 
                prevImage = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1); 
                prevImage = image; 
                image = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1); 
                cvCvtColor(frame, image, CV_RGB2GRAY); 
            } 
 
            if (diff == null) { 
                diff = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1); 
            } 
 
            if (prevImage != null) { 
                // perform ABS difference 
                cvAbsDiff(image, prevImage, diff); 
                // do some threshold for wipe away useless details 
                cvThreshold(diff, diff, 50, 255, CV_THRESH_BINARY); 
 
                canvasFrame.showImage(converter.convert(diff)); 
 
                // recognize contours 
                CvSeq contour = new CvSeq(null); 
                cvFindContours(diff, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE); 
 
                
                while (contour != null && !contour.isNull()) { 
                    if (contour.elem_size() > 0) { 
                    	

                        CvBox2D box = cvMinAreaRect2(contour, storage); 
                        // test intersection 
                        if (box != null) { 

                            CvPoint2D32f center = box.center(); 
                            CvSize2D32f size = box.size(); 
                            //System.out.println("("+center.x() + ", " + center.y() +")");
                        
                           
                            int x_legnth = (int) size.width()/pixelSize + 1;
                            int y_legnth = (int) size.height()/pixelSize + 1;

                            int leftx;
                            int topy;
                            
                            if((int) ((center.x()- (size.width()/2))/pixelSize + 1) <0){
                            	leftx = 0;
                            }else{
                                leftx = (int) ((center.x()- (size.width()/2))/pixelSize + 1);

                            }
                            
                            if ((int) ((center.y()- (size.height()/2))/pixelSize + 1)<0){
                            	topy =0;
                            }else{
                                topy = (int) ((center.y()- (size.height()/2))/pixelSize + 1);
                            }

/*
                          //Adds 1 to every area of motion detected
                            for(int y=0; y<(x_legnth); y++){
                            	 for(int x =0; x<(x_legnth); x++){
                            		 
                            		 System.out.println(topy + y);
                                     if (leftx + x >=64){//checks to make sure that if motion is at the border of the cammera view that it 
                                    	 //wont excede the array range
                                       	grid[topy + y][63]= grid[topy + y][63] + 1;

                                     }else if (topy + y >=36){
                                        	grid[35][leftx + x]= grid[35][leftx + x] + 1;

                                     }else {
                                        	grid[topy + y][leftx + x]= grid[topy + y][leftx + x] + 1;

                                     }
                                 }
                            }
 */
                            try{
                            	//Adds 1 to every area of motion detected
                                for(int y=0; y<(y_legnth); y++){
                                	 for(int x =0; x<(x_legnth); x++){
                                		 int number = grid[topy + y][leftx + x] + 1;
                                		 //System.out.println(number); //debug
                                         grid[topy + y][leftx + x]= number;
                                     }
                                }
                            }catch(IndexOutOfBoundsException e){
                            	System.out.println("subject left view of cammera");
                            }  
 
                        } 
                    } 
                    contour = contour.h_next(); 
                } 
            } 
        } 
        grabber.stop(); 
        canvasFrame.dispose(); 
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("data.ser"));
        out.writeObject(grid);
        out.flush();
        out.close();
    } 

    
}
