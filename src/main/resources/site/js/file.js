$(".nav-content").ready(function() {
	$('.file-address').keypress(function (e) {
        if (e.which == 13) {
            setList($(this).parent().find(".file-list"),$(this).val());
        }
	});

	$(".file-list").each( function() {
        setList($(this),'/sdcard');
    });
});

function setList(dom, address) {
        $.getJSON("/file/"+address).done( function( data ) {
            side = dom.parents(".file-part").attr('id');

            if (data["fileType"] == "directory") {
                list = "<ul>";
                $.each( data["files"], function( key, val ) {
                    //console.log(val["fileName"]);
                    if (val["fileName"] == "..") {
                        dom.parent().find(".file-upbutton").attr("src",val["fileAddress"]);
                    }
                    elem = "<li id='"+side+"-"+key+"' src='"+val["fileAddress"]+"'>";
                    if (val["fileType"] == "directory") {
                        elem += "<img src='/res/img/dir.png'>";
                    } else {
                        elem += "<img src='/res/img/file.png'>";
                    }
                    elem += "<a class='file-name'>"+
                                val["fileName"]+
                            "</a>"+
                         "</li>";
                    list += elem;
                });
                list += "</ul>";
                dom.html(list);

                dom.parent().find(".file-address").val(address);
            }
            updateEvents();
        });

        $(".file-upbutton").click(function() {
            filelist = $(this).parents(".file-part").find(".file-list");
            address = $(this).attr("src");
            if (address) {
                setList(filelist,address);
            }
        });
}

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
        fileRows = $(".file-list>ul>li");
        fileRows.css("background-color","white");
        fileRows.css("color","black");
        fileRows.children(".file-name").css("color","black");

        $(this).css("background-color","#809ECC");
        $(this).children(".file-name").css("color","white");
        $(this).css("color","white");
    });

    $('.file-name').click(function () {
        setList($(this).parents(".file-list"),$(this).parents("li").attr("src"));
    });
}