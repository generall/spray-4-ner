$(function () {

  var Reciever = {

    request : function(addr, data, type, isJson) {
      return $.ajax({
        type: type,
        url: addr,
        data: isJson ? JSON.stringify(data) : data,
        contentType: (isJson ? "application/json;" : "plain/text;") + " charset=utf-8",
        dataType: "json"
      });
    },

    requestJson : function(addt, data){ return request(addt, data, "POST", true); }

  }



  document.obj = Reciever


});


