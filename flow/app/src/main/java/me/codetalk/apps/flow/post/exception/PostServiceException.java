package me.codetalk.apps.flow.post.exception;

/**
 * 
 * @author guobxu
 *
 */
public class PostServiceException extends Exception {

	public PostServiceException(String msg, Exception ex) {
		super(msg, ex);
	}
	
	public PostServiceException(Exception ex) {
		super(ex);
	}
	
	public PostServiceException(String msg) {
		super(msg);
	}
	
}
