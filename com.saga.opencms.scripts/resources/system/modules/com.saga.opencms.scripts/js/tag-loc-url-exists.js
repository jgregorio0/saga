/**
 * Check if urls contained by <loc> tag exists. Url must be in the current site in order to avoid CORS error.
 */
function sitemapLocExists(){

    /**
     * Check if url exists
     * @param url
     */
    function getUrl(url) {
        console.log("getUrl", url);
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            console.log("readyState", this.readyState);
            console.log("status", this.status);
            if (this.readyState == 4 && this.status == 200) {
                console.log("responseText", this.responseText);
            }
        };
        xhttp.open("GET", url, true);
        xhttp.send();
    }

    // Find locs
    var locs = document.getElementsByTagName("loc");
    if (locs.length === 0) {
        console.warn("Tag 'loc' not found");
    } else {

        // Check url for each one
        for (var i = 0; i < locs.length; i++) {
            var loc = locs[i];
            var link = loc.innerHTML;
            getUrl(link);
        }
    }
}
