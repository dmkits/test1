/**
 * Created by ianagez on 20.10.16.
 */
function loadDataFromServer (url) {
    var data;
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, false);
    xhr.send();
    if (xhr.status != 200) {
        alert(xhr.status + ': ' + xhr.statusText); // пример вывода: 404: Not Found
        return null;
    } else {
        // вывести результат
        //            alert( xhr.responseText ); // responseText -- текст ответа.
        var dataText = xhr.responseText;
        data = JSON.parse(dataText);
        return data;
    }
}


var jsonHeader = {"X-Requested-With":"application/json; charset=utf-8"};
/**
 * doing JSON request and call onSuccess or onError
 * call preAction before send request.
 */
function doJSONRequest(jsonDataURI,requestMethod,reqJSONData, preAction,onSuccess,onError) {
    require(["dojo/request/xhr", "dijit/registry", "dojo/domReady!"],
        function (xhr, registry) {
            if (preAction!=null) reqJSONData = preAction(reqJSONData,registry);
            if (requestMethod!="post") {//get
                xhr.get(jsonDataURI, {headers:jsonHeader,handleAs:"json"}).then(
                    function(data){
                        onSuccess(data, registry);
                    }, function(error){
                        onError(error, registry);
                    })
            } else {//post
                xhr.post(jsonDataURI, {headers:jsonHeader,handleAs:"json",data:reqJSONData}).then(
                    function(data){
                        onSuccess(data, registry);
                    }, function(error){
                        onError(error, registry);
                    })
            }
        })
}

/** getJSONData
 * data.url, data.condition, data.consoleLog
 * if success : callback(true,data), if not success callback(false,error)
 * @param data
 * @param callback
 */
function getJSONData(data,callback){
    if (!data) return;
    require(["dojo/request/xhr", "dojo/domReady!"],
        function (xhr) {
            var url= data["url"],condition=data["condition"],consoleLog=data["consoleLog"];
            if(condition) url=url+"?"+condition;
            xhr.get(url, {headers:jsonHeader,handleAs:"json"}).then(
                function(data){
                    if(callback)callback(true, data);
                }, function(error){
                    if(consoleLog) console.log("getJSONData ERROR! url=",url," error=",error);
                    if(callback)callback(false, error);
                })
        });
}
/** postJSONData
 * data.url, data.condition, data.data, data.consoleLog
 * if success : callback(true,data), if not success callback(false,error)
 * @param data
 * @param callback
 */
function postJSONData(data,callback){
    if (!data) return;
    require(["dojo/request/xhr", "dojo/domReady!"],
        function (xhr) {
            var url= data["url"],condition=data["condition"],consoleLog=data["consoleLog"];
            if(condition) url=url+"?"+condition;
            xhr.post(url, {headers:jsonHeader,handleAs:"json",data:data["data"]}).then(
                function(data){
                    if(callback)callback(true, data);
                }, function(error){
                    if(consoleLog) console.log("postJSONData ERROR! url=",url," error=",error);
                    if(callback)callback(false, error);
                })
        });
}
