var CAE_LOGIN = function() {
    return {
        login : function() {
            var token = document.getElementById("previewToken").value;
            $.ajax({
                url:'/blueprint/servlet/externalpreview?token=' + token + '&method=login',
                dataType:'json',
                cache: false,
                data:[],
                success:function (json) {
                    var errorElement = document.getElementById("login-error");
                    errorElement.style.visibility="hidden";
                    if (json.status == 'ok') {
                        window.location = "preview.html#" + token;
                    }
                    else {
                        errorElement.style.visibility="visible";
                    }
                } ,
                error:function(result) {
                    alert(result.statusText);
                }
            });
        }
    };
}();