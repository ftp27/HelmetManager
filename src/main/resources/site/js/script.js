$(document).ready(function() {
	tab = document.location.pathname.split("/")[2];
	if (!tab) {
	    tab = "file";
	}
	$("#nav-tab-"+tab).addClass("active");



	$.getJSON("/info/header").done( function( data ) {
        $(".header-info>.battery").text(data["batteryLevel"]);
        $(".header-info>.phone").text(data["phoneName"]);
	});

});



