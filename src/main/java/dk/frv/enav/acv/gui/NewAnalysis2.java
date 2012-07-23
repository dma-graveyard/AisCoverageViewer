package dk.frv.enav.acv.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

import dk.dma.aiscoverage.calculator.CoverageCalculator;
import dk.dma.aiscoverage.calculator.DensityPlotCalculator;
import dk.dma.aiscoverage.project.ProjectHandler;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;




public class NewAnalysis2 extends JFrame {

	
	private String filePath;
	private long id;
	
	//helper tools
	private GUIHelper guiHelper = new GUIHelper();
	
	//panels
	private JPanel contentPane;
	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		JPanel inputPanel = new JPanel();
			JScrollPane scrollPane = new JScrollPane();
	
	
	
	//panel filling
	JTextArea ta = new JTextArea("Select File");
	
	//buttons
	//input panel
	ButtonGroup bg = new ButtonGroup();
	JRadioButton rdbtnInputFromStream = new JRadioButton("Input from Stream");
	JRadioButton rdbtnInputFromFile = new JRadioButton("Input from File");
	JButton btnSelectFile = new JButton("Select File");
	final JCheckBox chckbxSetAnalysisTimer = new JCheckBox("Set Analysis Timer");
	
	//coverage panel
	final JCheckBox chckbxEnableCoverage = new JCheckBox("Enabled");
	final JCheckBox chckbxCoverageAdvancedSettings = new JCheckBox("Advanced settings");
	JCheckBox chckbxIncludeTurningShips = new JCheckBox("Include turning ships");
	
	//density panel
	final JCheckBox chckbxEnableDensity = new JCheckBox("Enabled");
	
	//advanced panel
	final JCheckBox chckbxSetMapCenterpoint = new JCheckBox("Set Map Centerpoint");
	
	//frame buttons
	JButton btnCancel = new JButton("Cancel");
	JButton btnNew = new JButton("New");
	
