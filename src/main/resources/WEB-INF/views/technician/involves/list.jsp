<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>


<acme:list>
	<acme:list-column code="technician.involves.list.label.taskId" path="task.id" width="33%"/>
	<acme:list-column code="technician.involves.list.label.taskPriority" path="task.priority" width="33%"/>
	<acme:list-column code="technician.involves.list.label.taskType" path="task.type" width="33%"/>
</acme:list>
<acme:button code="technician.involves.list.button.create" action ="/technician/involves/create?id=${$request.data.id}"/>
