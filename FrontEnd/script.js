
function updateElevators(ids,requestFloor, currentFloor, occupents){
	for(let i =0; i<ids.length; i++){
		ids.forEach(function(i,obj) {
			$("#"+i).find(".circle").each(function(){
				$(this).removeClass("active")
				$(this).css("background-color","grey")
				if($(this).text() == requestFloor[obj]){
					
					$(this).addClass("active")
			}})
			
			$("#"+i).find(".RequestedFloorNum").text("Going to "+currentFloor[obj])
			$("#"+i).find(".currentFloor").text(currentFloor[obj])
			$("#"+i).find(".umberPassengers").text(occupents[obj])
	})
	$(".active").css("background-color","yellow")
}
}



function parseData(data){
	var data2=[]
	var split = data.split(',')
	split.forEach(element => {
				var data3 = element.replace(/\D+/g, '')
				data2.push(data3)})
	return data2
}



function startElevators(ids,requestFloor, currentFloor, occupents, _callback){
	for (var i = 0; i < ids.length; i++) {
		 
		$("<div id=\""+ids[i]+"\" class=\"card col-3\">\
		<div class=\"row\">\
		<div class=\"card align-self-left col-8 \" style=\"width: 18rem;\" id=\"1\">\
		<p class=\"card-top align-self-center\">Elevator #"+ids[i]+"</p>\
		<p class=\"card-top align-self-center square RequestedFloorNum\">"+currentFloor[i]+"</p>\
		<img class=\"card-img-top\" src=\"./download.jpg\" alt=\"Card image cap\">\
		<div class=\"card-body\">\
			<p class=\"card-text\">Current Floor <span class=\currentFloor\">"+currentFloor[i]+"</span></p>\
			<p class=\"card-text\">Passengers <span class=\numberPassengers\">"+occupents[i]+"</span></p>\
		</div>\
		</div>\
		<div class = \"container h-100 assign-self-right col-4 \">\
		<div class=\"row mt-3\">\
				<div class=\"col-6\"><span class=\"circle col-12 active\">G</span></div>\
				<div class=\"col-6\"><span class=\"circle col-12\">0</span></div>\
		</div>\
			<div class=\"row mt-3\">\
				<div class=\"col-6\"><span class=\"circle col-12\">1</span></div>\
				<div class=\"col-6\"><span class=\"circle col-12\">2</span></div>\
		</div>\
		<div class=\"row mt-3\">\
				<div class=\"col-6\"><span class=\"circle col-12\">3</span></div>\
				<div class=\"col-6\"><span class=\"circle col-12\">4</span></div>\
		</div>\
		<div class=\"row mt-3\">\
				<div class=\"col-6\"><span class=\"circle col-12\">5</span></div>\
				<div class=\"col-6\"><span class=\"circle col-12\">6</span></div>\
		</div>\
		<div class=\"row mt-3\">\
				<div class=\"col-6\"><span class=\"circle col-12\">7</span></div>\
				<div class=\"col-6\"><span class=\"circle col-12\">8</span></div>\
		</div>\
		<div class=\"row mt-3\">\
				<div class=\"col-6\"><span class=\"circle col-12\">9</span></div>\
				<div class=\"col-6\"><span class=\"circle col-12\">10</span></div>\
		</div>\
		<div class=\"row mt-3\">\
				<div class=\"col-6\"><span class=\"circle col-12\">11</span></div>\
				<div class=\"col-6\"><span class=\"circle col-12\"><p>12</p></span></div>\
		</div>\
		</div>\
		</div>\
		</div>").appendTo("#elevatorRow")
		template = ""
	}
	console.log(done)
	_callback();
}





$(document).ready(function() {
var responseLogs = []
firstRun = true
i=0


while(i<5000){
	if(i%1000 == 0){
			$.ajax({
			type:"GET",
			dataType:"text",
			async:false,
			url: "http://localhost:107/",
			error: function (request, error) {
			},
			success: function (data) {				
				const obj =JSON.parse(data)
				var ids2 = parseData(obj.ids)	
				var requestFloor2 = parseData(obj.requestTo)
				var currentFloor2 = parseData(obj.floor)
				var occupents2 = parseData(obj.occupents)
				if(firstRun){
					startElevators(ids2,requestFloor2, currentFloor2, occupents2, updateElevators(ids2,requestFloor2, currentFloor2, occupents2))
				}else{
					updateElevators(ids2,requestFloor2, currentFloor2, occupents2)	
				}
				
				
			}
			})
			firstRun = false
		}
		
		i+=1
	}	
	
})