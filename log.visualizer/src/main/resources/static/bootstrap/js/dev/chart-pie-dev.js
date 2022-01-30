// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

drawMyIndexPieChart = function (date) {
    const tagIds = [];
    const errorOccurCounts = [];
    const errorOccurPercents = [];
    const graphColors = [];
    $.ajax({
        url: '/visualize-api/v2/error-occur-services?page=0&size=2&loggingDate=' + date,
        method: 'GET',
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        success: function(response) {
            if(response != null) {
                const data = response.data;
                for(let idx = 0; idx < data.content.length; idx++) {
                    tagIds.push(data.content[idx].tagId);
                    errorOccurCounts.push(data.content[idx].errorOccurCount);
                }
                if(tagIds.length > 0){
                    if(tagIds.length > 5){
                        tagIds.slice(0, 4);
                    }
                    calculatePercentColor(errorOccurCounts,errorOccurPercents, graphColors);
                    drawCircleChart(errorOccurPercents, graphColors, tagIds, date);
                }else{
                    if (window.myPie !== undefined) {
                        window.myPie.destroy();
                       // console.log("디스트로이등");
                    }
                    $('#myPieLabel').children().remove();
                    $('#myPieLabel').append("<span class='mr-2'>ERROR 레벨이 존재하지 않습니다.</span>");
                }
            }
        },
        error: function (response) {
            for (let i in response) {
                console.log('response error:' + i + ":" + response[i]);
            }
            // $('#myPieDiv').append("<span>데이터가 없습니다.</span>");
        }
    });
}

calculatePercentColor = function (errorOccurCounts,errorOccurPercents, graphColors) {
    let sum = 0;
    for(let idx = 0; idx < errorOccurCounts.length; idx++) {
        sum += errorOccurCounts[idx];
    }
    for(let idx = 0; idx < errorOccurCounts.length; idx++) {
        errorOccurPercents.push(errorOccurCounts[idx] / sum * 100);
        const randomR = Math.floor((Math.random() * 130) + 100);
        const randomG = Math.floor((Math.random() * 130) + 100);
        const randomB = Math.floor((Math.random() * 130) + 100);
        const graphBackground = "rgb("
            + randomR + ", "
            + randomG + ", "
            + randomB + ")";
        console.log('rgb>>' + graphBackground);
        graphColors.push(graphBackground);
    }
    console.log('graph-c>>' + graphColors);
}

// Pie Chart Example
drawCircleChart = function(errorOccurPercents, graphColors, tagIds, date) {
    const config2 = {
        type: 'doughnut',
        data: {
            labels: tagIds,
            datasets: [{
                data: errorOccurPercents,
                backgroundColor: graphColors,
                borderColor: graphColors,
                //fill: 'origin',
            }],
        },
        options: {
            //responsive: true,
            // legend: {
            //     position: 'bottom',
            //     align: 'start',
            // },
            segmentShowStroke: true,
            maintainAspectRatio: false,
            tooltips: {
                backgroundColor: "rgb(255,255,255)",
                bodyFontColor: "#858796",
                borderColor: '#dddfeb',
                borderWidth: 10,
                xPadding: 15,
                yPadding: 15,
                caretPadding: 10,
            },
            // hoverMode: 'nearest',
            // intersect: true
            // legend: {
            //     display: false
            // },
            legend: {
                position: 'bottom',
                display: false
                // margin: 0,
                // padding: 0
            },
            cutoutPercentage: 0,
        }
    }
    const ctx2 = document.getElementById("myPieChart").getContext('2d');
    if (window.myPie !== undefined) {
        window.myPie.destroy();
        const label = $("#myPieLabel");
        label.children().remove();
    }
    window.myPie = new Chart(ctx2, config2);
    window.myPie.update();
    $('#myPieLabel').children().remove();
    for(let idx = 0; idx < tagIds.length; idx++) {
        $('#myPieLabel').append("<span class='mr-2'><span id='myPieIndex' style='background-color:" + rgbToHex(graphColors[idx]) +";height: 50%'>&nbsp;&nbsp;&nbsp;&nbsp;</span> " + tagIds[idx] + "</span>");
    }
    const circle = window.myPie;
    document.getElementById("myPieChart").onclick = function(evt)
    {
        console.log("circle >>" + circle);
        var activePoints = circle.getElementsAtEvent(evt);
        console.log("circle >>" + circle);
        if(activePoints.length > 0)
        {
            console.log("circle >>" + circle);
            //get the internal index of slice in pie chart
            var clickedElementindex = activePoints[0]["_index"];

            //get specific label by index
            var label =  circle.data.labels[clickedElementindex];

            //get value by index
            var value =  circle.data.datasets[0].data[clickedElementindex];
            location.href="history/tag-id/" + label + "/searchDate/" + date;
            /* other stuff that requires slice's label and value */
        }
    }
}

colorToHex = function(color) {
    const hexadecimal = color.toString(16);
    return hexadecimal.length == 1 ? "0" + hexadecimal : hexadecimal;
}

convertRGBtoHex = function(red, green, blue) {
    return "#" + colorToHex(red) + colorToHex(green) + colorToHex(blue);
}

stringToRGB = function(text) {
    const colors = text.toString().split(",");
    const red = colors[0].slice(4);
    const green = colors[1].slice(1);
    let blue = colors[2].slice(1);
    blue = blue.slice(0, blue.length - 1)
    const result =  convertRGBtoHex(red, green, blue);

    return result;
}
rgbToHex = function (rgbType){
    /*
    ** 컬러값과 쉼표만 남기고 삭제하기.
    ** 쉼표(,)를 기준으로 분리해서, 배열에 담기.
    */
    const rgb = rgbType.replace( /[^%,.\d]/g, "" ).split( "," );

    rgb.forEach(function (str, x, arr){

        /* 컬러값이 "%"일 경우, 변환하기. */
        if ( str.indexOf( "%" ) > -1 ) str = Math.round( parseFloat(str) * 2.55 );

        /* 16진수 문자로 변환하기. */
        str = parseInt( str, 10 ).toString( 16 );
        if ( str.length === 1 ) str = "0" + str;

        arr[ x ] = str;
    });
    const result = "#" + rgb.join( "" );
    return result;
}

