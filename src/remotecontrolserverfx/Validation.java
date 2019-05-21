/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserverfx;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import remotecontrolserver.exeptions.CommandsPoolException;

/**
 *
 * @author Username
 */
public abstract class Validation {

	private static final Pattern MUTE_PATTERN = Pattern.compile("mute", Pattern.CASE_INSENSITIVE);
	private static final Pattern UNMUTE_PATTERN = Pattern.compile("unmute", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern SHUTDOWN_PATTERN = Pattern.compile("shutdown( after (.+))?", Pattern.CASE_INSENSITIVE);
	private static final Pattern RESTART_PATTERN = Pattern.compile("restart( after (.+))?", Pattern.CASE_INSENSITIVE);
//	private static final Pattern SLEEP_PATTERN = Pattern.compile("sleep( after (.+))?", Pattern.CASE_INSENSITIVE);
	private static final Pattern LOG_OFF_PATTERN = Pattern.compile("log ?off", Pattern.CASE_INSENSITIVE);
	private static final Pattern ABORT_SHUTDOWN_PATTERN = Pattern.compile("abort", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern OPEN_PATTERN = Pattern.compile("open (.+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern OPEN_WITH_PATTERN = Pattern.compile("open (.+) with (.+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern CLOSE_PATTERN = Pattern.compile("close (.+)", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern HIDE_WINDOWS__PATTERN = Pattern.compile("hide windows", Pattern.CASE_INSENSITIVE);
	private static final Pattern RESUME_WINDOWS__PATTERN = Pattern.compile("resume windows", Pattern.CASE_INSENSITIVE);
	private static final Pattern CLOSE_WINDOW__PATTERN = Pattern.compile("close window", Pattern.CASE_INSENSITIVE);
	
	public static Map<String, String> processesPID = new LinkedHashMap<String, String>();
	
	public static String check(String command) throws CommandsPoolException {
		String message = "An error occurred";
		File script = new File("src/remotecontrolserverfx/scripts/executable.vbs");
		if (command != null) {
			if(MUTE_PATTERN.matcher(command).matches()){
				message = execute(() -> {
					try {
						Runtime.getRuntime().exec("nircmd.exe mutesysvolume 1");
//					File script = new File("src/remotecontrolserver/scripts/executable.vbs");
//					if(script.exists()){
//						Runtime.getRuntime().exec("wscript " + script.getAbsolutePath() + " \"nircmd.exe mutesysvolume 1\"");
//					}
					} catch (IOException ex) {
						Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
					}
					return "Volume muted";
				});
			} else if(UNMUTE_PATTERN.matcher(command).matches()){
				message = execute(() -> {
					try {
						Runtime.getRuntime().exec("nircmd.exe mutesysvolume 0");
					} catch (IOException ex) {
						Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
						return "Volume error"; 
					}
					return "Volume unmuted...";
				});
			} else if(HIDE_WINDOWS__PATTERN.matcher(command).matches()) {
				message = execute(() -> {
					try {
						Runtime.getRuntime().exec("nircmd.exe sendkeypress lwin+m");
					} catch (IOException ex) {
						Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
						return "Hiding error"; 
					}
					return "Hided...";
				});
			} else if(RESUME_WINDOWS__PATTERN.matcher(command).matches()) {
				message = execute(() -> {
					try {
						Runtime.getRuntime().exec("nircmd.exe sendkeypress lwin+shift+m");
					} catch (IOException ex) {
						Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
						return "Resuming error"; 
					}
					return "Resumed...";
				});
			} else if(CLOSE_WINDOW__PATTERN.matcher(command).matches()) {
				message = execute(() -> {
					try {
						Runtime.getRuntime().exec("nircmd.exe sendkeypress lwin+shift+m");
						Runtime.getRuntime().exec("nircmd.exe sendkeypress alt+f4");
					} catch (IOException ex) {
						Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
						return "Resuming error"; 
					}
					return "Resumed...";
				});
			}else if(OPEN_WITH_PATTERN.matcher(command).matches()){
				Matcher openWithMatcher = OPEN_WITH_PATTERN.matcher(command); openWithMatcher.find();
				String key = openWithMatcher.group(1);
				String parameter = openWithMatcher.group(2);
				String value = MainController.pathsMap.get(key);
				if(value != null) {
					try {
						Process process = Runtime.getRuntime().exec(
							"wmic process call create \""
								+ value + " "
								+ parameter + "\""
						);

						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
						String PID = null;
						String result;
						while ((result = reader.readLine()) != null) {	
							final Pattern PID_Pattern = Pattern.compile(".*ProcessId = (\\d+);");
							if(PID_Pattern.matcher(result).matches()){
								Matcher PID_Matcher = PID_Pattern.matcher(result); PID_Matcher.find();
								PID = PID_Matcher.group(1) != null ? PID_Matcher.group(1) : "";
							}
						}
						processesPID.put(key, PID);
						System.out.println(PID);
					} catch (IOException ex) {
						Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
					}
					return key + " opened";
				} else {
					return "Unknown program";
				}
			} else if(OPEN_PATTERN.matcher(command).matches()){
				System.out.println("open pattern");
				message = execute(() -> {
					Matcher openMatcher = OPEN_PATTERN.matcher(command); openMatcher.find();
					String key = openMatcher.group(1);
					String value = MainController.pathsMap.get(key);
					if(value != null){
						System.out.println(key + ":" + value);
						try {
							Process process = Runtime.getRuntime().exec(
								"wmic process call create \"" 
								+ value + "\""
							);
							
							BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
							String PID = null;
							String result;
							while ((result = reader.readLine()) != null) {	
								final Pattern PID_Pattern = Pattern.compile(".*ProcessId = (\\d+);");
								if(PID_Pattern.matcher(result).matches()){
									Matcher PID_Matcher = PID_Pattern.matcher(result); PID_Matcher.find();
									PID = PID_Matcher.group(1) != null ? PID_Matcher.group(1) : "";
								}
							}
							processesPID.put(key, PID);
						} catch (IOException ex) {
							Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
						}
						return key + " opened";
					} else {
						return "Unknown program";
					}
				});
			} else if(CLOSE_PATTERN.matcher(command).matches()){
				message = execute(() -> {
					Matcher closeMatcher = CLOSE_PATTERN.matcher(command); closeMatcher.find();
					String key = closeMatcher.group(1);
					String value = MainController.pathsMap.get(key);
					String PID = processesPID.get(key);
					
					if(PID != null) {
						try {
							Runtime.getRuntime().exec("taskkill /F /PID " + PID);
							System.out.println("Using PID");
						} catch (IOException ex) {
							Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
							return "Closing error";
						}
					} else if(value != null) {
						try {
							Runtime.getRuntime().exec("taskkill /F /IM \"" + value + "\"");
							System.out.println("Using name");
						} catch (IOException ex) {
							Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
							return "Closing error";
						}
					} else {
						return "Unknown program";
					}
					return key + " closed";
				});
			} else if(SHUTDOWN_PATTERN.matcher(command).matches()){
				message = execute(() -> {
					Matcher shutdownMatcher = SHUTDOWN_PATTERN.matcher(command); shutdownMatcher.find();
					String query;
					if(shutdownMatcher.group(2) != null) {
						long timeout = wordsToNumber(shutdownMatcher.group(2));
						if(timeout != -1) {
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
						Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
						return "Shutdown error";
					}
					return "Switched off";
				});
			} else if(RESTART_PATTERN.matcher(command).matches()){
				message = execute(() -> {
					Matcher restartMatcher = RESTART_PATTERN.matcher(command); restartMatcher.find();
					String query;
					if(restartMatcher.group(2) != null) {
						long timeout = wordsToNumber(restartMatcher.group(2));
						if(timeout != -1) {
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
						Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
						return "Restarting error";
					}
					return "Restarted";
				});
			} else if(LOG_OFF_PATTERN.matcher(command).matches()){
				String query = "shutdown /l";
				message = execute(() -> {
					try {
						Runtime.getRuntime().exec(query);
					} catch (IOException ex) {
						Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
						return "Logoff error";
					}
					return "Locked";
				});
			} else if(ABORT_SHUTDOWN_PATTERN.matcher(command).matches()) {
				try {
					Runtime.getRuntime().exec("shutdown /a");
				} catch (IOException ex) {
					Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
					return "Aborting error";
				}
				return "Aborted...";
			} else {
				message = "Unknown command";
			}
		} else {
			throw new CommandsPoolException(new NullPointerException("Input was null"));
		}
		return message;
	}
	
	private static String execute(CMDExecutable instance){
		return instance.call();
	}
	
	private static long wordsToNumber(String input){
    long result = 0;
    long finalResult = 0;
    List<String> allowedStrings = Arrays.asList(
			"zero","one","two","three","four","five","six","seven",
			"eight","nine","ten","eleven","twelve","thirteen","fourteen",
			"fifteen","sixteen","seventeen","eighteen","nineteen","twenty",
			"thirty","forty","fifty","sixty","seventy","eighty","ninety",
			"hundred","thousand","million","billion","trillion"
    );

    if(input != null && input.length() > 0) {
			input = input.replaceAll("-", " ");
			input = input.toLowerCase().replaceAll(" and", " ");
			String[] splittedParts = input.trim().split("\\s+");

			for(String str : splittedParts) {
				if(!allowedStrings.contains(str)) {
					System.out.println("Invalid word found: " + str);
					return -1;
				}
			}

			for(String str : splittedParts) {
				switch(str.toLowerCase()) {
					case "zero" : result += 0; break;
					case "one" : result += 1; break;
					case "two" : result += 2; break;
					case "three" : result += 3; break;
					case "four" : result += 4; break;
					case "five" : result += 5; break;
					case "six" : result += 6; break;
					case "seven" : result += 7; break;
					case "eight" : result += 8; break;
					case "nine" : result += 9; break;
					case "ten" : result += 10; break;
					case "eleven" : result += 11; break;
					case "twelve" : result += 12; break;
					case "thirteen" : result += 13; break;
					case "fourteen" : result += 14; break;
					case "fifteen" : result += 15; break;
					case "sixteen" : result += 16; break;
					case "seventeen" : result += 17; break;
					case "eighteen" : result += 18; break;
					case "nineteen" : result += 19; break;
					case "twenty" : result += 20; break;
					case "thirty" : result += 30; break;
					case "forty" : result += 40; break;
					case "fifty" : result += 50; break;
					case "sixty" : result += 60; break;
					case "seventy" : result += 70; break;
					case "eighty" : result += 80; break;
					case "ninety" : result += 90; break;
					case "hundred" : result *= 100; break;
					case "thousand" : {
						result *= 1000;
						finalResult += result;
						result = 0;
						break;
					}
					case "million" : {
						result *= 1000000;
						finalResult += result;
						result = 0;
						break;
					}
					case "billion" : {
						result *= 1000000000;
						finalResult += result;
						result=0;
						break;
					}
					case "trillion" : {
						result *= 1000000000000L;
						finalResult += result;
						result = 0;
						break;
					}
				}
			}

			finalResult += result;
			result = 0;
			return finalResult;
		} else {
			System.out.println("Incorrect input: " + input);
			return -1;
		}
	}
}
