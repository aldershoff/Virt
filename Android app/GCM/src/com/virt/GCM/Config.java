package com.virt.GCM;

/**
 * Class used for connecting with APP server and giving the project number
 * 
 * @author KjellZijlemaker
 * 
 */
public interface Config {

	// GCM server using java
	static final String APP_SERVER_URL = "http://projectvirt.bitnamiapp.com/ProjectVirt/login";

	// Google Project Number
	static final String GOOGLE_PROJECT_ID = "1090590412324";
	static final String MESSAGE_KEY = "message";

}
