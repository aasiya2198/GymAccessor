import java.io.IOException;
import java.util.ArrayList;

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

public class RegisterAgent extends Agent 
{
	
	ArrayList<Slot> slotInformation = new ArrayList();
	
	private AID hostAgent;
	private AID userId;
	
	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
		
		RegisterUI rGUI = new RegisterUI();
		
		RegisterForService();
		addBehaviour(new RegisterationBehaviour());
		addBehaviour(new GetHostBehaviour());
		addBehaviour(new RespondToUserRegisterationBehaviour());
	}
	
	private void RegisterForService()
	{
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("RegisterAgent");
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
			
			//isDone = true;
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			removeBehaviour(this);
			return isDone;
		}

	}
	
	private class RespondToUserRegisterationBehaviour extends CyclicBehaviour
	{

		private int step = 0;
		MessageTemplate messageTemplate = null;
		ACLMessage userReply = null;
		
		@Override
		public void action() {
			
			
			switch(step)
			{
			case 0:
				//System.out.println("Register @ case 0");
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
				ACLMessage msg = myAgent.receive(mt);
				ACLMessage aclHost = null;
				if(msg != null && msg.getConversationId().equals("User-Register"))
				{
					//System.out.println("The ID in register is  :"+msg.getConversationId());
					
					userReply = new ACLMessage(ACLMessage.CFP);
					userReply.addReceiver(msg.getSender());
					userReply.setConversationId("User-Register");
					
					aclHost = new ACLMessage(ACLMessage.CFP);
					aclHost.setConversationId("Register");
					aclHost.addReceiver(hostAgent);
					try {
						aclHost.setContentObject(msg.getContentObject());
					} catch (IOException | UnreadableException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					myAgent.send(aclHost);
					messageTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("Register"),
							MessageTemplate.MatchInReplyTo(aclHost.getReplyWith()));
					step = 1;
				}
				
				
				break;
			case 1:
				ACLMessage reply = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.CFP));
				if(reply != null && reply.getConversationId().equals("Register"))
				{
					//System.out.println("The Booking result is : "+reply.getConversationId()+" : "+reply.getContent().toString());
					
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
	

	
	private class RegisterationBehaviour extends CyclicBehaviour
	{

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null && msg.getConversationId().equals("slot-information"))
			{
				try {
					slotInformation = (ArrayList)msg.getContentObject();
					//System.out.print("Size is : "+slotInformation.size());
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
							
			}
			else if (msg != null && msg.getConversationId().equals("slot-info"))
			{
				System.out.println("Got the Request for the slot");
				ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
				//System.out.print(" @send : Size is : "+slotInformation.size());
				reply.setConversationId("slot-info");
				try {
					reply.setContentObject(slotInformation);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				reply.addReceiver(msg.getSender());
				myAgent.send(reply);
			}
			
		}		
	}
}
