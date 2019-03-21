/**
 * Sample Skeleton for 'RemoteControlServerFX.fxml' Controller Class
 */

package remotecontrolserverfx;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import remotecontrolserverfx.exeptions.ServerRunningException;

public class FXMLDocumentController {
	
	private final String BUTTON_ACTIVATED_PSEUDO_CLASS = "btn_RunActivated";
	
	private Map<String, String> pathsMap = new LinkedHashMap<>();
	
	private int countOfClicks = 0;

		@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="pane_MainView"
    private AnchorPane pane_MainView; // Value injected by FXMLLoader

    @FXML // fx:id="vbox_Body"
    private VBox vbox_Body; // Value injected by FXMLLoader

    @FXML // fx:id="lbl_Host"
    private Label lbl_Host; // Value injected by FXMLLoader

    @FXML // fx:id="btn_Run"
    private Button btn_Run; // Value injected by FXMLLoader

    @FXML // fx:id="pane_OutHeader"
    private Pane pane_OutHeader; // Value injected by FXMLLoader

    @FXML // fx:id="hbox_Header"
    private HBox hbox_Header; // Value injected by FXMLLoader

    @FXML // fx:id="img_Logo"
    private ImageView img_Logo; // Value injected by FXMLLoader

    @FXML // fx:id="hbox_Settings"
    private HBox hbox_Settings; // Value injected by FXMLLoader

    @FXML // fx:id="lstv_Pathes"
    private ListView<HBox> lstv_Pathes; // Value injected by FXMLLoader

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		lbl_Host.setText("Current IP: " + Server.getHostAddress());
		
		pathsMap.put("browser", "C:\\Users\\Username\\AppData\\Local\\Programs\\Opera\\launcher.exe");
		
		List<HBox> hboxes = new ArrayList<HBox>(4){{
			for (int i = 0; i < 10; i++) {
				HBox tmp = new HBox(); tmp.setAlignment(Pos.CENTER_LEFT); 
				tmp.getChildren().addAll(
					new TextField(new ArrayList<>(pathsMap.keySet()).get(0)), 
					new TextField(pathsMap.get(new ArrayList<>(pathsMap.keySet()).get(0)))
				);
				add(tmp);
			}
		}};
		
//		HBox tmp = new HBox(); tmp.setAlignment(Pos.CENTER_LEFT); tmp.getChildren().add(new Button("asd"));
		lstv_Pathes.getItems().addAll(hboxes);

		lbl_Host.setOnMouseClicked((MouseEvent event) -> {
			setClipboard(Server.getHostAddress());
		});

		btn_Run.setOnMouseClicked((MouseEvent event) -> {
			Server server = new Server();
			try {
				boolean isConnected = server.start();
			} catch (ServerRunningException ex) {
				ex.printStackTrace();
				Alert serverConnectionAlert = new Alert(Alert.AlertType.ERROR, "Server connection error");
				serverConnectionAlert.show();
			}
			if(/*isConnected*/ true){
				if((countOfClicks & 1) == 0){
					btn_Run.getStyleClass().add(BUTTON_ACTIVATED_PSEUDO_CLASS);
				} else {
					btn_Run.getStyleClass().remove(BUTTON_ACTIVATED_PSEUDO_CLASS);
				}
			}
			countOfClicks++;
		});
	}

	void setClipboard(String str){
		Clipboard buffer = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.putString(str);
		buffer.setContent(content);
	}
}
