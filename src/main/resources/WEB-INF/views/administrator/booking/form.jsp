<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<jstl:if test="${acme:anyOf(_command, 'show|update|delete|publish')}">
		<acme:input-textbox code="administrator.booking.form.locatorCode" path="locatorCode" readonly="true"/>
		<acme:input-moment code="administrator.booking.form.purchaseMoment" path="purchaseMoment" readonly="true"/>
	</jstl:if> 
	<acme:input-select code="administrator.booking.form.travelClass" path="travelClass" choices="${classes}"/>
	<acme:input-money code="administrator.booking.form.price" path="price" readonly="true"/>
	<acme:input-textbox code="administrator.booking.form.lastCardNibble" path="lastCardNibble"/>
	<acme:input-select code="administrator.booking.form.flight" path="flight" choices="${flights}"/>	

	<jstl:choose>
	
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && !draftMode}"  >		
			<acme:button code="administrator.booking.form.show.passengers" action="/administrator/passenger/list?bookingId=${bookingId}"/>
		</jstl:when>	
	</jstl:choose>
</acme:form>