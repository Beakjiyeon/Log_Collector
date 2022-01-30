
loadBoard = function(method, url, printContentFunc, indicatorFunc) {
    indicatorFunc();

    $.ajax({
        url: url,
        method: method,
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        //beforeSend: function(xhr) {
        //  xhr.setRequestHeader(header, token);
        //},
        success: function(response) {
            printContentFunc(response);
            if (indicatorFunc) indicatorFunc();
        },
        error: function(response) {
            for (i in response) {
                console.log('response error:' + i + ":" + response[i]);
            }
            if (indicatorFunc) indicatorFunc();

            if (response['status'] == 401) {
                swal({
                    title: "로그인 시간 만료.",
                    text: "로그인 시간이 만료되었습니다. 다시 로그인 부탁드립니다.",
                    confirmButtonColor: "#dc3545",
                }, function (isConfirm) {
                    location.href = "/login";
                });
            }
        }
    });
}

requestWithData = function(method, url, data, printContentFunc, indicatorFunc) {
    if (indicatorFunc) indicatorFunc();

    $.ajax({
        url: url,
        method: method,
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        data: data,
        success: function(response) {
            printContentFunc(response);
            if (indicatorFunc) indicatorFunc();
        },
        error: function(response) {
            for (i in response) {
                console.log('response error:' + i + ":" + response[i]);
            }
            if (indicatorFunc) indicatorFunc();

            if (response['status'] == 401) {
                swal({
                    title: "로그인 시간 만료.",
                    text: "로그인 시간이 만료되었습니다. 다시 로그인 부탁드립니다.",
                    confirmButtonColor: "#dc3545",
                }, function (isConfirm) {
                    location.href="/login";
                });
            }
        }
    });
}

reducefilter = function (filterMap) {
    if (filterMap == null && filterMap.length <= 0) return "";

    //console.log(filterMap);

    var filterPath = [];
    for(var fk in filterMap) {
    //for(let fk of filterMap.keys()) {
        filterPath.push(fk+":"+filterMap[fk])
    }
    return filterPath.join("/");
}

printPage = function(pageInfo, filter) {
    var pagination = $("#pagination");
    var lis = []

    var filterPath = reducefilter(filter);

    //console.log(pageInfo.prevPage + ":" + pageInfo.currentPage);

    if (pageInfo.prevPage > 0 && pageInfo.prevPage < pageInfo.blockBegin) {
        lis.push("<li class=\"page-item\">")
    } else {
        lis.push("<li class=\"page-item disabled\">")
    }
    lis.push("<a class=\"page-link\" " +
        "href=\"javascript:loadPage('"+ filterPath +"',"+ pageInfo.prevPage + ");\" " +
        "tabindex=\"-1\">Previous</a>");
    lis.push("</li>");

    var currentPageList = pageInfo.currentBlockPages;

    currentPageList.forEach(pageNum => {
        //console.log(pageNum + ", pageNum:" + typeof(pageNum) + ", pageInfo.currrentPage:" +  typeof(pageInfo.currentPage) );
        if (pageNum == pageInfo.currentPage){
            lis.push("<li class=\"page-item active\">" +
                "<a class=\"page-link\" href=\"javascript:loadPage('"+ filterPath +"',"+ pageNum + ");\">"
                + pageNum + "</a>" +
                "</li>");

            //console.log("current page:" + pageNum + "," +pageInfo.currentPage);
        } else {
            lis.push("<li class=\"page-item\">" +
                "<a class=\"page-link\" href=\"javascript:loadPage('"+ filterPath +"',"+ pageNum + ");\">"
                + pageNum + "</a>" +
                "</li>");

            //console.log("next page:" + pageNum + "," +pageInfo.currentPage);
        }
    });

    //console.log(pageInfo.nextPage + ":" + pageInfo.currentPage);
    if (pageInfo.nextPage > 0 && pageInfo.nextPage > pageInfo.blockEnd) {
        lis.push("<li class=\"page-item\">");
    } else {
        lis.push("<li class=\"page-item disabled\">");
    }
    lis.push("<a class=\"page-link\" " +
        "href=\"javascript:loadPage('"+ filterPath +"',"+ pageInfo.nextPage + ");\">Next</a>" +
        "</li>");

    pagination.append(lis.join(""));
}

