
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="any.leg.list.label.flightNumber" path="flightNumber" width="10%" sortable="false"/>
	<acme:list-column code="any.leg.list.label.scheduledDeparture" path="scheduledDeparture" width="15%"/>
    <acme:list-column code="any.leg.list.label.scheduledArrival" path="scheduledArrival" width="15%" sortable="false"/>
    <acme:list-column code="any.leg.list.label.departureAirport" path="departureAirport" width="15%" sortable="false"/>
    <acme:list-column code="any.leg.list.label.arrivalAirport" path="arrivalAirport" width="15%" sortable="false"/>
</acme:list>
		
	

