$(document).ready(function() {
	console.log( "ready!" );

	tab = document.location.pathname.split("/")[2];
	if (!tab) {
	    tab = "file";
	}
	$("#nav-tab-"+tab).addClass("active");

	$('.file-address').keypress(function (e) {
        if (e.which == 13) {
            setList($(this).parent().find(".file-list"),$(this).val());
        }
	});


	$(".file-list").each( function() {
        setList($(this),'/sdcard');
	});


});

function updateEvents() {
    $('.file-list>ul>li').hover(
        function () {
            $(this).find("img").css("-webkit-filter","brightness(130%)");
        },
        function () {
                $(this).find("img").css("-webkit-filter","brightness(100%)");
            }
    );

    $('.file-list>ul>li').click(function () {
        //console.log("one click");
        $(this).parent().find("li").css("background-color","white");
        $(this).parent().find("li").css("color","black");
        $(this).css("background-color","#809ECC");
        $(this).css("color","white");
    });

    $('.file-list>ul>li').dblclick(function () {
        //console.log("double click");
        setList($(this).parents(".file-list"),$(this).attr("src"));
    });
}

function setList(dom, address) {
        $.getJSON("/file/"+address).done( function( data ) {
            side = dom.parents(".file-part").attr('id');

            if (data["fileType"] == "directory") {
                list = "<ul>";
                $.each( data["files"], function( key, val ) {
                    //console.log(val["fileName"]);
                    elem = "<li id='"+side+"-"+key+"' src='"+val["fileAddress"]+"'>";
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
            updateEvents();
        });
}