package com.dzidzoiev.opencv;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.*;
import javafx.scene.control.*;

/**
 * Provide general purpose methods for handling OpenCV-JavaFX data conversion.
 * Moreover, expose some "low level" methods for matching few JavaFX behavior.
 *
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @author <a href="http://max-z.de">Maximilian Zuleger</a>
 * @version 1.0 (2016-09-17)
 * @since 1.0
 * 
 */
public final class Utils
{
	/**
	 * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
	 *
	 * @param frame
	 *            the {@link Mat} representing the current frame
	 * @return the {@link Image} to show
	 */
	public static Image mat2Image(Mat frame)
	{
		try
		{
			return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
		}
		catch (Exception e)
		{
			System.err.println("Cannot convert the Mat obejct: " + e);
			return null;
		}
	}
	
	/**
	 * Generic method for putting element running on a non-JavaFX thread on the
	 * JavaFX thread, to properly update the UI
	 * 
	 * @param property
	 *            a {@link ObjectProperty}
	 * @param value
	 *            the value to set for the given {@link ObjectProperty}
	 */
	public static <T> void onFXThread(final ObjectProperty<T> property, final T value)
	{
		Platform.runLater(() -> {
			property.set(value);
		});
	}

	/**
	 * Find a {@link Node} within a {@link Parent} by it's ID.
	 * <p>
	 * This might not cover all possible {@link Parent} implementations but it's
	 * a decent crack. {@link Control} implementations all seem to have their
	 * own method of storing children along side the usual
	 * {@link Parent#getChildrenUnmodifiable()} method.
	 *
	 * @param parent
	 *            The parent of the node you're looking for.
	 * @param id
	 *            The ID of node you're looking for.
	 * @return The {@link Node} with a matching ID or {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getChildByID(Parent parent, String id) {

		String nodeId = null;

		if(parent instanceof TitledPane) {
			TitledPane titledPane = (TitledPane) parent;
			Node content = titledPane.getContent();
			nodeId = content.idProperty().get();

			if(nodeId != null && nodeId.equals(id)) {
				return (T) content;
			}

			if(content instanceof Parent) {
				T child = getChildByID((Parent) content, id);

				if(child != null) {
					return child;
				}
			}
		}

		for (Node node : parent.getChildrenUnmodifiable()) {
			nodeId = node.idProperty().get();
			if(nodeId != null && nodeId.equals(id)) {
				return (T) node;
			}

			if(node instanceof SplitPane) {
				SplitPane splitPane = (SplitPane) node;
				for (Node itemNode : splitPane.getItems()) {
					nodeId = itemNode.idProperty().get();

					if(nodeId != null && nodeId.equals(id)) {
						return (T) itemNode;
					}

					if(itemNode instanceof Parent) {
						T child = getChildByID((Parent) itemNode, id);

						if(child != null) {
							return child;
						}
					}
				}
			}
			else if(node instanceof Accordion) {
				Accordion accordion = (Accordion) node;
				for (TitledPane titledPane : accordion.getPanes()) {
					nodeId = titledPane.idProperty().get();

					if(nodeId != null && nodeId.equals(id)) {
						return (T) titledPane;
					}

					T child = getChildByID(titledPane, id);

					if(child != null) {
						return child;
					}
				}
			}
			else   if(node instanceof Parent) {
				T child = getChildByID((Parent) node, id);

				if(child != null) {
					return child;
				}
			}
		}
		return null;
	}


	/**
	 * Support for the {@link mat2image()} method
	 * 
	 * @param original
	 *            the {@link Mat} object in BGR or grayscale
	 * @return the corresponding {@link BufferedImage}
	 */
	private static BufferedImage matToBufferedImage(Mat original)
	{
		// init
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);
		
		if (original.channels() > 1)
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		}
		else
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
		
		return image;
	}
}