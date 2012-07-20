package dk.frv.enav.acv.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ScrollPaneConstants;

import java.awt.Panel;
import java.awt.Button;
import javax.swing.JComboBox;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import javax.swing.Action;
import java.awt.event.ActionListener;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.EtchedBorder;

import dk.dma.aiscoverage.calculator.AbstractCoverageCalculator;
import dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced3;
import dk.dma.aiscoverage.project.ProjectHandler;
import javax.swing.JCheckBox;

import com.bbn.openmap.gui.OMComponentPanel;

import java.awt.FlowLayout;
import javax.swing.JScrollPane;

public class NewAnalysis1 extends JFrame {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GUIHelper guiHelper = new GUIHelper();
	//private JPanel contentPane;
	private JTextField txtTypeInInput;
	private JTextField textField;
	JComboBox comboBox = null;
	ButtonGroup bg = new ButtonGroup();
	ProjectHandler projectHandler = ProjectHandler.getInstance();
	JRadioButton rdbtnInputfile = new JRadioButton("Input-File");
	JRadioButton rdbtnInputstream = new JRadioButton("Input-Stream");
	JRadioButton rdbtnInputfromip = new JRadioButton("Input-From-IP");
	JCheckBox chckbxIncludeTurningShips = new JCheckBox("Include turning ships");
	JCheckBox chckbxAdvancedSettings = new JCheckBox("Advanced settings");
	JButton btnDone = new JButton("New");
	JButton btnCancel = new JButton("Cancel");
	private JTextField txtMessageBuffer;
	private JTextField textField_1;
	JFrame frame = this;
	private JTextField textField_2;
	private AnalysisPanel analysisPanel;
	private String fileName;
	private JTextField mapTextField;
	private JTextField textField_3;
	private JTextField textField_4;
	private Dimension onetwenty;
	private Dimension eighty;
	private Dimension forty;
	long id;
	
	JTextArea ta = new JTextArea("this is a");

	/**
	 * Create the frame.
	 */
	public NewAnalysis1(AnalysisPanel ap) {
		analysisPanel = ap; 
		onetwenty = new Dimension(425, 120);
		eighty = new Dimension(425, 80);
		forty = new Dimension(425, 40);
		id = 1;
		
		setResizable(false);
		setTitle("New Analysis");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//setBounds(100, 100, 460, 381);
		//contentPane = new JPanel();
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//setContentPane(contentPane);
		//contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		//frame.setMaximumSize(new Dimension(460, 370));
		//frame.setMinimumSize(new Dimension(460, 370));
		//frame.setSize(new Dimension(460,255));
		//frame.pack();
		frame.getContentPane().setLayout(new GridLayout(0,1));
		
		//frame.setMaximumSize(new Dimension(460, 500));
		ta.setBounds(10, 35, 404, 70);
		//ta.setRows(3);
		//ta.setVisible(false);
		//frame.pack();

		//frame.pack();
		//contentPane.
		//frame.pack();
		
	

		

		/*
		 * inputpanel
		 */
		final JPanel inputPanel = new JPanel();
		inputPanel.setBorder(new TitledBorder(null, "Input", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		inputPanel.setPreferredSize(eighty);
		//inputPanel.setPreferredSize(null);
		//inputPanel.setSize(eighty);
		//inputPanel.set
		frame.getContentPane().add(inputPanel);
		inputPanel.setLayout(null);

		txtTypeInInput = new JTextField();
		txtTypeInInput.setBounds(10, 37, 300, 20);
		inputPanel.add(txtTypeInInput);
		txtTypeInInput.setEditable(false);
		txtTypeInInput.setText("Select file");
		
		
		final JScrollPane scrollPane = new JScrollPane();
		//scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 35, 405, 70);
		inputPanel.add(scrollPane);
		scrollPane.setVisible(false);
		
		ta.setText("indtast ip");
		scrollPane.setViewportView(ta);
		
		
		
		//inputPanel.add(ta);
		

		final JButton btnSelectFile = new JButton("Select File");
		btnSelectFile.setBounds(318, 36, 96, 23);
		btnSelectFile.setVisible(true);
		inputPanel.add(btnSelectFile);
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtTypeInInput.setText(guiHelper.openAISFileDialog());
			}
		});
		
