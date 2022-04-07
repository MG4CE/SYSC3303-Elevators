var firstRun = true;
var validElevators = []
var elevatorPath = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAoHCBEREREPEQ8PDxIPEhIPDxEPER8PDw8RGBQaGRgUGBgcLjwlHB4sHxgYJjsmKy8xNTU3GiQ7QDs0Py80NjEBDAwMEA8QHhISHjghISQ7NDE0NDQ0NDQ/NDQ0NDE0NDQ0NDQ0MT8/MTQ0MTExMTQ0MTE/NDQ0ND80PzQ0PzQxNP/AABEIAOkA2AMBIgACEQEDEQH/xAAcAAABBQEBAQAAAAAAAAAAAAAAAQIDBQYECAf/xABJEAACAQIABwsIBwUIAwAAAAAAAQIDEQQGEiFSkrIFEyIxM1FxcnORsRYyNEFhs9HSFSRUYnSDoUJTgcLhFCNDRILB8PFjhKP/xAAYAQADAQEAAAAAAAAAAAAAAAAAAQMCBP/EACARAQACAgIDAQEBAAAAAAAAAAABAhExAxMSMlFBIWH/2gAMAwEAAhEDEQA/APswAAAgHNh+FKlBztd3SS4rtlJ9N1uanqv4morNtMzaI20gGc+m63NT1X8Q+m6vNT1X8R9di7IaMDO/TdXmhqv4h9NVeaGq/iHXYdkNEBnfpqrzQ1X8RfpmrzQ1X8Q67DshoQM99M1eaGq/ic/lRH99g+sviHXYdkNSBl1jMv3uD6y+I5YyL97g+t/UfXYdkNMBmljF/wCSh3/1F8oXp0O/+oddh2Q0gGfhu3OXmunLq5/9yT6Wqc0NV/EXXYdkLwCk+lqnNDVfxD6Vqc0NV/EOuw7IXYFL9KVOaGq/iH0pU5odz+Iddh2QugKX6Uqc0O5/EX6Tqc0O5/EOuw7IXIEGC18uKla3qa5mTmNNxOSgAAAAAAVOMPIx7SPgzOGixi5GPXWzIzZfj0hybOuKNuKVYOuKNuKAOuKNuABzbry+rYR2NTYZJg25tPe6cYwilkRSSWbiRHupCU8HrwisqU6VSMUuNtwaSFwTGDBMiDlUlCWRFOE6U1KDSzp5uMA6cLowpU+DCN7qKzetvjIciMJzhJRnanlxulFt8ViHD92MEqwlD+0ZLdnF73N2ad1+yVtDdKm6061bCYO8VThCnSmoRje7k7xzt5ujPzhg8n4LS/tFatCcYpUnBWj5sMpN29rLlYHTiklBJJZvYitwTdLBac6s1WVqrjLNTne6Vm3wR+E7s0JcGM3b18Cef9BT/RH8PwKcXhM4xVoqms+lwlnLYoNyainhFSUMpxVNJycXGN8pZs/rzMvbjI+4XGi3AHBcQLgDhbjRbgF3uRyb6z8Edxwbj8m+u/BHeclvaXRXUFAAE0AAACoxj5GPaLwkZo0uMfIx7ReEjMl+PSHJstyv3T3R3hxuo5Mk227u2ey4iwuZzGzih0fzIrG2EvlNT54asvgHlNDnhqyMi4ZUoQu45dSEG1xpSkl/uXjxVh9prfob8WPJY+VEPuasg8qYfc1ZlY8VofaK36CPFaH2it+geA8ln5Vw+5qzFWNkOeGrMqXivD7RW/Qa8V4faK36B4DyXPlbDnhqzDythzw1ZlK8WIfaK36DMGxbjPCN43+qlvbqZSte6aVujOKa4OLZXvlbD7mrMXyth9zVmVnklD7TX/QTyRh9pr/oLEDK08rYfc1Zh5XQ+5qzKrySh9pr/oHknD7TX/QMQMytfK6H3NWYvldD7mrMqfJOH2mv+geScPtNf9B4g8yto43RbSShdtJcGfGzRbn4S6lOM5JJtyVlxZm0fKKNPIqOF3LIrOCb42oztf8AQ+obiP8AuIdM9pimMCJWNwuNFuI17uPyb6z8Ed5X7jcm+u/BFgclvaXRXUFAAE0AAACnxk5GPaLZkZi5p8ZeRj2kfCRlrl+PSHJs+5nsauKHR/Mi/M/jRxQ6P5kVjac6ZqmuHS7alto3rRhKa4dLtqW2jetFoRsiaGtErQ1o0WUTQjRI0NaAzLDsApKOF39boyu/9SzHRSp2zvj8BuC+l/ky2kYtpusH2GjrCMyZrQjQ4GgBgCtCCDALl6n4ifvGfTNxeQh0z2mfM1y9T8RP3jPpe43Iw6Z7TFb8OP1YC3GC3E0v9xeTfXfgiwK7cTk3134IsTkt7S6K+sFAAE0AAACmxm5CPaR8JGWuanGfkI9pHZkZS5fj9UOTZ9ygxn/Y6v8AMi9KHGT9jqvaRWNpzpn6fn0u1pbaN1cw0eUpdrT2kbdMtCNjhGhUwNkY0SUqf7T/AIfEdTp3zvi8SczJ1Msc+C+l/ky2kdTRz4N6X+TLaRi2lK7OsI0SDWhzBGNCND2htjJmtCWHtCWAPnn+PU/E1PeM+k7jcjHpntM+b/5ip+Jqe8Z9H3H5GPTLaZm34df133FG3FE00G4fJPrvwRZFbuFyT678EWRyW9pdFfWCgACaAAABS40chHtI7MjJ3NXjTyEe0jsyMpcvx6Q5NnFFjHxw6r2i8KPGHjh1X4lY2nOlFHz6fa09tGzuY39un2tPbRr0y0JSkTJKcbvxI4K7t/xHVFJKxrJYPXMKxExbiaI0c+Del/ky2kdJzYMvrf5MtpGbaOu0thGPEaAGNDWh7QjQSDGhLD2hthG+d/5ip+Jqe8Z9G3I5GPTLaZ85fpE/xM/eM+i7k8jHpltMxb8OHfcUZcUTTRbhck+vLwRZlZuDyT68vBFmclvaXRX1goAAmgAAAUmNPIR7SOzIyVzWY1ejx7SOzIyNy/H6ocmzim3f44dV+JcFPu6+FDqvxKxtOdKRrh0+1p7aNbHO7IybXCp9pT20bOlC3S+MrEp4PhGy8SRMjTHJjyMJExyZGmOTGDkyDB/S/wAmW0iZMgwZ/W/yZbSM20cbdDQlhyBo0RlhLD2hr5zJmM56tX1LvFqzvmXF4kNhxDMywkeXn+In7xn0Xcrko9MtpnzpcvP8RP3jPom5fJR6ZbTJW/FKu64oy4ommk3A5J9eXgi0KvF/kn15eCLQ5Le0uivrBQABNAAAAo8a/R49pHZkZC5rsbPR49pHZkY+5fj9UOTZxUbt+dDqvxLa5UbtedDqvxKxtOdKr9un2tPbRsUzHtcOn2tPbRrkyv6x+HpipjExyYA5McmRpjkwJImQ4M/rf5MtpEiZFgr+t/ky2kFtHXbqTHIYmJKaRpk6bSzs5qk2+gJTb4wyb8Q8FMomiWnR9cu4lhStx8fgPMzJxD5rJfWKn4mp71n0Hcvko9MtpmAkvrFT8TU96zfbmcnHpltMlb8Uh2XFEuFxNNNi9yL678EWpU4u8jLtHsotjkt7S6K+sFAAE0AAACixt9Hj2kdmRjbmyxu9Hj2kdmRjLl+P1Q5NnFVux58Oq/Es7lZut59Pqy8SsbTnStkuFT7Wnto1VzLTXCh2lPbRprlf1j8SJjkyNMcmASJghqYqYA9EeCv63+TLaQ5M5sHrfXMlce8SbfNwkE6OHXOpb2shyr5+MiTJqcL5+JeJrSc/06EW/wDnEdMIpfEbHNmHphk8YOEsCYoG+ayX1ip+Jqe9Zvdzn/dx6ZbTMJJfWKn4mp71m63P5OPTLxI2/G4dYtxgtxNNRi7yMu0eyi2KjFzkZdpLZiW5yW9pdFdQUAATQAAAKDG/0ePax2ZGNubLHD0ePax2ZGLuX4/VDk2dcrN1fPh1ZFlcrd0/Pp9WRWNpzpwVOOHaU9tGjuZ2pxw7Snto0SZTP9Y/DkxyZGmOQwkTHXIsq2cgnVvm9XiASzq+pcXOcuAv67/68tpEkc+ZZyTAKSWF5XG95kujhILaFdp6VP1vuJ0MTFTAYSJjkyNMVMCSJipjEOTNB87fpFT8TP3rNxgD4C6ZeJiXy8/xNT3jNrgT4C6X4kbfjcOoW4wW4mmqxb5GXaPZiW5T4tchLtHsouDkt7S6K+sFAAE0AAADP44+jR7WPhIxVza44+jR7WOzIxJfj9UOTZ1yu3R8+HVkd9zgw/lIdWRWNpzpyVeOn2lPaReplHW/Y7SG0i6uUnbEaSJhKaSuyGdRR6fUiCU23dhAlLObl/shYQcviJSp3zvMvE6o5syzGskdCKXF/wBiYG/rf5MtpCobgfpf5MtpGbaartOmOTI0xyYyPTHJkdxkq69WfwAOhCSqpe1+w5JVG+N/w9QiY8M5Y6OetUfPhFR//Rm0wPzF0vxMZDlZ9vP3jNlgvmLpfiSt+KQ6QuIFxNNZizyEu0l4RLgp8WOQl2ktmJcHJb2l0V1BQABNAAAAz+OPo0e1jsyMSbbHH0aPax2ZGJL8fqhybBwYdylPqyO84MN5SHVkVjac6c9deZ2lPaRZVKtsyzvwK3CuKPXhtE8U27cZSdsRpJe/tZ0UqXrl3CUqajn43z8xMmGTPTHJkaY5MRHpjcCf1v8AJltIbKolxv8Ah6yDAsIbwzJSst4k/b5yCdHG3a5JcbsMlX5l/FnLlX/qKmUZylc2+N3BMjTHJgSRMcmRpjkwJlKa/vZ9vPbNfg3mL+PiZClysu3ntmvwd8Ffx8SNlYTDrjLi3E012K/IS7SWzEuSlxW5CXaS2Yl0clvaXRXUFAAE0AAADP44+jx7WOzIxJ9A3fwCeEUlTg4xkpxnebaWZNepPnM95J4Rp0NaXyluO0RH9RvWZn+KA4MM5Wn1ZGu8k8I06GtL5Tmr4mYVKcJqpg9opp3nK+f/AElIvXO2JrbGmUwpZo+2pDaO+EVHMv8AsuK2JOFSybVMHzSjLPOXEnfROryQwnTwfWl8pqeSn1mKW+KFMEy+eKOFeqeD/wAZS+Uilifhb/xMG6FOVtkOyn0eFvillWivb0EUq7fsXs4y9WJeFfvMH15fKO8jMK/eYPry+UcclPo8LfGdTE3Of1z8iW0jRrE3CtPB9aXyhgmJuFQwjfnUwdx3uULKcsrKbT0eLME8lMbEUtnShTHJl+sT8J08H1pfKKsUMJ08H1pfKPtp9Lrt8UKYqZfLFDCdOhrS+UVYpYTp0NaXyj7afR4W+KJMcmXnklhOnQ1pfKKsU8J06GtL5Q7afR12+PndHlJdvPbNZQ83v8RaeIGFqTk6mC2dSVTz5Xs5X0eMvIYrYQlbLoaz+UlPJX61WlvinuLcuPJnCNOjrS+UXyZr6VHWl8ovOv1rxt8WuK3IS7SWzEuit3FwKdCm6cnFtzcrwbazpL1r2Fkc9pzMr10UAARot+hpx1kG/Q046yPJVDBt8nGEIpym7RTsru2ZXfrfF0ki3PqOEKiozlCdsmUYOUc8nFJtLM3JWS43dc6NeBZesd+hpx1kG/Q046yPKMtyK6yb4NV4UZSSVKTklGWTK6SurO3HzrnRNS3Cryp746cKcXNU47/ONCVWTUZWhGVnLNKLzcd1a4eIy9Ub9DTjrIN+hpx1keVK+4mE05OMsEr3VV4OmqEpRlVTayIyStKWZ5kOluHXjk5dHe1KLlepHIjG0pxyJtrgzvTnwXn4IeP+jL1Tv0NOOsg36GnHWR5GyI6K7gyI6Me4PAZeud+hpx1kG/Q046yPI2RHRj3BkR0Y9weAy9c79DTjrIN+hpx1keRsiOjHuDIjox7g8Bl6536GnHWQb9DTjrI8jZEdGPcGRHRj3B4DL1zv0NOOsg36GnHWR5GyI6Me4MiOjHuDxGXrnfoacdZBv0NOOsjyNkR0Y9wZEdGPcHgMvXO/Q046yDfoacdZHkbIjox7hMmOjHuDwGXrrfoacdZBv0NOOsjyNkR0Y9wmRHmj3B4DL11v0NOOsg36GnHWR5FyY6Me4MmOjHuDwGXrrfoacdZBv0NOOsjyKox5o9yDJjzR7g8Rl6636GnHWQp5EUY6Me4A8BlJCcoyjOLyZQkpwloyi7p96LmWMM73VKEFF2pxhmjCm8hOm82U1aCzpxztvPmtSgUCxpbpxhGMFQThTlCdNSqNzThKU4ZUkldKU6l1ZXU1xZKZ1YLjFOk68owWXhDbbdWe854qHCop5M2s7i3xN3z2RSAING8bZ3nJYNRTqRnRqcOdpYPOc5ypKz4Lyqk+Gs6VvXdvg3R3Y3+hRwbeIU4YK5f2a03OdOM5znOLb85PKhx8W9q3G0VYBiAAABgAAAAAAAAAAAAAAAAAAB1bn4fKi5NQpzU8i8ascuPBfN7YynB+ycjlAAt4buWefA8DkuPJlS4N/XmXQu+XPmHu7JxhGWDYLU3uDhF1KeVdOyzq9rcFWStbPzlQAgtJ7s5VTfHg2DNqnCnZw4LjDJs2vXK0bN82Yct22s6wbBoNVFOMqcN6nGKteCnHPktKXt4TKkAC9jjRWUsre6MnZRvJPLaU8uzcbL2cXixKeM9WMVFUsHeSrJuMm+TULvPzJLoze0owHgJsLwh1JyqNWc7Nq986ilxvov8AxAhAA//Z"
var audioObjects =[];

