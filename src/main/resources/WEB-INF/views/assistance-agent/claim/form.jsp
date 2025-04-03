<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode}">
			<acme:input-moment code="assistance-agent.claim.form.label.registrationMoment" path="registrationMoment" readonly="true"/>
			<acme:input-email code="assistance-agent.claim.form.label.passengerEmail" path="passengerEmail"/>
			<acme:input-select code="assistance-agent.claim.form.label.type" path="type" choices="${types}"/>	
			<acme:input-textarea code="assistance-agent.claim.form.label.description" path="description"/>
			<acme:input-select code="assistance-agent.claim.form.label.leg" path="leg" choices="${legs}"/>
			
			<acme:submit code="assistance-agent.claim.form.button.update" action="/assistance-agent/claim/update"/>
			<acme:submit code="assistance-agent.claim.form.button.delete" action="/assistance-agent/claim/delete"/>
			<acme:submit code="assistance-agent.claim.form.button.publish" action="/assistance-agent/claim/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'show' && !draftMode}">
			<acme:input-moment code="assistance-agent.claim.form.label.registrationMoment" path="registrationMoment" readonly="true"/>
			<acme:input-email code="assistance-agent.claim.form.label.passengerEmail" path="passengerEmail" readonly="true"/>
			<acme:input-select code="assistance-agent.claim.form.label.type" path="type" choices="${types}" readonly="true"/>	
			<acme:input-textarea code="assistance-agent.claim.form.label.description" path="description" readonly="true"/>
			<acme:input-select code="assistance-agent.claim.form.label.leg" path="leg" choices="${legs}" readonly="true"/>
			<acme:input-checkbox code="assistance-agent.claim.form.label.accepted" path="accepted" readonly="true"/>
			
			<acme:button code="assistance-agent.claim.form.button.trackingLogs" action="/assistance-agent/claim/listCompleted?claimId=${id}"/>	
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-email code="assistance-agent.claim.form.label.passengerEmail" path="passengerEmail"/>
			<acme:input-select code="assistance-agent.claim.form.label.type" path="type" choices="${types}"/>	
			<acme:input-textarea code="assistance-agent.claim.form.label.description" path="description"/>
			<acme:input-select code="assistance-agent.claim.form.label.leg" path="leg" choices="${legs}"/>
			
			<acme:submit code="assistance-agent.claim.form.button.create" action="/assistance-agent/claim/create"/>
		</jstl:when>
	</jstl:choose>
</acme:form>