<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title></title>
</head>
<body>
<div class="seat-content">
    <c:forEach items="${seats}" var="item">
        <div style="width:100px;float:left">
            <span>${item}</span><input type="checkbox" value="${item}"/>
        </div>
    </c:forEach>
    <%--<p><span class="num">1</span>--%>
    <%--<span>01</span><input type="checkbox" value="01:1:1"/>--%>
    <%--<span>02</span><input type="checkbox" value="01:1:2"/>--%>
    <%--&lt;%&ndash;<a href="javascript:;" hidefocus="true" class="seat active selected" data-no="" data-type="N" data-row="1" data-column="01" title="1排01座"></a>&ndash;%&gt;--%>
    <%--&lt;%&ndash;<a href="javascript:;" hidefocus="true" class="seat active selected" data-no="" data-type="N" data-row="1" data-column="02" title="1排02座"></a>&ndash;%&gt;--%>
    <%--&lt;%&ndash;<a href="javascript:;" hidefocus="true" class="seat active" data-no="" data-type="N" data-row="1" data-column="03" title="1排03座"></a>&ndash;%&gt;--%>
    <%--</p>--%>
</div>
<div style="clear:both">
    <input id="btnSubmit" type="submit" value="锁坐">
</div>
<div style="clear:both">
    <input id="btnHotSeatSubmit" type="submit" value="座位加入任务表">
</div>
<div style="clear:both">
    <input id="buySeatsIntoLocalSubmit" type="submit" value="将任务表中的热门座位购入自有库存">
</div>
</body>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/resources/js/jquery-2.1.4.min.js"></script>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
        $("#btnSubmit").click(function () {

            var seats = "";
            $(":checkbox").each(function (envent) {
                if ($(this).is(":checked")) {
                    if (seats === "") {
                        seats += $(this).val().split("-")[0];
                    } else {
                        seats += "," + $(this).val().split("-")[0];
                    }

                }
            });
            var orderId = (new Date()).toISOString().replace(/[^0-9]/g, "").substr(2, 12)
                    + Math.ceil(Math.random() * (900000 - 100000) + 100000);
            $.ajax({
                url: "${pageContext.request.contextPath}/seats/lock", type: "POST", data: {
                    "scheduleId": $.urlParam('scheduleId'),
                    "tpId": $.urlParam('channelId'),
                    "mobile": "13800138000",
                    "baseCinemaNo": $.urlParam('cinemaNo'),
                    "orderId": orderId,
                    "seats": seats
                }, success: function (data) {
                    if (data.ret === "0") {
                        //                        alert("锁坐成功,订单号:" + orderId);
                        location.href = "${pageContext.request.contextPath}/test/order?orderId="
                                + orderId;
                    } else {
                        alert(JSON.stringify(data));
                        event.preventDefault();
                    }
                }

            });

        });

        $("#btnHotSeatSubmit").click(function () {

            var seats = "";
            $(":checkbox").each(function (envent) {
                if ($(this).is(":checked")) {
                    if (seats === "") {
                        seats += $(this).val().split("-")[0];
                    } else {
                        seats += "," + $(this).val().split("-")[0];
                    }

                }
            });

            $.ajax({
                url: "${pageContext.request.contextPath}/tasks/seat/purchaseForTest", type: "POST", data: {
                    "scheduleId": $.urlParam('scheduleId'),
                    "channelId": $.urlParam('channelId'),
                    "cinemaNo": $.urlParam('cinemaNo'),
                    "seats": seats
                }, success: function (data) {
                    if (data.ret === "0") {
                        alert("座位已加入任务表！");
                    } else {
                        alert(JSON.stringify(data));
                        event.preventDefault();
                    }
                }

            });

        });


        $("buySeatsIntoLocalSubmit").click(function(){
            $.ajax({
                url: "${pageContext.request.contextPath}/jobs/lockAndDraw", type: "POST", data: {},
                success: function (data) {
                    if (data.ret === "0") {
                        alert("已将任务表中的座位购入自有库存中");
                    } else {
                        alert(JSON.stringify(data));
                        event.preventDefault();
                    }
                }

            });
        });
    });
</script>
</html>
