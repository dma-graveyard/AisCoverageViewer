package dk.dma.aiscoverage.openmap.layers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;


import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMRaster;
import com.bbn.openmap.omGraphics.OMRect;

import dk.dma.aiscoverage.calculator.DensityPlotCalculator;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.ColorGenerator;
import dk.dma.aiscoverage.event.AisEvent;
import dk.dma.aiscoverage.event.IProjectHandlerListener;
import dk.dma.aiscoverage.event.AisEvent.Event;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.dma.aiscoverage.project.ProjectHandler;


public class DensityPlotLayer extends OMGraphicHandlerLayer implements Runnable, IProjectHandlerListener {

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
			System.out.println("exception");
			return Color.WHITE;
		}
		
	}


	public void doUpdate() {
		if(calc == null)
			return;
		
		BufferedImage bi = new BufferedImage(getProjection().getWidth(), getProjection().getHeight(), BufferedImage.TYPE_INT_ARGB); 
		Graphics2D g = bi.createGraphics();
		int width = getProjection().getWidth();
		int height = getProjection().getHeight();
		System.out.println("start get map");
		Collection<Cell> cs = calc.getDensityPlotCoverage();
		System.out.println("end get map");
		for (Cell cell : cs) {
			
			//Convert lat lon coords to x-y pixel coords
			Point2D point1 = getProjection().forward(cell.getLatitude(), cell.getLongitude());
			Point2D point2 = getProjection().forward(cell.getLatitude(), cell.getLongitude()+calc.getLongSize());
			Point2D point3 = getProjection().forward(cell.getLatitude()+calc.getLatSize(), cell.getLongitude()+calc.getLongSize());
			Point2D point4 = getProjection().forward(cell.getLatitude()+calc.getLatSize(), cell.getLongitude());

			//create arrays for polygon
			int[] xPoints = {(int)point1.getX(),(int) point2.getX(),(int) point3.getX(),(int)point4.getX()};
			int[] yPoints = {(int)point1.getY(),(int)point2.getY(),(int)point3.getY(),(int)point4.getY()};
			int nPoints = 4;
			
			//If cell is visible in current projection, draw polygon
			if(point1.getX() > 0 && point1.getX() < width){
				if(point1.getY() > 0 && point1.getY() < height){
					Color color = getColor(cell);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
					g.setColor(color);
					g.setBackground(color);
					g.fillPolygon(xPoints, yPoints, nPoints);
					if(drawBorder){
						g.drawPolygon(xPoints, yPoints, nPoints);
					}
				}
			}
		}

		System.out.println("end update");
		raster = new OMRaster(0, 0, bi);		
		graphics.clear();
		graphics.add(raster);
		System.out.println("UPDATING density layer");

		doPrepare();
	}
	public void projectionChanged(ProjectionEvent pe){
		super.projectionChanged(pe);
		reset();
		updateOnce = true;
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
	}

}
