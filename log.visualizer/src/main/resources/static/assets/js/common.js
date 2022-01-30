cellMerge = function(tid, tcol, vtd) {
    var id = tid;
    var col = tcol;
    var td = ((!!vtd) ? vtd : "td"  ); // 기본이 td 태그 기준으로 merge함

    var spanCnt = 1;
    var preText; //이전 텍스트
    var r = [];

    var rows = $("tbody[id="+id+"] tr").find(td+":eq("+col[0]+")").length; //row 개수

    if(rows <= 1){
        return false;
    }

    for(var i = 0; col.length > i; i++) {
        r.push({ idx : [], spans : []});

        if(i == 0){ //첫번째
            $("tbody[id="+id+"] tr").find(td+":eq("+col[i]+")").each(function(index, item){// row수만큼 돌린다.
                if(index == 0) { //첫번째 row
                    r[i].idx.push(index);
                    spanCnt = 1;
                } else if( (rows-1) == index) { //마지막 row
                    if(preText != $(this).text()) { //중복없이 단일행 마지막
                        r[i].idx.push(index);
                        r[i].spans.push(spanCnt);
                    } else { //전 값이 동일한 경우
                        spanCnt = spanCnt+1;
                        r[i].spans.push(spanCnt);
                    }
                } else if(preText != $(this).text()) { //텍스트가 바뀔때 index
                    r[i].idx.push(index);
                    r[i].spans.push(spanCnt);
                    spanCnt = 1;
                } else { //위 아래 텍스트가 같을 때
                    spanCnt = spanCnt+1;
                }

                preText = $(this).text();
            });
        } else if( !r[i-1].idx.length) { // rowSpan할 row가 없다면 종료
            break;
        } else { //그 뒤 그룹으로
            var j = 1; //그룹 구별자

            $("tbody[id="+id+"] tr").find(td+":eq("+col[i]+")").each(function(index, item) { // row수만큼 돌린다.
                if(index == 0) { //첫번째 row
                    r[i].idx.push(index);
                    spanCnt = 1;
                } else if(r[i-1].idx[j] == (index)) { //rowSpan 적용 시작 지점에서 리셋
                    r[i].idx.push(index);
                    r[i].spans.push(spanCnt);
                    spanCnt = 1;
                    j++;
                } else if( (rows-1) == index) { //마지막 row
                    if(preText != $(this).text()) { //중복없이 단일행 마지막
                        r[i].idx.push(index);
                        r[i].spans.push(spanCnt);
                    } else { //전 값이 동일한 경우
                        spanCnt = spanCnt+1;
                        r[i].spans.push(spanCnt);
                    }
                } else if(preText != $(this).text()) { //텍스트가 바뀔때 index
                    r[i].idx.push(index);
                    r[i].spans.push(spanCnt);
                    spanCnt = 1;
                } else { //위 아래 텍스트가 같을 때
                    spanCnt = spanCnt+1;
                }

                preText = $(this).text();
            });
        }
    }

    for(var k = col.length; k > 0; k--) { //역순으로 돌려야 그룹이 안깨짐
        $("tbody[id="+id+"] tr").find(td+":eq("+col[k-1]+")").each(function(index, item) {
            if(r[k-1].idx[0] == index) {
                $(this).attr("rowspan",r[k-1].spans[0]);
                r[k-1].idx.shift();
                r[k-1].spans.shift();
            } else {
                $(this).remove();
            }
        });
    }
}

