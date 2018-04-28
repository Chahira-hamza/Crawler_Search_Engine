function init(){
	
	var canvas = document.getElementById("canvas");
	var ptags = document.getElementsByTagName("p");
	
	ptags[0].onclick = changeColor;
	
	if (canvas.getContext){
		
		var ctx = canvas.getContext("2d");
		
		ctx.fillStyle = "#F2F0E6";
		
		ctx.fillRect(0, 0, canvas.width, canvas.height);
	}
}

function changeColor(){
	
	var randomcolor = '#'+Math.floor(Math.random()*16777215).toString(16);

	this.style.color = randomcolor;	
	
}

onload = init;