function githubIcon(username) {
	// new http request to github page
	var xmlhttp = new XMLHttpRequest();
	var url = "https://api.github.com/users/" + username;

	// checks for valid site before parsing
	xmlhttp.onreadystatechange = function() {
	    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
	        var userPage = JSON.parse(xmlhttp.responseText);
	        getPicture(userPage);
	    }
	};

	xmlhttp.open("GET", url, true);
	xmlhttp.send();

	function getPicture(site) {
		var out = "";
		out += site.avatar_url;
		document.getElementById("github-icon").innerHTML = out;
	}
}
