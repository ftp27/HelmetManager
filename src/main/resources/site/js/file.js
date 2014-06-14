$(".nav-content").ready(function() {
    $(this).keypress(function (e) {
        if (e.which == 46) {
            $(".file-delete").click();
        }
    });

	$('.file-address').keypress(function (e) {
        if (e.which == 13) {
            setList($(this).closest(".file-part").find(".file-list"),$(this).val());
        }
	});

	$(".file-list").each( function() {
        setList($(this),'/sdcard');
    });

    // Dialog windows

    $("#file-dialog-error").dialog({
            autoOpen: false,
            resizable: false,
            dialogClass: "no-close",
            height:250,
            width:450,
            modal: true,
            buttons: {
                Ok: function() {
                    $( this ).dialog( "close" );
                }
            }
    });

    $("#file-dialog-delete").dialog({
        autoOpen: false,
        resizable: false,
        dialogClass: "no-close",
        height:250,
        width:450,
        modal: true,
        buttons: {
            "Delete": function() {

                $.post(
                    "/file/",
                    {
                        action: "delete",
                        file: $(".file-selected").attr("src"),
                    },
                    function(data) {
                        if (data.Status == "error") {
                            $("#file-dialog-error>p").text(data.Code);
                            $("#file-dialog-error").dialog( "open" );
                        }
                        updateLists();
                    },
                    "json"
                );
                $( this ).dialog( "close" );
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });

    $("#file-dialog-newdir").dialog({
        autoOpen: false,
        resizable: false,
        dialogClass: "no-close",
        height:250,
        width:450,
        modal: true,
        buttons: {
            "Create": function() {

                $.post(
                    "/file/",
                    {
                        action: "newdir",
                        file: $("#file-newdir-address").val()+'/'+$("#file-newdir-name").val(),
                    },
                    function(data) {
                        if (data.Status == "error") {
                            $("#file-dialog-error>p").text(data.Code);
                            $("#file-dialog-error").dialog( "open" );
                        }
                        updateLists();
                    },
                    "json"
                );
                $( this ).dialog( "close" );
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });

    $("#file-dialog-copycut").dialog({
            autoOpen: false,
            resizable: false,
            dialogClass: "no-close",
            height:250,
            width:450,
            modal: true,
            buttons: {
                "Yes": function() {
                    $.post(
                        "/file/",
                        {
                            action: $("#file-copycut-action").val(),
                            source: $("#file-copycut-from").val(),
                            dest: $("#file-copycut-to").val()
                        },
                        function(data) {
                            if (data.Status == "error") {
                                $("#file-dialog-error>p").text(data.Code);
                                $("#file-dialog-error").dialog( "open" );
                            }
                            updateLists();
                        },
                        "json"
                    );
                    $( this ).dialog( "close" );
                },
                Cancel: function() {
                    $( this ).dialog( "close" );
                }
            }
        });



    //Button actions

    $(".file-delete").click(function() {
        fileAddress = $(".file-selected").attr("src");
        if (fileAddress != "") {
            $("#file-dialog-delete>p>span").text(fileAddress);
            $("#file-dialog-delete").dialog( "open" );
        }
    });

    $(".file-newdir").click(function() {
        fileAddress = $(".file-selected").parent("ul").attr("src");
        if (fileAddress != "") {
            $("#file-newdir-address").val(fileAddress);
            $("#file-dialog-newdir").dialog( "open" );
        }
    });

    $(".file-copy, .file-cut").click(function() {
        fileAddress = $(".file-selected").attr("src");
        fileName = $(".file-selected").find(".file-name").text();
        fromBlock = $(".file-selected").closest(".file-part");
        toAddress = $(".file-part:not(#" + fromBlock.attr("id") + ")")
                            .find(".file-address").val() + fileName;

        if ($(this).hasClass("file-copy")) {
            action = "copy";
        } else {
            action = "cut"
        }

        dialogTitle = action.charAt(0).toUpperCase()+action.slice(1);

        if (fileAddress != "" && toAddress != "") {
            $("#file-copycut-from").val(fileAddress);
            $("#file-copycut-to").val(toAddress);
            $("#file-copycut-action").val(action);

            $("#file-dialog-copycut").dialog({ title: dialogTitle });
            $("#file-dialog-copycut").dialog( "open" );
        }


    });

});

function updateLists() {
    $(".file-list").each( function() {
        address = $(this).children("ul").attr("src");
        setList($(this),address);
    });
}

function setList(dom, address) {
        $.getJSON("/file/"+address).done( function( data ) {
            side = dom.parents(".file-part").attr('id');

            if (data["fileType"] == "directory") {
                list = "<ul src='"+address+"'>";
                $.each( data["files"], function( key, val ) {
                    //console.log(val["fileName"]);
                    if (val["fileName"] == "..") {
                        dom.parent().find(".file-upbutton").attr("src",val["fileAddress"]);
                    }
                    elem = "<li id='"+side+"-"+key+"' src='"+val["fileAddress"]+"' class='file-dont-selected'>";
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
            filelist = $(this).closest(".file-part").find(".file-list");
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
        $(".file-selected").switchClass("file-selected","file-dont-selected",50);
        $(this).switchClass("file-dont-selected","file-selected",50)
    });

    $('.file-name').click(function () {
        setList($(this).parents(".file-list"),$(this).parents("li").attr("src"));
    });
}