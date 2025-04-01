<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>


<acme:list>
	<acme:list-column code="technician.involves.list.label.taskId" path="tasks.id" width="33%"/>
	<acme:list-column code="technician.involves.list.label.taskPriority" path="tasks.priority" width="33%"/>
	<acme:list-column code="technician.involves.list.label.taskType" path="tasks.type" width="33%"/>
</acme:list>


