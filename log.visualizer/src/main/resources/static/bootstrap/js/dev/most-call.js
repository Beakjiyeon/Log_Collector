
function getMostCall(date) {
    const mostCalledService = document.getElementById("mostCalledService")
    $.ajax({
        url: '/visualize-api/v2/most-call-services?loggingDate=' + date + '&order=-tagId,-processTime',
        method: 'GET',
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        success: function(response) {
            if (response != null) {
                if (response.data.services.length > 0) {
                    if (response.data.services.length > 1) {
                        mostCalledService.innerHTML = response.data.services[0] + "&nbsp;&nbsp;&nbsp;<a id='moreMostCalledService'></a>";
                    } else {
                        mostCalledService.innerHTML = response.data.services[0];
                    }
                    document.getElementById("leastCalledService").innerHTML = "service_q";
                    document.getElementById("failCount").innerHTML = "5";
                    drawMyIndexBarChart(date);
                    drawMyIndexPieChart(date);
                } else {
                    alert("선택하신 기간의 결과 값이 존재하지 않습니다.");
                }
            }
        },
        error: function (response) {
            for (let i in response) {
                console.log('response error:' + i + ":" + response[i]);
            }
        }
    });
}