<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <!-- Airports -->
    <acme:input-textarea code="administrator.dashboard.form.label.airportCountByScope" 
                         path="airportCountByScope" 
                         readonly="true"/>
    
    <!-- Airlines -->
    <acme:input-textarea code="administrator.dashboard.form.label.airlineCountByType" 
                         path="airlineCountByType" 
                         readonly="true"/>
    
    <acme:input-double code="administrator.dashboard.form.label.airlineWithContactRatio" 
                       path="airlineWithContactRatio" 
                       readonly="true"/>
    
    <!-- Aircrafts -->
    <acme:input-double code="administrator.dashboard.form.label.activeAircraftRatio" 
                       path="activeAircraftRatio" 
                       readonly="true"/>
    
    <acme:input-double code="administrator.dashboard.form.label.inactiveAircraftRatio" 
                       path="inactiveAircraftRatio" 
                       readonly="true"/>
    
    <!-- Reviews -->
    <acme:input-double code="administrator.dashboard.form.label.highScoreReviewRatio" 
                        path="highScoreReviewRatio" 
                        readonly="true"/>
    
    <acme:input-integer code="administrator.dashboard.form.label.reviewCountLast10Weeks" 
                        path="reviewCountLast10Weeks" 
                        readonly="true"/>
    
    <acme:input-double code="administrator.dashboard.form.label.reviewAverageLast10Weeks" 
                       path="reviewAverageLast10Weeks" 
                       readonly="true"/>
    
    <acme:input-integer code="administrator.dashboard.form.label.reviewMinLast10Weeks" 
                        path="reviewMinLast10Weeks" 
                        readonly="true"/>
    
    <acme:input-integer code="administrator.dashboard.form.label.reviewMaxLast10Weeks" 
                        path="reviewMaxLast10Weeks" 
                        readonly="true"/>
    
    <acme:input-double code="administrator.dashboard.form.label.reviewStdDevLast10Weeks" 
                       path="reviewStdDevLast10Weeks" 
                       readonly="true"/>
</acme:form>