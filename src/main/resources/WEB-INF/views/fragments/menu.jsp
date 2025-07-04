<%--
- menu.jsp
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
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:menu-bar>
	<acme:menu-left>
		<acme:menu-option code="master.menu.anonymous" access="isAnonymous()">
			<acme:menu-suboption code="master.menu.anonymous.list-flights" action="/any/flight/list"/>
			<acme:menu-suboption code="master.menu.anonymous.list-flight-assignments" action="/any/flight-assignment/list"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link" action="http://www.example.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-Candela" action="https://www.frivclassic.com/fc-es.html"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-Cristina" action="https://oldgameshelf.com/es/games/nes/elevator-action-314"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-Emmanuel" action="https://www.crunchyroll.com/es-es/series/GRE5K3NQ6/91-days"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-Marta" action="https://www.sevilla.org/fiestas-de-la-ciudad/feria-de-sevilla"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-Angel" action="https://www.duolingo.com/learn"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.authenticated" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.authenticated.list-flights" action="/any/flight/list"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.administrator" access="hasRealm('Administrator')">
			<acme:menu-suboption code="master.menu.administrator.list-user-accounts" action="/administrator/user-account/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-airports" action="/administrator/airport/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-airlines" action="/administrator/airline/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-aircraft" action="/administrator/aircraft/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-bookings" action="/administrator/booking/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-claims" action="/administrator/claim/list"/>
			<acme:menu-separator/>
  			<acme:menu-suboption code="master.menu.administrator.show-dashboard" action="/administrator/administrator-dashboard/show"/>			
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.populate-recommendations" action="/administrator/recommendation/populate"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-initial" action="/administrator/system/populate-initial"/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-sample" action="/administrator/system/populate-sample"/>			
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.shut-system-down" action="/administrator/system/shut-down"/>
			<acme:menu-separator/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.flight-crew-member" access="hasRealm('FlightCrewMember')">
			<acme:menu-suboption code="master.menu.flight-crew-member.flight-assignment.plannedList" action="/flight-crew-member/flight-assignment/planned-list"/>
			<acme:menu-suboption code="master.menu.flight-crew-member.flight-assignment.completedList" action="/flight-crew-member/flight-assignment/completed-list"/>
			<acme:menu-suboption code="master.menu.flight-crew-member.show-dashboard" action="/flight-crew-member/flight-crew-member-dashboard/show"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.provider" access="hasRealm('Provider')">
			<acme:menu-suboption code="master.menu.provider.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.consumer" access="hasRealm('Consumer')">
			<acme:menu-suboption code="master.menu.consumer.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.customer" access="hasRealm('Customer')">
 			<acme:menu-suboption code="master.menu.customer.booking" action="/customer/booking/list"/>
  			<acme:menu-suboption code="master.menu.customer.passenger" action="/customer/passenger/list"/>
  			<acme:menu-separator/>
  			<acme:menu-suboption code="master.menu.customer.show-dashboard" action="/customer/customer-dashboard/show"/>			
    </acme:menu-option>
    
		<acme:menu-option code="master.menu.manager" access="hasRealm('Manager')">
 			<acme:menu-suboption code="master.menu.manager.list-flights" action="/manager/flight/list"/>
 			<acme:menu-suboption code="master.menu.manager.show-dashboard" action="/manager/manager-dashboard/show"/>
 		</acme:menu-option>
 		
		<acme:menu-option code="master.menu.technician" access="hasRealm('Technician')">
			<acme:menu-suboption code="master.menu.technician.listMR" action="/technician/maintenance-record/list"/>
			<acme:menu-suboption code="master.menu.technician.listTask" action="/technician/task/list"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.assistance-agent" access="hasRealm('AssistanceAgent')">
 			<acme:menu-suboption code="master.menu.assistance-agent.listCompleted" action="/assistance-agent/claim/listCompleted"/>
 			<acme:menu-suboption code="master.menu.assistance-agent.listUndergoing" action="/assistance-agent/claim/listUndergoing"/>
 			<acme:menu-suboption code="master.menu.assistance-agent.show-dashboard" action="/assistance-agent/assistance-agent-dashboard/show"/>
 		</acme:menu-option>

	</acme:menu-left>

	<acme:menu-right>		
		<acme:menu-option code="master.menu.user-account" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.user-account.general-profile" action="/authenticated/user-account/update"/>
			<acme:menu-suboption code="master.menu.user-account.become-provider" action="/authenticated/provider/create" access="!hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.provider-profile" action="/authenticated/provider/update" access="hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.become-consumer" action="/authenticated/consumer/create" access="!hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.consumer-profile" action="/authenticated/consumer/update" access="hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.become-customer" action="/authenticated/customer/create" access="!hasRealm('Customer')"/>
			<acme:menu-suboption code="master.menu.user-account.customer-profile" action="/authenticated/customer/update" access="hasRealm('Customer')"/>
			<acme:menu-suboption code="master.menu.user-account.become-manager" action="/authenticated/manager/create" access="!hasRealm('Manager')"/>
			<acme:menu-suboption code="master.menu.user-account.manager-profile" action="/authenticated/manager/update" access="hasRealm('Manager')"/>
			<acme:menu-suboption code="master.menu.user-account.become-flight-crew-member" action="/authenticated/flight-crew-member/create" access="!hasRealm('FlightCrewMember')"/>
			<acme:menu-suboption code="master.menu.user-account.flight-crew-member-profile" action="/authenticated/flight-crew-member/update" access="hasRealm('FlightCrewMember')"/>
			<acme:menu-suboption code="master.menu.user-account.become-assistance-agent" action="/authenticated/assistance-agent/create" access="!hasRealm('AssistanceAgent')"/>
			<acme:menu-suboption code="master.menu.user-account.assistance-agent-profile" action="/authenticated/assistance-agent/update" access="hasRealm('AssistanceAgent')"/>
		</acme:menu-option>
	</acme:menu-right>
</acme:menu-bar>

