/**
 * Sort elements
 *
 * @param containerId Container selector (element-list)
 * @param elementType Element type (div)
 */
function sort(containerId, elementType){
    $("#" + containerId + " " + elementType).sort(function(a,b){
        return parseInt(a.id) > parseInt(b.id);
    }).each(function(){
        var elem = $(this);
        elem.remove();
        $(elem).appendTo(containerId);
    });
}