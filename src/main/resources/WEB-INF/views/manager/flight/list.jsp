
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.flight.list.label.tag" path="tag" width="10%"/>
    <%-- <acme:list-column code="manager.flight.list.label.indication" path="indication" width="10%"/>  --%>
    <acme:list-column code="manager.flight.list.label.originCity" path="originCity" width="15%"/>
    <acme:list-column code="manager.flight.list.label.destinationCity" path="destinationCity" width="15%"/>
    <acme:list-column code="manager.flight.list.label.scheduledDeparture" path="scheduledDeparture" width="15%"/>
    <acme:list-column code="manager.flight.list.label.scheduledArrival" path="scheduledArrival" width="15%"/>
    <%-- <acme:list-column code="manager.flight.list.label.cost" path="cost" width="25%"/> --%>
    <%-- <acme:list-column code="manager.flight.list.label.layovers" path="layovers" width="5%"/>--%>
</acme:list>

<jstl:if test="${_command == 'list'}">
	<acme:button code="manager.flight.list.button.create" action="/manager/flight/create"/>
</jstl:if>		
	

