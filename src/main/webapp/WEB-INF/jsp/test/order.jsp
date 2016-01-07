<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title></title>
</head>
<body>
<div class="cf">
    <div>自有库存</div>
    <c:if test="${localOrderInfoList.size()>0}">
        <ul id="local-seats" style="display: block;">
            <c:forEach items="${localOrderInfoList}" var="localOrderInfo">
                <li>${localOrderInfo.seat}</li>
            </c:forEach>
        </ul>
    </c:if>
    <div>非自有库存</div>
        <ul id="ciname-seats" style="display: block;">
                <li>${bisOrderInfo.seat}</li>
        </ul>
</div>
<div style="clear:both">
    <input id="btnUnlock" type="submit" value="取消锁座">
</div>
<div style="clear:both">
    <c:if test="${env eq 'dev'}">
        <input id="btnDrawTicket" type="submit" value="出票">
    </c:if>
</div>
<div style="clear:both">
    <c:if test="${env eq 'dev'}">
        <input id="btnRefundTicket" type="submit" value="退票">
    </c:if>
</div>
<div style="clear:both">
    <c:if test="${env eq 'dev'}">
        <input id="btnQueryTicketStatus" type="submit" value="查询票状态">
    </c:if>
</div>
</body>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/resources/js/jquery-2.1.4.min.js"></script>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
        var flag = false;//是否出票的标志,用于为票状态为已出票的票进行退票
        $("#btnDrawTicket").click(function () {
            $.ajax({
                url: "${pageContext.request.contextPath}/ticket/draw", type: "POST", data: {
                    "orderId": $.urlParam('orderId'), "mobile": "13800138000"
                }, success: function (data) {
                    if (data.ret === "0") {
                        flag = true;
                        alert("出票成功:    ticketCode:"
                                + data.data[0].ticketCode
                                + "     tpOrderId:"
                                + data.data[0].tpOrderId);

                    } else {
                        alert(JSON.stringify(data));
                        event.preventDefault();
                    }
                }

            });

        });
        $("#btnQueryTicketStatus").click(function () {
            $.ajax({
                url: "${pageContext.request.contextPath}/api/localhs/status", type: "POST", data: {
                    "orderId": $.urlParam('orderId')
                }, success: function (data) {
                    if (data.ret === "0") {
                        alert("查询票状态成功,ticketCode:" + data.data[0].ticketCode);
                    } else {
                        alert(JSON.stringify(data));
                        event.preventDefault();
                    }
                }

            });

        });
        $("#btnRefundTicket").click(function() {
            if(flag == true){
                $.ajax({
                    url: "${pageContext.request.contextPath}/ticket/refund", type: "POST", data: {
                        "orderId": $.urlParam('orderId')
                    },success: function (data) {
                        if (data.ret === "0") {
                            alert("退票成功   ordetId:"+$.urlParam('orderId'));
                        } else {
                            alert(JSON.stringify(data));
                            event.preventDefault();
                        }
                    }
                });
            }else{
                alert("---请先确定当前要退的票已出票---");
            }
        });
        $("#btnUnlock").click(function () {
            $.ajax({
                url: "${pageContext.request.contextPath}/seats/orders/"+$.urlParam('orderId')+"/seat/unlock", type: "POST", data: {
                    "orderId": $.urlParam('orderId'), "mobileNo": "13800138000"
                }, success: function (data) {
                    if (data.data == true) {
                        alert("取消锁座成功！")
                    } else {
                        alert(JSON.stringify(data));
                        event.preventDefault();
                    }
                }
            })
        })
    });
</script>
</html>