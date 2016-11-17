$(function(){
    for (i = 0; i < 10; i++) {
        $("#datepicker-" + i).datepicker($.datepicker.regional["es"]);
    }
});