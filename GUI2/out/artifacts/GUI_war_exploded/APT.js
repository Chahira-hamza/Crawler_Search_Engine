function init(){
	
	var ptags = document.getElementsByTagName("p");

	ptags[0].onclick = changeColor;
}

function changeColor(){
	
	var randomcolor = '#'+Math.floor(Math.random()*16777215).toString(16);

	this.style.color = randomcolor;	
	
}

function checkform(form) {
    // get all the inputs within the submitted form
    var input = form.getElementsByTagName('input');
    if (input[0].value == "")
    {
        if (input[1].onsubmit)
        {
            window.location.replace("index.jsp");
            return false;
        }
        if (input[2].onsubmit)
        {
            window.location.replace("https://www.google.com/doodles/");
            return false;
        }
    }
    return true;
}

onload = init;