<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="manager.dashboard.form.label.rankingManager" path="rankingManager"/>
	<acme:input-textbox code="manager.dashboard.form.label.yearsToRetire" path="yearsToRetire"/>
	<acme:input-money code="manager.flight.form.label.cost" path="cost"/>
	<acme:input-textarea code="manager.flight.form.label.description" path="description"/>
	<acme:input-checkbox code="manager.flight.form.label.draftMode" path="draftMode" readonly="true"/>
	<acme:input-moment code="manager.flight.form.label.scheduledDeparture" path="scheduledDeparture" readonly="true"/>
	<acme:input-moment code="manager.flight.form.label.scheduledArrival" path="scheduledArrival" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.originCity" path="originCity" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.destinationCity" path="destinationCity" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.layovers" path="layovers" readonly="true"/>
	
</acme:form>