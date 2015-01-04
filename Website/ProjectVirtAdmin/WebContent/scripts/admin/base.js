$(document).ready(function(){

	$("#getAllUsers tr td:not(:last-child)").click(function() {     
        var userID = $('td:first', $(this).parents('tr')).text();
        
        window.location = "/ProjectVirtAdmin/admin/overview?request=getalluservms&userID=" + userID;
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