	//project thingies
	ProjectHandler projectHandler = ProjectHandler.getInstance();
	
	
	
	
	private JTextField coverageCellsizeTxt;
	private JTextField messageBufferTxt;
	private JTextField rotationTxt;
	private JTextField densityCellSizeTxt;
	private JTextField analysisTime;
	private JTextField txtLat;
	private JTextField txtLong;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewAnalysis2 frame = new NewAnalysis2(new AnalysisPanel());
					frame.setVisible(true);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public NewAnalysis2(AnalysisPanel ap) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 377, 280);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		id = 1;
		
		/*
		 * tab panel
		 */
		tabbedPane.setLocation(5, 5);
		tabbedPane.setSize(new Dimension(352, 192));
		contentPane.add(tabbedPane);
		
		/*
		 * input tab
		 */
		tabbedPane.addTab("Input", null, inputPanel, null);
		inputPanel.setLayout(null);
		
		//text area
		scrollPane.setBounds(14, 37, 320, 79);
		inputPanel.add(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportView(ta);
		ta.setEditable(false);
		ta.setEnabled(false);
		
		//select file button
		btnSelectFile.setBounds(245, 127, 89, 23);
		inputPanel.add(btnSelectFile);
		btnSelectFile.addActionListener(new ActionListener() {	public void actionPerformed(ActionEvent e) 
		{	
			filePath = guiHelper.openAISFileDialog();
			String[] chunks = filePath.split("\\\\");
			final String filename = chunks[chunks.length-1];
			ta.setText(filename);	}	});
		
		
		//input from stream selection
		rdbtnInputFromStream.setBounds(197, 7, 135, 23);
		inputPanel.add(rdbtnInputFromStream);
		bg.add(rdbtnInputFromStream);
		rdbtnInputFromStream.addActionListener(new ActionListener() {	public void actionPerformed(ActionEvent e) 
		{	
			btnSelectFile.setEnabled(false);
			ta.setText("");
			ta.setEditable(true);	
			ta.setEnabled(true);
			contentPane.repaint();
		}	});
		
		
		//input from file selection
		rdbtnInputFromFile.setBounds(86, 7, 109, 23);
		inputPanel.add(rdbtnInputFromFile);
		bg.add(rdbtnInputFromFile);
		rdbtnInputFromFile.setSelected(true);
		rdbtnInputFromFile.addActionListener(new ActionListener() {	public void actionPerformed(ActionEvent e) 
		{		
			btnSelectFile.setEnabled(true);
			ta.setText("Select File");
			ta.setEditable(false);
			ta.setEnabled(false);
			contentPane.repaint();
		}	});
		
		//set analysis timer?
		chckbxSetAnalysisTimer.setBounds(14, 127, 113, 23);
		inputPanel.add(chckbxSetAnalysisTimer);
		chckbxSetAnalysisTimer.addActionListener(new ActionListener() {	public void actionPerformed(ActionEvent e) 
		{	
			if(chckbxSetAnalysisTimer.isSelected() == true)	
			{	analysisTime.setEditable(true);	
				contentPane.repaint();	}
			else	
			{	analysisTime.setEditable(false);	
				contentPane.repaint();	}	}	});
		
		analysisTime = new JTextField();
		analysisTime.setEditable(false);
		analysisTime.setText("00:00:00");
		analysisTime.setBounds(140, 127, 55, 20);
		inputPanel.add(analysisTime);
		analysisTime.setColumns(10);
		
		
		
		

		
		
		
		/*
		 * coverage tab
		 */
		JPanel coveragePanel = new JPanel();
		tabbedPane.addTab("Coverage Analysis", null, coveragePanel, null);
		coveragePanel.setLayout(null);
		
		JLabel lblCellsize = new JLabel("Cellsize: ");
		lblCellsize.setBounds(10, 37, 46, 14);
		coveragePanel.add(lblCellsize);
		
		coverageCellsizeTxt = new JTextField();
		coverageCellsizeTxt.setText("2500");
		coverageCellsizeTxt.setBounds(66, 34, 96, 20);
		coveragePanel.add(coverageCellsizeTxt);
		coverageCellsizeTxt.setEditable(true);
		coverageCellsizeTxt.setHorizontalAlignment(coverageCellsizeTxt.RIGHT);
		
		JLabel lblMeter = new JLabel("Meter");
		lblMeter.setBounds(172, 37, 46, 14);
		coveragePanel.add(lblMeter);
		
		
		chckbxCoverageAdvancedSettings.setBounds(10, 58, 142, 23);
		coveragePanel.add(chckbxCoverageAdvancedSettings);
		chckbxCoverageAdvancedSettings.addActionListener(new ActionListener() {	public void actionPerformed(ActionEvent e) 
		{	
			if(chckbxCoverageAdvancedSettings.isSelected() == true)	
			{	messageBufferTxt.setEditable(true);
				
				if(chckbxIncludeTurningShips.isSelected() == true)
				{	rotationTxt.setEditable(true);	}
				contentPane.repaint();	}
			else	
			{	messageBufferTxt.setEditable(false);
				rotationTxt.setEditable(false);	
				contentPane.repaint();	}	}	});
		
		JLabel lblMessageBuffer = new JLabel("Message buffer");
		lblMessageBuffer.setBounds(10, 88, 96, 14);
		coveragePanel.add(lblMessageBuffer);
		
		messageBufferTxt = new JTextField();
		messageBufferTxt.setEditable(false);
		messageBufferTxt.setText("20");
		messageBufferTxt.setBounds(96, 88, 40, 20);
		coveragePanel.add(messageBufferTxt);
		messageBufferTxt.setColumns(10);
		
		JLabel lblSekunder = new JLabel("Sekunder");
		lblSekunder.setBounds(146, 88, 46, 14);
		coveragePanel.add(lblSekunder);
		
		chckbxIncludeTurningShips.setBounds(10, 109, 142, 23);
		coveragePanel.add(chckbxIncludeTurningShips);
		chckbxIncludeTurningShips.addActionListener(new ActionListener() {	public void actionPerformed(ActionEvent e) 
		{	
			if(chckbxIncludeTurningShips.isSelected() == true)	
			{	
				rotationTxt.setEditable(true);
				contentPane.repaint();	}
			else	
			{	rotationTxt.setEditable(false);	
				contentPane.repaint();	}	}	});
		
		JLabel lblTurningIfRotation = new JLabel("Turning if rotation = ");
		lblTurningIfRotation.setBounds(10, 139, 127, 14);
		coveragePanel.add(lblTurningIfRotation);
		
		rotationTxt = new JTextField();
		rotationTxt.setEditable(false);
		rotationTxt.setText("20");
		rotationTxt.setBounds(146, 139, 46, 20);
		coveragePanel.add(rotationTxt);
		rotationTxt.setColumns(10);
		
		JLabel lblDegrees = new JLabel("Degrees per min");
		lblDegrees.setBounds(202, 139, 96, 14);
		coveragePanel.add(lblDegrees);
		
		chckbxEnableCoverage.setSelected(true);
		chckbxEnableCoverage.setBounds(10, 7, 97, 23);
		coveragePanel.add(chckbxEnableCoverage);
		chckbxEnableCoverage.addActionListener(new ActionListener() {	public void actionPerformed(ActionEvent e) 
		{	
			if(chckbxEnableCoverage.isSelected() == true)	
			{	
				coverageCellsizeTxt.setEditable(true);
				if(chckbxCoverageAdvancedSettings.isSelected() == true)
				{
					messageBufferTxt.setEditable(true);
					if(chckbxIncludeTurningShips.isSelected() == true)
					{
						rotationTxt.setEditable(true);
					}
				}
				contentPane.repaint();	}
			else	
			{	coverageCellsizeTxt.setEditable(false);
				messageBufferTxt.setEditable(false);
				rotationTxt.setEditable(false);
				contentPane.repaint();	}	}	});
		
		/*
		 * density tab
		 */
		JPanel densityPanel = new JPanel();
		tabbedPane.addTab("Density Plot", null, densityPanel, null);
		densityPanel.setLayout(null);
		
		JLabel lblCellsize_1 = new JLabel("Cellsize");
		lblCellsize_1.setBounds(10, 36, 46, 14);
		densityPanel.add(lblCellsize_1);
		
		densityCellSizeTxt = new JTextField();
		densityCellSizeTxt.setText("200");
		densityCellSizeTxt.setBounds(65, 33, 46, 20);
		densityPanel.add(densityCellSizeTxt);
		densityCellSizeTxt.setEditable(true);
		
		JLabel lblMeter_1 = new JLabel("Meter");
		lblMeter_1.setBounds(122, 36, 46, 14);
		densityPanel.add(lblMeter_1);
		
		chckbxEnableDensity.setSelected(true);
		chckbxEnableDensity.setBounds(10, 7, 97, 23);
		densityPanel.add(chckbxEnableDensity);
		chckbxEnableDensity.addActionListener(new ActionListener() {	public void actionPerformed(ActionEvent e) 
		{	
			if(chckbxEnableDensity.isSelected() == true)	
			{	
				densityCellSizeTxt.setEditable(true);
				contentPane.repaint();	}
			else	
			{	densityCellSizeTxt.setEditable(false);
				contentPane.repaint();	}	}	});
		
		/*
		 * advanced settings tab
		 */
		JPanel advancedPanel = new JPanel();
		tabbedPane.addTab("Advanced Settings", null, advancedPanel, null);
		advancedPanel.setLayout(null);
		
		chckbxSetMapCenterpoint.setBounds(6, 7, 125, 23);
		advancedPanel.add(chckbxSetMapCenterpoint);
		chckbxSetMapCenterpoint.addActionListener(new ActionListener() {	public void actionPerformed(ActionEvent e) 
		{	
			if(chckbxSetMapCenterpoint.isSelected() == true)	
			{	
				txtLat.setEditable(true);
				txtLong.setEditable(true);
				contentPane.repaint();	}
			else	
			{	txtLat.setEditable(false);
				txtLong.setEditable(false);
				contentPane.repaint();	}	}	});
		
		txtLat = new JTextField();
		txtLat.setEditable(false);
		txtLat.setBounds(41, 37, 46, 20);
		advancedPanel.add(txtLat);
		txtLat.setColumns(10);
		
		txtLong = new JTextField();
		txtLong.setEditable(false);
		txtLong.setBounds(41, 65, 46, 20);
		advancedPanel.add(txtLong);
		txtLong.setColumns(10);
		
		JLabel lblLat = new JLabel("Lat");
		lblLat.setBounds(16, 37, 46, 14);
		advancedPanel.add(lblLat);
		
		JLabel lblLong = new JLabel("Long");
		lblLong.setBounds(16, 68, 46, 14);
		advancedPanel.add(lblLong);
		
		/*
		 * frame buttons
		 */
		
		btnCancel.setBounds(262, 208, 89, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {	public void actionPerformed(ActionEvent e) 
		{	dispose();	}	});
		
		JButton btnNew = new JButton("New");
		btnNew.setBounds(163, 208, 89, 23);
		contentPane.add(btnNew);
		btnNew.addActionListener(new ActionListener() {	public void actionPerformed(ActionEvent e) 
		{	
			
			dk.dma.aiscoverage.project.AisCoverageProject project = projectHandler.createProject();
			
			if(rdbtnInputFromFile.isSelected() == true)
			{
				if(ta.getText() == "Select File")
				{
					//give error message
				}
				else
				{
				project.setFile(filePath);
				}
			}
			else if(rdbtnInputFromStream.isSelected() == true)
			{
				if(ta.getText().contains(""))
				{
					//give error message
				}
				else
				{
					String[] ip = ta.getText().split("\n");
					
					for (String ib : ip)
					{
						//assign ip's to the program
						project.addHostPort(ib, id);
						id++;
					}
				
			}
			}
		
			if(chckbxEnableCoverage.isSelected() == true)
			{
				//add coverage calculator
				CoverageCalculator coverageCalc = new CoverageCalculator(project, true);
				coverageCalc.setCellSize(Integer.parseInt(coverageCellsizeTxt.getText()));
				
				if (chckbxIncludeTurningShips.isSelected() == true)
				{
					coverageCalc.setIgnoreRotation(false);
					coverageCalc.setBufferInSeconds(Integer.parseInt(messageBufferTxt.getText()));
					coverageCalc.setDegreesPerMinute(Integer.parseInt(rotationTxt.getText()));
				}
				
				
				project.addCalculator(coverageCalc);
				
				System.out.println(coverageCalc.getBufferInSeconds());
				System.out.println(coverageCalc.getCellSize());
				System.out.println(coverageCalc.getDegreesPerMinute());
				System.out.println(coverageCalc.isIgnoreRotation());
				

			}
			
			if(chckbxEnableDensity.isSelected() == true)
			{
				//add densityplot calculator
				DensityPlotCalculator densityCalc = new DensityPlotCalculator(project, true);
				densityCalc.setCellSize(Integer.parseInt(densityCellSizeTxt.getText()));
				project.addCalculator(densityCalc);
			}			
			
			if(chckbxSetAnalysisTimer.isSelected() == true)
			{
				int hour = Integer.parseInt(analysisTime.getText().substring(0, 2));
				int min = Integer.parseInt(analysisTime.getText().substring(3, 5));
				int sec = Integer.parseInt(analysisTime.getText().substring(6,8));
				int time = ((((hour * 60) + min) * 60)+sec);
				System.out.println(analysisTime.getText());
				System.out.println(hour);
				System.out.println(min);
				System.out.println(sec);
				System.out.println(time);
				project.setTimeout(time);
				
			}				
					
			
			dispose();	}	});
	}
}
