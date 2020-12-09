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
	
	

}
