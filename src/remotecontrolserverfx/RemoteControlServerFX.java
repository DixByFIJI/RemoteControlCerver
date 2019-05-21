/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserverfx;

import remotecontrolserverfx.connections.Server;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import remotecontrolserverfx.connections.ServerStopingListener;

/**
 *
 * @author Username
 */
public class RemoteControlServerFX extends Application {
	protected final int SCENE_HEIGHT = 500;
	protected final int SCENE_WIDTH = 650;
	
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
		Server server = MainController.getServer();
		if(server.isServerStarted()) {
			server.stop(new ServerStopingListener() {
				@Override
				public void onBeginning() {
					System.out.println("Begin");
				}

				@Override
				public void onStopped() {
					System.out.println("Stop");
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
