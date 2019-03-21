/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserverfx.exeptions;

import java.io.IOException;

/**
 *
 * @author Username
 */
public class ServerRunningException extends RuntimeException{
	String message;
	
	public ServerRunningException(Throwable ex){
		message = ex.getMessage();
		initCause(ex);
	}
	
	@Override
	public String getMessage(){
		return message;
	}
}
