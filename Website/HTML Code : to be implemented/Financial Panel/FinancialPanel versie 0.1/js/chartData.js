

	var num1 = 200;
	var num2 = 300;
	var num3 = (num1 + num2) / 2
	

	var barChartData = {
		labels : ["Total Users","Total VM's", "Average VM per Users"],
		datasets : [
			{
				fillColor : "rgba(220,220,220,0.5)",
				strokeColor : "rgba(220,220,220,1.8)",
				highlightFill: "rgba(220,220,220,0.75)",
				highlightStroke: "rgba(220,220,220,1)",
				data : [num1, num2, num3]
			}
			
		]

	}
	window.onload = function(){
		var ctx = document.getElementById("bchart").getContext("2d");
		window.myBar = new Chart(ctx).Bar(barChartData, {
			responsive : true
		});
	}
