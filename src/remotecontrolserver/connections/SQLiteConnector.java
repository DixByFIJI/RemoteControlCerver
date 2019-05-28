/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserver.connections;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.SQLSyntaxErrorException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConnectionConfig;
import org.sqlite.SQLiteDataSource;

/**
 *
 * @author Username
 */
public class SQLiteConnector {	
	private final String TAG = "SQLiteConnector";
	
	public static final String CONFIG_PACKAGE = "configs";
	public static final String DATABASE_NAME = "user_config.db";
	
	private String packagePath;
	private String databasePath;
	
	private Connection connection;

	public SQLiteConnector() {
		packagePath = new File(
			this
				.getClass()
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath()
		).getParentFile().getParent() + "\\" + CONFIG_PACKAGE;
		
		databasePath = packagePath + "\\" + DATABASE_NAME;
	}
	
	public boolean isConfigFileExist(){
		return new File(databasePath).exists();
	}
	
	public boolean isConfigPackageExist(){
		return new File(packagePath).exists();
	}

	public boolean createConfigPackage(){
		File packageFile = new File(packagePath);
		System.out.println("package creating");
		return packageFile.mkdir();
	}
	
	public boolean createConfigFile() {
		boolean isCreated = true;
		System.out.println("Creating config database");
		FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				String url = "jdbc:sqlite:" + databasePath;
				SQLiteDataSource sqliteDataSource = new SQLiteDataSource();
				sqliteDataSource.setUrl(url);

				try (Connection connection = sqliteDataSource.getConnection()){
					return connection != null;
				} catch (SQLException ex) {
					log("Database file creating error");
					Logger.getLogger(SQLiteConnector.class.getName()).log(Level.SEVERE, null, ex);
					return false;
				}
			}
		});

		Thread creatingThread = new Thread(task);
		creatingThread.setName("DBCreatingThread");
		creatingThread.start();
		try {
			isCreated = task.get();
		} catch (InterruptedException | ExecutionException ex) {
			Logger.getLogger(SQLiteConnector.class.getName()).log(Level.SEVERE, null, ex);
		}
		return isCreated;
	}
	
	/**
	 * Make connection to remote MySQL database
	 * @param path
	 * @return @code true if connection is successful
	 */
	
	public boolean connect() {
		boolean isConncted = true;
		if(connection == null){
			FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					System.out.println("NEW CONNECTION...");
					String url = "jdbc:sqlite:" + databasePath;
					SQLiteDataSource sqliteDataSource = new SQLiteDataSource();
					sqliteDataSource.setUrl(url);

					try {
						connection = sqliteDataSource.getConnection();
					} catch (SQLException ex) {
						Logger.getLogger(SQLiteConnector.class.getName()).log(Level.SEVERE, null, ex);
						return false;
					}
					return true;
				}
			});
			
			Thread connectThread = new Thread(task);
			connectThread.setName("DBConnectionThread");
			connectThread.start();
			try {
				isConncted = task.get();
			} catch (InterruptedException | ExecutionException ex) {
				Logger.getLogger(SQLiteConnector.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return isConncted;
	}
	
	public Connection getConnection(){
		return connection;
	}
	
	private void log(String message){
		System.out.println(TAG + ": " + message);
	}
}
