/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserver;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;

import remotecontrolserver.exceptions.CommandsPoolException;
import remotecontrolserver.elements.Toast;
import remotecontrolserver.entities.NetworkDevice;
import remotecontrolserver.entities.NetworkNode;

/**
 *
 * @author Username
 */
public abstract class Validator {
	private static final String UTILITY_PACKAGE = "utility";
	private static final String UTILITY_NAME = "nircmd.exe";
	
	private static final String SCRIPT_PACKAGE = "scripts";
	private static final String SCRIPT_NAME = "executable.vbs";
	
	private static final Pattern MUTE_PATTERN = Pattern.compile("mute", Pattern.CASE_INSENSITIVE);
	private static final Pattern UNMUTE_PATTERN = Pattern.compile("unmute", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern SHUTDOWN_PATTERN = Pattern.compile("shut ?down( after (.+) (seconds?|minutes?|hours?))?", Pattern.CASE_INSENSITIVE);
	private static final Pattern RESTART_PATTERN = Pattern.compile("restart( after (.+) (seconds?|minutes?|hours?))?", Pattern.CASE_INSENSITIVE);
	private static final Pattern LOGOUT_PATTERN = Pattern.compile("log ?out", Pattern.CASE_INSENSITIVE);
	private static final Pattern CANCEL_SHUTDOWN_PATTERN = Pattern.compile("cancel", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern OPEN_PATTERN = Pattern.compile("open (.+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern OPEN_WITH_PATTERN = Pattern.compile("open (.+) with (.+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern CLOSE_PATTERN = Pattern.compile("close (.+)", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern HIDE_WINDOWS__PATTERN = Pattern.compile("hide windows", Pattern.CASE_INSENSITIVE);
	private static final Pattern RESUME_WINDOWS__PATTERN = Pattern.compile("resume windows", Pattern.CASE_INSENSITIVE);
	private static final Pattern CLOSE_WINDOW__PATTERN = Pattern.compile("close window", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern MESSAGE_PATTERN = Pattern.compile("(.+) send", Pattern.CASE_INSENSITIVE);
	private static final Pattern NOTE_PATTERN = Pattern.compile("(.+) as (.+)", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Checks incoming data from the network node
	 * @param node instance which represents a network unit 
	 * @return appropriate callback
	 */
	
	public static String check(NetworkNode node) throws CommandsPoolException {
		String callback = "An error occurred";
		String command = node.getData();
		NetworkDevice device = node.getDevice();
		
		InputStream scriptStream = Validator.class.getResourceAsStream(SCRIPT_PACKAGE + "/" + SCRIPT_NAME);
		
		File scriptPackage = new File(new File(Validator
				.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath()
			).getParentFile().getParent() + "\\" + SCRIPT_PACKAGE
		);
		
		scriptPackage.mkdir();

		File scriptFile = new File(scriptPackage.getAbsolutePath() + "/" + SCRIPT_NAME);
		
		try {
			Files.copy(scriptStream, scriptFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		InputStream utilityStream = Validator.class.getResourceAsStream(UTILITY_PACKAGE + "/" + UTILITY_NAME);
		
		File utilityPackage = new File(
			new File(Validator
				.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath()
			).getParentFile().getParent() + "\\" + UTILITY_PACKAGE
		);
		
		utilityPackage.mkdir();
		
		File utilityFile = new File(utilityPackage.getAbsolutePath() + "/" + UTILITY_NAME);
		
		try {
			Files.copy(utilityStream, utilityFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		if (command != null) {
			if(MESSAGE_PATTERN.matcher(command).matches()) {
				callback = execute(() -> {
					Matcher messageMatcher = MESSAGE_PATTERN.matcher(command); messageMatcher.find();
					String message = messageMatcher.group(1);
					showPopupCommandIntent("Message", device.getName(), message);
					return "Received...";
				});
			} else if(NOTE_PATTERN.matcher(command).matches()) {
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					Matcher noteMatcher = NOTE_PATTERN.matcher(command); noteMatcher.find();
					String text = noteMatcher.group(1);
					String name = noteMatcher.group(2);
					
					File notesDir = new File(System.getProperty("user.home") + "/Desktop/Notes");
					File note = new File(System.getProperty("user.home") + "/Desktop/Notes/" + name + ".txt");
					if(!notesDir.exists()) {
						notesDir.mkdir();
						try (BufferedWriter writer = new BufferedWriter(new FileWriter(note))) {
								note.createNewFile();
								writer.write(text);
							} catch (IOException ex) {
								Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
								return "Note creating error";
							}
					} else {
						try (BufferedWriter writer = new BufferedWriter(new FileWriter(note))) {
								note.createNewFile();
								writer.write(text);
							} catch (IOException ex) {
								Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
								return "Note creating error";
							}
					}
					
					return "Created...";
				});
			} else if(MUTE_PATTERN.matcher(command).matches()){
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					try {
						Runtime.getRuntime().exec(utilityFile.getAbsolutePath() + " mutesysvolume 1");
					} catch (IOException ex) {
						Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
					}
					return "Volume muted";
				});
			} else if(UNMUTE_PATTERN.matcher(command).matches()){
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					try {
						Runtime.getRuntime().exec(utilityFile.getAbsolutePath() + " mutesysvolume 0");
					} catch (IOException ex) {
						Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
						return "Volume error"; 
					}
					return "Volume unmuted...";
				});
			} else if(HIDE_WINDOWS__PATTERN.matcher(command).matches()) {
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					try {
						Runtime.getRuntime().exec(utilityFile.getAbsolutePath() + " sendkeypress lwin+m");
					} catch (IOException ex) {
						Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
						return "Hiding error"; 
					}
					return "Hided...";
				});
			} else if(RESUME_WINDOWS__PATTERN.matcher(command).matches()) {
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					try {
						Runtime.getRuntime().exec(utilityFile.getAbsolutePath() + " sendkeypress lwin+shift+m");
					} catch (IOException ex) {
						Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
						return "Resuming error"; 
					}
					return "Resumed...";
				});
			} else if(CLOSE_WINDOW__PATTERN.matcher(command).matches()) {
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					try {
						Runtime.getRuntime().exec(utilityFile.getAbsolutePath() + " sendkeypress lwin+shift+m");
						Runtime.getRuntime().exec(utilityFile.getAbsolutePath() + " sendkeypress alt+f4");
					} catch (IOException ex) {
						Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
						return "Resuming error"; 
					}
					return "Resumed...";
				});
			}else if(OPEN_WITH_PATTERN.matcher(command).matches()){
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					Matcher openWithMatcher = OPEN_WITH_PATTERN.matcher(command); openWithMatcher.find();
					String key = openWithMatcher.group(1);
					String parameter = openWithMatcher.group(2);
					String value = MainController.pathsMap.get(key);
					if(value != null) {
						try {
							Runtime.getRuntime().exec(value + " " + parameter);
						} catch (IOException ex) {
							Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
						}
						return key + " opened";
					} else {
						return "Unknown program";
					}
				});
			} else if(OPEN_PATTERN.matcher(command).matches()){
				System.out.println("open pattern");
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					Matcher openMatcher = OPEN_PATTERN.matcher(command); openMatcher.find();
					String key = openMatcher.group(1);
					String value = MainController.pathsMap.get(key);
					
					if(value != null){
						System.out.println(key + ":" + value);
						File programFile = new File(value);
						File programDir = programFile.getParentFile();
						try {
							Process process = Runtime.getRuntime().exec(new String[] {
								"wscript", 
								scriptFile.getAbsolutePath(), 
								"cmd.exe /c cd " + programDir.getAbsolutePath() + " && " + programFile.getAbsolutePath()
							});
						} catch (IOException ex) {
							Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
						}
						return key + " opened";
					} else {
						return "Unknown program";
					}
				});
			} else if(CLOSE_PATTERN.matcher(command).matches()){
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					Matcher closeMatcher = CLOSE_PATTERN.matcher(command); closeMatcher.find();
					String key = closeMatcher.group(1);
					String value = MainController.pathsMap.get(key);
					
					if(value != null) {
						File programFile = new File(value);
						try {
							Runtime.getRuntime().exec("taskkill /F /IM \"" + programFile.getName() + "\"");
						} catch (IOException ex) {
							Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
							return "Closing error";
						}
					} else {
						return "Unknown program";
					}
					return key + " closed";
				});
			} else if(SHUTDOWN_PATTERN.matcher(command).matches()){
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					Matcher shutdownMatcher = SHUTDOWN_PATTERN.matcher(command); shutdownMatcher.find();
					String query;
					if(shutdownMatcher.group(1) != null) {
						long timeout = Long.parseLong(shutdownMatcher.group(2));
						String unit = shutdownMatcher.group(3);
						
						if(timeout != -1) {
							timeout = unit.matches("minutes?") ? timeout * 60 : unit.matches("hours?") ? timeout * 3600 : timeout;
							
							query = "shutdown /s /f /t " + timeout;
						} else {
							return "Invalid timeout...";
						}
					} else {
						query = "shutdown /s /f /t 0";
					}
					
					try {
						Runtime.getRuntime().exec(query);
					} catch (IOException ex) {
						Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
						return "Shutdown error";
					}
					return "Switched off";
				});
			} else if(RESTART_PATTERN.matcher(command).matches()){
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					Matcher restartMatcher = RESTART_PATTERN.matcher(command); restartMatcher.find();
					String query;
					if(restartMatcher.group(1) != null) {
						long timeout = Long.parseLong(restartMatcher.group(2));
						String unit = restartMatcher.group(3);
						
						if(timeout != -1) {
							timeout = unit.matches("minutes?") ? timeout * 60 : unit.matches("hours?") ? timeout * 3600 : timeout;
							
							query = "shutdown /r /f /t " + timeout;
						} else {
							return "Invalid timeout...";
						}
					} else {
						query = "shutdown /r /f /t 0";
					}
					
					try {
						Runtime.getRuntime().exec(query);
					} catch (IOException ex) {
						Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
						return "Restarting error";
					}
					return "Restarted";
				});
			} else if(LOGOUT_PATTERN.matcher(command).matches()){
				String query = "shutdown /l";
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					try {
						Runtime.getRuntime().exec(query);
					} catch (IOException ex) {
						Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
						return "Logoff error";
					}
					return "Locked";
				});
			} else if(CANCEL_SHUTDOWN_PATTERN.matcher(command).matches()) {
				callback = execute(() -> {
					showPopupCommandIntent("Command", device.getName(), command);
					try {
						Runtime.getRuntime().exec("shutdown /a");
					} catch (IOException ex) {
						Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
						return "Aborting error";
					}
					return "Aborted...";
				});
			} else {
				callback = "Unknown command";
			}
		} else {
			throw new CommandsPoolException(new NullPointerException("Input was null"));
		}
		return callback;
	}
	
	/**
	 * Executes system request
	 * @param instance implemented instance of interface
	 * @return appropriate callback
	 */
	
	private static String execute(Executable instance){
		return instance.call();
	}
	
	/**
	 * Shows pop-up message on desktop
	 * @param title title of message
	 * @param source sender information
	 * @param message information for showing
	 */
	
	private static void showPopupCommandIntent(String title, String source, String message){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(title, source, message, 5000, 500, 500);
			}
		});
	}
}
