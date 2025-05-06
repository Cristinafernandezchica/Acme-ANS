<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="any.flight.form.label.tag" path="tag" readonly="true"/>
	<acme:input-checkbox code="any.flight.form.label.indication" path="indication" readonly="true"/>
	<acme:input-money code="any.flight.form.label.cost" path="cost" readonly="true"/>
	<acme:input-textarea code="any.flight.form.label.description" path="description" readonly="true"/>
	<acme:input-moment code="any.flight.form.label.scheduledDeparture" path="scheduledDeparture" readonly="true"/>
    <acme:input-moment code="any.flight.form.label.scheduledArrival" path="scheduledArrival" readonly="true"/>
    <acme:input-textbox code="any.flight.form.label.originCity" path="originCity" readonly="true"/>
    <acme:input-textbox code="any.flight.form.label.destinationCity" path="destinationCity" readonly="true"/>
    <acme:input-textbox code="any.flight.form.label.layovers" path="layovers" readonly="true"/>
    
	<jstl:choose>	 
		<jstl:when test="${_command == 'show' && draftMode == false}">
			<acme:button code="any.flight.form.button.legs" action="/any/leg/list?flightId=${id}"/>			
		</jstl:when>	
	</jstl:choose>
</acme:form>