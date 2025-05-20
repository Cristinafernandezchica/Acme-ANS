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
	<acme:input-moment code="flight-crew-member.flight-assignment.list.label.lastUpdate" path="lastUpdate" readonly ="true"/>
	<acme:input-textbox code="flight-crew-member.flight-assignment.list.label.currentStatus" path="currentStatus"  readonly ="true"/>
	<acme:input-textbox code="flight-crew-member.flight-assignment.list.label.remarks" path="remarks" placeholder="acme.flightAssignment.placeholder.remarks"/>
	<acme:input-select code="flight-crew-member.flight-assignment.list.label.legs" path="legRelated" choices="${legs}"/>
	<acme:input-select code="flight-crew-member.flight-assignment.list.label.availableFlightCrewMembers" path="flightCrewMemberAssigned" choices="${availableFlightCrewMembers}" readonly ="true"/>

</acme:form>