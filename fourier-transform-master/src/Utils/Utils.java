package Utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/*
 * Provide general purpose methods for handling OpenCV-javaFX data conversion.
 * Moreover, expose some "low level" methods for matching few JavaFX behavior.
 * 
 * 提供用于处理OpenCV-JavaFX数据转化的通用方法
 * 此外，展示一些：低级方法来匹配一些JavaFX行为
 * */
public final class Utils {
	/*
	 * Convert a Mat object (openCV) in the corresponding Image for JavaFX
	 * 
	 *  @param frame
	 *  		the {@link Mat} representing the current frame
	 *  @return the {@link Image} to show 
	 *  
	 * */
	public static Image mat2Image (Mat frame) {
		try {
			return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
		}catch(Exception e) {
			//show the exception details
			System.err.println("Cannot convert the Mat Object:");
			e.printStackTrace();
			
			return null;
		}
	}
	/*
	 * Generic method for putting element running on a non-JavaFX thread on the JavaFX thread, to properly update the UI
	 * @param property
	 * 			a {@link ObjectProperty}
	 * @param value
	 * 			the value to set for the given {@link ObjectProperty}
	 * */
	public static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
		Platform.runLater(()->{
			property.setValue(value);
		});
	}
	
	/*
	 * Support for the {@link  mat2image()} method
	 * @param original
	 * 			the {@link Mat} object in BGR or grayscale
	 * @return the corresponding {@link BufferedImage}
	 * */
	private static BufferedImage matToBufferedImage(Mat original) {
		//init
		BufferedImage image = null;
		int width = original.width() , height= original.height() , channels = original.channels();
		byte [] sourcePixels = new byte[width*height*channels];
		original.get(0,0,sourcePixels);
		if (original.channels()>1) {
			image = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
		}else {
			image = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte [] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
		
		return image;
	}
}
