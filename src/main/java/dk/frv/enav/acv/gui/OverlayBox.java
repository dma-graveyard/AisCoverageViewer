package dk.frv.enav.acv.gui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.PopupMenu;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;

public abstract class OverlayBox extends JPanel{
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