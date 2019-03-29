/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserverfx;

import javafx.scene.control.Button;

/**
 *
 * @author Username
 */
public class UserProgram {
	private final String BUTTON_FILE_CHOOSER_PSEUDO_CLASS = "btnFileChoose";
	
	private String path;
	private String name;
	private Button btnFileChoose;

	public UserProgram(String name, String path) {
		this.name = name;
		this.path = path;
		this.setBtnFileChoose(new Button());
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Button getBtnFileChoose() {
		return btnFileChoose;
	}

	@Override
	public String toString() {
		return this.getName() + " " + this.getPath();
	}
	
	public void setBtnFileChoose(Button btnFileChoose) {
		this.btnFileChoose = btnFileChoose;	
		this.btnFileChoose.setPrefWidth(10);
		this.btnFileChoose.setPrefHeight(10);
		this.btnFileChoose.getStyleClass().add(BUTTON_FILE_CHOOSER_PSEUDO_CLASS);
	}
}
