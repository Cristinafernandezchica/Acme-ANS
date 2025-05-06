<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <!-- 1. Destination Data -->
    <acme:input-textarea code="customer.dashboard.form.label.lastFiveDestinations" path="lastFiveDestinations" readonly="true"/>
    
    <!-- 2. Financials - Money Spent Last Year -->
    <acme:input-textarea code="customer.dashboard.form.label.moneySpentLastYear" 
                         path="moneySpentLastYearDisplay" 
                         readonly="true"/>

    <!-- 3. Booking Count by Travel Class -->
    <acme:input-textarea code="customer.dashboard.form.label.bookingCountByTravelClass" 
                         path="bookingCountByTravelClass" 
                         readonly="true"/>
            
    <!-- 4. Booking Count Last 5 Years -->
    <acme:input-integer code="customer.dashboard.form.label.bookingCountLastFiveYears" path="bookingCountLastFiveYears" readonly="true"/>
    
    <!-- 5. Booking Average Cost Last 5 Years -->
    <acme:input-textarea code="customer.dashboard.form.label.bookingAverageCostLastFiveYears" 
                         path="bookingAverageCostLastFiveYearsDisplay" 
                         readonly="true"/>
    
    <!-- 6. Booking Min Cost Last 5 Years -->
    <acme:input-textarea code="customer.dashboard.form.label.bookingMinCostLastFiveYears" 
                         path="bookingMinCostLastFiveYearsDisplay" 
                         readonly="true"/>
    
    <!-- 7. Booking Max Cost Last 5 Years -->
    <acme:input-textarea code="customer.dashboard.form.label.bookingMaxCostLastFiveYears" 
                         path="bookingMaxCostLastFiveYearsDisplay" 
                         readonly="true"/>
    
    <!-- 8. Booking Std Dev Cost Last 5 Years -->
    <acme:input-textarea code="customer.dashboard.form.label.bookingStdDevCostLastFiveYears" 
                         path="bookingStdDevCostLastFiveYearsDisplay" 
                         readonly="true"/>
        
    <!-- 9. Passenger Metrics -->
    <acme:input-integer code="customer.dashboard.form.label.passengerCount" path="passengerCount" readonly="true"/>
    <acme:input-double code="customer.dashboard.form.label.passengerAverage" path="passengerAverage" readonly="true"/>
    <acme:input-integer code="customer.dashboard.form.label.passengerMin" path="passengerMin" readonly="true"/>
    <acme:input-integer code="customer.dashboard.form.label.passengerMax" path="passengerMax" readonly="true"/>
    <acme:input-double code="customer.dashboard.form.label.passengerStdDev" path="passengerStdDev" readonly="true"/>
</acme:form>