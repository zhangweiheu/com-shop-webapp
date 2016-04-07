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
        url: "/api/system/user/list",
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
                            tbody += "<td class='fixWid' style='border-left: 1px solid #C1DAD7'>" + elem.id + "</td>";
                            tbody += "<td style='70px'>" + elem.username + "</td>";
                            tbody += "<td>" + elem.email + "</td>";
                            tbody += "<td>" + elem.phone + "</td>";
                            tbody += "<td>" + elem.wechat + "</td>";
                            if (!elem.delete) {
                                tbody += "<td>正常</td>";
                            } else {
                                tbody += "<td>已删除</td>";
                            }
                            tbody += "<td>" + elem.integration + "</td>";
                            tbody += "<td>" + elem.properties.createTime + "</td>";
                            if (elem.admin) {
                                tbody += "<td>管理员</td>";
                            } else {
                                tbody += "<td>普通用户</td>";
                            }
                            tbody += "<td class='fixWid'><a btn-type=\"edit\" uid=\"" + elem.id + "\" href=\"#\">编辑</a></td>";
                            tbody += "<td class='fixWid'><a  onclick=\"deleteRecord('" + elem.id + "')\"   btn-type=\"delete\" uid=\"" + elem.id + "\" href=\"#\">删除</a></td>";
                            tbody += "</tr>";
                        } else {
                            //超出部分
                            tbody += "<tr></tr>";
                        }

                    }
                    $("#system-user-tbody").html(tbody)
                    ;
                    buildPager(data.data.totalCount, data.data.page, data.data.pageSize);
                }
            } else {
                layer.alert('加载失败', {icon: 8});
            }
        }
    });
}

function edit_tmpl(uid) {
    layer.open({
        type: 2,
        title: '编辑用户',
        shadeClose: true,
        shade: 0.5,
        content: '/system/user/edit/' + uid,
        area: ['70%', '80%'],
        end: function () {
            buildTable($('#page').val(), $('#pageSize').val());
        }
    });
}

function add_tmpl() {
    buildCommonLayer('新增用户', '/system/user/edit/0');
}

$(function () {
    var page = $("#page").val();
    var pageSize = $("#pageSize").val();

    buildTable(page, pageSize);

    $(document).delegate("a[btn-type='edit']", "click", function () {
        var uid = $(this)[0].getAttribute("uid");
        edit_tmpl(uid);
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
        url: "/api/system/user/" + id,
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

function addPass() {
    var phoneNumber = $("#phoneNumber").val();
    if (!isCellphone(phoneNumber)) {
        layer.alert("手机号不正确！", {icon: 11, offset: '150px'});
        return false;
    }
    var d = {
        "phoneNumber": phoneNumber
    };
    $.ajax({
        method: "POST",
        url: "/api/system/pass/",
        data: d,
        async: true,
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

function isCellphone(str) {
    /**
     *@descrition:手机号码段规则
     * 13段：130、131、132、133、134、135、136、137、138、139
     * 14段：145、147
     * 15段：150、151、152、153、155、156、157、158、159
     * 17段：170、176、177、178
     * 18段：180、181、182、183、184、185、186、187、188、189
     *
     */
    var pattern = /^(13[0-9]|14[57]|15[012356789]|17[0678]|18[0-9])\d{8}$/;
    return pattern.test(str);
}