printMultiplePage = function(pageInfo, filter, pagination, linkPage) {
    var callFunction = linkPage;
    var lis = []

    var filterPath = reducefilter(filter);

    //console.log(pageInfo.prevPage + ":" + pageInfo.currentPage);

    if (pageInfo.prevPage > 0 && pageInfo.prevPage < pageInfo.blockBegin) {
        lis.push("<li class=\"page-item\">")
    } else {
        lis.push("<li class=\"page-item disabled\">")
    }
    lis.push("<a class=\"page-link\" " +
        "href=\"javascript:"+linkPage+"('"+ filterPath +"',"+ pageInfo.prevPage + ");\" " +
        "tabindex=\"-1\">Previous</a>");
    lis.push("</li>");

    var currentPageList = pageInfo.currentBlockPages;

    currentPageList.forEach(pageNum => {
        //console.log(pageNum + ", pageNum:" + typeof(pageNum) + ", pageInfo.currrentPage:" +  typeof(pageInfo.currentPage) );
        if (pageNum == pageInfo.currentPage){
            lis.push("<li class=\"page-item active\">" +
                "<a class=\"page-link\" href=\"javascript:"+linkPage+"('"+ filterPath +"',"+ pageNum + ");\">"
                + pageNum + "</a>" +
                "</li>");

            //console.log("current page:" + pageNum + "," +pageInfo.currentPage);
        } else {
            lis.push("<li class=\"page-item\">" +
                "<a class=\"page-link\" href=\"javascript:"+linkPage+"('"+ filterPath +"',"+ pageNum + ");\">"
                + pageNum + "</a>" +
                "</li>");

            //console.log("next page:" + pageNum + "," +pageInfo.currentPage);
        }
    });

    //console.log(pageInfo.nextPage + ":" + pageInfo.currentPage);
    if (pageInfo.nextPage > 0 && pageInfo.nextPage > pageInfo.blockEnd) {
        lis.push("<li class=\"page-item\">");
    } else {
        lis.push("<li class=\"page-item disabled\">");
    }
    lis.push("<a class=\"page-link\" " +
        "href=\"javascript:"+linkPage+"('"+ filterPath +"',"+ pageInfo.nextPage + ");\">Next</a>" +
        "</li>");

    pagination.append(lis.join(""));
}

loadTable = function() {
    $(".table.table-striped").each(function(){
        var tableId = $(this).attr('id');
        //console.log("reportMetric >>> "+reportMetric);

        // 화면에 표시될 총 컬럼(고정컬럼 + 리포트유형 컬럼)
        var showMetricTotCnt = $(this).find('[name=fixedCol]').length + reportMetric.length;
        var showConvMetricTotCnt = $(this).find('[name=fixedCol]').length + convMetric.length;

        var thIdx = 0;
        var metricIdx = 0;

        $(this).find("th").each(function() {
            var thId = $(this).attr("id");
            var thName = $(this).attr("name");

            if(thName == "metricCol") {
                if(thIdx >= showMetricTotCnt) {
                    $(this).css("display","none");
                } else {
                    $(this).text(reportMetric[metricIdx]);

                    var metricEngNm = "";
                    switch(reportMetric[metricIdx]) {
                        case "노출수":
                            metricEngNm = "impCount";
                            break;
                        case "클릭수":
                            metricEngNm = "clickCount";
                            break;
                        case "CTR":
                            metricEngNm = "ctr";
                            break;
                        case "CPC":
                            metricEngNm = "cpc";
                            break;
                        case "광고비":
                            metricEngNm = "adCost";
                            break;
                        case "전환수":
                            metricEngNm = "convCount";
                            break;
                        case "전환가치":
                            metricEngNm = "convValue";
                            break;
                        case "전환율":
                            metricEngNm = "cvr";
                            break;
                        case "객단가":
                            metricEngNm = "mediaUnitPrice";
                            break;
                        case "CPA":
                            metricEngNm = "cpa";
                            break;
                        case "ROAS":
                            metricEngNm = "mediaRoas";
                            break;
                        default:
                            break;
                    }

                    $(this).attr('id', metricEngNm);
                }
                metricIdx++;
            } else if(thName == "convMetricCol") {
                if(thIdx >= showConvMetricTotCnt) {
                    $(this).css("display","none");
                } else {
                    $(this).text(convMetric[metricIdx]);
                }
                metricIdx++;

            }
            thIdx++;
        });
    });
}

