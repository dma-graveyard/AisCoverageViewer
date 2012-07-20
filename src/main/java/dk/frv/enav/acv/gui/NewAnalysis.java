package dk.frv.enav.acv.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;

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

//<<<<<<< HEAD
import dk.dma.aiscoverage.calculator.CoverageCalculator;
import dk.dma.aiscoverage.calculator.DensityPlotCalculator;
//=======
//import dk.dma.aiscoverage.calculator.AbstractCoverageCalculator;
//import dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced3;
//>>>>>>> merged
import dk.dma.aiscoverage.project.ProjectHandler;
import javax.swing.JCheckBox;

import com.bbn.openmap.gui.OMComponentPanel;

import java.awt.FlowLayout;
import javax.swing.JScrollPane;

public class NewAnalysis extends JFrame {

	
	private static final long serialVersionUID = 1L;
	private GUIHelper guiHelper = new GUIHelper();
	private JPanel contentPane;
	private JTextField txtTypeInInput;
	private JTextField textField;
	ButtonGroup bg = new ButtonGroup();
	ProjectHandler projectHandler = ProjectHandler.getInstance();
	JRadioButton rdbtnInputfile = new JRadioButton("Input-File");
	JRadioButton rdbtnInputstream = new JRadioButton("Input-Stream");
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
	JTextArea ta = new JTextArea("this is a");
	private long id;

	/**
	 * Create the frame.
	 */
	public NewAnalysis(AnalysisPanel ap) {
		analysisPanel = ap; 
		id = 1;
		
		setResizable(false);
		setTitle("New Analysis");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		frame.setSize(new Dimension(460,255));
		ta.setBounds(10, 35, 404, 70);
	

		/*
		 * inputpanel
		 */
		final JPanel inputPanel = new JPanel();
		inputPanel.setBorder(new TitledBorder(null, "Input", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(inputPanel);
		inputPanel.setPreferredSize(new Dimension(425, 80));
		inputPanel.setLayout(null);

		txtTypeInInput = new JTextField();
		txtTypeInInput.setBounds(10, 37, 300, 20);
		inputPanel.add(txtTypeInInput);
		txtTypeInInput.setEditable(false);
		txtTypeInInput.setText("Select file");
		
		
		final JScrollPane scrollPane = new JScrollPane();

		scrollPane.setBounds(10, 35, 405, 70);
		inputPanel.add(scrollPane);
		scrollPane.setVisible(false);
		scrollPane.setViewportView(ta);

		

		final JButton btnSelectFile = new JButton("Select File");
		btnSelectFile.setBounds(318, 36, 96, 23);
		btnSelectFile.setVisible(true);
		inputPanel.add(btnSelectFile);
		btnSelectFile.addActionListener(new ActionListener() {	public void actionPerformed(ActionEvent e) 
		{	txtTypeInInput.setText(guiHelper.openAISFileDialog());	}	});
		
		/*
		 * radiobutton1
		 */
		//JRadioButton rdbtnInputfile = new JRadioButton("Input-File");
		rdbtnInputfile.setBounds(224, 10, 86, 23);
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
				scrollPane.setVisible(false);
				inputPanel.setPreferredSize(new Dimension(425, 80));
				contentPane.repaint();
				if (chckbxAdvancedSettings.isSelected() == false)
				{
				frame.setSize(new Dimension(460,255));
				frame.repaint();
				}
				else if (chckbxAdvancedSettings.isSelected() == true)
				{
				frame.setSize(new Dimension(460,425));
				frame.repaint();
				}	
			}
		});
		
		/*
		 * radiobutton2
		 */
		//JRadioButton rdbtnInputstream = new JRadioButton("Input-Stream");
		rdbtnInputstream.setBounds(319, 10, 96, 23);
		inputPanel.add(rdbtnInputstream);
		bg.add(rdbtnInputstream);
		rdbtnInputstream.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ta.setText("Type in input stream url");
				scrollPane.setVisible(true);
				ta.setWrapStyleWord(true);
				txtTypeInInput.setVisible(false);
				btnSelectFile.setVisible(false);
				
				inputPanel.setPreferredSize(new Dimension(425, 120));
				