function updateConsole(timingInfo){

	console.log(timingInfo)
	if(timingInfo != null && timingInfo.length > 0)
	{
		
		if(timingInfo !=""){
			$("#timingInfo").prepend(timingInfo+"\r\n")
		}
	}
}

/**
 * Main Gui update method
 * Takes the data from the JSON returned and updates each elevator with it's new data
 * */
function updateElevators(ids, requestFloor, currentFloor, directions, states, numFloors) {
	for(let i =0; i<ids.length; i++){
		//Loop through all ids to update which floor button to light up
		ids.forEach(function(i,obj) {
			$("#"+i).find(".circle").each(function(){
				$(this).removeClass("active")
				$(this).css("background-color","grey")
				
				if($(this).text() == requestFloor[obj] | (($(this).text() == "G") & requestFloor[obj] == 0) & states[obj] != 9) {
					$(this).addClass("active")
			}})
			//Change text of the elevator current and dest floor
			$("#"+i).find(".RequestedFloorNum").text(requestFloor[obj])
			$("#"+i).find(".currentFloor").text(currentFloor[obj])

			//Deal with states
			//Updates info about elevator state
			//Updates the picture of the elevator
			if(states[obj] == 0 & currentFloor[obj] == requestFloor[obj] & directions[obj] == 0){
				//stopped
				$("#"+i).find(".elevatorImage").attr('src',"./images/sleep.png")
				audioObjects[obj].pause()
			}
			else if(states[obj] == 2) {
				//Door Fault
				$("#"+i).find(".elevatorImage").attr('src',"./images/error.png")
				$("#"+i).find(".RequestedFloorNum").text("SoftFault")
				$("#"+i).find(".currentFloor").text(currentFloor[obj])
				audioObjects[obj].pause()
			}
			else if(states[obj]==9){
				$("#"+i).find(".elevatorImage").attr('src',"./images/killed.png")
				$("#"+i).find(".currentFloor").text("Offline")
				$("#"+i).find(".RequestedFloorNum").text("Offline")
				audioObjects[obj].pause()
			}
			else{
				//Moving
				if(requestFloor[obj] > currentFloor[obj] | directions[obj] == 2){
					$("#"+i).find(".elevatorImage").attr('src',"./images/up.png")
					audioObjects[obj].play()
				}
				else if(requestFloor[obj] < currentFloor[obj] | directions[obj]==1){
					$("#"+i).find(".elevatorImage").attr('src',"./images/down.png")
					audioObjects[obj].play()
				}
				else{
					$("#"+i).find(".elevatorImage").attr('src',"./images/sleep.png")
					audioObjects[obj].pause()
				}
			}
	})
	$(".circle.active").css("background-color","yellow")
}
}


