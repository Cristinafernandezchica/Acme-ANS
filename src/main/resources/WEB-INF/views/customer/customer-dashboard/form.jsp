<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <!-- 1. Last Five Destinations -->
    <acme:input-textarea code="customer.dashboard.form.label.lastFiveDestinations" path="lastFiveDestinations" readonly="true"/>

    <!-- 2. Money Spent Last Year -->
    <jstl:forEach items="${moneySpentLastYear}" var="money" varStatus="loop">
        <acme:input-textbox code="customer.dashboard.form.label.moneySpentLastYear (${money.currency})"
                            path="moneySpentLastYear[${loop.index}].amount"
                            readonly="true"/>
    </jstl:forEach>

    <!-- 3. Booking Count by Travel Class -->
    <acme:input-textarea code="customer.dashboard.form.label.bookingCountByTravelClass" 
                         path="bookingCountByTravelClass" 
                         readonly="true"/>

    <!-- 4. Booking Count Last 5 Years -->
    <acme:input-integer code="customer.dashboard.form.label.bookingCountLastFiveYears" path="bookingCountLastFiveYears" readonly="true"/>

    <!-- 5. Booking Average Cost Last 5 Years -->
    <jstl:forEach items="${bookingAverageCostLastFiveYears}" var="avgMoney" varStatus="loop">
        <acme:input-textbox code="customer.dashboard.form.label.bookingAverageCostLastFiveYears (${avgMoney.currency})"
                            path="bookingAverageCostLastFiveYears[${loop.index}].amount"
                            readonly="true"/>
    </jstl:forEach>

    <!-- 6. Booking Min Cost Last 5 Years -->
    <jstl:forEach items="${bookingMinCostLastFiveYears}" var="minMoney" varStatus="loop">
        <acme:input-textbox code="customer.dashboard.form.label.bookingMinCostLastFiveYears (${minMoney.currency})"
                            path="bookingMinCostLastFiveYears[${loop.index}].amount"
                            readonly="true"/>
    </jstl:forEach>

    <!-- 7. Booking Max Cost Last 5 Years -->
    <jstl:forEach items="${bookingMaxCostLastFiveYears}" var="maxMoney" varStatus="loop">
        <acme:input-textbox code="customer.dashboard.form.label.bookingMaxCostLastFiveYears (${maxMoney.currency})"
                            path="bookingMaxCostLastFiveYears[${loop.index}].amount"
                            readonly="true"/>
    </jstl:forEach>

    <!-- 8. Booking Std Dev Cost Last 5 Years -->
    <jstl:forEach items="${bookingStdDevCostLastFiveYears}" var="stdMoney" varStatus="loop">
        <acme:input-textbox code="customer.dashboard.form.label.bookingStdDevCostLastFiveYears (${stdMoney.currency})"
                            path="bookingStdDevCostLastFiveYears[${loop.index}].amount"
                            readonly="true"/>
    </jstl:forEach>

    <!-- 9. Passenger Stats -->
    <acme:input-integer code="customer.dashboard.form.label.passengerCount" path="passengerCount" readonly="true"/>
    <acme:input-double code="customer.dashboard.form.label.passengerAverage" path="passengerAverage" readonly="true"/>
    <acme:input-integer code="customer.dashboard.form.label.passengerMin" path="passengerMin" readonly="true"/>
    <acme:input-integer code="customer.dashboard.form.label.passengerMax" path="passengerMax" readonly="true"/>
    <acme:input-double code="customer.dashboard.form.label.passengerStdDev" path="passengerStdDev" readonly="true"/>
</acme:form>
