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
	<jstl:if test="${_command == 'create'}">
		<acme:input-select code="flight-crew-member.flight-assignment.list.label.flightCrewsDuty" path="flightCrewsDuty" choices="${flightcrewsDuties}"/>
		<acme:input-moment code="flight-crew-member.flight-assignment.list.label.lastUpdate" path="lastUpdate" readonly ="true"/>
		<acme:input-textbox code="flight-crew-member.flight-assignment.list.label.currentStatus" path="currentStatus"  readonly ="true"/>
		<acme:input-textbox code="flight-crew-member.flight-assignment.list.label.remarks" path="remarks"/>
		<acme:input-select code="flight-crew-member.flight-assignment.list.label.legs" path="legRelated" choices="${legs}"/>
		<acme:input-select code="flight-crew-member.flight-assignment.list.label.availableFlightCrewMembers" path="flightCrewMemberAssigned" choices="${availableFlightCrewMembers}" readonly ="true"/>
	</jstl:if>	
	<jstl:if test="${acme:anyOf(_command, 'show|update|publish|delete')}">
		<acme:input-select code="flight-crew-member.flight-assignment.list.label.flightCrewsDuty" path="flightCrewsDuty" choices="${flightcrewsDuties}"/>
		<acme:input-moment code="flight-crew-member.flight-assignment.list.label.lastUpdate" path="lastUpdate" readonly ="true"/>
		<acme:input-select code="flight-crew-member.flight-assignment.list.label.currentStatus" path="currentStatus" choices="${statuses}"/>
		<acme:input-textbox code="flight-crew-member.flight-assignment.list.label.remarks" path="remarks"/>
		<acme:input-select code="flight-crew-member.flight-assignment.list.label.legs" path="legRelated" choices="${legs}"/>
		<acme:input-select code="flight-crew-member.flight-assignment.list.label.availableFlightCrewMembers" path="flightCrewMemberAssigned" choices="${availableFlightCrewMembers}" readonly ="true"/>
	</jstl:if>	
	
	
	<jstl:choose>
		<jstl:when test="${_command == 'show' && draftMode == false}"> 
			<acme:button code="flight-crew-member.flight-assignment.form.button.activityLogs" action="/flight-crew-member/activity-log/list?faId=${id}"/>			
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && draftMode == true}">
			<acme:input-checkbox code="flight-crew-member.flight-assignment.form.label.confirmation" path="confirmation"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.update" action="/flight-crew-member/flight-assignment/update"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.publish" action="/flight-crew-member/flight-assignment/publish"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.delete" action="/flight-crew-member/flight-assignment/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="flight-crew-member.flight-assignment.form.label.confirmation" path="confirmation"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.create" action="/flight-crew-member/flight-assignment/create"/>
		</jstl:when>		
	</jstl:choose>

</acme:form>