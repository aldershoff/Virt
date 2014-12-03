
            var barData = {
                labels : ["VM Online","VM Offline"],
                datasets : [
                    {
                        fillColor : "#48A497",
                        strokeColor : "#48A4D1",
                        data : [456, 500]
                    }

                ]
            }
            // get bar chart canvas
            var income = document.getElementById("bchart").getContext("2d");
            // draw bar chart
            new Chart(income).Bar(barData);
