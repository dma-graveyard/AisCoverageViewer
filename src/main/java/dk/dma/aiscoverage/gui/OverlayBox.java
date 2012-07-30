package dk.dma.aiscoverage.gui;


import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public abstract class OverlayBox extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPopupMenu popup = new JPopupMenu();


	public OverlayBox(int width, int height) {
		
		popup.setPopupSize(width, height);
		
		this.setSize(width, height);
		popup.add(this);

	}
	public void show(Component invoker, int x, int y){
		
		popup.show(invoker, x, y);
		
	}
	public void setVisible(boolean b){
		super.setVisible(b);
		popup.setVisible(b);
	}

}