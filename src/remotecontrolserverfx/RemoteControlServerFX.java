/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserverfx;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Username
 */
public class RemoteControlServerFX extends Application {
	protected final int SCENE_HEIGHT = 550;
	protected final int SCENE_WIDTH = 750;
	
	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("RemoteControlServerFX.fxml"));
		Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
		scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("RemoteControl");
		//stage.setResizable(false);
		stage.show();
	}

	@Override
	public void stop() {
		Server.stop();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
}
