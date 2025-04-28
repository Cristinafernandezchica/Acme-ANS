<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 

	<acme:input-textbox code="any.leg.form.label.flightNumber" path="flightNumber" readonly="true" placeholder="acme.leg.placeholder.flightNumber"/>
	<acme:input-moment code="any.leg.form.label.scheduledDeparture" path="scheduledDeparture" readonly="true"/>
	<acme:input-moment code="any.leg.form.label.scheduledArrival" path="scheduledArrival" readonly="true"/>
	<acme:input-select code="any.leg.form.label.status" path="status" choices="${statuses}"/>
	<acme:input-select code="any.leg.form.label.departureAirport" path="departureAirport" choices="${departureAirports}"  readonly="true"/>
	<acme:input-select code="any.leg.form.label.arrivalAirport" path="arrivalAirport" choices="${arrivalAirports}"  readonly="true"/>
	<acme:input-select code="any.leg.form.label.aircraft" path="aircraft" choices="${aircrafts}"  readonly="true"/>
	<acme:input-textbox code="any.leg.form.label.duration" path="duration" readonly="true"/>
	
</acme:form>