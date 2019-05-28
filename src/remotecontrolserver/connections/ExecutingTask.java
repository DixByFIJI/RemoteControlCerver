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
				System.out.println("Executing query error");
				Logger.getLogger(ExecutingTask.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return data;
	}
	
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
	
	public abstract D onPostExecuted(ResultSet resultSet);
}
