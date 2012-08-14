package dk.dma.aiscoverage.openmap.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;
import com.bbn.openmap.omGraphics.OMRaster;

import dk.dma.aiscoverage.calculator.DensityPlotCalculator;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.event.AisEvent;
import dk.dma.aiscoverage.event.NavigationMouseMode;
import dk.dma.aiscoverage.event.AisEvent.Event;
import dk.dma.aiscoverage.event.IProjectHandlerListener;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.dma.aiscoverage.project.ProjectHandler;


public class DensityPlotLayer extends OMGraphicHandlerLayer implements Runnable, IProjectHandlerListener, MapMouseListener {

	private DensityPlotCalculator calc;
	private static final long serialVersionUID = 1L;
	public boolean isRunning = false;
	private OMGraphicList graphics = new OMGraphicList();
	private Color[] colors = new Color[10];
	private int updateDelay;
	private final int defaultUpdatedelay = 100;
	private boolean updateOnce = true;
	private int dhigh;
	private int dmedium;
	private int dlow;
	private OMRaster raster;
	private boolean drawBorder;
	private int antialisingValue = 1;

	

	public DensityPlotLayer(){
		setRenderPolicy(new com.bbn.openmap.layer.policy.PanningImageRenderPolicy());
		
		colors[0] = Color.WHITE;
		colors[1] = interpolate(Color.WHITE, Color.YELLOW, 0.2f);
		colors[2] = interpolate(Color.WHITE, Color.YELLOW, 0.6f);
		colors[3] = interpolate(Color.WHITE, Color.YELLOW, 0.8f);
		colors[4] = Color.YELLOW;
		colors[5] = interpolate(Color.YELLOW, Color.RED, 0.2f);
		colors[6] = interpolate(Color.YELLOW, Color.RED, 0.4f);
		colors[7] = interpolate(Color.YELLOW, Color.RED, 0.6f);
		colors[8] = interpolate(Color.YELLOW, Color.RED, 0.8f);
		colors[9] = Color.RED;
		
		new Thread(this).start();
		
		ProjectHandler.getInstance().addProjectHandlerListener(this);

		
	}
	Color interpolate(Color colorA, Color colorB, float bAmount) {	    
	    float aAmount = (float) (1.0 - bAmount);
	    int r =  (int) (colorA.getRed() * aAmount + colorB.getRed() * bAmount);
	    int g =  (int) (colorA.getGreen() * aAmount + colorB.getGreen() * bAmount);
	    int b =  (int) (colorA.getBlue() * aAmount + colorB.getBlue() * bAmount);
	    return new Color(r, g, b);
	}
	

	public void setHighMedLow(int high, int medium, int low)
	{
		dhigh = high;
		dmedium = medium;
		dlow = low;
	}
	
	public Color getColor(Cell c){
		try{


			int x1 = dlow+1;
			int x2 = (int) (((dmedium - dlow)*0.25) + dlow)+1;
			int x3 = (int) (((dmedium - dlow)*0.50) + dlow)+1;
			int x4 = (int) (((dmedium - dlow)*0.75) + dlow)+1;
			int x5 = dmedium+1;
			int x6 = (int) (((dhigh - dmedium)*0.25) + dmedium)+1;
			int x7 = (int) (((dhigh - dmedium)*0.50) + dmedium)+1;
			int x8 = (int) (((dhigh - dmedium)*0.75) + dmedium)+1;
			int x9 = dhigh;
			
			
			double seconds = calc.getTimeDifference(calc.getFirstMessage().getTimestamp().getTime(), calc.getCurrentMessage().getTimestamp().getTime());
			int shipsPerDay = (int) ((double)(c.getShipCount()-1)/seconds*86400);

			Color color;
			if(shipsPerDay < x1)
				color = colors[0];
			else if(shipsPerDay < x2)
				color = colors[1];
			else if(shipsPerDay < x3)
				color = colors[2];
			else if(shipsPerDay < x4)
				color = colors[3];
			else if(shipsPerDay < x5)
				color = colors[4];
			else if(shipsPerDay < x6)
				color = colors[5];
			else if(shipsPerDay < x7)
				color = colors[6];
			else if(shipsPerDay < x8)
				color = colors[7];
			else if(shipsPerDay < x9)
				color = colors[8];
			else
				color = colors[9];
			return color;
		}catch(Exception e){
//			System.out.println("exception");
			return Color.WHITE;
		}
		
	}
	
