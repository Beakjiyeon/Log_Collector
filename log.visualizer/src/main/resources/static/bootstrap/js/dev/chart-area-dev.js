// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

drawMyIndexBarChart = function (date) {
    const time_tagIds = [];
    const time_tagIds2 = [];
    const avgProcessTimes = [];
    const diffProcessTimes = [];
    const tagIdResult = [];
    const avgResult = [];
    const diffResult = [];
    $("#totalContentBody").children().remove();
    $.ajax({
        url: '/visualize-api/v2/avg-process-time?loggingDate=' + date,
        method: 'GET',
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            if (response != null) {
                const avgs = response.data.avgProcessTimes.content;
                if (avgs.length > 0) {
                    for (let idx = 0; idx < avgs.length; idx++) {
                        time_tagIds.push(avgs[idx].tagId);
                        avgProcessTimes.push(avgs[idx].avgProcessTime);
                    }
                    // 간극 api 호출
                    $.ajax({
                        url: '/visualize-api/v2/process-time-diff?loggingDate=' + date,
                        method: 'GET',
                        dataType: 'json',
                        contentType: "application/json; charset=utf-8",
                        success: function (response) {
                            if (response != null) {
                                const diffs = response.data.maxProcessTimeDiffs.content;
                                for (let idx2 = 0; idx2 < diffs.length; idx2++) {
                                    time_tagIds2.push(diffs[idx2].tagId);
                                    diffProcessTimes.push(diffs[idx2].maxProcessTimeDiff);
                                }
                                // tagIds를 기준으로 짝 맞추기
                                for (let k = 0; k < time_tagIds.length; k++) {
                                    for (let j = 0; j < time_tagIds2.length; j++) {
                                        if (time_tagIds[k] == time_tagIds2[j]) {
                                            tagIdResult.push(time_tagIds[k]);
                                            avgResult.push(avgProcessTimes[k]);
                                            diffResult.push(diffProcessTimes[j]);
                                            break;
                                        }
                                    }
                                }
                                drawProcessTimeChart(tagIdResult, diffResult, avgResult);
                                printRow(tagIdResult, avgResult, diffResult);
                            }
                        },
                        error: function (response) {
                            for (let i in response) {
                                console.log('response error:' + i + ":" + response[i]);
                            }
                        }
                    });
                } else {
                   // $('#myBar').append("<span>데이터가 없습니다.</span>");
                    $("#totalContentBody").append("<tr></tr><td colspan=4 align='center'>조회된 데이터가 없습니다.</td></tr>");
                }
            }
        },
        error: function (response) {
            for (let i in response) {
                console.log('response error:' + i + ":" + response[i]);
            }
            //$('#myBar').append("<span>데이터가 없습니다.</span>");
            //$("#totalContentBody").append("<tr></tr><td colspan=4 align='center'>조회된 데이터가 없습니다.</td></tr>");
        }
    });
}
let DMP_CHART = {}
DMP_CHART.chartColors = {
    red: 'rgb(255, 99, 132)',
    orange: 'rgb(255, 159, 64)',
    yellow: 'rgb(255, 205, 86)',
    green: 'rgb(75, 192, 192)',
    blue: 'rgb(54, 162, 235)',
    purple: 'rgb(153, 102, 255)',
    grey: 'rgb(201, 203, 207)'
};

drawProcessTimeChart = function (tagIdResult, diffResult, avgResult) {
    const config = {
        type: 'bar',
        data: {
            xLabels: tagIdResult,
            datasets: [{
                type: 'bar',
                label: "평균", // 'My First dataset',
                yAxisID: 'y-axis-1',
                backgroundColor: transparentize('rgb(255, 99, 132)'),
                borderColor: 'rgb(255, 99, 132)',
                data: avgResult,
                fill: 'origin',
            },
                {
                    type: 'bar',
                    label: "최대-최소 간극", // 'My First dataset',
                    yAxisID: 'y-axis-1',
                    backgroundColor: transparentize('rgb(54, 162, 235)'),
                    borderColor: 'rgb(54, 162, 235)',
                    data: diffResult,
                    fill: 'origin',
                },
                {
                    type: 'line',
                    label: "최대-최소 간극", // 'My Second dataset',
                    yAxisID: 'y-axis-2',
                    backgroundColor: transparentize('rgb(54, 162, 235)'),
                    borderColor: 'rgb(54, 162, 235)',
                    data: diffResult,
                    fill: false,
                }
            ]
        }
        ,
        options: {
            // responsive: true,
            hoverMode: 'nearest',
            intersect: true,
            scales: {
                yAxes: [{
                    type: 'linear', // only linear but allow scale type registration. This allows extensions to exist solely for log scale for instance
                    display: true,
                    scaleLabel: {
                        display: true,
                        labelString: '시간(초)'
                    },
                    position: 'left',
                    id: 'y-axis-1'
                }, {
                    type: 'linear', // only linear but allow scale type registration. This allows extensions to exist solely for log scale for instance
                    // ticks: {
                    //   userCallback: function(tick) {
                    //     return "hihello";
                    //   }
                    // },
                    display: true,
                    position: 'right',
                    reverse: true,
                    id: 'y-axis-2',
                    label: '',
                    // grid line settings
                    gridLines: {
                        drawOnChartArea: false, // only want the grid lines for one axis to show up
                    },
                }],
            }
        }
    };

    const ctx = document.getElementById('myAreaChart').getContext('2d');
    const barDiv = document.getElementById("myBar");

    const height = window.getComputedStyle(barDiv).height;
    const width = window.getComputedStyle(barDiv).width;

    ctx.width = width;
    ctx.height = height;

    if (window.dailyChart !== undefined) {
        window.dailyChart.destroy();
    }
    window.dailyChart = new Chart(ctx, config);
    window.dailyChart.update();
    window.dailyChart.aspectRatio = 0;

    console.log(window.dailyChart);
}


transparentize = function (color, opacity) {
    const alpha = opacity === undefined ? 0.5 : 1 - opacity;
    return Color(color).alpha(alpha).rgbString();
}

printRow = function (tagIdResult, avgResult, diffResult) {
    const totalBody = $("#totalContentBody");
    totalBody.children().remove();
    const tr = [];
    if (tagIdResult != null && tagIdResult.length > 0) {
        for (let idx = 0; idx < tagIdResult.length; idx++) {
            tr.push("<tr>");
            tr.push("<td style='text-align: center;'>" + tagIdResult[idx] + "</td>");
            tr.push("<td style='text-align: center;'>" + avgResult[idx] + "</td>");
            tr.push("<td style='text-align: center;'>" + diffResult[idx] + "</td>");
            tr.push("<td style='text-align: center;'>Running</td>");
            tr.push("</tr>");
        }
    } else {
        tr.push("<tr>");
        tr.push("<td colspan=4 align='center'>조회된 데이터가 없습니다.</td>");
        tr.push("</tr>");
    }
    totalBody.append(tr.join(""));
}
