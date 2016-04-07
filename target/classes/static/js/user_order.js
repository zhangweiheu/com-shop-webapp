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
    var pageSize = Math.floor(window.innerHeight / 45) - 6;
    if ($('#page').val() == "") {
        page = 1;
    }
    $('#page').val(page);
    $.ajax({
        method: "GET",
        url: "/api/user/order/list",
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
                            tbody += "<tr>";
                            tbody += "<td width='50px' style='border-left:1px solid #C1DAD7'>" + elem.id + "</td>";
                            tbody += "<td>" + elem.orderPrice + "</td>";
                            tbody += "<td>" + elem.expressStatus + "</td>";
                            tbody += "<td>" + elem.properties.createTime + "</td>";
                            tbody += "<td width='50px'><a  onclick=\"deleteRecord('" + elem.id + "')\"   btn-type=\"delete\" pid=\"" + elem.id + "\" href=\"#\">删除</a></td>";
                            tbody += "</tr>";
                        } else {
                            //超出部分
                            tbody += "<tr></tr>";
                        }
                    }
                    $("#user-order-tbody").html(tbody)
                    ;
                    buildPager(data.data.totalCount, data.data.page, data.data.pageSize);
                }
            } else {
                layer.alert('加载失败', {icon: 8});
            }
        }
    });
}


$(function () {
    var page = $("#page").val();
    var pageSize = $("#pageSize").val();

    buildTable(page, pageSize);

    $('#tmpl-select').on('change', function () {
        var page = 1;
        var pageSize = $("#pageSize").val();

        buildTable(page, pageSize);
    });
});
function deleteRecord(id) {
    layer.confirm('确认删除？', {
        icon: 4, offset: '150px', yes: function () {
            remove(id);
        }
    });
}
function remove(id) {
    $.ajax({
        method: "DELETE",
        url: "/api/user/order/" + id,
        async: true,
        success: function (data) {
            if (data.code == 0) {
                layer.alert('删除成功', {
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
