import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UserUI extends JFrame {
	
	protected UserAgent myAgent;
	JButton btnBookSlot;
	public UserUI(UserAgent agent, ArrayList<Slot> slotInformation)
	{
		this.myAgent = agent;
		DrawUI(slotInformation);
	}
	private void DrawUI(ArrayList<Slot> slotInformation)
	{
		
		//JPanel jPanel = new JPanel();
		
		JPanel jPanelRoot = new JPanel();
		JPanel jPanelBooking = new JPanel();
		JPanel jPanelEntering = new JPanel();
		
		String[] bookingSlots = new String[slotInformation.size()];
		for(int i = 0; i < slotInformation.size(); i++) {
			bookingSlots[i] = "Slot : "+(i+1);
		}
		JComboBox jComboBooking = new JComboBox (bookingSlots);
		//JLabel lblText=new JLabel();

		JComboBox jComboEnter = new JComboBox (bookingSlots);
		//JLabel lblText1=new JLabel();

		GridBagConstraints c = new GridBagConstraints();	
		c.gridx=0;
		c.gridy=1;
		jPanelBooking.add(jComboBooking,c);
		c.gridx=0;
		c.gridy=2;
		jPanelEntering.add(jComboEnter,c);
		
		btnBookSlot = new JButton("Book Time Slot");
		jPanelBooking.add(btnBookSlot,c);
		btnBookSlot.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				myAgent.BookASlot(jComboBooking.getSelectedIndex());
			}
		});
		
		
		
		JButton btnEnterGym = new JButton("Enter The GYM");
		jPanelEntering.add(btnEnterGym,c);
		
		btnEnterGym.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//System.out.println(cmbMessageList1.getSelectedItem());
				myAgent.EnterTheGym(jComboEnter.getSelectedIndex());
				//setVisible(false);
			}
		});
		
		jPanelRoot.add(jPanelBooking);
		jPanelRoot.add(jPanelEntering);
		
		getContentPane().add(jPanelRoot,BorderLayout.CENTER);

	}
	
	public void DisableBookingButton()
	{
		btnBookSlot.setEnabled(false);
	}


}