				contentPane.repaint();
				if (chckbxAdvancedSettings.isSelected() == false)
				{
				frame.setSize(new Dimension(460,300));
				frame.repaint();
				}
				else if (chckbxAdvancedSettings.isSelected() == true)
				{
				frame.setSize(new Dimension(460,460));
				frame.repaint();
				}	
			}
		});
		
		/*
		 * analysis panel
		 */
		final JPanel gridPanel = new JPanel();
		gridPanel.setBorder(new TitledBorder(null, "Analysis settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		gridPanel.setPreferredSize(new Dimension(425, 85));
		contentPane.add(gridPanel);
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
					contentPane.repaint();
				}
				else if(chckbxSetAnalysisTimer.isSelected() == true)
				{
					lblRunAnalysis.setVisible(true);
					textField_2.setVisible(true);
					contentPane.repaint();
				}				
			}
		});
		

		/*
		 * calculator panel (advanced settings)
		 */
		final JPanel calculatorPanel = new JPanel();
		calculatorPanel.setBorder(new TitledBorder(null, "Calculator", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(calculatorPanel);
		calculatorPanel.setVisible(false);
		calculatorPanel.setPreferredSize(new Dimension(425, 80));
		calculatorPanel.setLayout(null);
		
		txtMessageBuffer = new JTextField();
		txtMessageBuffer.setText("20");
		txtMessageBuffer.setBounds(94, 22, 86, 20);
		calculatorPanel.add(txtMessageBuffer);
		
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
		chckbxIncludeTurningShips.setSelected(false);
		chckbxIncludeTurningShips.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxIncludeTurningShips.isSelected() == false)
				{
					lblShipRotationPer.setVisible(false);
					lblDegrees.setVisible(false);
					textField_1.setVisible(false);
					contentPane.repaint();
				}
				else if(chckbxIncludeTurningShips.isSelected() == true)
				{
					lblShipRotationPer.setVisible(true);
					lblDegrees.setVisible(true);
					textField_1.setVisible(true);
					contentPane.repaint();
				}				
			}
		});
		
		
		/*
		 * map panel (advanced settings)
		 */
		final JPanel mapPanel = new JPanel();
		mapPanel.setBorder(new TitledBorder(null, "Map", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(mapPanel);
		mapPanel.setVisible(false);
		mapPanel.setPreferredSize(new Dimension(425, 50));
		mapPanel.setLayout(null);
		
		//mapTextField = new JTextField();
		//mapTextField.setEditable(false);
		//mapTextField.setBounds(10, 21, 306, 20);
		//mapPanel.add(mapTextField);
		
		//JButton btnSelectFile_1 = new JButton("Select File");
		//btnSelectFile_1.setBounds(326, 20, 89, 23);
		//mapPanel.add(btnSelectFile_1);
		//btnSelectFile_1.addActionListener(new ActionListener() {
		//	public void actionPerformed(ActionEvent e) {
		//		mapTextField.setText(guiHelper.openShapeFileDialog());
		//	}
		//});
		
		final JLabel lblLat = new JLabel("Lat");
		lblLat.setBounds(165, 18, 24, 14);
		mapPanel.add(lblLat);
		lblLat.setVisible(false);
		
		textField_3 = new JTextField();
		textField_3.setBounds(205, 15, 76, 20);
		mapPanel.add(textField_3);
		textField_3.setVisible(false);
		
		final JLabel lblLong = new JLabel("Long");
		lblLong.setBounds(291, 18, 37, 14);
		mapPanel.add(lblLong);
		lblLong.setVisible(false);
		
		textField_4 = new JTextField();
		textField_4.setBounds(336, 15, 79, 20);
		mapPanel.add(textField_4);
		textField_4.setVisible(false);
		
		final JCheckBox chckbxSetMapCenterpoint = new JCheckBox("Set map centerpoint");
		chckbxSetMapCenterpoint.setBounds(10, 15, 149, 23);
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
					contentPane.repaint();
				}
				else if(chckbxSetMapCenterpoint.isSelected() == true)
				{
					lblLat.setVisible(true);
					lblLong.setVisible(true);
					textField_3.setVisible(true);
					textField_4.setVisible(true);
					mapPanel.repaint();
					contentPane.repaint();
				}				
			}
		});
		
		
		
		/*
		 * button panel
		 */
		final JPanel buttonPanel = new JPanel();
		contentPane.add(buttonPanel);
		buttonPanel.setPreferredSize(new Dimension(425, 60));
		buttonPanel.setLayout(null);
		chckbxAdvancedSettings.setBounds(0, 5, 115, 23);
		buttonPanel.add(chckbxAdvancedSettings);
		
		

		chckbxAdvancedSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("har");
				if(chckbxAdvancedSettings.isSelected() == false)
				{
					calculatorPanel.setVisible(false);
					mapPanel.setVisible(false);
					
					if (rdbtnInputstream.isSelected() == false)
					{
						frame.setSize(new Dimension(460,260));
						contentPane.repaint();
						frame.repaint();
					}
					else if (rdbtnInputstream.isSelected() == true)
					{
						frame.setSize(new Dimension(460,300));
						contentPane.repaint();
						frame.repaint();
					}
					
				}
				else if(chckbxAdvancedSettings.isSelected() == true)
				{

					calculatorPanel.setVisible(true);
					mapPanel.setVisible(true);
					
					if (rdbtnInputstream.isSelected() == false)
					{
						frame.setSize(new Dimension(460,410));
						contentPane.repaint();
						frame.repaint();
					}
					else if (rdbtnInputstream.isSelected() == true)
					{
						frame.setSize(new Dimension(460,440));
						contentPane.repaint();
						frame.repaint();
					}
					
				}				
			}
		});
		btnDone.setBounds(287, 5, 53, 23);
		
		/*
		 * new button
		 */
		//JButton btnDone = new JButton("New");
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				dk.dma.aiscoverage.project.AisCoverageProject project = projectHandler.createProject();

				//add coverage calculator
				CoverageCalculator coverageCalc = new CoverageCalculator(project, true);
				coverageCalc.setCellSize(Integer.parseInt(textField.getText()));
				project.addCalculator(coverageCalc);
				
				//add densityplot calculator
				DensityPlotCalculator densityCalc = new DensityPlotCalculator(project, true);
				densityCalc.setCellSize(200);
				project.addCalculator(densityCalc);

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

						
						if(chckbxIncludeTurningShips.isSelected() == false)
						{
							coverageCalc.setIgnoreRotation(true);
							coverageCalc.setBufferInSeconds(Integer.parseInt(txtMessageBuffer.getText()));
						}
						else if (chckbxIncludeTurningShips.isSelected() == true)
						{
							coverageCalc.setIgnoreRotation(false);
							coverageCalc.setBufferInSeconds(Integer.parseInt(txtMessageBuffer.getText()));
							coverageCalc.setDegreesPerMinute(Integer.parseInt(textField_1.getText()));
						}
						

						coverageCalc.setCellSize(Integer.parseInt(textField.getText()));
						analysisPanel.setAnalysisData(txtTypeInInput.getText(), "Advanced", textField.getText(), "999h");
						
						if(chckbxSetAnalysisTimer.isSelected() == true)
						{
							int hour = Integer.parseInt(textField_2.getText().substring(0, 2));
							int min = Integer.parseInt(textField_2.getText().substring(3, 5));
							int sec = (((hour * 60) + min) * 60);
							System.out.println(hour);
							System.out.println(min);
							project.setTimeout(sec);
							
							//System.out.println("0,2 "+textField_2.getText().substring(0,2));
							//System.out.println("2,2 "+textField_2.getText().substring(2,2));
							//System.out.println("2 " +textField_2.getText().substring(2));
							//System.out.println("3 " + textField_2.getText().substring(3));
						}
						
						
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
						//project.addHostPort(txtTypeInInput.getText(), id);
		
						//id++;
						
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
						
						
						if(chckbxIncludeTurningShips.isSelected() == false)
						{
							coverageCalc.setIgnoreRotation(true);
							coverageCalc.setBufferInSeconds(Integer.parseInt(txtMessageBuffer.getText()));
							if(chckbxSetAnalysisTimer.isSelected() == true)
							{
							analysisPanel.setAnalysisData("Streams", "Advanced", textField.getText(), textField_2.getText());
							}
							else
							{
								analysisPanel.setAnalysisData("Streams", "Advanced", textField.getText(), "-");
							}
						}
						else if (chckbxIncludeTurningShips.isSelected() == true)
						{
							coverageCalc.setIgnoreRotation(false);
							coverageCalc.setBufferInSeconds(Integer.parseInt(txtMessageBuffer.getText()));
							coverageCalc.setDegreesPerMinute(Integer.parseInt(textField_1.getText()));
							if(chckbxSetAnalysisTimer.isSelected() == true)
							{
							analysisPanel.setAnalysisData("Streams", "Advanced", textField.getText(), textField_2.getText());
							}
							else
							{
								analysisPanel.setAnalysisData("Streams", "Advanced", textField.getText(), "-");
							}
						}
						
						
						coverageCalc.setCellSize(Integer.parseInt(textField.getText()));
						
						if(chckbxSetAnalysisTimer.isSelected() == true)
						{
							int hour = Integer.parseInt(textField_2.getText().substring(0, 2));
							int min = Integer.parseInt(textField_2.getText().substring(3, 5));
							int sec = (((hour * 60) + min) * 60);
							System.out.println(hour);
							System.out.println(min);
							project.setTimeout(sec);
						}
						
						analysisPanel.setAnalysisData("Streams", "Advanced", textField.getText(), textField_2.getText());
						dispose();
					}
				}					
				
				System.out.println("set alle settings");
				//System.out.println(project.getFile());
				
				
				
			}
		});
		
		buttonPanel.add(btnDone);
		btnCancel.setBounds(350, 5, 65, 23);

		//JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		buttonPanel.add(btnCancel);
		

	}
}
