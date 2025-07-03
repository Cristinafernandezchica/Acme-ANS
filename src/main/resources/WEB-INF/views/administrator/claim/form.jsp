<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<jstl:choose>
		<jstl:when test="${_command == 'show'}">
			<acme:input-moment code="administrator.claim.form.label.registrationMoment" path="registrationMoment" readonly="true"/>
			<acme:input-email code="administrator.claim.form.label.passengerEmail" path="passengerEmail" readonly="true"/>
			<acme:input-textbox code="administrator.claim.form.label.type" path="type" readonly="true"/>	
			<acme:input-textarea code="administrator.claim.form.label.description" path="description" readonly="true"/>
			<acme:input-textbox code="administrator.claim.form.label.leg" path="leg" readonly="true"/>
			<acme:input-textbox code="administrator.claim.form.label.accepted" path="accepted" readonly="true"/>
			
			<acme:button code="administrator.claim.form.button.trackingLogs" action="/administrator/tracking-log/list?claimId=${id}"/>				
		</jstl:when>
	</jstl:choose>
</acme:form>