/**
 * 
 * Parse array as a string into an array
 * */
function parseDataFromArrayStringIntoArray(data) {
	var data2=[]
	if(data != null){
	var split = data.split(',')
	split.forEach(element => {
				var data3 = element.replace(/\D+/g, '')
				data2.push(data3)})
	}
	return data2
}


/**
 * Method used on the first ajax call to dynamically add elevator elements to the webpage
 * */
function startElevators(ids, requestFloor, currentFloor, directions, states, numFloors) {
	for (var i = 0; i < ids.length; i++) {
		 validElevators.push(ids[i])
		 audioObjects.push(new Audio('elevatorSound.mp3'))
		 /*jshint multistr: true */
		 var top ="<div id=\""+ids[i]+"\" class=\"card col-3\">\
		<div class=\"row\">\
		<div class=\"card align-self-left col-8 \" style=\"width: 18rem;\" id=\"1\">\
		<p class=\"card-top align-self-center\">Elevator #"+ids[i]+"</p>\
		<div class=\"row mt-1 align-self-center\">\
		<img class=\"elevatorImage align-self-center\" src=\"./images/sleep.png\" alt=\"Card image cap\">\
		</div>\
		<div class=\"row mt-1 align-self-center\">\
		<p class=\"card-top align-self-center square currentFloor\">"+currentFloor[i]+"</p>\
		</div>\
		<img class=\"card-img-top\" src=\""+elevatorPath+"\" alt=\"Card image cap\">\
		<div class=\"card-body\">\
			<p class=\"card-text\">Requested Floor <span class=\"RequestedFloorNum\">"+requestFloor[i]+"</span></p>\
		</div>\
		</div>\
		<div class = \"container h-100 assign-self-right col-4\">\
		<div class =\"center\">"
		var buttons="";
		for(let k =numFloors; k>=0; k--){
			console.log(typeof(k))
			if(k %2 == 0){
				buttons += "<div class=\"row mt-2\">"
				if(k == 0){
						buttons += "<div class=\"col-6\"><span class=\"circle col-12 active\">G</span></div>"
				}
				else{
						buttons += "<div class=\"col-6\"><span class=\"circle col-12\">"+k+""+"</span></div>"
				}
			}
			else{
					buttons += "<div class=\"col-6\"><span class=\"circle col-12\">"+k+"</span></div>"
					buttons += "</div>"
				}
		}
		buttons+="</div>"
		buttons+="</div>"
		
		var template = top + buttons
		
		$(template).appendTo("#elevatorRow")
		template = ""
	}
}

