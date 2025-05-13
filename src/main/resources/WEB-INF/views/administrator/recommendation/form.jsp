<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-select code="administrator.recommendation.form.label.airport" path="airport" choices="${cityChoices}"/>
    
    <acme:input-textbox code="administrator.recommendation.form.label.title" path="title"/>
    <acme:input-textarea code="administrator.recommendation.form.label.description" path="description"/>
    
    <acme:input-select code="administrator.recommendation.form.label.category" path="category" choices="${categoryChoices}"/>
    
    <acme:input-moment code="administrator.recommendation.form.label.startDate" path="startDate"/>
    <acme:input-moment code="administrator.recommendation.form.label.endDate" path="endDate"/>
    
    <acme:input-money code="administrator.recommendation.form.label.estimatedCost" path="estimatedCost"/>
    <acme:input-url code="administrator.recommendation.form.label.link" path="link"/>
    
    <acme:submit code="administrator.recommendation.form.button.populate" action="/administrator/recommendation/populate"/>
</acme:form>