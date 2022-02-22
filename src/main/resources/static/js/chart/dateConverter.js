/*
 * @(#)dateConverter.js        1.1.0 2022/2/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

/**
 * string <-> localDateTime 변환 담당 js
 * @author 양영준
 * @version 1.1.0 2022년 2월 22일
 */

const DateConverter = {

    /**
     * 예시 : "05"->5
     *
     * @param str 문자로 된 숫자
     * @returns {number} 숫자
     */
    formatString: function (str) {
        if (str === undefined) {
            return undefined;
        } else if (str.indexOf(0) === '0') {
            return parseInt(str.substring(1, 2));
        } else {
            return parseInt(str);
        }
    },

    /**
     *
     * @param startDateSplit 시작 날짜 문자열에 해당하는 $("# ").val().split('/') 형식의 파라미터
     * @param endDateSplit 끝 날짜 문자열에 해당하는 $("# ").val().split('/') 형식의 파라미터
     * @returns {{betweenDate: {startMonth: number, startDay: number, endDay: number, startYear: *, endMonth: number, endYear: *}, errorMessage: string, isConverted: boolean}}
     */
    convertStringToLocalDateTime: function (startDateSplit, endDateSplit) {

        const convertedDate = {
            /**
             * 변환이 정상적으로 되었는 가 여부
             */
            isConverted: false,

            /**
             * 변환이 비정상적일 경우 sweet alert으로 보여줘야 할 메시지 내용
             */
            errorMessage: "",

            /**
             * 변환이 정상적일 경우 ajax로 보내야 하는 데이터
             */
            betweenDate: {
                startYear: startDateSplit[2],
                startMonth: this.formatString(startDateSplit[0]),
                startDay: this.formatString(startDateSplit[1]),

                endYear: endDateSplit[2],
                endMonth: this.formatString(endDateSplit[0]),
                endDay: this.formatString(endDateSplit[1])
            }

        };


        //Date 객체에서 월은 0부터 시작
        const startDate = new Date(convertedDate.betweenDate.startYear, convertedDate.betweenDate.startMonth - 1, convertedDate.betweenDate.startDay);
        const endDate = new Date(convertedDate.betweenDate.endYear, convertedDate.betweenDate.endMonth - 1, convertedDate.betweenDate.endDay);

        if (convertedDate.betweenDate.startMonth === undefined || convertedDate.betweenDate.startDay === undefined) {
            convertedDate.errorMessage = "시작 날짜를 입력해주세요!!";
            return convertedDate;
        }
        if (convertedDate.betweenDate.endMonth === undefined || convertedDate.betweenDate.endDay === undefined) {
            convertedDate.errorMessage = "끝 날짜를 입력해주세요!!";
            return convertedDate;
        }
        //끝 날짜가 시작 날짜보다 앞서면 안된다.
        if (startDate > endDate) {
            convertedDate.errorMessage = "끝 날짜가 시작 날짜보다 앞서면 안되요!!";
            return convertedDate;
        }

        convertedDate.isConverted = true;
        return convertedDate;
    },

    /**
     *
     * @param startDate $("#modalStartDate").val() 형식의 날짜 문자열 파라미터
     * @param endDate $("#modalEndDate").val() 형식의 날짜 문자열 파라미터
     */
    convertStringToLocalDateTimeForFoodBoard(startDate, endDate) {
        const convertedDateForFoodBoard = {
            converted: false,
            errorMessage: "",

            convertedStartDate: false,
            startYear: "",
            startMonth: "",
            startDay: "",

            convertedEndDate: false,
            endYear: "",
            endMonth: "",
            endDay: ""
        };

        if (startDate != null && startDate !== "") {
            startDate = startDate.split("/");
            convertedDateForFoodBoard.startYear = startDate[2];
            convertedDateForFoodBoard.startMonth = this.formatString(startDate[0]);
            convertedDateForFoodBoard.startDay = this.formatString(startDate[1]);
            convertedDateForFoodBoard.convertedStartDate = true;
        }

        if (endDate != null && endDate !== "") {
            endDate = endDate.split("/");
            convertedDateForFoodBoard.endYear = endDate[2];
            convertedDateForFoodBoard.endMonth = foodBoardFormatter.formatString(endDate[0]);
            convertedDateForFoodBoard.endDay = foodBoardFormatter.formatString(endDate[1]);
            convertedDateForFoodBoard.convertedEndDate = true;
        }

        if (convertedDateForFoodBoard.convertedStartDate === true && convertedDateForFoodBoard.convertedEndDate === true) {
            //Date 객체에서 월은 0부터 시작
            const startDateOfBloodSugar = new Date(convertedDateForFoodBoard.startYear, convertedDateForFoodBoard.startMonth - 1, convertedDateForFoodBoard.startDay);
            const endDateOfBloodSugar = new Date(convertedDateForFoodBoard.endYear, convertedDateForFoodBoard.endMonth - 1, convertedDateForFoodBoard.endDay);
            if (startDateOfBloodSugar > endDateOfBloodSugar) {
                convertedDateForFoodBoard.errorMessage = "끝 날짜가 시작 날짜보다 앞서면 안되요!!!";
                return convertedDateForFoodBoard;
            }
        }

        convertedDateForFoodBoard.converted = true;
        return convertedDateForFoodBoard;
    }
};
