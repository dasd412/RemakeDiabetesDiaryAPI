let StringBuffer = function() {

    this.buffer = [];

};

StringBuffer.prototype.append = function(str) {

    this.buffer[this.buffer.length] = str;

};

StringBuffer.prototype.toString = function() {

    return this.buffer.join("");

};