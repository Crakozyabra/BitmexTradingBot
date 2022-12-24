$(function() {

    $( "#add-bot-id" ).click(function() {
        let botInputDataJsonObj = {}
        botInputDataJsonObj["apiKey"] = $("#api-key").val();
        botInputDataJsonObj["apiSecret"] = $("#api-secret").val();
        botInputDataJsonObj["orderPrice"] = $("#order-price").val();
        botInputDataJsonObj["stepBetweenOrders"] = $("#step-between-orders").val();
        botInputDataJsonObj["ordersQuanity"] = $("#orders-quanity").val();
        console.log(botInputDataJsonObj);
        $.ajax({
            url: "http://localhost:8080/",
            method: "POST",
            dataType: "json",
            data: JSON.stringify(botInputDataJsonObj),
            success: function(result){
                console.log(result);
                if (result["botId"] > 0) {
                    $("body").append($(
                        "<div class='bot-container' id='bot-container" + result["botId"] + "' >" +
                        "<div><b>Api key:</b>" + botInputDataJsonObj["apiKey"] + "</div>" +
                        "<div><b>Bot id:</b>" + result["botId"] + "</div>" +
                        "<button class='stop-bot' id='" + result["botId"] + "' >Stop bot</button>" +
                        "</div>"
                    ));
                } else {
                    alert("Keys in the first and second bitmex bot data fields are not valid! Bot was not started.")
                }
            }
        });
    });


});

$(document).on('click', '.stop-bot', function () {
    let botId = $(this).attr('id');
    let htmlBotContainerId = "#bot-container" + botId;
    $(htmlBotContainerId).remove();
    let removeBotByIdJsonObj = {};
    removeBotByIdJsonObj["botIdForRemove"] = botId
    console.log(removeBotByIdJsonObj);
    $.ajax({
        url: "http://localhost:8080/remove_bot",
        method: "POST",
        dataType: "json",
        data: JSON.stringify(removeBotByIdJsonObj),
        success: function(result){}
    });
});