/**
 * Sample Skeleton for 'RemoteControlServerFX.fxml' Controller Class
 */

package remotecontrolserver;

import remotecontrolserver.connections.Server;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.event.DocumentEvent;
import remotecontrolserver.connections.SQLiteConnector;
import remotecontrolserver.connections.ServerStartingListener;
import remotecontrolserver.elements.Toast;
import remotecontrolserver.exceptions.ServerRunningException;
import remotecontrolserver.connections.ServerCompletingListener;

public class MainController {
	private final String BUTTON_ACTIVATED_PSEUDO_CLASS = "btnRunActivated";

	public static Map<String, String> pathsMap = new LinkedHashMap<String, String>();

	private HBox selectedRow;

	private static Server server;
	
	private final Pattern IPV4_PATTERN = Pattern.compile("(\\d{1,3}\\.){3}(\\d{1,3})");

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

	@FXML // fx:id="imgServer"
	private ImageView imgServer; // Value injected by FXMLLoader

	@FXML // fx:id="imgConfigurations"
	private ImageView imgConfigurations; // Value injected by FXMLLoader

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

	@FXML // fx:id="hboxCurrentConfig"
	private HBox hboxCurrentConfig; // Value injected by FXMLLoader

	@FXML // fx:id="lblCurrentConfig"
	private Label lblCurrentConfig; // Value injected by FXMLLoader

	@FXML // fx:id="hboxTableHeader"
	private HBox hboxTableHeader; // Value injected by FXMLLoader

	@FXML // fx:id="lblName"
	private Label lblName; // Value injected by FXMLLoader

	@FXML // fx:id="lblPath"
	private Label lblPath; // Value injected by FXMLLoader

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
		Image loader = new Image(getClass().getResourceAsStream("sources/loader.gif"));
		imgLoader.setImage(loader);
		
		ImageView antennaIcon = new ImageView(new Image(getClass().getResourceAsStream("sources/antenna.png")));
		btnRun.setGraphic(antennaIcon);
		
		ImageView refreshConnection = new ImageView(new Image(getClass().getResourceAsStream("sources/wifi.png")));
		
		Image antennaImage = new Image(getClass().getResourceAsStream("sources/antenna.gif"));
		ImageView antennaAnimIcon = new ImageView();
		antennaAnimIcon.setImage(antennaImage);
		
		ImageView saveConfigIcon = new ImageView(new Image(getClass().getResourceAsStream("sources/save.png")));
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
		
		Tooltip startingServerTooltip = new Tooltip("Server");
		Tooltip.install(imgServer, startingServerTooltip);
		
		Tooltip configurationsTooltip = new Tooltip("Configurations");
		Tooltip.install(imgConfigurations, configurationsTooltip);
		
		Tooltip settingsTooltip = new Tooltip("Settings");
		Tooltip.install(imgSettings, settingsTooltip);
		
		SQLiteConnector connector = new SQLiteConnector();
		
		if(connector.isConfigPackageExist()){
			if(!connector.isConfigFileExist()) {
				connector.createConfigFile();
			}
		} else if(connector.createConfigPackage()){
			connector.createConfigFile();
		}

		String localIp = Server.getCurrentInetAddress().getHostAddress();
		
