<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 

	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|delete')}">
			<acme:input-select code="technician.involves.form.label.task" path="task" choices="${tasks}" readonly="true" />	
			<acme:submit code="technician.involves.form.button.delete" action="/technician/involves/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-select code="technician.involves.form.label.task" path="task" choices="${tasks}" />	
			<acme:submit code="technician.involves.form.button.create" action="/technician/involves/create?id=${$request.data.id}"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>