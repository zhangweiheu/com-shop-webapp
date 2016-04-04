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
        url: "/api/user/cart/list",
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
                            tbody += "<td width='50px'>" + elem.goodsId + "</td>";
                            tbody += "<td>" + elem.properties.goodsName + "</td>";
                            tbody += "<td width='50px'>" + elem.goodsPrice + "</td>";
                            tbody += "<td>" + elem.goodsCount + "</td>";
                            tbody += "<td width='50px'>" + elem.totalPrice + "</td>";
                            tbody += "<td>" + elem.properties.createTime + "</td>";
                            tbody += "<td width='50px'><input btn-type='pay' name='pay' type='checkbox' value='"+elem.id +"'></td>";
                            tbody += "<td width='50px'><a btn-type=\"edit\" pid=\""+elem.id +"\" href=\"#\">编辑</a></td>";
                            tbody += "<td width='50px'><a  onclick=\"deleteRecord('" + elem.id + "')\"   btn-type=\"delete\" pid=\"" + elem.id + "\" href=\"#\">删除</a></td>";
                            tbody += "</tr>";
                        } else {
                            //超出部分
                            tbody += "<tr></tr>";
                        }
                    }
                    $("#user-cart-tbody").html(tbody)
                    $("#user-cart-tfoot").html("<span style='position: relative;top: 55px;left: 1000%'>优惠码：</span><input id='serial-number' type='number' style='position: relative;left: 1100%;width: 400%;margin-top: 30px' required><input id='pay-btn' style='background-color:#fff;color: #0f0f0f;font-size: 15px;position: relative;left: 1200%;margin-top: 30px;width: 200%' type='button' value='支付' onclick='pay()'>");
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

    $(document).delegate("a[btn-type='edit']", "click", function () {
        var qid = $(this)[0].getAttribute("pid");
        edit_tmpl(qid);
    });

    $('#tmpl-select').on('change', function () {
        var page = 1;
        var pageSize = $("#pageSize").val();

        buildTable(page, pageSize);
    });
});


function edit_tmpl(qid) {
    layer.open({
        type: 2,
        title: '编辑商品',
        shadeClose: true,
        shade: 0.5,
        content: '/user/cart/edit/' + qid,
        area: ['70%', '80%'],
        end: function () {
            buildTable($('#page').val(), $('#pageSize').val());
        }
    });
}

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
        url: "/api/user/cart/" + id,
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

function pay(){
    var serialNumber = $("#serial-number").val().trim();
    if(serialNumber === undefined){
        layer.alert("优惠码不能为空", {icon: 11, offset: '150px'});
        return
    }
    var id_array = new Array();
    var inputName = "input[name=pay]:checked";
    $(inputName.valueOf()).each(function () {
        id_array.push($(this).val());
    });
    var ids = id_array.join();
    var d = {
        "serialNumber":serialNumber,
        "ids":ids
    }

    $.ajax({
        method: "POST",
        url: "/api/user/order/",
        data:d,
        async: true,
        success: function (data) {
            if (data.code == 0) {
                layer.alert('支付成功', {
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
