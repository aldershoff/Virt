$(document).ready(function(){

	
	$("#monitorVM").click(function(){
		var vmID = getUrlParameter("vmid");
		window.location = "/ProjectVirt/customer/controlpanel/monitor?vmid=" + vmID;
		});

});
   

	
//	//start ajax request
//    $.ajax({
//    	
//        url: "/BackEnd/customer/controlpanel/getvms?request=getallvm",
//        //force to handle it as text
//        dataType: "text",
//        success: function(data) {
//            
//            //data downloaded so we call parseJSON function 
//            //and pass downloaded data
//            var json = $.parseJSON(data);
//            //now json variable contains data in json format
//            //let's display a few items
//            
//            $.each(JSON.parse(data), function(idx, vmBeanArray) {
//            	$('#showallvms').append("<a href='/ProjectVirt/customer/controlpanel?request=monitor&vmid=" +  vmBeanArray.vmID + "'>" + vmBeanArray.vmName + "</a>" + "<br />");
//            });
//            
// 
//            
//            
//        },
//    error: function(xhr, textStatus, errorThrown){
//    	  $('#showallvms').html(errorThrown);
//    	 
//     }
//       
//    });



/**
 * Function for getting the parameter from URL (VMID, etc)
 * @param sParam
 * @returns
 */
function getUrlParameter(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            return sParameterName[1];
        }
    }
}          
	

