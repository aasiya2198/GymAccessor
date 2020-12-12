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
	
	private class GetHostBehaviour extends Behaviour
	{
		boolean isDone = false;
		@Override
		public void action() {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("GymHost");
			template.addServices(sd);
			
			DFAgentDescription[] result = null;
			try {
				result = DFService.search(myAgent, template);
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(result.length >= 1)
				hostAgent = result[0].getName();
			
			isDone = true;
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return isDone;
		}

	}
	
	
	private class RespondToUserRegisterationBehaviour extends CyclicBehaviour
	{

		private int step = 0;
		ACLMessage userReply = null;
		MessageTemplate messageTemplate = null;
		@Override
		public void action() {
			switch(step)
			{
			case 0:
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
				ACLMessage msg = myAgent.receive(mt);
				ACLMessage aclHost = null;
				if(msg != null && msg.getConversationId().equals("User-Admitter"))
				{
					//System.out.println("Comm with Admitter-User");
					userReply = msg.createReply();
					aclHost = new ACLMessage(ACLMessage.CFP);
					aclHost.setConversationId("Admitter");
					aclHost.addReceiver(hostAgent);
					try {
						aclHost.setContentObject(msg.getContentObject());
					} catch (IOException | UnreadableException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					myAgent.send(aclHost);
					messageTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("Admitter"),
							MessageTemplate.MatchInReplyTo(aclHost.getReplyWith()));
					step = 1;
					UserDetail user = null;
					try {
						user = (UserDetail)msg.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				
				
				break;
			case 1:
				ACLMessage reply = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.CFP));
				if(reply != null && reply.getConversationId().equals("Admitter"))
				{
					
					//System.out.println("The entering result is : "+reply.getConversationId()+" : "+reply.getContent().toString());
					userReply.setContent(reply.getContent());
					myAgent.send(userReply);
					step = 0;
					userReply = null;
					messageTemplate = null;
				}
				break;
			}
			
		}

//		@Override
//		public boolean done() {
//			// TODO Auto-generated method stub
//			return false;
//		}
		
	}
}

