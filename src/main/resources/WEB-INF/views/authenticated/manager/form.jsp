<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<jstl:if test="${acme:anyOf(_command, 'update')}">
		<acme:input-textbox code="authenticated.manager.form.label.identifierNumber" path="identifierNumber" readonly="true"/>
	</jstl:if>
	<acme:input-textbox code="authenticated.manager.form.label.yearsExperience" path="yearsExperience"/>
	<acme:input-moment code="authenticated.manager.form.label.dateBirth" path="dateBirth"/>
	<acme:input-url code="authenticated.manager.form.label.picture" path="picture"/>
	
	<jstl:if test="${_command == 'create'}">
		<acme:submit code="authenticated.manager.form.button.create" action="/authenticated/manager/create"/>
	</jstl:if>
	<jstl:if test="${_command == 'update'}">
		<acme:submit code="authenticated.manager.form.button.update" action="/authenticated/manager/update"/>
	</jstl:if>
</acme:form>
