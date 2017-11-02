package utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/*
 * Provide general purpose methods for handling OpenCV-JavaFX data conversion.
 * Moreover, expose some "low level" methods for matching few JavaFX behavior.
 * 提供处理OpenCV-JavaFX数据转换的通用方法
 * 此外，展示几个low level方法匹配一些JavaFX行为
 * */
public final class Utils {
	/*
	 * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
	 * @param frame
	 * 			the {@link Mat} representing the current frame.
	 * @return the {@link Image} to show
	 * 将JavaFX相应的图像转换成OpenCV中的Mat对象
	 * */
	public static Image mat2Image(Mat frame) {
		try {
			return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
		}catch(Exception e) {
			System.err.println("Cannot convert the Mat Object: "+ e);
			return null;
		}
	}
	
	/*
	 * Generic method for putting element running on a non-JavaFX thread on the
	 * JavaFX thread, to properly update the UI
	 * @param property
	 * 			a {@link ObjectProperty}
	 * @param value
	 * 			the value to set for the given {@link ObjectProperty}
	 * 将不再JavaFX线程上的元素放在JavaFX线程上运行，来正确更新UI
	 * 
	 * 
	 * */
	public static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
		Platform.runLater(()->{property.setValue(value);});
	}

	/*
	 * Support for the {@link mat2image()} method
	 * @param original
	 * 			the {@link Mat} object in BGR or grayscale
	 * @return the corresponding {@link BufferedImage}
	 * 给mat2image方法提供支持
	 * */
	private static BufferedImage matToBufferedImage(Mat original) {
		//init
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte [] sourcePixels = new byte[width*height*channels];
		original.get(0,0, sourcePixels);
		
		if(original.channels()>1) {
			image = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
		}else {
			image = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels,0,targetPixels,0,sourcePixels.length);
		return image;
	}
}
