<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.lastDestinationsAssigned" path="lastDestinationsAssigned" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.numberOfLegsLowIncident" path="numberOfLegsLowIncident" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.numberOfLegsMediumIncident" path="numberOfLegsMediumIncident" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.numberOfLegsHighIncident" path="numberOfLegsHighIncident" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.crewMembersLastLeg" path="crewMembersLastLeg" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.confirmedFlightAssignments" path="confirmedFlightAssignments" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.pendingFlightAssignments" path="pendingFlightAssignments" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.cancelledFlightAssignments" path="cancelledFlightAssignments" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.averageDeviationNumberOfFlights" path="averageDeviationNumberOfFlights" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.minimumDeviationNumberOfFlights" path="minimumDeviationNumberOfFlights" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.maximumDeviationNumberOfFlights" path="maximumDeviationNumberOfFlights" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.standardDeviationNumberOfFlights" path="standardDeviationNumberOfFlights" readonly="true"/>
</acme:form>