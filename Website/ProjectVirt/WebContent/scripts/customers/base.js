$(document).ready(function(){

	$("#getAllVM tr td:not(:last-child)").click(function() {     
        var vmID = $('td:first', $(this).parents('tr')).text();
        
        window.location = "/ProjectVirt/customer/controlpanel?request=getvm&vmid=" + vmID;
 });
	
	// When clicking on one of the table hover links, the click will result in a redirection to the monitor
	$("#monitorVM").click(function(){
		var vmID = getUrlParameter("vmid");
		window.location = "/ProjectVirt/customer/controlpanel/monitor?vmid=" + vmID;
		});


	$('#refreshVMPage').click(function(){
			var vmID = getUrlParameter("vmid");
		   window.location.href='/ProjectVirt/customer/controlpanel?request=getvm&vmid=' + vmID;
		})


$('#refreshVMState').click(function(){
	alert("refresh the state");
	
	//start ajax request
    $.ajax({
    	type: "POST",/*method type*/
        contentType: "application/json; charset=utf-8",
        url: "/ProjectVirt/customer/controlpanel/vmcontrol?vmid=$vm.getVMID()",
        data: '{"data":"' + param + '"}',/*parameter pass data is parameter name param is value */
        dataType: "json",
        success: function(data) {
            
            //data downloaded so we call parseJSON function 
            //and pass downloaded data
            var json = $.parseJSON(data);
            
            //now json variable contains data in json format
            //let's display a few items
            $(this).find("td:first").html(data.vmStatus);       
        },
    error: function(xhr, textStatus, errorThrown){
    	  $('#refreshStateResult').html(errorThrown);
    	 
     }
});
    
});


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
	
});
