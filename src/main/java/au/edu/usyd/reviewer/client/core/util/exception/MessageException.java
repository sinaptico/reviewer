package au.edu.usyd.reviewer.client.core.util.exception;

import java.io.Serializable;

public class MessageException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public MessageException(){
		
	}
	
	public MessageException(String message){
		super(message);
	}
}
