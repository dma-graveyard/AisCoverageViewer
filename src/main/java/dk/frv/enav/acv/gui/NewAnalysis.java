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
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import java.awt.Panel;
import java.awt.Button;
import javax.swing.JComboBox;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;

import javax.swing.Action;
import java.awt.event.ActionListener;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.EtchedBorder;

import dk.dma.aiscoverage.project.ProjectHandler;
import javax.swing.JCheckBox;
import java.awt.FlowLayout;

public class NewAnalysis extends JFrame {

	private GUIHelper guiHelper = new GUIHelper();
	private JPanel contentPane;
	private JTextField txtTypeInInput;
	private JTextField textField;
	JComboBox comboBox = null;
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

	/**
	 * Create the frame.
	 */
	public NewAnalysis() {
		setResizable(false);
		setTitle("New Analysis");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//setBounds(100, 100, 460, 381);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		//frame.setMaximumSize(new Dimension(460, 370));
		//frame.setMinimumSize(new Dimension(460, 370));
		frame.setSize(new Dimension(460,250));
		//frame.pack();

		//frame.pack();
		
	

		

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
		rdbtnInputfile.setBounds(199, 7, 109, 23);
		inputPanel.add(rdbtnInputfile);
		bg.add(rdbtnInputfile);
		rdbtnInputfile.setSelected(true);
		rdbtnInputfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtTypeInInput.setEditable(false);
				txtTypeInInput.setText("Select File");
				txtTypeInInput.setSize(300, 20);
				btnSelectFile.setVisible(true);
				//inputPanel.revalidate();
				contentPane.repaint();
				
				
			}
		});
		
		/*
		 * radiobutton2
		 */
		//JRadioButton rdbtnInputstream = new JRadioButton("Input-Stream");
		rdbtnInputstream.setBounds(310, 7, 109, 23);
		inputPanel.add(rdbtnInputstream);
		bg.add(rdbtnInputstream);
		rdbtnInputstream.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("ha");
				txtTypeInInput.setEditable(true);
				txtTypeInInput.setText("Type in input stream url or select file");
				txtTypeInInput.setSize(400, 20);
				btnSelectFile.setVisible(false);
				//inputPanel.revalidate();
				contentPane.repaint();
				
			}
		});
		
		/*
		 * grid panel
		 */
		final JPanel gridPanel = new JPanel();
		gridPanel.setBorder(new TitledBorder(null, "Analysis settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		gridPanel.setPreferredSize(new Dimension(425, 80));
		contentPane.add(gridPanel);
		gridPanel.setLayout(null);


		
		final JLabel lblCellSize = new JLabel("CellSize: ");
		lblCellSize.setBounds(6, 16, 43, 20);
		gridPanel.add(lblCellSize);
		
		textField = new JTextField();
		textField.setLocation(49, 16);
		gridPanel.add(textField);
		textField.setText("2500");
		textField.setSize(339, 20);
		textField.setHorizontalAlignment(textField.RIGHT);

		final JLabel lblMeter = new JLabel(" Meter");
		lblMeter.setBounds(388, 16, 31, 20);
		gridPanel.add(lblMeter);
		
		
		
		final JLabel lblRunAnalysis = new JLabel("Run analysis for");
		lblRunAnalysis.setBounds(212, 47, 80, 14);
		lblRunAnalysis.setVisible(false);
		gridPanel.add(lblRunAnalysis);
		
		textField_2 = new JTextField();
		textField_2.setText("00:01");
		textField_2.setBounds(302, 44, 86, 20);
		textField_2.setVisible(false);
		gridPanel.add(textField_2);
		textField_2.setColumns(10);
		
		final JCheckBox chckbxSetAnalysisTimer = new JCheckBox("Set analysis timer");
		chckbxSetAnalysisTimer.setBounds(6, 43, 174, 23);
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
		txtMessageBuffer.setColumns(10);
		
		JLabel lblMessageBuffer = new JLabel("Message buffer");
		lblMessageBuffer.setBounds(10, 25, 98, 14);
		calculatorPanel.add(lblMessageBuffer);
		
		textField_1 = new JTextField();
		textField_1.setBounds(222, 50, 133, 20);
		calculatorPanel.add(textField_1);
		textField_1.setText("Ship rotation per min");
		
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
		 * button panel
		 */
		final JPanel buttonPanel = new JPanel();
		contentPane.add(buttonPanel);
		buttonPanel.setPreferredSize(new Dimension(425, 40));
		buttonPanel.setLayout(null);
		chckbxAdvancedSettings.setBounds(0, 5, 115, 23);
		buttonPanel.add(chckbxAdvancedSettings);
		
		

		chckbxAdvancedSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("har");
				if(chckbxAdvancedSettings.isSelected() == false)
				{
					calculatorPanel.setVisible(false);
					frame.setSize(new Dimension(460,250));
					contentPane.repaint();
					frame.repaint();
				}
				else if(chckbxAdvancedSettings.isSelected() == true)
				{

					calculatorPanel.setVisible(true);
					frame.setSize(new Dimension(460,350));
					contentPane.repaint();
					frame.repaint();
				}				
			}
		});
		btnDone.setBounds(287, 5, 53, 23);
		
		//JButton btnDone = new JButton("New");
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				dk.dma.aiscoverage.project.AisCoverageProject project = projectHandler.createProject();
				project.setCellSize(Integer.parseInt(textField.getText()));
				
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
						project.setCalculator(new dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced3());
						
						
						
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
						project.setHostPort(txtTypeInInput.getText());
						project.setCalculator(new dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced3());
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