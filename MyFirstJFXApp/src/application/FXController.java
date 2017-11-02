package application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Utils;

/*	The controller for our application ,where the application logic is 
 * implemented. It handles the button for starting/stopping the camera and the
 * acquired video stream.
 * 这是我们app的控制器，它来实现app的逻辑功能。它处理了开始和停止camera截取视频流
 * 
 * */

public class FXController {
	//The FXML button
	@FXML
	private Button button;
	//The FXML image view
	@FXML
	private ImageView currentFrame;
	
	//a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	//the openCV object that realizes the video capture
	private VideoCapture capture = new VideoCapture();
	//a flag to change the button behavior
	private boolean	cameraActive = false;
	//the id of the camera to be used
	private static int cameraId = 0;
	
	/*the action triggered by pushing the button on the GUI
	 * event the push button event
	 *动作触发由GUI界面上的按钮事件驱动
	 */
	@FXML
	protected void startCamera(ActionEvent event) {
		if(!this.cameraActive) {
			//start the video capture
			this.capture.open(cameraId);
			//is the video stream available?
			if(this.capture.isOpened()) {
				this.cameraActive = true;
				
				//grab a frame every  33ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {
					@Override
					public void run() {
						//effectively grab and process a single frame
						Mat frame = grabFrame();
						//convert and show the frame
						Image imageToShow = Utils.mat2Image(frame);
						updateImageView(currentFrame,imageToShow);
					}
				};
				
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber,0,33,TimeUnit.MILLISECONDS);
				
				//Update the button content
				this.button.setText("Stop Camera");
			}else {
				//log the error
				System.err.println("Impossible to open the camera connection...");
			}
		}else {
			//the camera is not active at this point
			this.cameraActive = false;
			//update again the button content
			this.button.setText("Start Camera");
			
			//stop the camera
			this.stopAcquisition();
		}
	}

	/*
	 * Get a Frame from the  opened video stream (if any)
	 * @return the {@link Mat} to show
	 * 
	 * 抓取frame从打开的视频流
	 * */

	private Mat grabFrame() {
		//init everything
		Mat frame = new Mat();
		
		//check if the capture is open
		if(this.capture.isOpened()) {
			try {
				//read the current frame
				this.capture.read(frame);
				
				//if the frame is not empty, process it
				if(!frame.empty()) {
					Imgproc.cvtColor(frame,frame,Imgproc.COLOR_BGR2GRAY);
				}
			}catch(Exception e) {
				//log the error
				System.err.println("Exception during the image elaboration: "+e);
			}
		}
		return frame;
	}
	
	/*
	 *Stop the acquisition from the camera and release all the resources
	 * 停下camera,释放资源，定格瞬间
	 * */
	private void stopAcquisition() {
		if(this.timer!=null && !this.timer.isShutdown()) {
			try {
				//stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(33,TimeUnit.MILLISECONDS);
			}catch(InterruptedException e) {
				//log the error
				System.err.println("Exception in stoppinng the frame capture,trying to release the camera now..."+e);
				
			}
		}
		if(this.capture.isOpened()) {
			//release the camera
			this.capture.release();
		}
		
	}
	/*
	 * Update the {@link ImageView} in the JavaFX main thread
	 * @param view
	 * 			the {@link  ImageView} to update
	 * @Param image
	 * 			the {@link Image} to show
	 * */
	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(),image);
	}
	
	/*
	 * On application close, stop the acquisition from the camera
	 * */
	protected void setClosed() {
		this.stopAcquisition();
	}
}
