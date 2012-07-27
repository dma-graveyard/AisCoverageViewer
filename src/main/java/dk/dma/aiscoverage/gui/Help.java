package dk.dma.aiscoverage.gui;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;


public class Help extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;


	/**
	 * Create the frame.
	 */
	@SuppressWarnings("static-access")
	public Help() {
		setResizable(false);
		setTitle("Help");
		setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JTextArea ta = new JTextArea("This is the help page");
		ta.setBounds(0, 0, 444, 272);
		ta.setEditable(false);
		contentPane.add(ta);
		ta.setVisible(true);
		
	}

}
