<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.booking.list.locatorCode" path="locatorCode" width="15%"/>
	<acme:list-column code="administrator.booking.list.purchaseMoment" path="purchaseMoment" width="20%"/>
	<acme:list-column code="administrator.booking.list.travelClass" path="travelClass" width="15%"/>
	<acme:list-column code="administrator.booking.list.price" path="price" width="20%"/>
	<acme:list-column code="administrator.booking.list.flight" path="flight" width="30%"/>
	
	<acme:list-payload path="payload"/>
</acme:list>
