//$(document).ready(function() { 
//
//	 //Stops the submit request
//    $("#loginForm").submit(function(e){
//           e.preventDefault();
//    });
//	
//	//checks for the button click event
//    $("#submit").click(function(e){
//           
//            //get the form data and then serialize that
//            dataString = $("#loginForm").serialize();
//
//            
//            //make the AJAX request, dataType is set to json
//            //meaning we are expecting JSON data in response from the server
//            $.ajax({
//                type: "POST",
//                url: "login",
//                data: dataString,
//                dataType: "json",
//                //if received a response from the server
//                success: function( data, textStatus, jqXHR) {
//                    //our country code was correct so we have some information to display
//                     if(data.success){
//                    	
//                    	
//                    	 $("#ajaxResponse").html("<div><input type='password' name='pin' id='pin' placeholder='PIN here'/> <input id='submitPin' name='submitPin' type='submit'/> </div>");
//                     } 
//                     //display error message
//                     else {
//                         $("#ajaxResponse").html(data.error);
//                     }
//                },
//                
//                //If there was no resonse from the server
//                error: function(jqXHR, textStatus, errorThrown){
//                     console.log("Something really bad happened " + textStatus);
//                      $("#ajaxResponse").html(jqXHR.responseText);
//                }});
//            });
//    
//  //checks for the button click event
//    $("ajaxResponse").on("click", "#submitPin", function(){
//           
//            //get the form data and then serialize that
//            dataString = $("#pin").serialize();
//
//            
//            //make the AJAX request, dataType is set to json
//            //meaning we are expecting JSON data in response from the server
//            $.ajax({
//                type: "POST",
//                url: "login",
//                data: dataString,
//                dataType: "json",
//                //if received a response from the server
//                success: function( data, textStatus, jqXHR) {
//                    //our country code was correct so we have some information to display
//                     if(data.success){
//                    	
//                    	
//                    	 $("#newResponse").html(data.error);
//                     } 
//                     //display error message
//                     else {
//                         $("#newResponse").html(data.error);
//                     }
//                },
//                
//                //If there was no resonse from the server
//                error: function(jqXHR, textStatus, errorThrown){
//                     console.log("Something really bad happened " + textStatus);
//                      $("#newResponse").html(jqXHR.responseText);
//                }});
//            });
//});
//
