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
        url: "/api/system/goods/list",
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
                            tbody += "<td style='border-left:1px solid #C1DAD7'>" + elem.id + "</td>";
                            tbody += "<td style='width:auto'>" + elem.name + "</td>";
                            tbody += "<td>" + elem.price + "</td>";
                            tbody += "<td style='width:auto'>" + elem.remain + "</td>";
                            tbody += "<td style='width:auto'>" + elem.sellCount + "</td>";
                            tbody += "<td style='width:50px'>" + elem.discount + "</td>";
                            tbody += "<td>" + elem.status + "</td>";
                            tbody += "<td>" + elem.providerName + "</td>";
                            tbody += "<td>" + elem.providerPhone + "</td>";
                            tbody += "<td>" + elem.properties.createTime + "</td>";
                            tbody += "<td class='fixWid'><a btn-type=\"edit\" qid=\"" + elem.id + "\" href=\"#\">编辑</a></td>";
                            tbody += "<td class='fixWid'><a  onclick=\"deleteRecord('" + elem.id + "')\"   btn-type=\"delete\" qid=\"" + elem.id + "\" href=\"#\">删除</a></td>";
                            tbody += "</tr>";
                        } else {
                            //超出部分
                            tbody += "<tr></tr>";
                        }
                    }
                    $("#system-goods-tbody").html(tbody)
                    ;
                    buildPager(data.data.totalCount, data.data.page, data.data.pageSize);
                }
            } else {
                layer.alert('加载失败', {icon: 8});
            }
        }
    });
}

function edit_tmpl(qid) {
    layer.open({
        type: 2,
        title: '编辑商品',
        shadeClose: true,
        shade: 0.5,
        content: '/system/goods/edit/' + qid,
        area: ['70%', '80%'],
        end: function () {
            buildTable($('#page').val(), $('#pageSize').val());
        }
    });
}

function add_tmpl() {
    buildCommonLayer('新增商品', '/system/goods/edit/0');
}

$(function () {
    var page = $("#page").val();
    var pageSize = $("#pageSize").val();

    buildTable(page, pageSize);

    $(document).delegate("a[btn-type='edit']", "click", function () {
        var qid = $(this)[0].getAttribute("qid");
        edit_tmpl(qid);
    });

    $(document).delegate("a[btn-type='add']", "click", function () {
        add_tmpl();
    });

    $('#tmpl-select').on('change', function () {
        var page = 1;
        var pageSize = $("#pageSize").val();

        buildTable(page, pageSize);
    });
});
function deleteRecord(qid) {
    layer.confirm('确认删除？', {
        icon: 4, offset: '150px', yes: function () {
            remove(qid);
        }
    });
}
function remove(qid) {
    $.ajax({
        method: "DELETE",
        url: "/api/system/goods/" + qid,
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
