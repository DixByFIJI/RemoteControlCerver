/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserverfx.requests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import remotecontrolserverfx.connections.SQLiteConnector;
import remotecontrolserverfx.connections.ExecutingTask;

/**
 *
 * @author Username
 */
public abstract class Requestable {
	
	public static synchronized ResultSet execute(String query, ExecutingTask listener){
		ResultSet resultSet = null;
		SQLiteConnector connector = new SQLiteConnector();
		
		if(connector.connect()) {
			try {
				Connection connection = connector.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.execute();
				resultSet = preparedStatement.getResultSet();
			} catch (SQLException ex) {
				Logger.getLogger(Requestable.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return resultSet;
	}
	
	public static synchronized ResultSet execute(String query, String ... values){
		ResultSet resultSet = null;
		SQLiteConnector connector = new SQLiteConnector();
		
		Connection connection = connector.getConnection();
	
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			for (int i = 0; i < values.length; i++) {
				preparedStatement.setString(i + 1, values[i]);
			}
			preparedStatement.execute();
			resultSet = preparedStatement.getResultSet();
		} catch (SQLException ex) {
			Logger.getLogger(Requestable.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return resultSet;
	}
}
