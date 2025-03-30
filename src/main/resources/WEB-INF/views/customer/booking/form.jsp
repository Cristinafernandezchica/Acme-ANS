<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="customer.booking.form.locatorCode" path="locatorCode"/>
	<acme:input-select code="customer.booking.form.travelClass" path="travelClass" choices="${classes}"/>	 <%-- multiple ="true" /> --%>
	<acme:input-money code="customer.booking.form.price" path="price"/>
	<acme:input-textbox code="customer.booking.form.lastCardNibble" path="lastCardNibble"/>
	<acme:input-select code="customer.booking.form.flight" path="flight" choices="${flights}"/>	

<%-- Hay que a�adir el selector multiopci�n de los passengers y el bot�n para a�adir los passengers --%>


<jstl:choose>
 	<jstl:when test="${_command == 'create'}">
 		<acme:input-checkbox code="customer.booking.form.confirmation" path="confirmation" />
 		<acme:submit code="customer.booking.form.button.create"	action="/customer/booking/create" />
 	</jstl:when>
 	<jstl:when test="${acme:anyOf(_command, 'show|update')}">
 		<acme:submit code="customer.booking.form.button.update"	action="/customer/booking/update" />
 	</jstl:when>
 </jstl:choose>

</acme:form>