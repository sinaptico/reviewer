package au.edu.usyd.reviewer.client.core.util.exception;

import java.io.Serializable;

public class MessageException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;
	private int statusCode = 0;
	public MessageException(){
		
	}
	
	public MessageException(String message){
		super(message);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	
}
