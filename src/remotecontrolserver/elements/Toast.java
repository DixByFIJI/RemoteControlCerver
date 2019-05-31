/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserver.elements;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author Username
 */
public abstract class Toast {
	private static int countOfMessages = 0;
	
	/**
	 * Shows pop-up message on the desktop
	 * @param title name of title
	 * @param source sender information
	 * @param toastMsg message content
	 * @param toastDelay display duration
	 * @param fadeInDelay fade-in effect duration
	 * @param fadeOutDelay fade-out effect duration
	 */
	
	public static void makeText(String title, String source, String toastMsg, int toastDelay, int fadeInDelay, int fadeOutDelay) {
		countOfMessages++;
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();   
		Stage toastStage = new Stage();
		toastStage.setResizable(false);
		toastStage.initStyle(StageStyle.TRANSPARENT);
		toastStage.setAlwaysOnTop(true);
		toastStage.setX(primaryScreenBounds.getMaxX() - 300 - 10);
		toastStage.setY(primaryScreenBounds.getMaxY() - ((100 + 10) * countOfMessages));

		VBox root = new VBox();
		root.setStyle("-fx-background-radius: 10; -fx-background-color: rgba(0, 30, 60, 0.5); -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 0, 0, 0, 0);");
		root.setOpacity(0);
		root.setAlignment(Pos.TOP_LEFT);
		
		StackPane header = new StackPane();
		header.setStyle("-fx-padding: 5; -fx-background-radius: 10 10 0 0; -fx-background-color: rgba(0,30,60, 0.8);");
		
		Image close = new Image(Toast.class.getClassLoader().getResourceAsStream("remotecontrolserver/sources/close.png"));
		ImageView imgClose = new ImageView(close);
		imgClose.setStyle("-fx-cursor: hand;");
		imgClose.setOnMouseClicked((event) -> {
			Timeline fadeOutTimeline = new Timeline();
			KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(fadeOutDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0)); 
			fadeOutTimeline.getKeyFrames().add(fadeOutKey1);   
			fadeOutTimeline.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent aeb) {
					countOfMessages--;
					toastStage.close();
				}
			}); 
			fadeOutTimeline.play();
		});
		
		Label lblTiltle = new Label(title);
		lblTiltle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 12");
		lblTiltle.setAlignment(Pos.CENTER);
		
		header.getChildren().addAll(lblTiltle, imgClose);
		StackPane.setAlignment(imgClose, Pos.CENTER_RIGHT);
		
		Label lblSender = new Label(source + ":");
		lblSender.setFont(Font.font("Verdana", 18));
		lblSender.setStyle("-fx-text-fill: #78BFFF");
		lblSender.setAlignment(Pos.CENTER_LEFT);
		lblSender.setPadding(new Insets(0, 10, 0, 10));

		Label lblMessage = new Label(toastMsg);
		lblMessage.setFont(Font.font("Verdana", 14));
		lblMessage.setStyle("-fx-text-fill: #ffffff;");
		lblMessage.setAlignment(Pos.TOP_LEFT);
		lblMessage.setPadding(new Insets(0, 10, 0, 10));
		lblMessage.setWrapText(true);
		
		root.getChildren().addAll(header, lblSender, lblMessage);
		
		Scene scene = new Scene(root, 300, 100);
		scene.setFill(Color.TRANSPARENT);
		toastStage.setScene(scene);
		
		toastStage.show();

		Timeline fadeInTimeline = new Timeline();
		KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(fadeInDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 1)); 
		fadeInTimeline.getKeyFrames().add(fadeInKey1);   
		fadeInTimeline.setOnFinished((ae) -> {
			new Thread(() -> {
				try {
					Thread.sleep(toastDelay);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				Timeline fadeOutTimeline = new Timeline();
				KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(fadeOutDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0)); 
				fadeOutTimeline.getKeyFrames().add(fadeOutKey1);   
				fadeOutTimeline.setOnFinished((ActionEvent aeb) -> {
					countOfMessages--;
					toastStage.close();
				}); 
				
				fadeOutTimeline.play();
			}).start();
		});
		
		fadeInTimeline.play();
  }
}
