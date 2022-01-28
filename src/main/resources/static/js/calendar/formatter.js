/**
 * 문자열 변환 유틸리티. 본인이 작성한 코드 아님.
 *
 * @version 1.0.1 2022년 1월 22일
 */
class Formatter {


    /**
     * 예시 : 5-> "05"
     *
     * @param number 숫자
     * @returns {string} 문자로 된 숫자
     */
    formatNumber(number) {
        let str = "" + number;
        if (number < 10 && str.indexOf('0') === -1 || str.length === 1) {
            str = "0" + number;
        }
        return str;
    }

    /**
     * 예시 : "05"->5
     *
     * @param str 문자로 된 숫자
     * @returns {number} 숫자
     */
    formatString(str) {
        if (str===undefined){
            return undefined;
        }
        else if (str.indexOf(0) === '0') {
            return parseInt(str.substring(1, 2));
        } else {
            return parseInt(str);
        }
    }

    /**
     * 예시: "2020-02-01:T00:00:00" -> 202021
     *
     * @param str LocalDateTime 형식의 문자열
     * @returns {string} 축약 문자열
     */
    convertDateFormat(str) {
        const strArr = str.split('T');
        const arr = strArr[0].split('-');

        return arr[0] + this.formatString(arr[1]) + this.formatString(arr[2]);
    }
}