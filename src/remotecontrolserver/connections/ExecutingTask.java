/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserver.connections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Username
 */
public abstract class ExecutingTask <D> {
	
	/**
	 * Executes specified query regarding connection
	 * @param query query for executing
	 * @return instance of generic type
	 */
	public D execute(String query){
		D data = null;
		ResultSet resultSet = null;
		SQLiteConnector connector = new SQLiteConnector();
		
		if(connector.connect()) {
			try (Connection connection = connector.getConnection()){
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.execute();
				resultSet = preparedStatement.getResultSet();
				data = onPostExecuted(resultSet);
			} catch (SQLException ex) {
				Logger.getLogger(ExecutingTask.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return data;
	}
	
	/**
	 * Executes specified query regarding connection
	 * @param query query for executing
	 * @param values data for executing
	 * @return instance of generic type
	 */
	
	public D execute(String query, String ... values){
		D data = null;
		ResultSet resultSet = null;
		SQLiteConnector connector = new SQLiteConnector();
		
		if(connector.connect()) {
			try (Connection connection = connector.getConnection()){
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				for (int i = 0; i < values.length; i++) {
					preparedStatement.setString(i + 1, values[i]);
				}
				preparedStatement.execute();
				resultSet = preparedStatement.getResultSet();
				data = onPostExecuted(resultSet);
			} catch (SQLException ex) {
				Logger.getLogger(ExecutingTask.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return data;
	}
	
	/**
	 * Performs actions after executing
	 * @param resultSet instance of result of executing
	 * @return instance of generic type
	 */
	
	public abstract D onPostExecuted(ResultSet resultSet);
}
