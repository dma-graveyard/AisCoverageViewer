package dk.frv.enav.acv.gui;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;


public class Help extends JFrame {

	private JPanel contentPane;


	/**
	 * Create the frame.
	 */
	public Help() {
		setResizable(false);
		setTitle("Help");
		setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JTextArea ta = new JTextArea("this is a");
		ta.setBounds(0, 0, 444, 272);
		ta.setEditable(false);
		contentPane.add(ta);
		ta.setVisible(true);
		
	}

}
