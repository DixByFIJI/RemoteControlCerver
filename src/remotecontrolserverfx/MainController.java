/**
 * Sample Skeleton for 'RemoteControlServerFX.fxml' Controller Class
 */

package remotecontrolserverfx;

import remotecontrolserverfx.connections.Server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.filechooser.FileNameExtensionFilter;
import remotecontrolserverfx.connections.SQLiteConnector;
import remotecontrolserverfx.connections.ServerStartingListener;
import remotecontrolserverfx.connections.ServerStopingListener;
import remotecontrolserverfx.elements.Toast;
import remotecontrolserverfx.exeptions.ServerRunningException;
import remotecontrolserverfx.requests.Requestable;

public class MainController {
	private final String BUTTON_ACTIVATED_PSEUDO_CLASS = "btnRunActivated";

	public static Map<String, String> pathsMap = new LinkedHashMap<String, String>();

	private HBox selectedRow;

	private static Server server;

	private int countOfClicks = 0;

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

	@FXML // fx:id="imgLoader"
	private ImageView imgLoader; // Value injected by FXMLLoader

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

	@FXML // fx:id="paneConfigAdjust"
	private Pane paneConfigAdjust; // Value injected by FXMLLoader

	@FXML // fx:id="lblChooseConfig"
	private Label lblChooseConfig; // Value injected by FXMLLoader

	@FXML // fx:id="choiceBoxConfig"
	private ChoiceBox<String> choiceBoxConfig; // Value injected by FXMLLoader

	@FXML // fx:id="btnRemove"
	private Button btnRemove; // Value injected by FXMLLoader

	@FXML // fx:id="btnApply"
	private Button btnApply; // Value injected by FXMLLoader

	@FXML // fx:id="vboxConfigurationTable"
	private VBox vboxConfigurationTable; // Value injected by FXMLLoader

	@FXML // fx:id="vboxTable"
	private VBox vboxTable; // Value injected by FXMLLoader

	@FXML // fx:id="vboxTableContent"
	private VBox vboxTableContent; // Value injected by FXMLLoader

	@FXML // fx:id="hboxTableActions"
	private HBox hboxTableActions; // Value injected by FXMLLoader

	@FXML // fx:id="imgAdd"
	private ImageView imgAdd; // Value injected by FXMLLoader

	@FXML // fx:id="imgRemove"
	private ImageView imgRemove; // Value injected by FXMLLoader

	@FXML // fx:id="imgSave"
	private ImageView imgSave; // Value injected by FXMLLoader

	@FXML // fx:id="hboxSaveConfig"
	private HBox hboxSaveConfig; // Value injected by FXMLLoader

	@FXML // fx:id="txtConfigName"
	private TextField txtConfigName; // Value injected by FXMLLoader

	@FXML // fx:id="btnSaveConfig"
	private Button btnSaveConfig; // Value injected by FXMLLoader
		
	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Toast.makeText((Stage) apaneMainView.getScene().getWindow(), "Hello", 20000, 500, 500);
			}
		});
		lblHost.setText("Current IP: " + Server.getCurrentInetAddress().getHostAddress());
		
		Image loader = new Image(getClass().getResourceAsStream("sources/Loader.gif"));
		imgLoader.setImage(loader);
		
		ImageView saveConfigIcon = new ImageView(new Image(getClass().getResourceAsStream("sources/data.png")));
		btnSaveConfig.setGraphic(saveConfigIcon);
		
		ImageView removeConfigIcon = new ImageView(new Image(getClass().getResourceAsStream("sources/remove.png")));
		btnRemove.setGraphic(removeConfigIcon);
		
		ImageView applyConfigIcon = new ImageView(new Image(getClass().getResourceAsStream("sources/use.png")));
		btnApply.setGraphic(applyConfigIcon);
		
		Tooltip addTooltip = new Tooltip("Add row");
		Tooltip.install(imgAdd, addTooltip);
		
		Tooltip removeTooltip = new Tooltip("Remove selected row");
		Tooltip.install(imgRemove, removeTooltip);
		
		Tooltip saveTooltip = new Tooltip("Save changes");
		Tooltip.install(imgSave, saveTooltip);
		
		SQLiteConnector connector = new SQLiteConnector();
		
		if(connector.isConfigPackageExist()){
			if(!connector.isConfigFileExist()) {
				connector.createConfigFile();
			}
		} else if(connector.createConfigPackage()){
			connector.createConfigFile();
		}
		
		List<String> tables = Operator.getTables();
		System.out.println(tables);
