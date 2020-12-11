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




}
