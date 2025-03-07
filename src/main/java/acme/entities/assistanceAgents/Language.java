
package acme.entities.assistanceAgents;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Language extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				spokenLanguage;

	@ManyToOne(optional = false)
	@JoinColumn(name = "assistanceAgent_id", nullable = false)
	private AssistanceAgent		assistanceAgent;

}