		if(IPV4_PATTERN.matcher(localIp).matches()) {
			lblHost.setText("Current IP: " + Server.getCurrentInetAddress().getHostAddress());
			
			List<String> tables = Operator.getTables();
			if(tables.isEmpty()) {
				Operator.createTable("default_config");
				tables = Operator.getTables();
			}

			choiceBoxConfig.getItems().addListener(new ListChangeListener<String>() {
				@Override
				public void onChanged(ListChangeListener.Change<? extends String> c) {
					while (c.next()) {					
						if(c.getList().size() <= 1) {
							btnRemove.setDisable(true);
						} else {
							btnRemove.setDisable(false);
						}
					}
				}
			});

			choiceBoxConfig.getItems().addAll(tables);
			choiceBoxConfig.setValue(tables.get(0));
			loadData(choiceBoxConfig.getValue());

			txtConfigName.textProperty().addListener((observable, oldValue, newValue) -> {
				if(txtConfigName.getText().isEmpty()) {
					btnSaveConfig.setDisable(true);
				} else {
					btnSaveConfig.setDisable(false);
				}
			});
			System.out.println(pathsMap.values());

			imgConfigurations.setOnMouseClicked((MouseEvent event) -> {
				vboxRecognition.setVisible(false);
				vboxConfigurationTable.setVisible(true);
				vboxSettings.setVisible(false);

				txtConfigName.clear();
				btnSaveConfig.setDisable(true);

				lblCurrentConfig.setText(choiceBoxConfig.getValue());
			});

			imgServer.setOnMouseClicked((MouseEvent event) -> {
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
				ObservableList<String> items = choiceBoxConfig.getItems();
				if(!config.isEmpty()) {
					if(!choiceBoxConfig.getItems().contains(config)) {
						Operator.createTable(config);
						items.clear();
						items.addAll(Operator.getTables());
						choiceBoxConfig.setValue(items.get(0));
						lblCurrentConfig.setText(choiceBoxConfig.getValue());
						uploadData(choiceBoxConfig.getValue());
						loadData(choiceBoxConfig.getValue());
					} else {
						Alert alert = new Alert(Alert.AlertType.WARNING);
						alert.setHeaderText(null);
						alert.setContentText("A configuration with that name already exists");
						alert.showAndWait();
					}
				} else {
					Alert alert = new Alert(Alert.AlertType.WARNING);
						alert.setHeaderText(null);
						alert.setContentText("Empty name of config");
						alert.showAndWait();
				}
			});

			btnRemove.setOnMouseClicked((MouseEvent event) -> {
				ObservableList<String> items = choiceBoxConfig.getItems();
				if(items.size() > 1) {
					Operator.deleteTable(choiceBoxConfig.getValue());
					items.clear();
					items.addAll(Operator.getTables());
					choiceBoxConfig.setValue(items.get(0));
					loadData(choiceBoxConfig.getValue());
				} else {
					System.out.println("You cannot delete single config");
				}
			});

			btnApply.setOnMouseClicked((MouseEvent event) -> {
				vboxRecognition.setVisible(false);
				vboxConfigurationTable.setVisible(true);
				vboxSettings.setVisible(false);

				txtConfigName.clear();
				btnSaveConfig.setDisable(true);

				loadData(choiceBoxConfig.getValue());
				choiceBoxConfig.setValue(choiceBoxConfig.getItems().get(0));
				lblCurrentConfig.setText(choiceBoxConfig.getValue());
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
								imgLoader.setVisible(true);
								btnRun.setDisable(true);
							}

							@Override
							public void onEstablished() {
								btnRun.getStyleClass().add(BUTTON_ACTIVATED_PSEUDO_CLASS);
								imgLoader.setVisible(false);
								btnRun.setDisable(false);
								Platform.runLater(() -> {
									btnRun.setGraphic(antennaAnimIcon);
								});
							}
						});
					} catch (ServerRunningException ex) {
						ex.printStackTrace();
						Alert serverConnectionAlert = new Alert(Alert.AlertType.ERROR, "Server connection error");
						serverConnectionAlert.showAndWait();
					}
				} else {
					server.stop(new ServerCompletingListener() {
						@Override
						public void onBeginning() {
							imgLoader.setVisible(true);
							btnRun.setDisable(true);
						}

						@Override
						public void onStopped() {
							imgLoader.setVisible(false);
							btnRun.setDisable(false);
							Platform.runLater(() -> {
								btnRun.setGraphic(antennaIcon);
							});
						}
					});
					btnRun.getStyleClass().remove(BUTTON_ACTIVATED_PSEUDO_CLASS);
				}
			});
		}
	}
	
	/**
	 * Uploads data to specified table of database
	 * @param table name of the table to which data is will be added
	 */
	
	private void uploadData(String table){
		ObservableList<Node> rows = vboxTableContent.getChildren();
		List<ArrayList<String>> data = new ArrayList<ArrayList<String>>(){{
			for (Node row : rows) {
				ArrayList<String> rowData = new ArrayList<String>(){{
					for (Node cell : ((HBox)row).getChildren()) {
						if(cell.getClass() == TextField.class){
							String cellText = ((TextField)cell).getText();
							if(!cellText.isEmpty()){
								add(cellText);
							}
						}
					}
				}};
				
				if(rowData.size() == 2) {
					add(rowData);
				}
			}
		}};

		Operator.deleteAll(choiceBoxConfig.getValue());
		Operator.insert(choiceBoxConfig.getValue(), data);
	}
	
	/**
	 * Loads data from specified table of database
	 * @param table name of tale from which data will be downloaded
	 */
	
	public void loadData(String table){
		pathsMap.clear();
		
		List<ArrayList<String>> data = Operator.selectAll(table);
		for (ArrayList<String> row : data) {
			String key = row.get(0).toLowerCase();
			String value = row.get(1);
			pathsMap.put(key, value);
		}
		
		vboxTableContent.getChildren().clear();
		
		for (Entry<String, String> entry : pathsMap.entrySet()) {
			HBox row = new HBox();
			TextField nameCell = new TextField(entry.getKey());
			TextField pathCell = new TextField(entry.getValue());
			
			ImageView imgChooseFile = new ImageView(new Image(getClass().getResourceAsStream("sources/list.png")));
			imgChooseFile.getStyleClass().add("imgChooseFile");
			imgChooseFile.setOnMouseClicked((MouseEvent event) -> {
				chooseFile(row);
			});
			
			HBox backgroundImgChoose = new HBox();
			backgroundImgChoose.setStyle("-fx-background-color: #ebebeb;");
			backgroundImgChoose.setAlignment(Pos.CENTER_LEFT);
			backgroundImgChoose.getChildren().add(imgChooseFile);
			
			Tooltip tooltip = new Tooltip("Choose file");
			Tooltip.install(imgChooseFile, tooltip);
			
			nameCell.prefWidthProperty().bind(lblName.widthProperty());
			pathCell.prefWidthProperty().bind(lblPath.widthProperty());
			nameCell.getStyleClass().add("cell");
			pathCell.getStyleClass().add("cell");
			nameCell.setOnMouseClicked(rowSelecting);
			pathCell.setOnMouseClicked(rowSelecting);
			
			pathCell.setEditable(false);
			
			row.getChildren().addAll(nameCell, pathCell, backgroundImgChoose);
			row.getStyleClass().add("row");
			row.setAlignment(Pos.CENTER);
			row.setOnMouseClicked(rowSelecting);
			vboxTableContent.getChildren().add(row);
		}
	}
	
	/**
	 * Adds empty row into configuration table
	 */
	
	public void addEmptyRowToTable(){
		HBox emptyRow = new HBox();
		TextField nameCell = new TextField();
		TextField pathCell = new TextField();
		
		ImageView imgChooseFile = new ImageView(new Image(getClass().getResourceAsStream("sources/list.png")));
		imgChooseFile.getStyleClass().add("imgChooseFile");
		imgChooseFile.setOnMouseClicked((MouseEvent event) -> {
			chooseFile(emptyRow);
		});
		
		HBox backgroundImgChoose = new HBox();
		backgroundImgChoose.setStyle("-fx-background-color: #ebebeb;");
		backgroundImgChoose.setAlignment(Pos.CENTER_LEFT);
		backgroundImgChoose.getChildren().add(imgChooseFile);
		
		Tooltip tooltip = new Tooltip("Choose file");
		Tooltip.install(imgChooseFile, tooltip);

		nameCell.prefWidthProperty().bind(lblName.widthProperty());
		pathCell.prefWidthProperty().bind(lblPath.widthProperty());
		nameCell.getStyleClass().add("cell");
		pathCell.getStyleClass().add("cell");
		nameCell.setOnMouseClicked(rowSelecting);
		pathCell.setOnMouseClicked(rowSelecting);
		
		pathCell.setEditable(false);

		emptyRow.getChildren().addAll(nameCell, pathCell, backgroundImgChoose);
		emptyRow.getStyleClass().add("row");
		emptyRow.setAlignment(Pos.CENTER);
		emptyRow.setOnMouseClicked(rowSelecting);
		vboxTableContent.getChildren().add(emptyRow);
		
		chooseFile(emptyRow);
	}
	
	/**
	 * Removes selected row from configuration table
	 */
	
	public void removeRowFromTable() {
		vboxTableContent.getChildren().remove(selectedRow);
	}
	
	/**
	 * Adds string to clipboard
	 * @param str string which will be added to clipboard
	 */
	
	public void setClipboard(String str){
		Clipboard buffer = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.putString(str);
		buffer.setContent(content);
	}
	
	/**
	 * Calls file explorer for choosing file
	 * @param source node from which method was called
	 */
	
	private void chooseFile(Node source){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose program file");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Executable files (*.exe)", "*.exe"));
		File file = fileChooser.showOpenDialog(source.getScene().getWindow());
		if(file != null) {
			((TextField)((HBox)source).getChildren().get(0)).setText(file.getName().replaceFirst("[.][^.]+$", "").replaceAll("[\\W_]", ""));
			((TextField)((HBox)source).getChildren().get(1)).setText(file.getAbsolutePath());
		}
	}
	
	/**
	 * Gets instance of server
	 * @return instance of server
	 */
	
	public static Server getServer(){
		return server;
	}
	
	private final EventHandler<MouseEvent> rowSelecting = new EventHandler<MouseEvent>(){
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
		}
	};
}
