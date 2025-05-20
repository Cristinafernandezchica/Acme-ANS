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
	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:input-textbox code="administrator.aircraft.form.label.model" path="model" placeholder="acme.aicraft.placeholder.model"/>
			<acme:input-select code="administrator.aircraft.form.label.airline" path="airline" choices="${airlines}"/>
			<acme:input-textbox code="administrator.aircraft.form.label.registrationNumber" path="registrationNumber" placeholder="acme.aicraft.placeholder.registrationNumber"/>
			<acme:input-integer code="administrator.aircraft.form.label.numberPassengers" path="numberPassengers" placeholder="acme.aicraft.placeholder.numberPassengers"/>
			<acme:input-select code="administrator.aircraft.form.label.status" path="status" choices="${statuses}"/>
			<acme:input-integer code="administrator.aircraft.form.label.cargoWeight" path="cargoWeight" placeholder="acme.aicraft.placeholder.cargoWeight"/>
			<acme:input-textbox code="administrator.aircraft.form.label.details" path="details" placeholder="acme.aicraft.placeholder.details"/>
		</jstl:when>
		
		<jstl:when test="${acme:anyOf(_command, 'show|update')}">
			<acme:input-textbox code="administrator.aircraft.form.label.model" path="model" readonly ="true"/>
			<acme:input-select code="administrator.aircraft.form.label.airline" path="airline" choices="${airlines}"/>
			<acme:input-textbox code="administrator.aircraft.form.label.registrationNumber" path="registrationNumber" readonly ="true" />
			<acme:input-integer code="administrator.aircraft.form.label.numberPassengers" path="numberPassengers" readonly ="true"/>
			<acme:input-select code="administrator.aircraft.form.label.status" path="status" choices="${statuses}"/>
			<acme:input-integer code="administrator.aircraft.form.label.cargoWeight" path="cargoWeight" readonly ="true"/>
			<acme:input-textbox code="administrator.aircraft.form.label.details" path="details" placeholder="acme.aicraft.placeholder.details"/>
		</jstl:when>
	</jstl:choose>		
	
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