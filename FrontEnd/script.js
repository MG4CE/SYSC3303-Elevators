

var responseLogs = []
var elevators = 

console.log("Helllo from texty")

$(".texty").text("Text that I am adding")

$(document).ready(function() {
	$(".texty").text("Text that I am adding")
    var objects = $(".main-class");
    for (var obj of objects) {
        console.log(obj);
    }
	for (let i = 0; i < 2; i++) {
		$(".texty").text("Loading text")
	$.ajax({
	type:"GET",
	dataType:"text",
	async: false,
	url: "http://localhost:105/",
	error: function (request, error) {
        console.log(request);
        alert(" Can't do because: " + error);
    },
    success: function (data) {
		console.log(data)
		$(".texty").text(data)
        
    }
	}).done(function(error,response) {
		console.log(response)
	});
	}	
});



function updateElevators(data){
	
	
	
}



