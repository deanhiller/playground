
function getUrlWeCameFrom() {
	return sessionStorage.getItem("url0");
}

//pushs url on to this tabs stack
function pushUrlOnTab(url){
	var val1 = sessionStorage.getItem("url0");
	if(val1 == url) //don't push on a simple refresh here
		return;
	
	if(document.getElementById("noputonstack") != null) {
		var lastUrl = getUrlWeCameFrom();
		currentElement = document.createElement("input");
		currentElement.setAttribute("type", "hidden");
		currentElement.setAttribute("name", "lastUrl");
		currentElement.setAttribute("id", "hiddenName");
		currentElement.setAttribute("value", lastUrl);
		document.forms[0].appendChild(currentElement);
		return;
	}
		
	var val2 = sessionStorage.getItem("url1");
	var val3 = sessionStorage.getItem("url2");
	var val4 = sessionStorage.getItem("url3");
	
	sessionStorage.setItem("url0", url);
	sessionStorage.setItem("url1", val1);
	sessionStorage.setItem("url2", val2);
	sessionStorage.setItem("url3", val3);
	sessionStorage.setItem("url4", val4);
}

$(document).ready(function(){
 	var url = window.location.href;
 	pushUrlOnTab(url);

	var val0 = sessionStorage.getItem("url0");
	var val1 = sessionStorage.getItem("url1");
	var val2 = sessionStorage.getItem("url2");
	var val3 = sessionStorage.getItem("url3");
	var val4 = sessionStorage.getItem("url4");
	
	var temp = val0 + "\n"
				+ val1 + "\n"
				+ val2 + "\n"
				+ val3+ "\n"
				+ val4+ "\n";
 	//alert("pushed urls for this tab=\n"+temp);
});

