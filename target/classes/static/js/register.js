/**
 * Created by zhang on 2016/2/21.
 */
function md5password() {
    if ($("#username").val().textTrim === "") {
        layer.alert("用户名不能为空",{icon: 11});
        $("#submit_btn").attr("disabled", 'disabled');
        return false;
    }
    if ($("#password").val().textTrim === "" || $("#password").val() != $("#password2").val()) {
        layer.alert("两次密码不一致",{icon: 11});
        $("#submit_btn").attr("disabled", 'disabled');
        return false;
    }

    $("#password").val($.md5($("#password").val()));
    return true;
}

function checkpassword(){
    if ($("#password").val().textTrim === "" || $("#password").val() != $("#password2").val()) {
        layer.alert("两次密码不一致",{icon: 11});
        $("#submit_btn").attr("disabled", 'disabled');
        return false;
    }
}
