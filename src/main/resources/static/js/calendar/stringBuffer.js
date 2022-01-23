/**
 * 문자열 변환에 사용된다. 본인이 작성한 코드 아님.
 *
 * @version 1.0.1 2022년 1월 22일
 */

let StringBuffer = function () {

    this.buffer = [];

};

StringBuffer.prototype.append = function (str) {

    this.buffer[this.buffer.length] = str;

};

StringBuffer.prototype.toString = function () {

    return this.buffer.join("");

};