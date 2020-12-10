import jade.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Serializable;

class UserDetail implements Serializable
{
	public AID userId;
	public int slotNumber;
}

class Slot implements Serializable
{
	public int totalLimit; 
	public int currentBookedSlot;
}

//R3:RegisterAgent;R4:RegisterAgent;R5:RegisterAgent;R6:RegisterAgent;A2:AdmitterAgent;A3:AdmitterAgent;A4:AdmitterAgent;A5:AdmitterAgent;

public class HostAgent extends Agent
{
	
	//min 30 minutes
	int timeSlotDuration;
	private AID[] registerAgents;
	private boolean shouldUpdateAllRegister;
	ArrayList<Slot> slotInformation = new ArrayList();
	ArrayList<UserDetail> usersBooking = new ArrayList();
	
	
	
	
	
	private void setupUI() {
	    HostUI m_frame = new HostUI(this);

	    m_frame.setSize( 400, 200 );
	    m_frame.setLocation( 400, 400 );
	    m_frame.setVisible( true );
	    m_frame.validate();
	}
	 
	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
		RegisterForService();
		System.out.println("Host Agent Initialized");
		
		setupUI();
		//new HostUI();
		
//		InitializeTheSlot();

		addBehaviour(new TickerBehaviour(this,500) {
			
			@Override
			protected void onTick() {
				// TODO Auto-generated method stub
				shouldUpdateAllRegister = true;
			}
		});
		
		addBehaviour(new RegisterInformationBehaviour());
		addBehaviour(new HostRegisterCommunicationBehaviour());
	}
	
	
	
	
	public void InitializeSlots(int numberOfSlot, int slotLimit)
	{
		slotInformation = new ArrayList<>();
		Slot slot = new Slot();
		for(int i = 0; i < numberOfSlot ; i++)
		{
			slot = new Slot();
			slot.currentBookedSlot = 0;
			slot.totalLimit = slotLimit;
			slotInformation.add(slot);
		}
		
		System.out.println("Slot Initialized : "+numberOfSlot+" : slotLimit is : "+slotLimit);
	}
	
	private void RegisterForService()
	{
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("GymHost");
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
			System.err.println(getLocalName()+"Host Successfull");
			doDelete();
		}
	}
	
	
	private boolean ConfirmBookingSlot(int slotNumber)
	{
		 Slot slot = (Slot)slotInformation.get(slotNumber);
		 if(slot.totalLimit - slot.currentBookedSlot > 0)
			 return true;
		
		return false;
	}
	
	private boolean BookSlot(int slotNumber,AID userId)
	{
		Slot slot = (Slot)slotInformation.get(slotNumber);
		 if(slot.totalLimit - slot.currentBookedSlot > 0)
		 {
			 slot.currentBookedSlot+=1;
			 UserDetail uBooked = new UserDetail();
			 uBooked.slotNumber = slotNumber;
			 uBooked.userId = userId;
			 usersBooking.add(uBooked);
			// System.out.println("booking for slot : "+slotNumber);
			 return true;
		 }
		
		return false;
		
	}
	
	private boolean CheckUserAdmission(int slotNumber, AID userId)
	{
		//System.out.println("Admission for slot : "+slotNumber);
		for(int i = 0; i < usersBooking.size(); i++)
		{
			if(usersBooking.get(i).userId.equals(userId))
			{
				if(usersBooking.get(i).slotNumber == slotNumber)
					return true;
			}
		}
		
		return false;
	}
	
//	private void InitializeTheSlot()
//	{
//		Slot slot = new Slot();
//		for(int i = 0; i < 10 ; i++)
//		{
//			slot = new Slot();
//			slot.currentBookedSlot = i;
//			slotInformation.add(slot);
//		}
//		
//		System.out.println("Initialize the Slot");
//	}
	
	
	
	private class HostRegisterCommunicationBehaviour extends Behaviour
	{

		@Override
		public void action() {
			MessageTemplate fmt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage fmsg = myAgent.receive(fmt);
			ACLMessage reply = null;
			if(fmsg != null)
			{
				
				UserDetail ud = null;
				//messgae from register agent to confirm the booking
				if(fmsg.getConversationId().equals("Register"))
				{
					reply = new ACLMessage(ACLMessage.CFP);
					reply.addReceiver(fmsg.getSender());
					reply.setConversationId("Register");
					//System.out.println("Comm with Register Agent");
					try {
						ud = (UserDetail)fmsg.getContentObject();
						//System.out.println("UD is :"+ud.slotNumber+" && "+ud.userId);
						if(ConfirmBookingSlot(ud.slotNumber)) {
							//Booking successfull
							BookSlot(ud.slotNumber, ud.userId);
							//reply.setPerformative(ACLMessage.INFORM);
							reply.setContent("Hurray!! Booking is Successfull.");
							//System.out.println(" H : Booking successfull");
						}
						else
						{
							//booking is not a success
							//reply.setPerformative(ACLMessage.FAILURE);
							reply.setContent("Oops !! Booking Failed.");
							//System.out.println(" H : Booking failed");
							
						}
						
//						System.out.println("Sending the message : ID : "+reply.getConversationId()+"\n"+
//						"performative is : "+reply.getPerformative()+"\n Reciever : "+
//								reply.getAllReceiver());
						myAgent.send(reply);
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else if (fmsg.getConversationId().equals("Admitter"))
				{
					reply = new ACLMessage(ACLMessage.CFP);
					reply.addReceiver(fmsg.getSender());
					reply.setConversationId("Admitter");
					
					//System.out.println("Comm with Admitter Agent");
					try {
						ud = (UserDetail)fmsg.getContentObject();
						if(CheckUserAdmission(ud.slotNumber, ud.userId))
						{
							//TODO : user is legit and will be admitted
							//reply.setPerformative(ACLMessage.INFORM);
							reply.setContent("Hurray!! User may Enter.");
							
							//System.out.println("Valid Entry");
						}
						else
						{
							//reply.setPerformative(ACLMessage.FAILURE);
							reply.setContent("Oops!! User can't Enter");	
							//System.out.println("Unsuccessfull Entery");
						}

						myAgent.send(reply);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				
			}
			
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
		
	private class RegisterInformationBehaviour extends CyclicBehaviour
	{
		@Override
		public void action() 
		{
			if(shouldUpdateAllRegister)
			{

				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("RegisterAgent");
				template.addServices(sd);
				try
				{
					DFAgentDescription[] result = DFService.search(myAgent, template);
					registerAgents = new AID[result.length];
					
					for(int i = 0; i < result.length; i++)
					{
						registerAgents[i] = result[i].getName();
					}
					
					ACLMessage cfp = new ACLMessage(ACLMessage.INFORM);
					
					for(int i = 0; i < registerAgents.length; i++)
						cfp.addReceiver(registerAgents[i]);
					
				cfp.setConversationId("slot-information");
				try {
					cfp.setContentObject(slotInformation);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myAgent.send(cfp);
					
				}
				catch (FIPAException fe)
				{
					fe.printStackTrace();
				}
			}
						
			shouldUpdateAllRegister = false;
		}		
	}

	
	

}