setTableMetricData = function(tr, data, metricArray) {
    for(var metricIdx=0; metricIdx<metricArray.length; metricIdx++) {
        switch(metricArray[metricIdx]) {

            case "노출수":
                tr.push("<td id='impCount' name='metricCol'>" + data.impCount.toLocaleString() + "</td>");
                break;
            case "클릭수":
                tr.push("<td id='clickCount' name='metricCol'>" + data.clickCount.toLocaleString() + "</td>");
                break;
            case "CTR":
                tr.push("<td id='ctr' name='metricCol'>" + data.ctr.toFixed(2) + "%</td>");
                break;
            case "CPC":
                tr.push("<td id='cpc' name='metricCol'>" + data.cpc.toLocaleString() + "</td>");
                break;
            case "광고비":
                tr.push("<td id='adCost' name='metricCol'>" + data.adCost.toLocaleString() + "</td>");
                break;
            case "전환수":
                tr.push("<td id='convCount' name='metricCol'>" + data.convCount.toLocaleString() + "</td>");
                break;
            case "전환가치":
                tr.push("<td id='convValue' name='metricCol'>" + data.convValue.toLocaleString() + "</td>");
                break;
            case "전환율":
                tr.push("<td id='cvr' name='metricCol'>" + data.cvr.toFixed(2) + "%</td>");
                break;
            case "객단가":
                tr.push("<td id='mediaUnitPrice' name='metricCol'>" + data.mediaUnitPrice.toLocaleString()  +"</td>");
                break;
            case "CPA":
                tdName = "cpa";
                tr.push("<td id='cpa' name='metricCol'>" + data.cpa.toLocaleString()+"</td>");
                break;
            case "ROAS":
                tdName = "mediaRoas";
                tr.push("<td id='mediaRoas' name='metricCol'>" + data.mediaRoas.toLocaleString() +"%</td>");
                break;
            default:
                break;
        }
    }
}

