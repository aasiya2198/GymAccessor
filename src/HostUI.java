import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class HostUI extends JFrame
{
	
	private HostAgent agent;

	public HostUI(HostAgent agent)
	{
		this.agent = agent;
		DrawUI();
	}
	private void DrawUI()
	{
		JPanel jPanel = new JPanel();
		
//		JLabel label = new JLabel();        
//	    label.setText("Enter Start Time :");
//		JTextField fld = new JTextField(20);
//		JTextArea jArea = new JTextArea();
//		
//		JLabel label1 = new JLabel();        
//	    label1.setText("Enter End Time :");
//		JTextField fld1 = new JTextField(20);
//		JTextArea jArea1 = new JTextArea();
		
		JLabel label2 = new JLabel();        
	    label2.setText("Enter number of Slots :");
		JTextField fld2 = new JTextField(20);
		JTextArea jArea2 = new JTextArea();

		JLabel label3 = new JLabel();        
	    label3.setText("Maximum people per Slot :");
		JTextField fld3 = new JTextField(20);
		JTextArea jArea3 = new JTextArea();
		
//		jPanel.add(label);
//		jPanel.add(fld);
//		
//		jPanel.add(label1);
//		jPanel.add(fld1);
		
		jPanel.add(label2);
		jPanel.add(fld2);

		jPanel.add(label3);
		jPanel.add(fld3);
		

//		jPanel.add(jArea);
//		jPanel.add(jArea1);
		//jPanel.setLayout(new GridLayout(5,1));
		JButton btn = new JButton("Create Slots");
		jPanel.add(btn);
		
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				
				int numberOfSlot;
				int slotLimit;
				
				if(tryParseInt(fld2.getText().trim()))
				{
					if(tryParseInt(fld3.getText().trim()))
					{
						numberOfSlot = Integer.parseInt(fld2.getText().trim());
						slotLimit = Integer.parseInt(fld3.getText().trim());
						agent.InitializeSlots(numberOfSlot, slotLimit);
					}
					else
					{
						System.out.println("Not an int slotLimit");
					}
				}
				else
				{
					System.out.println("Not an int slotNumber");
				}
				
				
				setVisible(false);
				
			}
		});
		
		getContentPane().add(jPanel,BorderLayout.CENTER);

		
	}
	
	boolean tryParseInt(String value) {  
	     try {  
	         Integer.parseInt(value);  
	         return true;  
	      } catch (NumberFormatException e) {  
	         return false;  
	      }  
	}
}