//		if(tables.isEmpty()) {
//			Operator.createTable("default_config");
//			tables = Operator.getTables();
//		}
//		choiceBoxConfig.getItems().addAll(tables);
//		choiceBoxConfig.setValue(tables.get(0));
//		
//		loadData(choiceBoxConfig.getValue());
		
		System.out.println(pathsMap.values());

		imgPrograms.setOnMouseClicked((MouseEvent event) -> {
			vboxRecognition.setVisible(false);
			vboxConfigurationTable.setVisible(true);
			vboxSettings.setVisible(false);
		});
		
		imgRecognition.setOnMouseClicked((MouseEvent event) -> {
			vboxRecognition.setVisible(true);
			vboxConfigurationTable.setVisible(false);
			vboxSettings.setVisible(false);
		});
		
		imgSettings.setOnMouseClicked((MouseEvent event) -> {
			vboxRecognition.setVisible(false);
			vboxConfigurationTable.setVisible(false);
			vboxSettings.setVisible(true);
		});
		
		imgAdd.setOnMouseClicked((MouseEvent event) -> {
			addEmptyRowToTable();
		});
		
		imgRemove.setOnMouseClicked((MouseEvent event) -> {
			removeRowFromTable();
		});
		
		imgSave.setOnMouseClicked((event) -> {//Rewrite
			uploadData(choiceBoxConfig.getValue());
			loadData(choiceBoxConfig.getValue());
		});
		
		btnSaveConfig.setOnMouseClicked((MouseEvent event) -> {
			String config = txtConfigName.getText();
			if(!config.isEmpty()) {
				if(Operator.createTable(config)) {
					choiceBoxConfig.getItems().addAll(Operator.getTables());
					choiceBoxConfig.setValue(config);
					uploadData(choiceBoxConfig.getValue());
					loadData(choiceBoxConfig.getValue());
				} else {
					System.out.println("Creating configuration error");
				}
			} else {
				System.out.println("Empty field");
			}
		});
		
		btnApply.setOnMouseClicked((MouseEvent event) -> {
			loadData(choiceBoxConfig.getValue());
			vboxSettings.setVisible(false);
			vboxConfigurationTable.setVisible(true);
		});
		
		btnRemove.setOnMouseClicked((MouseEvent event) -> {
			ObservableList<String> items = choiceBoxConfig.getItems();
			if(items.size() > 1) {
				Operator.deleteTable(choiceBoxConfig.getValue());
				items.clear();
				items.addAll(Operator.getTables());
				choiceBoxConfig.setValue(items.get(0));
			} else {
				System.out.println("You cannot delete single config");
			}
		});

		lblHost.setOnMouseClicked((MouseEvent event) -> {
			setClipboard(Server.getCurrentInetAddress().getHostAddress());
		});

		btnRun.setOnMouseClicked((MouseEvent event) -> {
			server = new Server();
			if(!server.isServerStarted()){
				try {
					server.start(new ServerStartingListener() {
						@Override
						public void onBeginning() {
							System.out.println("Started...");
							imgLoader.setVisible(true);
							btnRun.setDisable(true);
						}

						@Override
						public void onEstablished() {
							System.out.println("Established...");
							btnRun.getStyleClass().add(BUTTON_ACTIVATED_PSEUDO_CLASS);
							imgLoader.setVisible(false);
							btnRun.setDisable(false);
							System.out.println("Server started: " + server.isServerStarted());
						}
					});
				} catch (ServerRunningException ex) {
					ex.printStackTrace();
					Alert serverConnectionAlert = new Alert(Alert.AlertType.ERROR, "Server connection error");
					serverConnectionAlert.showAndWait();
				}
			} else {
				server.stop(new ServerStopingListener() {
					@Override
					public void onBeginning() {
						imgLoader.setVisible(true);
						btnRun.setDisable(true);
						System.out.println("Beginned");
					}

					@Override
					public void onStopped() {
						System.out.println("Stopped");
						imgLoader.setVisible(false);
						btnRun.setDisable(false);
						System.out.println("Server started: " + server.isServerStarted());
					}
				});
				btnRun.getStyleClass().remove(BUTTON_ACTIVATED_PSEUDO_CLASS);
			}
		});
	}
	
	public void uploadData(String table){
		ObservableList<Node> rows = vboxTableContent.getChildren();
		rows.remove(0);
		List<ArrayList<String>> list = new ArrayList<ArrayList<String>>(){{
			for (Node row : rows) {
				add(new ArrayList<String>(){{
					for (Node cell : ((HBox)row).getChildren()) {
						if(cell.getClass() == TextField.class){
							String cellText = ((TextField)cell).getText();
							if(!cellText.isEmpty()){
								add(((TextField)cell).getText());
							}
						}
					}
				}});
			}
		}};

		Operator.deleteAll(choiceBoxConfig.getValue());
		Operator.insert(choiceBoxConfig.getValue(), list);
	}
	
	public void loadData(String table){
		pathsMap.clear();
		
		List<ArrayList<String>> data = Operator.selectAll(table);
		for (ArrayList<String> row : data) {
			String key = row.get(0);
			String value = row.get(1);
			pathsMap.put(key, value);
		}
		
		vboxTableContent.getChildren().clear();
		Pane header = new Pane();
		Label name = new Label("Name");
		Label path = new Label("Path");
		name.setLayoutX(75 - name.getWidth() - 12);
		path.setLayoutX(350 - path.getWidth() - 12);
		name.setLayoutY(5);
		path.setLayoutY(5);
		header.getChildren().addAll(name, path);
		header.setMinHeight(16);
		System.out.println(vboxTableContent.getPrefWidth());
		header.getStyleClass().add("header");
		vboxTableContent.getChildren().add(header);
		
		for (Entry<String, String> entry : pathsMap.entrySet()) {
			HBox row = new HBox();
			TextField nameCell = new TextField(entry.getKey());
			TextField pathCell = new TextField(entry.getValue());
			
			ImageView imgChooseFile = new ImageView("remotecontrolserverfx/sources/list.png");
			imgChooseFile.getStyleClass().add("imgChooseFile");
			imgChooseFile.setOnMouseClicked(fileChoosing);
			
			HBox backgroundImgChoose = new HBox();
			backgroundImgChoose.setStyle("-fx-background-color: #ebebeb;");
			backgroundImgChoose.setAlignment(Pos.CENTER_LEFT);
			backgroundImgChoose.getChildren().add(imgChooseFile);
			
			Tooltip tooltip = new Tooltip("Choose file");
			Tooltip.install(imgChooseFile, tooltip);
			
			nameCell.setPrefWidth((150 - 12));
			pathCell.setPrefWidth((350 - 12));
			nameCell.getStyleClass().add("cell");
			pathCell.getStyleClass().add("cell");
			nameCell.setOnMouseClicked(rowSelecting);
			pathCell.setOnMouseClicked(rowSelecting);
			
			row.getChildren().addAll(nameCell, pathCell, backgroundImgChoose);
			row.getStyleClass().add("row");
			row.setPadding(new Insets(0, 0, 0, 5));
			row.setAlignment(Pos.CENTER);
			row.setOnMouseClicked(rowSelecting);
			vboxTableContent.getChildren().add(row);
		}
	}
	
	public void addEmptyRowToTable(){
		HBox emptyRow = new HBox();
		TextField nameCell = new TextField();
		TextField pathCell = new TextField();
		
		ImageView imgChooseFile = new ImageView("remotecontrolserverfx/sources/list.png");
		imgChooseFile.getStyleClass().add("imgChooseFile");
		imgChooseFile.setOnMouseClicked(fileChoosing);
		
		HBox backgroundImgChoose = new HBox();
		backgroundImgChoose.setStyle("-fx-background-color: #ebebeb;");
		backgroundImgChoose.setAlignment(Pos.CENTER_LEFT);
		backgroundImgChoose.getChildren().add(imgChooseFile);
		
		Tooltip tooltip = new Tooltip("Choose file");
		Tooltip.install(imgChooseFile, tooltip);

		nameCell.setPrefWidth((150 - 12));
		pathCell.setPrefWidth((350 - 12));
		nameCell.getStyleClass().add("cell");
		pathCell.getStyleClass().add("cell");
		nameCell.setOnMouseClicked(rowSelecting);
		pathCell.setOnMouseClicked(rowSelecting);

		emptyRow.getChildren().addAll(nameCell, pathCell, backgroundImgChoose);
		emptyRow.getStyleClass().add("row");
		emptyRow.setAlignment(Pos.CENTER);
		emptyRow.setOnMouseClicked(rowSelecting);
		vboxTableContent.getChildren().add(emptyRow);
	}
	
	public void removeRowFromTable() {
		vboxTableContent.getChildren().remove(selectedRow);
	}
	
	public void setClipboard(String str){
		Clipboard buffer = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.putString(str);
		buffer.setContent(content);
	}
	
	public EventHandler<MouseEvent> fileChoosing = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Choose program file");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Executable files (*.exe)", "*.exe"));
			Node source = (Node)event.getSource();
			File file = fileChooser.showOpenDialog(source.getScene().getWindow());
			if(file != null) {
				((TextField)((HBox)source.getParent().getParent()).getChildren().get(0)).setText(file.getName().replaceAll("[\\W_]?\\.\\w+$", ""));
				((TextField)((HBox)source.getParent().getParent()).getChildren().get(1)).setText(file.getAbsolutePath());
			}
		}
	};
	
	public EventHandler<MouseEvent> rowSelecting = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent event) {
			if(selectedRow != null){
				selectedRow.getStyleClass().remove("selectedRow");
			}
			Object source = event.getSource();
			if(source.getClass() == TextField.class){
				selectedRow = (HBox)((TextField)source).getParent();
			} else if(source.getClass() == ImageView.class){
				selectedRow = (HBox)((ImageView)source).getParent();
			} else {
				selectedRow = (HBox)source;
			}
			selectedRow.getStyleClass().add("selectedRow");
			System.out.println("row is selected");
		}
	};
	
	public static Server getServer(){
		return server;
	}
}
