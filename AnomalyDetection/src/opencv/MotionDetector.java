package opencv;


import static org.bytedeco.javacpp.helper.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvAbsDiff;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_GAUSSIAN;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvMinAreaRect2;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvBox2D;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.CvSize2D32f;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import enroll.Messenger;
 
public class MotionDetector { 
	static int pixelSize = 10;
	static final int width = 720;
	static final int legnth = 1280;
	static int[][] grid = new int[width / pixelSize][legnth / pixelSize];
	static final int motionSensitivity = 5;
	static final int trainedStrictness = 2;
	static int motionHits = 0;
	
	
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
                        
                           
                            int x_legnth = (int) size.width()/pixelSize;
                            int y_legnth = (int) size.height()/pixelSize;

                            int leftx;
                            int topy;
                            
                            if((int) ((center.x()- (size.width()/2))/pixelSize ) <0){
                            	leftx = 0;
                            }else{
                                leftx = (int) ((center.x()- (size.width()/2))/pixelSize );

                            }
                            
                            if ((int) ((center.y()- (size.height()/2))/pixelSize)<0){
                            	topy =0;
                            }else{
                                topy = (int) ((center.y()- (size.height()/2))/pixelSize);
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
                            
                            ObjectInputStream in = new ObjectInputStream(
									new FileInputStream("data.ser"));
							int[][] array = (int[][]) in.readObject();
							in.close();
/*							
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
                            	//System.out.println("subject left view of cammera");
                            }  
*/							
                            
							
							try {
								// Adds 1 to every area of motion detected
								for(int y=0; y<(y_legnth); y++){
                               	 for(int x =0; x<(x_legnth); x++){

                               		 
                               		 if(array[topy + y][leftx + x]==0){

    										motionHits++;
    										System.out.println(motionHits);
    										//System.out.println("here");
    									}
                                    }
                               }
							} catch (IndexOutOfBoundsException e) {
								//System.out.println("subject left view of cammera");//DEBUG
							}
                            	
                            if (motionHits>motionSensitivity){
                            	Messenger.sendMsg("ANOAMLY DETECTED:!");
                            	//System.out.println("more than 5");
                            	 grabber.stop(); 
                                 canvasFrame.dispose(); 
                            	return;
                            }
  
                        } 
                    } 
                    contour = contour.h_next(); 
                } 
            } 
        } 
        grabber.stop(); 
        canvasFrame.dispose(); 
        
    } 

    
}
