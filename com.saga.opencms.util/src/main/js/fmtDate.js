var currDate = new Date();
currDate.getUTCFullYear()
    + "-" + ("0" + (currDate.getUTCMonth() + 1)).slice(-2)
    + "-" + ("0" + currDate.getUTCDate()).slice(-2)
    + "-" + ("0" + currDate.getUTCHours()).slice(-2)
    + ("0" + currDate.getUTCMinutes()).slice(-2);