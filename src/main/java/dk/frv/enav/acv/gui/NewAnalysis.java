package dk.frv.enav.acv.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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

public class NewAnalysis extends JFrame {

	private GUIHelper guiHelper = new GUIHelper();
	private JPanel contentPane;
	private JTextField txtTypeInInput;
	private JTextField textField;
	JComboBox comboBox = null;
	ProjectHandler projectHandler = ProjectHandler.getInstance();

	/**
	 * Create the frame.
	 */
	public NewAnalysis() {
		setTitle("New Analysis");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 461, 296);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		/*
		 * skal det vare nogle fastsatte strrelser i en combobox istedet?
		 */

		JButton btnDone = new JButton("New");
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				dk.dma.aiscoverage.project.AisCoverageProject project = projectHandler.createProject();
				project.setCellSize(Integer.parseInt(textField.getText()));
				System.out.println(textField.getText());
				project.setFile(txtTypeInInput.getText());
//				project.addHostPort("172.28.25.66:9240");
//				project.addHostPort("172.28.37.66:9240");
//				project.addHostPort("10.3.246.210:9240", 666);
//				project.addHostPort("10.10.11.30:8030", 777);
				
				project.addHostPort("10.10.32.2:9240", 1);
				project.addHostPort("88.85.35.18:9240", 2);
				project.addHostPort("93.160.251.222:9240", 3);
				project.addHostPort("95.209.148.160:9240", 4);
				
	
//				if (comboBox.getSelectedItem() == "Calculator 1") {
//					System.out.println("calculator 1");
//					project.setCalculator(new dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced1());
//				} else if (comboBox.getSelectedItem() == "Calculator 2") {
					System.out.println("calculator 2");
					project.setCalculator(new dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced3());
//				}

				dispose();
				System.out.println("set alle settings");
				
				System.out.println(project.getCellSize());
				System.out.println(project.getCalculator().toString());
				System.out.println(project.getFile());
				
				
				
			}
		});
		btnDone.setBounds(356, 226, 79, 23);
		contentPane.add(btnDone);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnCancel.setBounds(257, 226, 89, 23);
		contentPane.add(btnCancel);

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(10, 11, 425, 68);
		contentPane.add(panel);
		panel.setLayout(null);

		JLabel lblInput = new JLabel("Input");
		lblInput.setBounds(10, 11, 46, 14);
		panel.add(lblInput);

		txtTypeInInput = new JTextField();
		txtTypeInInput.setBounds(10, 30, 298, 20);
		panel.add(txtTypeInInput);
		txtTypeInInput.setText("Type in input stream url or select file");
		txtTypeInInput.setColumns(10);

		JButton btnSelectFile = new JButton("Select File");
		btnSelectFile.setBounds(318, 29, 96, 23);
		panel.add(btnSelectFile);
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtTypeInInput.setText(guiHelper.openAISFileDialog());
			}
		});

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(10, 90, 425, 60);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		JLabel lblCellSize = new JLabel("Cell size");
		lblCellSize.setBounds(10, 11, 46, 14);
		panel_1.add(lblCellSize);

		textField = new JTextField();
		textField.setBounds(10, 28, 86, 20);
		panel_1.add(textField);
		textField.setText("2500");

		JLabel lblMeter = new JLabel("Meter");
		lblMeter.setBounds(106, 31, 46, 14);
		panel_1.add(lblMeter);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_2.setBounds(10, 161, 425, 60);
		contentPane.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblSelectCoverageCalculator = new JLabel(
				"Select Coverage calculator");
		lblSelectCoverageCalculator.setBounds(10, 11, 229, 14);
		panel_2.add(lblSelectCoverageCalculator);

		comboBox = new JComboBox();
		comboBox.setBounds(10, 31, 142, 20);
		panel_2.add(comboBox);
		comboBox.addItem("Calculator 1");
		comboBox.addItem("Calculator 2");

	}
}
