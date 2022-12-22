
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Title</title>
        <meta charset="utf-8">
        <link href="static/css/styles.css" rel="stylesheet">
        <%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
        <script src="static/js/scripts.js"></script>
    </head>
    <body>
        <div class="form-position">
            <div>
                <b>Bitmex bot data:</b>
            </div>
            <input type="text" id="api-key" placeholder="Api key" />
            <input type="text" id="api-secret" placeholder="Api secret" />
            <input type="text" id="order-price" placeholder="Order price in USD" />
            <input type="text" id="step-between-orders" placeholder="Step between bots.algoritms.ordermakers.orders" />
            <input type="number" id="orders-quanity" placeholder="Orders quanity" />
            <br>
            <input type="button" id="add-bot-id" value="Confirm"/>
        </div>
    </body>
</html>
