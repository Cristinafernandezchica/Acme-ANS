<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="manager.leg.form.label.flightNumber" path="flightNumber" readonly="true"/>
	<acme:input-textbox code="manager.leg.form.label.scheduledDeparture" path="scheduledDeparture"/>
	<acme:input-textbox code="manager.leg.form.label.scheduledArrival" path="scheduledArrival"/>
	<acme:input-select code="manager.leg.form.label.status" path="status" choices="${statuses}"/>
	<acme:input-checkbox code="manager.leg.form.label.draftMode" path="draftMode" readonly="true"/>
	<acme:input-textbox code="manager.leg.form.label.departureAirport" path="departureAirport" readonly="true"/>
	<acme:input-textbox code="manager.leg.form.label.arrivalAirport" path="arrivalAirport" readonly="true"/>
	<acme:input-select code="manager.leg.form.label.flight" path="flight" choices="${flights}"/>
	<acme:input-select code="manager.leg.form.label.aircraft" path="aircraft" choices="${aircrafts}"/>
	

	<jstl:choose>	 
		<jstl:when test="${_command == 'show' && draftMode == false}">
			<acme:button code="manager.leg.form.button.legs" action="/manager/leg/list?masterId=${id}"/>			
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
			<acme:button code="manager.leg.form.button.legs" action="/manager/leg/list?masterId=${id}"/>
			<acme:submit code="manager.leg.form.button.update" action="/manager/leg/update"/>
			<acme:submit code="manager.leg.form.button.delete" action="/manager/leg/delete"/>
			<acme:submit code="manager.leg.form.button.publish" action="/manager/leg/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.leg.form.button.create" action="/manager/leg/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>