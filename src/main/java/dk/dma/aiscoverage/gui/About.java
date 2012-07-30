package dk.dma.aiscoverage.gui;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;


public class About extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public About() {
		setResizable(false);
		setTitle("About");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 537, 129);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JTextArea ta = new JTextArea("Created by Danish Maritime Authority, as an open source project. \nThe source code can be found on GitHub at this address: \nhttps://github.com/DaMSA/AisCoverageViewer \nAnd is free to use as long as the License requirements are meet. ");
		ta.setBounds(0, 0, 524, 272);
		ta.setEditable(false);
		contentPane.add(ta);
		ta.setVisible(true);
		
		
	}

}
