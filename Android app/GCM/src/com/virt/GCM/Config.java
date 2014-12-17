package com.virt.GCM;

public interface Config {

	// used to share GCM regId with application server - using php app server
	//static final String APP_SERVER_URL = "http://192.168.1.17/gcm/gcm.php?shareRegId=1";

	// GCM server using java
	static final String APP_SERVER_URL = "http://172.16.1.198:8080/ProjectVirt/login";
	
		
	// Google Project Number
	static final String GOOGLE_PROJECT_ID = "1090590412324";
	static final String MESSAGE_KEY = "message";

}
