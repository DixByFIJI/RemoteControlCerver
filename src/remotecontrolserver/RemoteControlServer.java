/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserver;

import java.io.File;
import remotecontrolserver.connections.Server;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import remotecontrolserver.connections.ServerCompletingListener;

/**
 *
 * @author Username
 */
public class RemoteControlServer extends Application {
	protected final int SCENE_HEIGHT = 500;
	protected final int SCENE_WIDTH = 650;
	
	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("RemoteControlServer.fxml"));
		Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
		scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("RemoteControl");
		Image icon = new Image(getClass().getResourceAsStream("sources/icon.png"));
		Image smallIcon = new Image(getClass().getResourceAsStream("sources/icon_small.png"));
		stage.getIcons().addAll(smallIcon, icon);
		stage.setResizable(false);
		stage.show();
	}

	@Override
	public void stop() {
		Server server = MainController.getServer();
		if(server.isServerStarted()) {
			server.stop(new ServerCompletingListener() {
				@Override
				public void onBeginning() {
					
				}

				@Override
				public void onStopped() {
					
				}
			});
		}
	}
	
	

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
}
