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
		).getParent() + "\\" + CONFIG_PACKAGE;
		
		databasePath = packagePath + "\\" + DATABASE_NAME;
	}
	
	/**
	 * Checks configuration file for availability
	 * @return {@code true} if file is exists
	 */
	
	public boolean isConfigFileExist(){
		return new File(databasePath).exists();
	}
	
	/**
	 * Checks configuration package for availability
	 * @return {@code true} if package is exists
	 */
	
	public boolean isConfigPackageExist(){
		return new File(packagePath).exists();
	}
	
	/**
	 * Creates configuration package
	 * @return {@code true} if package was created
	 */

	public boolean createConfigPackage(){
		File packageFile = new File(packagePath);
		return packageFile.mkdir();
	}
	
	/**
	 * Creates configuration file
	 * @return {@code true} if file was created
	 */
	
	public boolean createConfigFile() {
		boolean isCreated = true;
		FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				String url = "jdbc:sqlite:" + databasePath;
				SQLiteDataSource sqliteDataSource = new SQLiteDataSource();
				sqliteDataSource.setUrl(url);

				try (Connection connection = sqliteDataSource.getConnection()){
					return connection != null;
				} catch (SQLException ex) {
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
	 * Makes connection to SQLite database
	 * @return {@code} true if connection is successful
	 */
	
	public boolean connect() {
		boolean isConncted = true;
		if(connection == null){
			FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
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
}