		/*
		 * radiobutton1
		 */
		//JRadioButton rdbtnInputfile = new JRadioButton("Input-File");
		rdbtnInputfile.setBounds(116, 7, 86, 23);
		inputPanel.add(rdbtnInputfile);
		bg.add(rdbtnInputfile);
		rdbtnInputfile.setSelected(true);
		rdbtnInputfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtTypeInInput.setVisible(true);
				txtTypeInInput.setEditable(false);
				txtTypeInInput.setText("Select File");
				txtTypeInInput.setSize(300, 20);
				btnSelectFile.setVisible(true);
				//inputPanel.revalidate();
				scrollPane.setVisible(false);
				inputPanel.setPreferredSize(eighty);
				//inputPanel.setSize(eighty);
				//contentPane.repaint();
				
				if (chckbxAdvancedSettings.isSelected() == false)
				{
				//frame.setSize(new Dimension(460,255));
					frame.pack();
				frame.repaint();
				}
				else if (chckbxAdvancedSettings.isSelected() == true)
				{
				//frame.setSize(new Dimension(460,425));
					frame.pack();
				frame.repaint();
				}
				
				
			}
		});
		
		/*
		 * radiobutton2
		 */
		//JRadioButton rdbtnInputstream = new JRadioButton("Input-Stream");
		rdbtnInputstream.setBounds(204, 7, 96, 23);
		inputPanel.add(rdbtnInputstream);
		bg.add(rdbtnInputstream);
		rdbtnInputstream.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("ha");
				//txtTypeInInput.setVisible(true);
				//txtTypeInInput.setEditable(true);
				ta.setText("Type in input stream url");
				//ta.setVisible(true);
				//txtTypeInInput.setSize(400, 20);
				
				scrollPane.setVisible(true);
				ta.setWrapStyleWord(true);
				
				txtTypeInInput.setVisible(false);
				btnSelectFile.setVisible(false);
				
				//inputPanel.revalidate();
				//scrollPane.setVisible(false);
				inputPanel.setPreferredSize(onetwenty);
				//inputPanel.setSize(onetwenty);
				frame.pack();
				//contentPane.repaint();
				if (chckbxAdvancedSettings.isSelected() == false)
				{
				//frame.setSize(new Dimension(460,255));
				//inputPanel.setPreferredSize(new Dimension(425, 80));
				frame.repaint();
				frame.pack();
				
				}
				else if (chckbxAdvancedSettings.isSelected() == true)
				{
				//frame.setSize(new Dimension(460,425));
				//inputPanel.setPreferredSize(new Dimension(425, 120));
				frame.repaint();
				frame.pack();
				}
				
			}
		});
		
		
		/*
		 * radiobutton 3
		 */
		//JRadioButton rdbtnInputfromip = new JRadioButton("Input-From-IP");
		rdbtnInputfromip.setBounds(305, 7, 109, 23);
		inputPanel.add(rdbtnInputfromip);
		bg.add(rdbtnInputfromip);
		rdbtnInputfromip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("ha");
				//txtTypeInInput.setEditable(true);
				//txtTypeInInput.setText("Type in input stream url or select file");
				//txtTypeInInput.setSize(400, 20);
				
				//JTextArea ta = new JTextArea("this is a");
				//ta.setBounds(0, 0, 444, 272);
				//ta.setEditable(true);
				//inputPanel.add(ta);
				scrollPane.setVisible(true);
				ta.setWrapStyleWord(true);
				
				txtTypeInInput.setVisible(false);
				btnSelectFile.setVisible(false);
				//inputPanel.revalidate();
				inputPanel.setPreferredSize(onetwenty);
				//contentPane.repaint();
				
				
				//if (chckbxAdvancedSettings.isSelected() == false)
				//{
				//frame.setSize(new Dimension(460,295));
				//frame.repaint();
				//}
				//else if (chckbxAdvancedSettings.isSelected() == true)
				//{
				//frame.setSize(new Dimension(460,465));
				//frame.repaint();
			//	}
				
			}
		});
		
		
		
		
		
		
		/*
		 * analysis panel
		 */
		final JPanel gridPanel = new JPanel();
		gridPanel.setBorder(new TitledBorder(null, "Analysis settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		frame.getContentPane().add(gridPanel);
		gridPanel.setPreferredSize(eighty);
		//gridPanel.setPreferredSize(null);
		gridPanel.setLayout(null);


		
		final JLabel lblCellSize = new JLabel("CellSize: ");
		lblCellSize.setBounds(10, 21, 43, 20);
		gridPanel.add(lblCellSize);
		
		textField = new JTextField();
		textField.setLocation(52, 21);
		gridPanel.add(textField);
		textField.setText("2500");
		textField.setSize(335, 20);
		textField.setHorizontalAlignment(textField.RIGHT);

		final JLabel lblMeter = new JLabel(" Meter");
		lblMeter.setBounds(388, 21, 31, 20);
		gridPanel.add(lblMeter);
		
		
		
		final JLabel lblRunAnalysis = new JLabel("Run analysis for");
		lblRunAnalysis.setBounds(246, 52, 80, 14);
		lblRunAnalysis.setVisible(false);
		gridPanel.add(lblRunAnalysis);
		
		textField_2 = new JTextField();
		textField_2.setText("00:01");
		textField_2.setBounds(336, 52, 52, 20);
		textField_2.setVisible(false);
		gridPanel.add(textField_2);
		textField_2.setColumns(10);
		
		final JCheckBox chckbxSetAnalysisTimer = new JCheckBox("Set analysis timer");
		chckbxSetAnalysisTimer.setBounds(6, 52, 174, 23);
		gridPanel.add(chckbxSetAnalysisTimer);
		chckbxSetAnalysisTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("har");
				if(chckbxSetAnalysisTimer.isSelected() == false)
				{
					lblRunAnalysis.setVisible(false);
					textField_2.setVisible(false);
					//contentPane.repaint();
				}
				else if(chckbxSetAnalysisTimer.isSelected() == true)
				{
					lblRunAnalysis.setVisible(true);
					textField_2.setVisible(true);
					//contentPane.repaint();
				}				
			}
		});
		

		/*
		 * calculator panel (advanced settings)
		 */
		final JPanel calculatorPanel = new JPanel();
		calculatorPanel.setBorder(new TitledBorder(null, "Calculator", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		//frame.add(calculatorPanel);
		//calculatorPanel.setVisible(false);
		calculatorPanel.setPreferredSize(eighty);
		//calculatorPanel.setPreferredSize(null);
		//calculatorPanel.setPreferredSize(new Dimension(425, 0));
		calculatorPanel.setLayout(null);
		
		txtMessageBuffer = new JTextField();
		txtMessageBuffer.setText("20");
		txtMessageBuffer.setBounds(94, 22, 86, 20);
		calculatorPanel.add(txtMessageBuffer);
		txtMessageBuffer.setColumns(10);
		
		JLabel lblMessageBuffer = new JLabel("Message buffer");
		lblMessageBuffer.setBounds(10, 25, 98, 14);
		calculatorPanel.add(lblMessageBuffer);
		
		textField_1 = new JTextField();
		textField_1.setBounds(222, 50, 133, 20);
		calculatorPanel.add(textField_1);
		textField_1.setText("20");
		
		final JLabel lblShipRotationPer = new JLabel("Turning = ");
		lblShipRotationPer.setBounds(168, 53, 156, 14);
		calculatorPanel.add(lblShipRotationPer);
		
		final JLabel lblDegrees = new JLabel("Degrees");
		lblDegrees.setBounds(365, 53, 46, 14);
		calculatorPanel.add(lblDegrees);
		
		
		
		JLabel lblSekunder = new JLabel("Sekunder");
		lblSekunder.setBounds(186, 25, 46, 14);
		calculatorPanel.add(lblSekunder);
		
		//JCheckBox chckbxIncludeTurningShips = new JCheckBox("Include turning ships");
		chckbxIncludeTurningShips.setBounds(10, 49, 152, 23);
		calculatorPanel.add(chckbxIncludeTurningShips);
		chckbxIncludeTurningShips.setSelected(true);
		chckbxIncludeTurningShips.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("har");
				if(chckbxIncludeTurningShips.isSelected() == false)
				{
					lblShipRotationPer.setVisible(false);
					lblDegrees.setVisible(false);
					textField_1.setVisible(false);
					//contentPane.repaint();
				}
				else if(chckbxIncludeTurningShips.isSelected() == true)
				{
					lblShipRotationPer.setVisible(true);
					lblDegrees.setVisible(true);
					textField_1.setVisible(true);
					//contentPane.repaint();
				}				
			}
		});
		
		
		/*
		 * map panel (advanced settings)
		 */
		final JPanel mapPanel = new JPanel();
		mapPanel.setBorder(new TitledBorder(null, "Map", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		//frame.add(mapPanel);
		//mapPanel.setVisible(false);
		mapPanel.setPreferredSize(eighty);
		//mapPanel.setPreferredSize(null);
		//mapPanel.setPreferredSize(new Dimension(425, 0));
		mapPanel.setLayout(null);
		
		mapTextField = new JTextField();
		mapTextField.setEditable(false);
		mapTextField.setBounds(10, 21, 306, 20);
		mapPanel.add(mapTextField);
		mapTextField.setColumns(10);
		
		JButton btnSelectFile_1 = new JButton("Select File");
		btnSelectFile_1.setBounds(326, 20, 89, 23);
		mapPanel.add(btnSelectFile_1);
		btnSelectFile_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mapTextField.setText(guiHelper.openShapeFileDialog());
			}
		});
		
		final JLabel lblLat = new JLabel("Lat");
		lblLat.setBounds(165, 52, 30, 14);
		mapPanel.add(lblLat);
		lblLat.setVisible(false);
		
		textField_3 = new JTextField();
		textField_3.setBounds(205, 49, 76, 20);
		mapPanel.add(textField_3);
		textField_3.setVisible(false);
		
		final JLabel lblLong = new JLabel("Long");
		lblLong.setBounds(291, 52, 46, 14);
		mapPanel.add(lblLong);
		lblLong.setVisible(false);
		
		textField_4 = new JTextField();
		textField_4.setBounds(336, 49, 79, 20);
		mapPanel.add(textField_4);
		textField_4.setVisible(false);
		
		final JCheckBox chckbxSetMapCenterpoint = new JCheckBox("Set map centerpoint");
		chckbxSetMapCenterpoint.setBounds(10, 48, 149, 23);
		mapPanel.add(chckbxSetMapCenterpoint);
		chckbxSetMapCenterpoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("har");
				if(chckbxSetMapCenterpoint.isSelected() == false)
				{
					lblLat.setVisible(false);
					lblLong.setVisible(false);
					textField_3.setVisible(false);
					textField_4.setVisible(false);
					mapPanel.repaint();
					//contentPane.repaint();
				}
				else if(chckbxSetMapCenterpoint.isSelected() == true)
				{
					lblLat.setVisible(true);
					lblLong.setVisible(true);
					textField_3.setVisible(true);
					textField_4.setVisible(true);
					mapPanel.repaint();
					//contentPane.repaint();
				}				
			}
		});
		
		
		
		/*
		 * button panel
		 */
		final JPanel buttonPanel = new JPanel();
		frame.getContentPane().add(buttonPanel);
		buttonPanel.setPreferredSize(forty);
		//buttonPanel.setPreferredSize(null);
		buttonPanel.setLayout(null);
		buttonPanel.setBorder(new TitledBorder(null, "Button", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		chckbxAdvancedSettings.setBounds(6, 11, 115, 23);
		buttonPanel.add(chckbxAdvancedSettings);
		
		

		chckbxAdvancedSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("har");
				if(chckbxAdvancedSettings.isSelected() == false)
				{
					frame.remove(calculatorPanel);
					frame.remove(mapPanel);
					//calculatorPanel.setPreferredSize(new Dimension(425, 0));
					//mapPanel.setPreferredSize(new Dimension(425, 0));
					//frame.remove(mapPanel);
					frame.pack();
					frame.repaint();
					//calculatorPanel.setVisible(false);
					//mapPanel.setVisible(false);
					
					if (rdbtnInputfromip.isSelected() == false)
					{
						//frame.setSize(new Dimension(460,255));
						//contentPane.repaint();
						frame.repaint();
					}
					else if (rdbtnInputfromip.isSelected() == true)
					{
						//frame.setSize(new Dimension(460,295));
						//contentPane.repaint();
						frame.repaint();
					}
					
				}
				else if(chckbxAdvancedSettings.isSelected() == true)
				{

					frame.remove(buttonPanel);
					frame.getContentPane().add(calculatorPanel);
					frame.getContentPane().add(mapPanel);
					frame.getContentPane().add(buttonPanel);
					//calculatorPanel.setPreferredSize(new Dimension(425, 80));
					//mapPanel.setPreferredSize(new Dimension(425, 80));
					//frame.add(calculatorPanel);
					//frame.add(mapPanel);
					frame.pack();
					frame.repaint();
					//calculatorPanel.setVisible(true);
					//mapPanel.setVisible(true);
					
					if (rdbtnInputfromip.isSelected() == false)
					{
						//frame.setSize(new Dimension(460,425));
						//contentPane.repaint();
						frame.pack();
						frame.repaint();
						
					}
					else if (rdbtnInputfromip.isSelected() == true)
					{
						//frame.setSize(new Dimension(460,465));
						//contentPane.repaint();
						frame.pack();
						frame.repaint();
					}
					
				}				
			}
		});
		btnDone.setBounds(286, 11, 53, 23);
		
		/*
		 * new button
		 */
		//JButton btnDone = new JButton("New");
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				dk.dma.aiscoverage.project.AisCoverageProject project = projectHandler.createProject();
				
				
				
				
				System.out.println(textField.getText());
				if(rdbtnInputfile.isSelected())
				{
					if(txtTypeInInput.getText() == "Select File")
					{
						//give error message
					}
					else
					{
						project.setFile(txtTypeInInput.getText());
						CoverageCalculatorAdvanced3 calc = new dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced3();
						
						if(chckbxIncludeTurningShips.isSelected() == false)
						{
							calc.setIgnoreRotation(true);
							calc.setBufferInSeconds(Integer.parseInt(txtMessageBuffer.getText()));
						}
						else if (chckbxIncludeTurningShips.isSelected() == true)
						{
							calc.setIgnoreRotation(false);
							calc.setBufferInSeconds(Integer.parseInt(txtMessageBuffer.getText()));
							calc.setDegreesPerMinute(Integer.parseInt(textField_1.getText()));
						}
						
						project.setCalculator(calc);
						project.setCellSize(Integer.parseInt(textField.getText()));
						analysisPanel.setAnalysisData(txtTypeInInput.getText(), "Advanced", textField.getText(), "999h");
						
						
						
						dispose();
					}
				}
				else if (rdbtnInputstream.isSelected())
				{
					if(txtTypeInInput.getText().contains("Type in input stream url or select file"))
					{
						//give error message
					}
					else
					{
						//if(txtTypeInInput.getText().contains("port number")))
						String ips = ta.getText();
						System.out.println(ips);
						//System.out.println(ips.split("\n"));
						//ip = ips.split("\n");
						System.out.println(ips.split("\n").length);
						String[] ip;
						ip = ips.split("\n");
						
						for (String ib : ip)
						{
							//System.out.println(ib);
							//assign ip's to the program
							project.addHostPort(ib, id);
							id++;
						}
						
						//project.addHostPort(txtTypeInInput.getText(), id );
						//id++;
						CoverageCalculatorAdvanced3 calc = new dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced3();
						
						if(chckbxIncludeTurningShips.isSelected() == false)
						{
							calc.setIgnoreRotation(true);
							calc.setBufferInSeconds(Integer.parseInt(txtMessageBuffer.getText()));
							if(chckbxSetAnalysisTimer.isSelected() == true)
							{
							analysisPanel.setAnalysisData(txtTypeInInput.getText(), "Advanced", textField.getText(), textField_2.getText());
							}
							else
							{
								analysisPanel.setAnalysisData(txtTypeInInput.getText(), "Advanced", textField.getText(), "-");
							}
						}
						else if (chckbxIncludeTurningShips.isSelected() == true)
						{
							calc.setIgnoreRotation(false);
							calc.setBufferInSeconds(Integer.parseInt(txtMessageBuffer.getText()));
							calc.setDegreesPerMinute(Integer.parseInt(textField_1.getText()));
							if(chckbxSetAnalysisTimer.isSelected() == true)
							{
							analysisPanel.setAnalysisData(txtTypeInInput.getText(), "Advanced", textField.getText(), textField_2.getText());
							}
							else
							{
								analysisPanel.setAnalysisData(txtTypeInInput.getText(), "Advanced", textField.getText(), "-");
							}
						}
						
						
						project.setCalculator(calc);
						project.setCellSize(Integer.parseInt(textField.getText()));
						
						if(chckbxSetAnalysisTimer.isSelected() == true)
						{
							int hour = Integer.parseInt(textField_2.getText().substring(0, 1));
							int min = Integer.parseInt(textField_2.getText().substring(3, 4));
							int sec = (((hour * 60) + min) * 60);
							project.setTimeout(sec);
						}
						
						//analysisPanel.setAnalysisData(txtTypeInInput.getText(), "Advanced", textField.getText(), textField_2.getText());
						dispose();
					}
				}
				else if (rdbtnInputfromip.isSelected())
				{
					if(ta.getText().contains("indtast ip"))
					{
						//give error message
					}
					else
					{
						//if(txtTypeInInput.getText().contains("port number")))
						
						//ArrayList<String> ip = new ArrayList<String>();
						
						String ips = ta.getText();
						System.out.println(ips);
						//System.out.println(ips.split("\n"));
						//ip = ips.split("\n");
						System.out.println(ips.split("\n").length);
						String[] ip;
						ip = ips.split("\n");
						
						for (String ib : ip)
						{
							//System.out.println(ib);
							//assign ip's to the program
							project.addHostPort(ib, id);
							id++;
						}
						
						
						
						
						//project.addHostPort(ta.getText());
						CoverageCalculatorAdvanced3 calc = new dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced3();
						
						if(chckbxIncludeTurningShips.isSelected() == false)
						{
							calc.setIgnoreRotation(true);
							calc.setBufferInSeconds(Integer.parseInt(txtMessageBuffer.getText()));
							if(chckbxSetAnalysisTimer.isSelected() == true)
							{
							analysisPanel.setAnalysisData(txtTypeInInput.getText(), "Advanced", textField.getText(), textField_2.getText());
							}
							else
							{
								analysisPanel.setAnalysisData(txtTypeInInput.getText(), "Advanced", textField.getText(), "-");
							}
						}
						else if (chckbxIncludeTurningShips.isSelected() == true)
						{
							calc.setIgnoreRotation(false);
							calc.setBufferInSeconds(Integer.parseInt(txtMessageBuffer.getText()));
							calc.setDegreesPerMinute(Integer.parseInt(textField_1.getText()));
							if(chckbxSetAnalysisTimer.isSelected() == true)
							{
							analysisPanel.setAnalysisData(txtTypeInInput.getText(), "Advanced", textField.getText(), textField_2.getText());
							}
							else
							{
								analysisPanel.setAnalysisData(txtTypeInInput.getText(), "Advanced", textField.getText(), "-");
							}
						}
						
						
						project.setCalculator(calc);
						project.setCellSize(Integer.parseInt(textField.getText()));
						
						if(chckbxSetAnalysisTimer.isSelected() == true)
						{
							int hour = Integer.parseInt(textField_2.getText().substring(0, 1));
							int min = Integer.parseInt(textField_2.getText().substring(3, 4));
							int sec = (((hour * 60) + min) * 60);
							project.setTimeout(sec);
						}
						
						//analysisPanel.setAnalysisData(txtTypeInInput.getText(), "Advanced", textField.getText(), textField_2.getText());
						dispose();
					}
				}
				
				

				System.out.println("set alle settings");
				
				System.out.println(project.getCellSize());
				System.out.println(project.getCalculator().toString());
				System.out.println(project.getFile());
				
				
				
			}
		});
		
		buttonPanel.add(btnDone);
		btnCancel.setBounds(350, 11, 65, 23);

		//JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		buttonPanel.add(btnCancel);
		

		//frame.setMaximumSize(new Dimension(460,460));
		frame.pack();
	}
}
