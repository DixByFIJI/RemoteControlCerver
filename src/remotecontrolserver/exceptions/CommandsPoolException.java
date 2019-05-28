/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserver.exceptions;

/**
 *
 * @author Username
 */
public class CommandsPoolException extends NullPointerException{
	
	private final String message;
	
	public CommandsPoolException(Throwable e){
		this.message = e.getMessage();
		initCause(e);
	}

	@Override
	public String getMessage() {
		return message;
	}
}
