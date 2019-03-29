/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserverfx;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import remotecontrolserver.exeptions.CommandsPoolException;

/**
 *
 * @author Username
 */
public abstract class Validation {

	private static final Pattern MUTE_PATTERN = Pattern.compile("mute", Pattern.CASE_INSENSITIVE);
	private static final Pattern UNMUTE_PATTERN = Pattern.compile("unmute", Pattern.CASE_INSENSITIVE);
	private static final Pattern SHUTDOW_PATTERN = Pattern.compile("shutdown", Pattern.CASE_INSENSITIVE);
	private static final Pattern OPEN_PATTERN = Pattern.compile("open (.+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern CLOSE_PATTERN = Pattern.compile("close (.+)", Pattern.CASE_INSENSITIVE);
	
	public static boolean check(String command) throws CommandsPoolException {
		File script = new File("src/remotecontrolserverfx/scripts/executable.vbs");
		if (command != null) {
			if(MUTE_PATTERN.matcher(command).matches()){
				execute(() -> {
					try {
						Runtime.getRuntime().exec("nircmd.exe mutesysvolume 1");
//					File script = new File("src/remotecontrolserver/scripts/executable.vbs");
//					if(script.exists()){
//						Runtime.getRuntime().exec("wscript " + script.getAbsolutePath() + " \"nircmd.exe mutesysvolume 1\"");
//					}
					} catch (IOException ex) {
						Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
					}
				});
			} else if(UNMUTE_PATTERN.matcher(command).matches()){
				execute(() -> {
					try {
						Runtime.getRuntime().exec("nircmd.exe mutesysvolume 0");
					} catch (IOException ex) {
						Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
					}
				});
			} else if(OPEN_PATTERN.matcher(command).matches()){
				execute(() -> {
					if(command.matches("open browser")){
//						Desktop environment = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
//						if (environment != null && environment.isSupported(Desktop.Action.BROWSE)) {
//							try {
//									environment.browse(URI.create("http://www.google.com"));
//							} catch (Exception e) {
//									e.printStackTrace();
//							}
//						}
						if(script.exists()){
							try {
								Runtime.getRuntime().exec(new String[]{"wscript", 
									script.getAbsolutePath(), 
									"cmd.exe /c start http://"
								});
							} catch (IOException ex) {
								Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					} else if(command.matches("open browser with (.+)")){
						Matcher openWithMatcher = Pattern.compile("open browser with (.+)").matcher(command); openWithMatcher.find();
						if(script.exists()){
							try {
								Runtime.getRuntime().exec(new String[]{
									"wscript", 
									script.getAbsolutePath(), 
									"cmd.exe /c start http://" + openWithMatcher.group(1)
								});
							} catch (IOException ex) {
								Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					} else {	
						Matcher openMatcher = OPEN_PATTERN.matcher(command); openMatcher.find();
						try {
							Runtime.getRuntime().exec(openMatcher.group(1));
						} catch (IOException ex) {
							Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				});
			} else if(CLOSE_PATTERN.matcher(command).matches()){
				Matcher closeMatcher = CLOSE_PATTERN.matcher(command); closeMatcher.find();
				try {
					Runtime.getRuntime().exec("taskkill /F /IM \"" + closeMatcher.group(1) + "\"");
				} catch (IOException ex) {
					Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else {
				System.out.println("Unknown command...");
				return false;
			}
			return true;
		} else {
			throw new CommandsPoolException(new NullPointerException("Input was null"));
		}
	}
	
	private static void execute(IExecutable instance){
		instance.call();
	}
	
//	private static final Map<Pattern, IExecutable> COMMANDS_PATTERNS = new LinkedHashMap<Pattern, IExecutable>(){{
//		put(MUTE_PATTERN, () -> {
//			try {
//						Runtime.getRuntime().exec("nircmd.exe mutesysvolume 1");
////					File script = new File("src/remotecontrolserver/scripts/executable.vbs");
////					if(script.exists()){
////						Runtime.getRuntime().exec("wscript " + script.getAbsolutePath() + " \"nircmd.exe mutesysvolume 1\"");
////					}
//					} catch (IOException ex) {
//						Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
//					}
//		});
//		
//		put(UNMUTE_PATTERN, () -> {
//			try {
//				Runtime.getRuntime().exec("nircmd.exe mutesysvolume 0");
//			} catch (IOException ex) {
//				Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
//			}
//		});
//		
//		put(OPEN__PATTERN, () -> {
//			Matcher openMatcher = OPEN__PATTERN.matcher(input)
//		});
//	}};
}
