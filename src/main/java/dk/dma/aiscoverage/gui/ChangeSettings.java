package dk.dma.aiscoverage.gui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import dk.dma.aiscoverage.project.ProjectHandler;


public class ChangeSettings extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GUIHelper guiHelper = new GUIHelper();
	private JPanel contentPane;
	private JTextField txtBlablablashape;
	private JTextField mapUpdateTextField;
	private JTextField messageBufferTextFieldC1;
	private JTextField degreesTextFieldC1;
	private JTextField messageBufferTextFieldC2;
	private JTextField degreesTextFieldC2;
	ProjectHandler projectHandler = ProjectHandler.getInstance();


	/**
	 * Create the frame.
	 */
	public ChangeSettings() {
		setTitle("Standart settings");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 338, 263);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		

		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { dispose();	}	});
		btnCancel.setBounds(120, 194, 89, 23);
		contentPane.add(btnCancel);
		
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{ 
				//set calculator is turning
				//set calculator degrees
				//set other calculator stuff
				
				
				
				
				
				dispose();	}	});
		btnOk.setBounds(219, 194, 89, 23);
		contentPane.add(btnOk);	
		
		
		/*
		 * Creating tabbed panel element
		 */
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 298, 172);
		contentPane.add(tabbedPane);
		
		/*
		 * first tap (map settings)
		 */
		JPanel panel = new JPanel();
		tabbedPane.addTab("Map settings", null, panel, null);
		panel.setLayout(null);
		
		JLabel lblSelectMap = new JLabel("Map image");
		lblSelectMap.setBounds(10, 11, 68, 14);
		panel.add(lblSelectMap);
		
		txtBlablablashape = new JTextField();
		txtBlablablashape.setText("blabla/bla.shape");
		txtBlablablashape.setBounds(10, 36, 182, 20);
		panel.add(txtBlablablashape);
		txtBlablablashape.setColumns(10);
		
		JButton btnSelectMapFile = new JButton("Select map file");
		btnSelectMapFile.setBounds(88, 7, 101, 23);
		panel.add(btnSelectMapFile);
		btnSelectMapFile.addActionListener(new ActionListener()  {public void actionPerformed(ActionEvent e)	{	guiHelper.openShapeFileDialog();	}	});
		
		JLabel lblLiveCoverageBuilder = new JLabel("Live coverage update speed - in seconds");
		lblLiveCoverageBuilder.setBounds(10, 67, 236, 14);
		panel.add(lblLiveCoverageBuilder);
		
		mapUpdateTextField = new JTextField();
		mapUpdateTextField.setText("5");
		mapUpdateTextField.setBounds(10, 92, 182, 20);
		panel.add(mapUpdateTextField);
		

		/*
		 * tab 2 calculator 1 settings
		 */
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Calculator 1", null, panel_1, null);
		panel_1.setLayout(null);
		
		JLabel lblBufferInSeconds = new JLabel("Message buffer - in seconds");
		lblBufferInSeconds.setBounds(10, 11, 340, 14);
		panel_1.add(lblBufferInSeconds);
		
		messageBufferTextFieldC1 = new JTextField();
		messageBufferTextFieldC1.setText("20");
		messageBufferTextFieldC1.setBounds(10, 24, 137, 20);
		panel_1.add(messageBufferTextFieldC1);
		
		JLabel lblDegreedsPerMinute = new JLabel("Turning = x degrees per minute");
		lblDegreedsPerMinute.setBounds(10, 55, 160, 14);
		panel_1.add(lblDegreedsPerMinute);
		
		degreesTextFieldC1 = new JTextField();
		degreesTextFieldC1.setText("20");
		degreesTextFieldC1.setBounds(10, 69, 137, 20);
		panel_1.add(degreesTextFieldC1);
		
		JRadioButton rdbtnIncludeTurningShips = new JRadioButton("Include turning ships");
		rdbtnIncludeTurningShips.setBounds(10, 96, 262, 23);
		panel_1.add(rdbtnIncludeTurningShips);
		
		JRadioButton rdbtnIncludeBetweenCell = new JRadioButton("Include between cell calculation");
		rdbtnIncludeBetweenCell.setBounds(10, 122, 248, 23);
		panel_1.add(rdbtnIncludeBetweenCell);
		
		/*
		 * tab 3 calculator 2 settings
		 */
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Calculator 2", null, panel_2, null);
		panel_2.setLayout(null);
		
		JLabel lblBufferInSeconds1 = new JLabel("Message buffer - in seconds");
		lblBufferInSeconds1.setBounds(10, 11, 340, 14);
		panel_2.add(lblBufferInSeconds1);
		
		messageBufferTextFieldC2 = new JTextField();
		messageBufferTextFieldC2.setText("20");
		messageBufferTextFieldC2.setBounds(10, 24, 137, 20);
		panel_2.add(messageBufferTextFieldC2);
		
		JLabel lblDegreedsPerMinute1 = new JLabel("Turning = x degrees per minute");
		lblDegreedsPerMinute1.setBounds(10, 55, 160, 14);
		panel_2.add(lblDegreedsPerMinute1);
		
		degreesTextFieldC2 = new JTextField();
		degreesTextFieldC2.setText("20");
		degreesTextFieldC2.setBounds(10, 69, 137, 20);
		panel_2.add(degreesTextFieldC2);
		
		JRadioButton rdbtnIncludeTurningShips1 = new JRadioButton("Include turning ships");
		rdbtnIncludeTurningShips1.setBounds(10, 96, 262, 23);
		panel_2.add(rdbtnIncludeTurningShips1);
		
		JRadioButton rdbtnIncludeBetweenCell1 = new JRadioButton("Include between cell calculation");
		rdbtnIncludeBetweenCell1.setBounds(10, 122, 248, 23);
		panel_2.add(rdbtnIncludeBetweenCell1);
		
		/*
		 * tab 4 other settings
		 */
		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("odder settings", null, panel_4, null);
		panel_4.setLayout(null);
		
		JLabel lblOdderStff = new JLabel("Odder st");
		lblOdderStff.setBounds(10, 11, 109, 14);
		panel_4.add(lblOdderStff);
		

	}
}
