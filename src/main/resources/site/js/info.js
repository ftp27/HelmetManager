$(".nav-content").ready(function() {
    $.getJSON("/info/build").done( function( data ) {
        list = "<ul>";
        $.each( data, function( key, val ) {
            if (key != "VERSION") {
                elem = "<li><b>"+key+":</b>"+val+"</li>";
            } else {
                elem = "<li><b>"+key+":</b><ul>";
                $.each(val, function( key2, val2 ) {
                    elem += "<li><b>"+key2+":</b>"+val2+"</li>";

                });
                elem += "</ul></li>";
            }
            list += elem;
        });
        list += "</ul>";
        $("#info-build>.panel-body").html(list);
    });
});