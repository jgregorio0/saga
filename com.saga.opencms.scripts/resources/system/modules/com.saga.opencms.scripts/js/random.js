/**
 * Generate random strings
 */

/**
 * Generate random string for length
 */
function rdmX(length) {
    return rdmStrTemplate(length, "X");
}

/**
 * Generate random string for length
 */
function rdmNumber(length) {
    return rdmStrTemplate(length, "0123456789");
}

/**
 * Generate random string using template. Template contains all possible characters.
 */
function rdmStrTemplate(length, template) {
    var text = "";
    for( var i=0; i < length; i++ )
        text += template.charAt(Math.floor(Math.random() * template.length));

    return text;
}