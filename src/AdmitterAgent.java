import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class AdmitterAgent extends Agent
{
	private AID hostAgent;
	
	protected void setup()
	{
		RegisterForService();
		addBehaviour(new GetHostBehaviour());
		addBehaviour(new RespondToUserRegisterationBehaviour());
	}
	
	private void RegisterForService()
	{
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("AdmitterAgent");
		sd.setName(getName());
		sd.setOwnership("ExampleofJADE");
		dfd.addServices(sd);
		dfd.setName(getAID());
		dfd.addOntologies("Test_Example");
		try
		{
			DFService.register(this, dfd);
		}
		catch(FIPAException e)
		{
			System.err.println(getLocalName()+" registration unsuccessfull");
			doDelete();
		}
	}
}
