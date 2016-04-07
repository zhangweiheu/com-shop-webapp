/**
 * Created by zhang on 2016/2/21.
 */
function md5password() {
    if ($("#password").val().textTrim === "" || $("#username").val().textTrim === "") {
        $("#submit_btn").attr("disabled", 'disabled');
        return false;
    }
    $("#password").val($.md5($("#password").val()));
    return true;
}
