<%--
- list.jsp
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


<acme:list>
	<acme:list-column code="technician.maintenance-record.list.label.model" path="aircraft.model" width="20%"/>
	<acme:list-column code="technician.maintenance-record.list.label.moment" path="moment" width="20%"/>
	<acme:list-column code="technician.maintenance-record.list.label.status" path="status" width="20%"/>
	<acme:list-column code="technician.maintenance-record.list.label.inspectionDueDate" path="inspectionDueDate" width="20%"/>
	<acme:list-column code="technician.maintenance-record.list.label.estimatedCost" path="estimatedCost" width="20%"/>
</acme:list>

<acme:button code="technician.maintenance-record.list.button.create" action ="/technician/maintenance-record/create"/>