/**
 * Sample Skeleton for 'RemoteControlServerFX.fxml' Controller Class
 */

package remotecontrolserverfx;

import java.io.IOException;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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

		private final String BUTTON_ACTIVATED_PSEUDO_CLASS = "btnRunActivated";
	
		private Map<String, String> pathsMap = new LinkedHashMap<>();
	
		private ObservableList<UserProgram> userProgramsList = FXCollections.observableArrayList();
		
		private Server server;
		
		private int countOfClicks = 0;
		
		private boolean serverIsRunning = false;
	
		@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="apaneMainView"
    private AnchorPane apaneMainView; // Value injected by FXMLLoader

    @FXML // fx:id="vboxRecognition"
    private VBox vboxRecognition; // Value injected by FXMLLoader

    @FXML // fx:id="lblHost"
    private Label lblHost; // Value injected by FXMLLoader

    @FXML // fx:id="btnRun"
    private Button btnRun; // Value injected by FXMLLoader

    @FXML // fx:id="paneOutHeader"
    private Pane paneOutHeader; // Value injected by FXMLLoader

    @FXML // fx:id="hboxHeader"
    private HBox hboxHeader; // Value injected by FXMLLoader

    @FXML // fx:id="imgLogo"
    private ImageView imgLogo; // Value injected by FXMLLoader

    @FXML // fx:id="vboxNavigator"
    private VBox vboxNavigator; // Value injected by FXMLLoader

    @FXML // fx:id="imgRecognition"
    private ImageView imgRecognition; // Value injected by FXMLLoader

    @FXML // fx:id="imgPrograms"
    private ImageView imgPrograms; // Value injected by FXMLLoader

    @FXML // fx:id="imgSettings"
    private ImageView imgSettings; // Value injected by FXMLLoader

    @FXML // fx:id="vboxSettings"
    private VBox vboxSettings; // Value injected by FXMLLoader

    @FXML // fx:id="tblCmdsPathNames"
    private TableView<?> tblCmdsPathNames; // Value injected by FXMLLoader

    @FXML // fx:id="clmnName"
    private TableColumn<UserProgram, String> clmnName; // Value injected by FXMLLoader

    @FXML // fx:id="clmnPath"
    private TableColumn<UserProgram, String> clmnPath; // Value injected by FXMLLoader

    @FXML // fx:id="clmnFileChoose"
    private TableColumn<UserProgram, Button> clmnFileChoose; // Value injected by FXMLLoader

    @FXML // fx:id="hboxTableActions"
    private HBox hboxTableActions; // Value injected by FXMLLoader

    @FXML // fx:id="imgAdd"
    private ImageView imgAdd; // Value injected by FXMLLoader

    @FXML // fx:id="imgRemove"
    private ImageView imgRemove; // Value injected by FXMLLoader

		
	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		lblHost.setText("Current IP: " + Server.getHostAddress());
		
		initTable();
		userProgramsList = FXCollections.observableArrayList(
			new UserProgram("Opera", "C:/username/olol/Opera.exe"),
			new UserProgram("Chrome", "C:/username/olol/Opera.exe"),
			new UserProgram("Edge", "C:/username/olol/Opera.exe")
		);
		loadDataToTable(userProgramsList);

		imgPrograms.setOnMouseClicked((MouseEvent event) -> {
			vboxRecognition.setVisible(false);
			vboxSettings.setVisible(true);
		});
		
		imgRecognition.setOnMouseClicked((MouseEvent event) -> {
			vboxRecognition.setVisible(true);
			vboxSettings.setVisible(false);
		});
		
		imgAdd.setOnMouseClicked((MouseEvent event) -> {
			addEmptyRowToTable();
		});
		
		imgRemove.setOnMouseClicked((MouseEvent event) -> {
			removeRowFromTable();
		});

		lblHost.setOnMouseClicked((MouseEvent event) -> {
			setClipboard(Server.getHostAddress());
		});

		btnRun.setOnMouseClicked((MouseEvent event) -> {
			if(!serverIsRunning){
				server = new Server();
				try {
					if(server.start()){
						serverIsRunning = true;
						btnRun.getStyleClass().add(BUTTON_ACTIVATED_PSEUDO_CLASS);
					}
				} catch (ServerRunningException ex) {
					ex.printStackTrace();
					Alert serverConnectionAlert = new Alert(Alert.AlertType.ERROR, "Server connection error");
					serverConnectionAlert.show();
				}
			} else {
				serverIsRunning = false;
				Server.stop();
				btnRun.getStyleClass().remove(BUTTON_ACTIVATED_PSEUDO_CLASS);
			}
		});
	}

	void initTable(){
		clmnName.setCellValueFactory(new PropertyValueFactory<UserProgram, String>("name"));
		clmnPath.setCellValueFactory(new PropertyValueFactory<UserProgram, String>("path"));
		clmnFileChoose.setCellValueFactory(new PropertyValueFactory<UserProgram, Button>("btnFileChoose"));
		editableColumns();
	}
	
	void editableColumns(){
		clmnName.setCellFactory(TextFieldTableCell.<UserProgram>forTableColumn());
		clmnName.setOnEditCommit(e -> {
			e.getTableView().getItems().get(e.getTablePosition().getRow()).setName(e.getNewValue());
		});
		
		clmnPath.setCellFactory(TextFieldTableCell.forTableColumn());
		clmnPath.setOnEditCommit(e -> {
			e.getTableView().getItems().get(e.getTablePosition().getRow()).setPath(e.getNewValue());
		});
		
		tblCmdsPathNames.setEditable(true);
	}
	
	void loadDataToTable(ObservableList list){
		tblCmdsPathNames.setItems(list);
	}
	
	void removeRowFromTable() {
		userProgramsList.remove(userProgramsList.size() - 1);
		loadDataToTable(userProgramsList);
	}
	
	void addEmptyRowToTable(){
		if(!tblCmdsPathNames.getItems().get(tblCmdsPathNames.getItems().size() - 1).toString().replaceAll("\\s", "").isEmpty()){
			userProgramsList.add(new UserProgram("", ""));
			loadDataToTable(userProgramsList);
		}
	}
	
	void setClipboard(String str){
		Clipboard buffer = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.putString(str);
		buffer.setContent(content);
	}
}