setCompareTableMetricData = function(tr, data, metricArray) {
    for(var metricIdx=0; metricIdx<metricArray.length; metricIdx++) {
        var classStyle = "";
        var imgStyle = "";

        var strIdx = data.dataDesc.search("증감률");

        switch(metricArray[metricIdx]) {
            case "노출수":
                if(data.impCount > 0) {
                    classStyle = "text-navy";
                    imgStyle = "fa-level-up";
                } else if (data.impCount < 0) {
                    classStyle = "text-danger";
                    imgStyle = "fa-level-down";
                }

                if(strIdx > 0) {
                    tr.push("<td id='impCount' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.impCount.toFixed(2) + "%</td>");
                } else {
                    tr.push("<td id='impCount' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.impCount.toLocaleString() + "</td>");
                }
                break;
            case "클릭수":
                if(data.clickCount > 0) {
                    classStyle = "text-navy";
                    imgStyle = "fa-level-up";
                } else if (data.clickCount < 0) {
                    classStyle = "text-danger";
                    imgStyle = "fa-level-down";
                }

                if(strIdx > 0) {
                    tr.push("<td id='clickCount' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.clickCount.toFixed(2) + "%</td>");
                } else {
                    tr.push("<td id='clickCount' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.clickCount.toLocaleString() + "</td>");
                }
                break;
            case "CTR":
                if(data.ctr > 0) {
                    classStyle = "text-navy";
                    imgStyle = "fa-level-up";
                } else if (data.ctr < 0) {
                    classStyle = "text-danger";
                    imgStyle = "fa-level-down";
                }

                tr.push("<td id='ctr' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.ctr.toFixed(2) + "%</td>");
                break;
            case "CPC":
               if(data.cpc > 0) {
                    classStyle = "text-navy";
                    imgStyle = "fa-level-up";
                } else if (data.cpc < 0) {
                    classStyle = "text-danger";
                    imgStyle = "fa-level-down";
                }

                if(strIdx > 0) {
                    tr.push("<td id='cpc' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.cpc.toFixed(2) + "%</td>");
                } else {
                    tr.push("<td id='cpc' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.cpc.toLocaleString() + "</td>");
                }
                break;
            case "광고비":
                if(data.adCost > 0) {
                    classStyle = "text-navy";
                    imgStyle = "fa-level-up";
                } else if (data.adCost < 0) {
                    classStyle = "text-danger";
                    imgStyle = "fa-level-down";
                }

                if(strIdx > 0) {
                    tr.push("<td id='adCost' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.adCost.toFixed(2) + "%</td>");
                } else {
                    tr.push("<td id='adCost' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.adCost.toLocaleString() + "</td>");
                }
                break;
            case "전환수":
                if(data.convCount > 0) {
                    classStyle = "text-navy";
                    imgStyle = "fa-level-up";
                } else if (data.convCount < 0) {
                    classStyle = "text-danger";
                    imgStyle = "fa-level-down";
                }

                if(strIdx > 0) {
                    tr.push("<td id='convCount' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.convCount.toFixed(2) + "%</td>");
                } else {
                    tr.push("<td id='convCount' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.convCount.toLocaleString() + "</td>");
                }
                break;
            case "전환가치":
                if(data.convValue > 0) {
                    classStyle = "text-navy";
                    imgStyle = "fa-level-up";
                } else if (data.convValue < 0) {
                    classStyle = "text-danger";
                    imgStyle = "fa-level-down";
                }

                if(strIdx > 0) {
                    tr.push("<td id='convValue' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.convValue.toFixed(2) + "%</td>");
                } else {
                    tr.push("<td id='convValue' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.convValue.toLocaleString() + "</td>");
                }
                break;
            case "전환율":
                if(data.cvr > 0) {
                    classStyle = "text-navy";
                    imgStyle = "fa-level-up";
                } else if (data.cvr < 0) {
                    classStyle = "text-danger";
                    imgStyle = "fa-level-down";
                }

                tr.push("<td id='cvr' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.cvr.toFixed(2) + "%</td>");
                break;
            case "객단가":
                if(data.mediaUnitPrice > 0) {
                    classStyle = "text-navy";
                    imgStyle = "fa-level-up";
                } else if (data.mediaUnitPrice < 0) {
                    classStyle = "text-danger";
                    imgStyle = "fa-level-down";
                }

                if(strIdx > 0) {
                    tr.push("<td id='mediaUnitPrice' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.mediaUnitPrice.toFixed(2) + "%</td>");
                } else {
                    tr.push("<td id='mediaUnitPrice' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.mediaUnitPrice.toLocaleString() + "</td>");
                }
                break;
            case "CPA":
                tdName = "cpa";
                if(data.cpa > 0) {
                    classStyle = "text-navy";
                    imgStyle = "fa-level-up";
                } else if (data.cpa < 0) {
                    classStyle = "text-danger";
                    imgStyle = "fa-level-down";
                }

                if(strIdx > 0) {
                    tr.push("<td id='cpa' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.cpa.toFixed(2) + "%</td>");
                } else {
                    tr.push("<td id='cpa' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.cpa.toLocaleString() + "</td>");
                }
                break;
            case "ROAS":
                tdName = "mediaRoas";
                if(data.mediaRoas > 0) {
                    classStyle = "text-navy";
                    imgStyle = "fa-level-up";
                } else if (data.mediaRoas < 0) {
                    classStyle = "text-danger";
                    imgStyle = "fa-level-down";
                }

                if(strIdx > 0) {
                    tr.push("<td id='mediaRoas' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.mediaRoas.toFixed(2) + "%</td>");
                } else {
                    tr.push("<td id='mediaRoas' name='metricCol' class=\""+classStyle+"\"><i class=\"fa "+imgStyle+"\">" + data.mediaRoas.toLocaleString() + "%</td>");
                }

                break;
            default:
                break;
        }
    }
}