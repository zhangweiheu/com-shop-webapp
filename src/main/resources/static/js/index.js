/**
 * Created by zhang on 2016/3/23.
 */
/**
 * Created by zhang on 2016/3/7.
 */

function buildPager(_totalCnt, _currentPage, pageSize) {
    $("#pageArea").pagination({
        items: _totalCnt,
        itemsOnPage: pageSize,
        currentPage: _currentPage,
        cssStyle: 'compact-theme',
        prevText: '上一页',
        nextText: '下一页',
        onPageClick: function (page, event) {
            buildTable(page, pageSize);
        }
    });
    $("#pageArea").append("<li class='page-link next' style='margin-left:30px;margin-top: 3px;font-size: 15px;'>共 " + _totalCnt + " 条</li>");
}

function buildTable(page, pageSize) {
    var pageSize = Math.floor(window.innerHeight / 34) - 6;
    if ($('#page').val() == "") {
        page = 1;
    }
    $('#page').val(page);
    $.ajax({
        method: "GET",
        url: "/api/user/goods/list",
        async: true,
        data: {"page": page, "pageSize": pageSize},
        dataType: "json",
        success: function (data) {
            var code = data.code;
            if (code == 0) {
                var curPageSize = data.data.data.length;
                if (curPageSize > 0) {
                    var tbody = "";
                    for (var i = 0; i < pageSize; i++) {
                        if (i < curPageSize) {
                            var elem = data.data.data[i];

                            tbody += "<div class='div-style'>";
                            tbody += "<a>";
                            tbody = tbody + "<img class='img-style' width='160px' height='160px' src='" + elem.linkPhoto + "'>";
                            tbody += "</a>";
                            tbody += "<div>";
                            tbody += "<span class='name-style'>商品名：</span>";
                            tbody += "<span class='value-style'>" + elem.name + "</span>";
                            tbody += "</div><br/>";
                            tbody += "<div>";
                            tbody += "<span class='name-style' >价格：</span>";
                            tbody += "<span class='value-style'>" + elem.price + "</span>";
                            tbody += "</div><br/>";
                            tbody += "<div>";
                            tbody += "<span class='name-style' >总销量：</span>";
                            tbody += "<span class='value-style'>" + elem.sellCount + "</span>";
                            tbody += "</div><br/>";
                            tbody += "<div>";
                            tbody += "<span class='name-style' >存货量：</span>";
                            tbody += "<span class='value-style'>" + elem.remain + "</span>";
                            tbody += "</div><br/>";
                            tbody += "<div>";
                            tbody += "<span class='name-style' >状态：</span>";
                            tbody += "<span class='value-style'>" + elem.status + "</span>";
                            tbody += "</div><br/>";
                            tbody += "<form>";
                            tbody += "<div>";
                                tbody += "<div align='center'>";
                                tbody += "<span>购买数量</span>";
                                tbody += "<input style='width: 40%' id='" + elem.id + "' name='count' type='number' min='1' max='"　+　elem.remain　+　"' required='required' maxV='"+elem.remain+"'/>";
                                tbody += "</div><br/>";
                                if (elem.remain < 1) {
                                    tbody += "<input class='btn-style' type='submit' value='加入购物车' disabled>";
                                } else {
                                    tbody += "<input class='btn-style' type='submit' value='加入购物车'  onclick=\"submit2Cart('" + elem.id + "')\"/>";
                                }
                            tbody += "</div>";
                            tbody += "</form>";
                            tbody += "</div>";
                        } else {
                            //超出部分
                            tbody += "<tr></tr>";
                        }
                    }
                    $("#index-goods-tbody").html(tbody)
                    ;
                    buildPager(data.data.totalCount, data.data.page, data.data.pageSize);
                }
            } else {
                layer.alert('加载失败', {icon: 8});
            }
        }
    });
}

function submit2Cart(id) {
    var count = $("#" +id).val();
    var max = $("#" +id)[0].getAttribute("maxV");
    if(count === undefined || count < 1 || count > max){
        layer.alert("购买数量不符合要求", {icon: 8});
        return;
    }
    var d = {
        "id": id,
        "count": count
    };

    $.ajax({
        method: "post",
        url: "/api/user/cart/",
        async: true,
        data: d,
        success: function (data) {
            if (data.code == 0) {
                layer.alert('添加成功', {
                    icon: 1, offset: '150px', end: function () {
                        location.reload(true);
                    }
                });
            } else {
                layer.alert(data.msg, {icon: 11, offset: '150px'})
            }
        }
    });
}

$(function () {
    var page = $("#page").val();
    var pageSize = $("#pageSize").val();
    buildTable(page, pageSize);
});
