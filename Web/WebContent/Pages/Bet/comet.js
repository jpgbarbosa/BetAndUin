/*
 * comet.js
 * 
 * This Library aims to make it easier to request remote URLs and make comet use easier.
 * 
 * To create a Comet instance (that represents your project) simple call: 
 * 
 * var server = Comet('http://localhost:8080/Project');
 * 
 * 
 * To perform a GET request, you can use:
 * 
 * server.get('LuisServlet?team=sporting', function(response) {
 * 		alert('Got from server: ' + response);
 * });
 * 
 * This method will call http://localhost:8080/Project/LuisServlet?team=sporting and will open an alertbox with the response from the server.
 * Notice that the second argument, is a callback function that is executed when the response is fetched.
 * If your servlet is a CometProcessor, the callback will be called every time there is a new update.
 * 
 * 
 * To perform a POST request, you can use the post method:
 * 
 * server.post('LoginServlet', 'admin:admin', function(response) {
 * 		if (response == "ok")
 * 			alert('Logged In!');
 * 		else
 * 			alert('Wrong credentials');
 * }
 */

function Comet(prefix) {
	var makeRequest = function() {
		var request;
	    if (typeof XMLHttpRequest != 'undefined') {
	        request = new XMLHttpRequest();
	    }
	    else{
	        try {
	            request = new ActiveXObject("Microsoft.XMLHTTP");
	        }
	        catch (e) {
	            try {
	                request = new ActiveXObject("Microsoft.XMLHTTP");
	            } catch (e) {}
	        }
	    }
	    return request;
	}
	var ajax = function(method, url, args, callback) {
		var req = makeRequest();
		
		req.onreadystatechange = function() {
	        if(req.readyState == 3){
	            if(req.status==200){
	                if (req.responseText) {
	                	callback(req.responseText);
	                } else {
	                	alert("Error: " + req.status + "\n" + req.responseText);
	                }
	            }
	        } else if (req.readyState == 4){
	            if(req.status == 300){
	                window.location = req.getResponseHeader("redirect");
	            }
	        }
	    };
		
		req.open(method, url);
	    req.setRequestHeader("Connection", "close");
	    req.setRequestHeader("Content-Type","application/x-javascript;");
	    req.send(args);
	};
	
	var get = function(url, callback) {
		return ajax('GET', prefix + url, null, callback);
	};
	
	var post = function(url, args, callback) {
		return ajax('POST', prefix + url, args, callback);
	};
	
	return {
		'get': get,
		'post': post,
		'ajax': ajax
	};
}