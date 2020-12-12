import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class UserAgent extends Agent 
{
	
	ArrayList<Slot> slotInformation = new ArrayList();
	UserUI m_frame = null;
	
	Slot userSlot = new Slot();
	private AID registerAgent;
	private AID admitterAgent;
	
	@Override
	protected void setup() {
		super.setup();
		
		addBehaviour(new GetRegisterAgent());
		addBehaviour(new GetAdmitterAgent());
	}
	
	private void setupUI() {
	    m_frame = new UserUI(this,slotInformation);

	    m_frame.setSize( 400, 200 );
	    m_frame.setLocation( 400, 400 );
	    m_frame.setVisible( true );
	    m_frame.validate();
	}
	
	private int bookingSlotNumber, enteringSlotNumber;
	public void BookASlot(int slotNumber)
	{

		bookingSlotNumber = slotNumber;
		//System.out.println("Book A Slot : "+slotNumber);
		addBehaviour(new UserRegisterBehaviour());
	}
	
	
	public void EnterTheGym(int slotNumber)
	{
		//System.out.println("Enter the Gym : "+slotNumber);
		enteringSlotNumber = slotNumber;
		addBehaviour(new UserAdmitterBehaviour());
	}
	
	
	
	private void OnCallbackSlotDetail(Slot _userSlot)
	{
		this.userSlot = _userSlot;
	}
	private class GetRegisterAgent extends Behaviour
	{
		boolean isDone = false;
		
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			//addBehaviour(new UserBehaviour());
			return isDone;
		}
		
		@Override
		public void action() {

			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("RegisterAgent");
			template.addServices(sd);
			
			DFAgentDescription[] result = null;
			try {
				result = DFService.search(myAgent, template);
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(result.length >= 1) {
				Random rand = new Random();
				int randIndex = rand.nextInt(result.length);
				registerAgent = result[randIndex].getName();
				//System.out.println("find the register agent :"+registerAgent);
				addBehaviour(new GetSlotBehaviour());
				isDone = true;
				
				
		}
			
		}
	}
	
	private class GetAdmitterAgent extends Behaviour
	{
		boolean isDone = false;
		
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			//addBehaviour(new UserRegisterBehaviour());
			return isDone;
		}
		
		@Override
		public void action() {

			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("AdmitterAgent");
			template.addServices(sd);
			
			DFAgentDescription[] result = null;
			try {
				result = DFService.search(myAgent, template);
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(result.length >= 1) {
				//TODO: we will do the put the random agent to assigned here
				
				Random rand = new Random();
				int randIndex = rand.nextInt(result.length);
				admitterAgent = result[randIndex].getName();
				//isDone = true;
		}
			
		}
	}
	
	private class UserAdmitterBehaviour extends Behaviour
	{
		private int step = 0;
		boolean isDone = false;
		@Override
		public void action() {
			MessageTemplate mt = null;
			switch(step)
			{
			case 0:
				//After you get the slot you will send the detail to the RegisterAgent
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				cfp.addReceiver(admitterAgent);
				UserDetail user = new UserDetail();
				user.userId = getAID();
				user.slotNumber = enteringSlotNumber;
				try {
					cfp.setContentObject(user);
				} catch (IOException e) {
					e.printStackTrace();
				}
				cfp.setConversationId("User-Admitter");
				myAgent.send(cfp);
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("User-Admitter"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			case 1:
				ACLMessage reply = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.CFP));
				if(reply != null)
				{
					System.out.println("Result : "+reply.getContent());
					step = -1;
					isDone = true;
					if(reply.getContent().contains("Hurray"))
					{
						myAgent.doDelete();
						m_frame.setVisible(false);
					}
				}
				break;
			}
			
		}

		@Override
		public boolean done() {
			return isDone;
		}
		
	}
	
	private class UserRegisterBehaviour extends Behaviour
	{
		private int step = 0;
		MessageTemplate mt = null;
		boolean isDone = false;
		@Override
		public void action() {
			
			switch(step)
			{
			case 0:
				//System.out.println("URB - 0");
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				if(registerAgent == null)
					System.out.println("register AGent is null");
				cfp.addReceiver(registerAgent);
				UserDetail user = new UserDetail();
				user.userId = getAID();
				user.slotNumber = bookingSlotNumber;
				try {
					cfp.setContentObject(user);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cfp.setConversationId("User-Register");
				myAgent.send(cfp);
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("User-Register"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			case 1:
				//System.out.println("URB - 1");
				ACLMessage reply = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.CFP));
				if(reply != null)
				{
					System.out.println("Result : "+reply.getContent().toString());
					step = -1;
					isDone = true;
					
					if(reply.getContent().contains("Success"))
					{
						m_frame.DisableBookingButton();
					}
					
				}
				break;
			}
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			//System.out.println("successfully behaviour is finish");
			//removeBehaviour(this);
			return isDone;
		}
		
	}
	
	
	private class GetSlotBehaviour extends Behaviour
	{
		boolean isDone = false;
		int step = 0;
		MessageTemplate messageTemplate = null;
		@Override
		public void action() 
		{
			
			switch(step)
			{
			case 0:
				//System.out.println("requesting slot info");
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(registerAgent);
				msg.setConversationId("slot-info");
				myAgent.send(msg);
				messageTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("slot-info"),
						MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
				
				step = 1;
				break;
			case 1:
				MessageTemplate mt1 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				ACLMessage rply = myAgent.receive(mt1);
				
				if(rply != null && rply.getConversationId().equals("slot-info"))
				{
					
					//System.out.println("got the reply of slot-info");
					try {
						slotInformation = (ArrayList)rply.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					
					//System.out.println("Size is : "+slotInformation.size());
					step = -1;
					setupUI();
					isDone = true;
				}
				
				
				break;
			}			
			
		}		

		
		@Override
		public boolean done() {
			return isDone;
		}
	}

}
