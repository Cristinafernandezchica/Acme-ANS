<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.booking.list.locatorCode" path="booking.locatorCode" width="30%"/>
	<acme:list-column code="administrator.passenger.list.fullName" path="passenger.fullName" width="40%"/>
	<acme:list-column code="administrator.passenger.list.passport" path="passenger.passport" width="30%"/>
	
	<acme:list-payload path="payload"/>
</acme:list>
