/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserverfx.exeptions;

/**
 *
 * @author Username
 */
public class ServerInterruptedException extends RuntimeException {
	private String message;
	
	public ServerInterruptedException(Throwable ex){
		initCause(ex);
	}

	@Override
	public String getMessage() {
		return message;
	}
}
