package dk.dma.aiscoverage.openmap.layers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.proj.coords.LatLonPoint;

public class GridPolygon extends OMGraphicList {

	private static final long serialVersionUID = 1L;
	private List<LatLonPoint> polygon;
	private Rectangle hatchFillRectangle;
	private BufferedImage hatchFill;

	public GridPolygon(List<LatLonPoint> polygon, Color color) {

		hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D big = hatchFill.createGraphics();
		Composite originalComposite = big.getComposite();
		big.setComposite(makeComposite(0.5f));
		big.setColor(color);
		big.drawLine(0, 0, 10, 10);

		hatchFillRectangle = new Rectangle(0, 0, 10, 10);
		big.setComposite(originalComposite);

		this.polygon = polygon;
		drawPolygon();
	}

	private void drawPolygon() {
		// space for lat-lon points plus first lat-lon pair to close the polygon
		double[] polyPoints = new double[polygon.size() * 2 + 2];
		int j = 0;
		for (int i = 0; i < polygon.size(); i++) {
			polyPoints[j] = polygon.get(i).getY();
			polyPoints[j + 1] = polygon.get(i).getX();
			j += 2;
		}
		polyPoints[j] = polyPoints[0];
		polyPoints[j + 1] = polyPoints[1];
		OMPoly poly = new OMPoly(polyPoints, OMGraphic.DECIMAL_DEGREES,
				OMGraphic.LINETYPE_RHUMB, 1);
		//poly.setLinePaint(clear);
		poly.setFillPaint(new Color(0, 0, 0, 1));
		poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

		add(poly);
	}

	private AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}

//	@Override
//	public void render(Graphics gr) {
//
//		Graphics2D image = (Graphics2D) gr;
//		image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//				RenderingHints.VALUE_ANTIALIAS_ON);
//		super.render(image);
//		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);
//
//		String message0 = "If you want to print directly on screen";
//
//		gr.setFont(font);
//		gr.setColor(Color.red);
//		
//		gr.drawString(message0, 5, 20);
//
//	}
}
