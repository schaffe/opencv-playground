package com.dzidzoiev.opencv;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URL;

/**
 * The main class for a JavaFX application. It creates and handle the main
 * window with its resources (style, graphics, etc.).
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @version 1.5 (2016-09-17)
 * @since 1.0 (2013-10-20)
 * 
 */
public class FXHelloCV extends Application
{
    private ImageView currentFrame;

	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			// load the FXML resource
            URL resource = getClass().getClassLoader().getResource("FXHelloCV.fxml");
            FXMLLoader loader = new FXMLLoader(resource);
			// store the root element so that the controllers can use it
			BorderPane rootElement = (BorderPane) loader.load();
			// create and style a scene
			Scene scene = new Scene(rootElement, 800, 600);


			primaryStage.setTitle("JavaFX meets OpenCV");
			primaryStage.setScene(scene);
			// show the GUI
			primaryStage.show();

            currentFrame = Utils.getChildByID(rootElement, "currentFrame");

            BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource("fok.png"));
            Mat frame = bufferedImageToMat(image);
//            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
            Image imageToShow = Utils.mat2Image(frame);
            Utils.onFXThread(currentFrame.imageProperty(), imageToShow);



        }
		catch (Exception e)
		{
			e.printStackTrace();
		}





    }

    public static Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }
	
	/**
	 * For launching the application...
	 * 
	 * @param args
	 *            optional params
	 */
	public static void main(String[] args)
	{
		// load the native OpenCV library
		nu.pattern.OpenCV.loadShared();
		
		launch(args);
	}
}