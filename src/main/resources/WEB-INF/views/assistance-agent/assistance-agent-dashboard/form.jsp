<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="assistance-agent.dashboard.form.label.ratioAcceptedClaims" path="ratioAcceptedClaims" readonly="true"/>
	<acme:input-textbox code="assistance-agent.dashboard.form.label.ratioRejectedClaims" path="ratioRejectedClaims" readonly="true"/>
	<acme:input-textbox code="assistance-agent.dashboard.form.label.top3MonthsWithMostClaims" path="top3MonthsWithMostClaims" readonly="true"/>
	<acme:input-textbox code="assistance-agent.dashboard.form.label.averageTrackingLogsPerClaim" path="averageTrackingLogsPerClaim" readonly="true"/>
	<acme:input-textbox code="assistance-agent.dashboard.form.label.maxTrackingLogsPerClaim" path="maxTrackingLogsPerClaim" readonly="true"/>
	<acme:input-textbox code="assistance-agent.dashboard.form.label.minTrackingLogsPerClaim" path="minTrackingLogsPerClaim" readonly="true"/>
	<acme:input-textbox code="assistance-agent.dashboard.form.label.standardDeviationOfTrackingLogsPerClaim" path="standardDeviationOfTrackingLogsPerClaim" readonly="true"/>
	<acme:input-textbox code="assistance-agent.dashboard.form.label.averageClaimsPerAssistanceAgentForLastMonth" path="averageClaimsPerAssistanceAgentForLastMonth" readonly="true"/>
	<acme:input-textbox code="assistance-agent.dashboard.form.label.maxClaimsPerAssistanceAgentForLastMonth" path="maxClaimsPerAssistanceAgentForLastMonth" readonly="true"/>
	<acme:input-textbox code="assistance-agent.dashboard.form.label.minClaimsPerAssistanceAgentForLastMonth" path="minClaimsPerAssistanceAgentForLastMonth" readonly="true"/>
	<acme:input-textbox code="assistance-agent.dashboard.form.label.standardDeviationClaimsPerAssistanceAgentForLastMonth" path="standardDeviationClaimsPerAssistanceAgentForLastMonth" readonly="true"/>
</acme:form>