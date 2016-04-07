/**
 * Created by zhang on 2016/2/21.
 */
$.ajax({
    // 获取id，challenge，success（是否启用failback）
    url: "/api/token/geetest",
    type: "get",
    dataType: "json", // 使用jsonp格式
    success: function (data) {
        // 使用initGeetest接口
        // 参数1：配置参数，与创建Geetest实例时接受的参数一致
        // 参数2：回调，回调的第一个参数验证码对象，之后可以使用它做appendTo之类的事件
        initGeetest({
            gt: data.data.gt,
            challenge: data.data.challenge,
            product: "embed", // 产品形式
            offline: !data.data.success
        }, function (captchaObj) {
            // 将验证码加到id为captcha的元素里
            captchaObj.appendTo("#captcha");
            captchaObj.onSuccess(function () {
                $("#submit_btn").removeAttr("disabled");
            });
            captchaObj.refresh(function () {
                $("#submit_btn").attr("disabled", 'disabled');
            });
            captchaObj.onRefresh(function () {
                $("#submit_btn").attr("disabled", 'disabled');
            });
            captchaObj.onFail(function () {
                $("#submit_btn").attr("disabled", 'disabled');
            });
        });
    }
});