/**
 * Method that calls the api on the server
 * It is in a infinite callback of running every 1 second
 * */
function AjaxCall() {
		$.ajax({
			type:"GET",
			dataType:"text",
			async:false,
			url: "http://localhost:107/",
			error: function (request, error) {
			},
			success: function (data) {		
				console.log(data)			
				const obj =JSON.parse(data)
				var ids2 = parseDataFromArrayStringIntoArray(obj.ids)	
				var requestFloor2 = parseDataFromArrayStringIntoArray(obj.requestTo)
				var currentFloor2 = parseDataFromArrayStringIntoArray(obj.floor)
				var numFloors2 = obj.numFloors
				var directions2 = parseDataFromArrayStringIntoArray(obj.directions)
				var states2 = parseDataFromArrayStringIntoArray(obj.states)
				var timingInfo = obj.timing
				console.log(JSON.stringify(timingInfo))
				console.log(JSON.stringify(timingInfo))
				console.log(typeof(JSON.stringify(timingInfo)))
				if(firstRun){
					startElevators(ids2,requestFloor2, currentFloor2, directions2, states2, numFloors2)
					firstRun = false
				}else{
				updateElevators(ids2,requestFloor2, currentFloor2,directions2, states2, numFloors2)	
				updateConsole(timingInfo)
				}
			}
			})
			//Starts the infinite call back loop of ajax calls every 1 second
			//It allows for updates to happen since the script won't run until the 1second
			//This gives time for the script to update before the next call
			setTimeout(AjaxCall, 1000);
}



/**
 * When the document fully loads run the main script
 * */
$(document).ready(function() {
	var responseLogs = []
	//Call Ajax method after 1 second
	setTimeout(AjaxCall, 1000);
})