cellImageMerge = function(tid, tcol, vtd) {
    var id = tid;
    var col = tcol;
    var td = ((!!vtd) ? vtd : "td"  ); // 기본이 td 태그 기준으로 merge함

    var spanCnt = 1;
    var preText; //이전 텍스트
    var r = [];

    var rows = $("tbody[id="+id+"] tr").find(td+":eq("+col[0]+")").length; //row 개수

    if(rows <= 1){
        return false;
    }

    for(var i = 0; col.length > i; i++) {
        r.push({ idx : [], spans : []});

        if(i == 0){ //첫번째
            $("tbody[id="+id+"] tr").find(td+":eq("+col[i]+")").each(function(index, item){// row수만큼 돌린다.
                var img = $(this).find('img');

                if(index == 0) { //첫번째 row
                    r[i].idx.push(index);
                    spanCnt = 1;
                } else if( (rows-1) == index) { //마지막 row
                    if(preText != img.attr('src')) { //중복없이 단일행 마지막
                        r[i].idx.push(index);
                        r[i].spans.push(spanCnt);
                    } else { //전 값이 동일한 경우
                        spanCnt = spanCnt+1;
                        r[i].spans.push(spanCnt);
                    }
                } else if(preText != img.attr('src')) { //텍스트가 바뀔때 index
                    r[i].idx.push(index);
                    r[i].spans.push(spanCnt);
                    spanCnt = 1;
                } else { //위 아래 텍스트가 같을 때
                    spanCnt = spanCnt+1;
                }

                preText = img.attr('src');
            });
        } else if( !r[i-1].idx.length) { // rowSpan할 row가 없다면 종료
            break;
        } else { //그 뒤 그룹으로
            var j = 1; //그룹 구별자

            $("tbody[id="+id+"] tr").find(td+":eq("+col[i]+")").each(function(index, item) { // row수만큼 돌린다.
                var img = $(this).find('img');

                if(index == 0) { //첫번째 row
                    r[i].idx.push(index);
                    spanCnt = 1;
                } else if(r[i-1].idx[j] == (index)) { //rowSpan 적용 시작 지점에서 리셋
                    r[i].idx.push(index);
                    r[i].spans.push(spanCnt);
                    spanCnt = 1;
                    j++;
                } else if( (rows-1) == index) { //마지막 row
                    if(preText != img.attr('src')) { //중복없이 단일행 마지막
                        //if(preText != $(this).text()) { //중복없이 단일행 마지막
                        r[i].idx.push(index);
                        r[i].spans.push(spanCnt);
                    } else { //전 값이 동일한 경우
                        spanCnt = spanCnt+1;
                        r[i].spans.push(spanCnt);
                    }
                } else if(preText != img.attr('src')) { //텍스트가 바뀔때 index
                    r[i].idx.push(index);
                    r[i].spans.push(spanCnt);
                    spanCnt = 1;
                } else { //위 아래 텍스트가 같을 때
                    spanCnt = spanCnt+1;
                }

                preText = img.attr('src');
            });
        }
    }

    for(var k = col.length; k > 0; k--) { //역순으로 돌려야 그룹이 안깨짐
        $("tbody[id="+id+"] tr").find(td+":eq("+col[k-1]+")").each(function(index, item) {
            if(r[k-1].idx[0] == index) {
                $(this).attr("rowspan",r[k-1].spans[0]);
                r[k-1].idx.shift();
                r[k-1].spans.shift();
            } else {
                $(this).remove();
            }
        });
    }
}

getLandingUrl  = function () {
    var currentLocation = window.location.pathname;
    var pathNameArray = currentLocation.split("/");
    var landingUrl = pathNameArray[1];

    return landingUrl;
}

getDateFormat = function(date) {
    var year = date.getFullYear();
    var month = (1 + date.getMonth());
    month = month >= 10 ? month : '0' + month;
    var day = date.getDate();
    day = day >= 10 ? day : '0' + day;

    return year + '-' + month + '-' + day;
}

// 콤마 적용
inputNumberFormat = function(obj) {
    obj.value = comma(uncomma(obj.value));
}

// 콤마 추가
comma = function(str) {
    str = String(str);
    return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
}

// 콤마 제거
uncomma = function(str) {
    str = String(str);
    return str.replace(/[^\d]+/g, '');
}

replacePattern = function(str, pattern) {
    var regex;
    if (pattern == ",") {
        regex = /\,/g;
    } else if(pattern == "-") {
        regex = /\-/g;
    } else {
        return str;
    }

    return str.replace(regex, '');
}

getUrlParameter = function (param) {
    var pageUrl = window.location.search.substring(1);
    var urlVariables = pageUrl.split('&');

    for (var i = 0; i < urlVariables.length; i++) {
        var pramName = urlVariables[i].split('=');
        if (pramName[0] == param) {
            return pramName[1];
        }
    }
}

isNumberKey = function(evt) {
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode != 46 && charCode > 31 && (charCode < 48 || charCode > 57)) {
        return false;
    }

    return true;
}

isCheckNumber = function(val) {
    var numbers = /^[0-9]+$/;
    if(val.match(numbers)) {
        return true;
    }
    else {
        return false;
    }
}