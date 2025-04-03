<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<jstl:if test="${acme:anyOf(_command, 'show|update') && draftMode == false}">
		<acme:input-textbox code="manager.leg.form.label.flightNumber" path="flightNumber" readonly="true"/>
		<acme:input-moment code="manager.leg.form.label.scheduledDeparture" path="scheduledDeparture" readonly="true"/>
		<acme:input-moment code="manager.leg.form.label.scheduledArrival" path="scheduledArrival" readonly="true"/>
		<acme:input-select code="manager.leg.form.label.status" path="status" choices="${statuses}"/>
		<acme:input-select code="manager.leg.form.label.departureAirport" path="departureAirport" choices="${departureAirports}"  readonly="true"/>
		<acme:input-select code="manager.leg.form.label.arrivalAirport" path="arrivalAirport" choices="${arrivalAirports}"  readonly="true"/>
		<acme:input-select code="manager.leg.form.label.aircraft" path="aircraft" choices="${aircrafts}"  readonly="true"/>
	</jstl:if>
	
	<jstl:if test="${acme:anyOf(_command, 'show|update|delete|publish|create') && draftMode == true}">
		<acme:input-textbox code="manager.leg.form.label.flightNumber" path="flightNumber"/>
		<acme:input-moment code="manager.leg.form.label.scheduledDeparture" path="scheduledDeparture"/>
		<acme:input-moment code="manager.leg.form.label.scheduledArrival" path="scheduledArrival"/>
		<acme:input-textbox code="manager.leg.form.label.status" path="status" readonly="true"/>
		<acme:input-select code="manager.leg.form.label.departureAirport" path="departureAirport" choices="${departureAirports}"/>
		<acme:input-select code="manager.leg.form.label.arrivalAirport" path="arrivalAirport" choices="${arrivalAirports}"/>
		<acme:input-select code="manager.leg.form.label.aircraft" path="aircraft" choices="${aircrafts}"/>
	</jstl:if>
	
	<jstl:if test="${_command == 'show'}">
		<acme:input-textbox code="manager.leg.form.label.duration" path="duration" readonly="true"/>
	</jstl:if>
	

	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
			<acme:submit code="manager.leg.form.button.update" action="/manager/leg/update"/>
			<acme:submit code="manager.leg.form.button.delete" action="/manager/leg/delete"/>
			<acme:submit code="manager.leg.form.button.publish" action="/manager/leg/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.leg.form.button.create" action="/manager/leg/create?flightId=${flightId}"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update') && draftMode == false}">
			<acme:submit code="manager.leg.form.button.update" action="/manager/leg/update"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>