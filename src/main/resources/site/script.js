$( document ).ready(function() {
	console.log( "ready!" );

	$(".file-list").each( function() {
        setList($(this),'/sdcard');
	});
});

function setList(dom, address) {
        $.getJSON("/file/"+address).done( function( data ) {
            if (data["fileType"] == "directory") {
                list = "<ul>";
                $.each( data["files"], function( key, val ) {
                    console.log(val["fileName"]);
                    elem = "<li>";
                    if (val["fileType"] == "directory") {
                        elem += "<img src='/res/site/img/dir.png'>";
                    } else {
                        elem += "<img src='/res/site/img/file.png'>";
                    }
                    elem += "<div class='file-name'>"+
                                val["fileName"]+
                            "</div>"+
                         "</li>";
                    list += elem;
                });
                list += "</ul>";
                dom.html(list);
                dom.parent().find(".file-address").val(address);
            }
        });
}