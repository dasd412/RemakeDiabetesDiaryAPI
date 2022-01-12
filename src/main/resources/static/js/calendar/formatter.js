class Formatter {


    formatNumber(number){// 5-> "05"
        let str=""+number;
        if(number<10&&str.indexOf('0')===-1||str.length===1){
            str="0"+number;
        }
        return str;
    }

    formatString(str){// "05"->5
        if(str.indexOf(0)==='0'){
            return  parseInt(str.substring(1,2));
        }
        else{
            return parseInt(str);
        }
    }

    convertDateFormat(str){// "2020-02-01:T00:00:00" -> 202021
        const strArr=str.split('T');
        const arr=strArr[0].split('-');

        return arr[0]+this.formatString(arr[1])+this.formatString(arr[2]);
    }
}