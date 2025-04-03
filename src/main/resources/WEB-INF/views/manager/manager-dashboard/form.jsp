<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="manager.dashboard.form.label.rankingManager" path="rankingManager"/>
	<acme:input-textbox code="manager.dashboard.form.label.yearsToRetire" path="yearsToRetire"/>
	<acme:input-textbox code="manager.flight.form.label.ratioOnTimeDelayedLegs" path="ratioOnTimeDelayedLegs"/>
	<acme:input-textbox code="manager.flight.form.label.mostPopularAirport" path="mostPopularAirport"/>
	<acme:input-textbox code="manager.flight.form.label.lessPopularAirport" path="lessPopularAirport" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.legOnTime" path="legOnTime" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.legDelayed" path="legDelayed" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.legCancelled" path="legCancelled" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.legLanded" path="legLanded" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.averageFlightCost" path="averageFlightCost" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.minFlightCost" path="minFlightCost" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.maxFlightCost" path="maxFlightCost" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.standarDerivationFlightCost" path="standarDerivationFlightCost" readonly="true"/>
	
</acme:form>