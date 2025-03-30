<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.booking.list.locatorCode" path="locatorCode" width="20%"/>
	<acme:list-column code="customer.booking.list.purchaseMoment" path="purchaseMoment" width="20%"/>
	<acme:list-column code="customer.booking.list.travelClass" path="travelClass" width="20%"/>
	<acme:list-column code="customer.booking.list.price" path="price" width="20%"/>
	<acme:list-column code="customer.booking.list.flight" path="flight" width="20%"/>
	
	<acme:list-payload path="payload"/>
</acme:list>

<jstl:if test="${_command == 'list'}">
	<acme:button code="customer.booking.list.button.create" action="/customer/booking/create"/>
</jstl:if>


<%--
- <jstl:if test="${_command == 'create'}">
-	<acme:submit code="customer.booking.list.button.create" action="/customer/booking/create"/>
- </jstl:if>	
--%>
