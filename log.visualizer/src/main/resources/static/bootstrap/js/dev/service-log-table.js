Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

getServiceLog = function(tagId, date) {
    $.ajax({
        url: '/visualize-api/v2/logs/tag-id/' + tagId +'?loggingDate=' + date,
        method: 'GET',
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            if (response != null) {
                const logs = response.data.logs.content;
                printRow(logs);
                printPageInfo(response.data.logs);
            }
        },
        error: function (response) {
            for (let i in response) {
                console.log('response error:' + i + ":" + response[i]);
            }
        }
    });
}

printRow = function (logs) {
    const totalBody = $("#serviceLogBody");
    const tr = [];
    if (logs != null && logs.length > 0) {
        for (let idx = 0; idx < logs.length; idx++) {
            tr.push("<tr>");
            tr.push("<td style='text-align: center;'>" + logs[idx].loggingTime + "</td>");
            tr.push("<td style='text-align: center;'>" + logs[idx].logType + "</td>");
            tr.push("<td style='text-align: center;'>" + logs[idx].logData + "</td>");
            tr.push("<td style='text-align: center;' onclick='getTraceLog(this)'><a href='#'>" + logs[idx].traceId + "</a></td>");
            tr.push("</tr>");
        }
    } else {
        tr.push("<tr>");
        tr.push("<td colspan=4 align='center'>조회된 데이터가 없습니다.</td>");
        tr.push("</tr>");
    }
    totalBody.append(tr.join(""));
}

printPageInfo = function (p) {
    let lis1 = [];
    const totalPages = p.totalPages;
    const number = p.number;

    if (number >= 0) {
        lis1.push("<li class=\"page-item\">")
    } else {
        lis1.push("<li class=\"page-item disabled\">")
    }

    const linkPage = 0;
    const filterPath = 1;
    lis1.push("<a class=\"page-link\" " +
        "href=\"javascript:" + linkPage + "('" + filterPath + "'," + (number - 1) + ");\" " +
        "tabindex=\"-1\">Previous</a>");
    lis1.push("</li>");

    for (let pidx = 0; pidx < totalPages; pidx++) {
        if (pidx == number) {
            let pNum = pidx + 1;
            lis1.push("<li class=\"page-item active\">" +
                "<a class=\"page-link\" href=\"javascript:" + linkPage + "('" + filterPath + "'," + number + ");\">"
                + pNum + "</a>" +
                "</li>");
        } else {
            lis1.push("<li class=\"page-item\">" +
                "<a class=\"page-link\" href=\"javascript:" + linkPage + "('" + filterPath + "'," + number + ");\">"
                + pNum + "</a>" +
                "</li>");
        }
    }

    if (number + 1 >= totalPages) {
        lis1.push("<li class=\"page-item disabled\">");
    } else {
        lis1.push("<li class=\"page-item\">");
    }
    lis1.push("<a class=\"page-link\" " +
        "href=\"javascript:" + linkPage + "('" + filterPath + "'," + (number + 1) + ");\">Next</a>" +
        "</li>");
    $("#paginationServiceLog").append(lis1.join(""));
}