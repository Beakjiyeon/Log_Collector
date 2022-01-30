Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';


$.ajax({
    url: 'http://localhost:8080/collect-api/v2/fail-mails?regDate=2021-12-15',
    method: 'GET',
    dataType: 'jsonp',
    jsonp: 'callback',
    // dataType: 'json',
    contentType: "application/json; charset=utf-8",
    success: function(response) {
        alert("g");
        if(response != null) {
            const logs = response.data.content;
            printRow2(logs);
           // printPageInfo2(response.data.logs);
        }
    },
    error: function (response) {
        for (let i in response) {
            console.log('response error:' + i + ":" + response[i]);
        }
    }
});


printRow2 = function(logs) {
    const traceLogBody = $("#mailBody");
    const traceTr = [];
    if(logs != null && logs.length > 0) {
        for (let idx = 0; idx < logs.length; idx++) {
            traceTr.push("<tr>");
            traceTr.push("<td style='text-align: center;'>" + logs[idx].createAt + "</td>");
            traceTr.push("<td style='text-align: center;'>" + logs[idx].causeKeyword + "</td>");
            traceTr.push("<td style='text-align: center;'>" + logs[idx].loggingTime + "</td>");
            traceTr.push("<td style='text-align: center;'>" + logs[idx].traceId + "</td>");
            traceTr.push("<td style='text-align: center;'>" + logs[idx].tagId + "</td>");
            traceTr.push("<td style='text-align: center;'>" + logs[idx].logType + "</td>");
            traceTr.push("<td style='text-align: center;'>" + logs[idx].logData + "</td>");
            // setTableMetricData(tr, campaignReportDataList[idx], reportMetric);
            traceTr.push("</tr>");
        }
    } else {
        traceTr.push("<tr>");
        traceTr.push("<td colspan=4 align='center'>조회된 데이터가 없습니다.</td>");
        traceTr.push("</tr>");
    }
    traceLogBody.append(traceTr.join(""));
}


printPageInfo2 = function(p) {
    let lis2 = [];
    const totalPages2 = p.totalPages;
    const number2 = p.number;

    if (number2 >= 0) {
        lis2.push("<li class=\"page-item\">")
    } else {
        lis2.push("<li class=\"page-item disabled\">")
    }

    const linkPage = 0;
    const filterPath = 1;

    lis2.push("<a class=\"page-link\" " +
        "href=\"javascript:" + linkPage + "('"+ filterPath + "'," + (number2 - 1) + ");\" " +
        "tabindex=\"-1\">Previous</a>");
    lis2.push("</li>");


    for(let pidx2 = 0; pidx2 < totalPages2; pidx2++){
        if(pidx2 == number2) {
            lis2.push("<li class=\"page-item active\">" +
                "<a class=\"page-link\" href=\"javascript:"+ linkPage + "('"+ filterPath +"',"+ number2 + ");\">"
                + pidx2 + 1 + "</a>" +
                "</li>");
        }else {
            lis2.push("<li class=\"page-item\">" +
                "<a class=\"page-link\" href=\"javascript:"+ linkPage + "('"+ filterPath +"',"+ number2 + ");\">"
                + pidx2 + 1 + "</a>" +
                "</li>");
        }
    }
    if (number2 + 1 >= totalPages2) {
        lis2.push("<li class=\"page-item disabled\">");
    } else {
        lis2.push("<li class=\"page-item\">");
    }
    lis2.push("<a class=\"page-link\" " +
        "href=\"javascript:"+linkPage+"('"+ filterPath +"',"+ (number2 + 1) + ");\">Next</a>" +
        "</li>");
    $("#paginationTraceLog").append(lis2.join(""));
}