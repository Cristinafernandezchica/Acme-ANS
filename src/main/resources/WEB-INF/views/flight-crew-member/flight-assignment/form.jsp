<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-select code="flight-crew-member.flight-assignment.list.label.flightCrewsDuty" path="flightCrewsDuty" choices="${flightcrewsDuties}"/>
	<acme:input-moment code="flight-crew-member.flight-assignment.list.label.lastUpdate" path="lastUpdate" placeholder="2025/01/01 00:00"/>
	<acme:input-select code="flight-crew-member.flight-assignment.list.label.currentStatus" path="currentStatus" choices="${statuses}"/>
	<acme:input-textbox code="flight-crew-member.flight-assignment.list.label.remarks" path="remarks"/>
	<acme:input-select code="flight-crew-member.flight-assignment.list.label.legs" path="legs" choices="${legs}"/>
	<acme:input-select code="flight-crew-member.flight-assignment.list.label.availableFlightCrewMembers" path="availableFlightCrewMembers" choices="${availableFlightCrewMembers}"/>
	
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update')}">
			<acme:input-checkbox code="administrator.aircraft.form.label.confirmation" path="confirmation"/>
			<acme:submit code="administrator.aircraft.form.button.update" action="/administrator/aircraft/update"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="administrator.aircraft.form.label.confirmation" path="confirmation"/>
			<acme:submit code="administrator.aircraft.form.button.create" action="/administrator/aircraft/create"/>
		</jstl:when>		
	</jstl:choose>

</acme:form>