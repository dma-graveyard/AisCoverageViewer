package dk.dma.aiscoverage.gui;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;


public class License extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String licenceText;

	
	/**
	 * Create the frame.
	 */
	@SuppressWarnings("static-access")
	public License() {
		setResizable(false);
		setTitle("License");
		setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 658, 519);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		licenceText = "Copyright 2012 Danish Maritime Authority. All rights reserved.\n\n"+

 "Redistribution and use in source and binary forms, with or without modification, \nare permitted provided that the following conditions are met:\n\n"+
	 
  "1. Redistributions of source code must retain the above copyright notice, \nthis list of conditions and the following disclaimer.\n\n"+
 
  "2. Redistributions in binary form must reproduce the above copyright notice, \nthis list of conditions and the following disclaimer in the documentation \nand/or other materials provided with the distribution.\n\n"+
  
 "THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS''\nAND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE \nIMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE \nDISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR \nANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES \n(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; \nLOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON \nANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT \n(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS \nSOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. \n\n"+
 
 "The views and conclusions contained in the software and documentation are those \nof the authors and should not be interpreted as representing official policies, \neither expressed or implied, of Danish Maritime Authority.";
		
		
		JTextArea ta = new JTextArea(licenceText);
		ta.setBounds(0, 0, 646, 492);
		ta.setEditable(false);
		contentPane.add(ta);
		ta.setVisible(true);
	}
}
