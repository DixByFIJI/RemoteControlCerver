/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserverfx.elements;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author Username
 */
public final class Toast {
	public static void makeText(Stage ownerStage, String toastMsg, int toastDelay, int fadeInDelay, int fadeOutDelay) {
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();   
		Stage toastStage = new Stage();
//        toastStage.initOwner(ownerStage);
		toastStage.setResizable(false);
		toastStage.initStyle(StageStyle.TRANSPARENT);
		toastStage.setX(primaryScreenBounds.getMaxX() - 300 - 10);
		toastStage.setY(primaryScreenBounds.getMaxY() - 100 - 10);

		VBox root = new VBox();
		root.setStyle("-fx-background-radius: 10; -fx-background-color: rgba(0, 0, 0, 0.5); -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 0, 0, 0, 0);");
		root.setOpacity(0);
		root.setAlignment(Pos.TOP_CENTER);
		root.setSpacing(10);
		
		StackPane header = new StackPane();
		header.setStyle("-fx-padding: 5; -fx-background-radius: 10 10 0 0; -fx-background-color: rgba(0, 0, 0, 0.1);");
		
		Image close = new Image(Toast.class.getResourceAsStream("/remotecontrolserverfx/sources/close.png"));
		ImageView imgClose = new ImageView(close);
		imgClose.setLayoutX(300 - 39);
		imgClose.setOnMouseClicked((event) -> {
			Timeline fadeOutTimeline = new Timeline();
			KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(fadeOutDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0)); 
			fadeOutTimeline.getKeyFrames().add(fadeOutKey1);   
			fadeOutTimeline.setOnFinished((aeb) -> toastStage.close()); 
			fadeOutTimeline.play();
		});
		
		Label lblDevice = new Label("Redmi S2");
		lblDevice.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 12");
		lblDevice.setAlignment(Pos.CENTER);
		
		header.getChildren().addAll(lblDevice, imgClose);
		StackPane.setAlignment(imgClose, Pos.CENTER_RIGHT);
		
		Label lblMessage = new Label("Hello. How are you? How are you doing? asddddddddddddddddddddddddddddddddddasd");
		lblMessage.setStyle("-fx-text-fill: #ffffff");
		lblMessage.prefWidthProperty().bind(header.widthProperty());
		lblMessage.setPadding(new Insets(0, 0, 0, 10));
		lblMessage.setAlignment(Pos.CENTER_LEFT);
		
		ScrollBar sb = new ScrollBar();
		sb.setMin(0);
		sb.setMax(lblMessage.getText().length());
		
		TextField txtAnswer = new TextField(toastMsg);
		txtAnswer.setFont(Font.font("Verdana", 12));
		txtAnswer.setStyle("-fx-background-color: #9e9e9e; -fx-focus-traversable: false; -fx-background-radius: 5;");
		
		root.getChildren().addAll(header, txtAnswer);
		
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
						// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Timeline fadeOutTimeline = new Timeline();
				KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(fadeOutDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0)); 
				fadeOutTimeline.getKeyFrames().add(fadeOutKey1);   
				fadeOutTimeline.setOnFinished((aeb) -> toastStage.close()); 
				fadeOutTimeline.play();
			}).start();
		}); 
		fadeInTimeline.play();
    }
}
