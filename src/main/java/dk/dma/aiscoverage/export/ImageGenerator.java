package dk.dma.aiscoverage.export;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImageGenerator {

	public static void generateJPG(JPanel panel, String path){
		BufferedImage bi = new BufferedImage(panel.getSize().width, panel.getSize().height, BufferedImage.TYPE_INT_ARGB); 
		Graphics g = bi.createGraphics();
		panel.paint(g);  //this == JComponent
		g.dispose();
		try{
			ImageIO.write(bi,"jpg",new File(path));
		}catch (Exception e) {}
	}
	public static void generatePNG(JPanel panel, String path){
		BufferedImage bi = new BufferedImage(panel.getSize().width, panel.getSize().height, BufferedImage.TYPE_INT_ARGB); 
		Graphics g = bi.createGraphics();
		panel.paint(g);  //this == JComponent
		g.dispose();
		try{
			ImageIO.write(bi,"png",new File(path));
		}catch (Exception e) {}
	}
	
}