	public static BufferedImage scaleImage(BufferedImage img, int finalWidth, int finalHeight) {
		System.out.println("weeh");
	    int imgWidth = img.getWidth();
	    int imgHeight = img.getHeight();
	    int shrinkWidth = (int) (img.getWidth()*0.5);
	    int shrinkHeight = (int) (img.getHeight()*0.5);
	    if(finalWidth > shrinkWidth){
	    	
	    	BufferedImage newImage = new BufferedImage(finalWidth, finalHeight,
		            BufferedImage.TYPE_INT_ARGB);
		    Graphics2D g = newImage.createGraphics();
		    try {
		        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		        g.drawImage(img, 0, 0, finalWidth, finalHeight, null);
		    } finally {
		        g.dispose();
		    }
		    return newImage;
	    }
	    BufferedImage newImage = new BufferedImage(shrinkWidth, shrinkHeight,
	            BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = newImage.createGraphics();
	    try {
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

	        g.drawImage(img, 0, 0, shrinkWidth, shrinkHeight, null);
	    } finally {
	        g.dispose();
	    }
	    return scaleImage(newImage, finalWidth, finalHeight);

	}
	public void doUpdate() {
		
		if(calc == null)
			return;
		
		BufferedImage bi = new BufferedImage(getProjection().getWidth()*antialisingValue, getProjection().getHeight()*antialisingValue, BufferedImage.TYPE_INT_ARGB); 
		Graphics2D g = bi.createGraphics();
		int width = getProjection().getWidth();
		int height = getProjection().getHeight();
		Collection<Cell> cs = calc.getDensityPlotCoverage();
		System.out.println("start generating density plot");
		for (Cell cell : cs) {
			
			//Convert lat lon coords to x-y pixel coords
			Point2D point1 = getProjection().forward(cell.getLatitude(), cell.getLongitude());

			//If cell is visible in current projection, draw polygon
			if(point1.getX() > 0 && point1.getX() < width){
				if(point1.getY() > 0 && point1.getY() < height){
					Point2D point2 = getProjection().forward(cell.getLatitude(), cell.getLongitude()+calc.getLongSize());
					Point2D point3 = getProjection().forward(cell.getLatitude()-calc.getLatSize(), cell.getLongitude()+calc.getLongSize());
					Point2D point4 = getProjection().forward(cell.getLatitude()-calc.getLatSize(), cell.getLongitude());
					
					//create arrays for polygon
					int[] xPoints = {(int)Math.floor(point1.getX()*antialisingValue),(int) Math.ceil(point2.getX()*antialisingValue),(int) Math.ceil(point3.getX()*antialisingValue),(int)Math.floor(point4.getX()*antialisingValue)};
					int[] yPoints = {(int)Math.floor(point1.getY()*antialisingValue),(int)Math.floor(point2.getY()*antialisingValue),(int)Math.ceil(point3.getY()*antialisingValue),(int)Math.ceil(point4.getY()*antialisingValue)};
					int nPoints = 4;

					Color color = getColor(cell);
					g.setColor(color);
					g.setBackground(color);
					g.fillPolygon(xPoints, yPoints, nPoints);
				}
			}
		}

		System.out.println("density plot ended");
		System.out.println("start post processing");
		if(antialisingValue == 1)
			raster = new OMRaster(0, 0, bi);	
		else
			raster = new OMRaster(0, 0, DensityPlotLayer.scaleImage(bi, width, height));
		System.out.println("Postprocessing ended");
		graphics.clear();
		graphics.add(raster);
//		System.out.println("UPDATING coverage layer");
		doPrepare();
	}


	public void projectionChanged(ProjectionEvent pe){
		super.projectionChanged(pe);
		updateOnce = true;
		updateDelay = 1;
	}
	
	@Override
	public void findAndInit(Object obj) {
		//This is used in case we need to communicate with other handler objects such as AIS 
	}

	
	@Override
	public synchronized OMGraphicList prepare() {
		graphics.project(getProjection());
		return graphics;
	}
	public void reset() {
		graphics.clear();
		updateOnce = true;
		updateDelay = 1;
	}
	@Override
	public void run() {
		while(true){
			try {
				while(updateDelay > 0){
					Thread.sleep(100);
					updateDelay--;
				}
				updateDelay = defaultUpdatedelay ; //default update delay
				
				AisCoverageProject project = ProjectHandler.getInstance().getProject();
				if(project != null){
					this.calc = project.getDensityPlotCalculator();
					if(calc != null){
						if((updateOnce && isVisible()) || (ProjectHandler.getInstance().getProject().isRunning() && isVisible()) ){
							doUpdate();
							updateOnce = false;
						}
					}
					
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	@Override
	public void aisEventReceived(AisEvent event) {
		if(event.getEvent() == Event.ANALYSIS_STOPPED){
			updateOnce = true;
		}else if(event.getEvent() == Event.PROJECT_CREATED){
			reset();
		}else if(event.getEvent() == Event.PROJECT_LOADED){
			updateOnce();
		}
	}

	public void updateOnce() {
		this.updateOnce = true;
		updateDelay = 1;
	}
	public MapMouseListener getMapMouseListener(){
		return this;
	}
	
	@Override
	public String[] getMouseModeServiceList() {
		String[] ret = new String[1];
		ret[0] = NavigationMouseMode.modeID; // "Gestures"
		
		
		
		return ret;
	}

	@Override
	public boolean mouseClicked(MouseEvent arg0) {
		if(antialisingValue == 1){
			antialisingValue = 4;
			System.out.println("antialiasing on X4");
		}else if(antialisingValue == 4){
			antialisingValue = 2;
			System.out.println("antialiasing on X2");
		}else{
			antialisingValue = 1;
			System.out.println("antialiasing off");
		}
		updateOnce();
		return false;
	}

	@Override
	public boolean mouseDragged(MouseEvent arg0) {
		return false;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved() {
	}

	@Override
	public boolean mouseMoved(MouseEvent e) {

		return false;
	}

	@Override
	public boolean mousePressed(MouseEvent arg0) {
		return false;
	}

	@Override
	public boolean mouseReleased(MouseEvent arg0) {
		return false;
	}

}
