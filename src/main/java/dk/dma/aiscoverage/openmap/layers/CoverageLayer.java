package dk.dma.aiscoverage.openmap.layers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMRaster;

import dk.dma.aiscoverage.calculator.CoverageCalculator;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.ColorGenerator;
import dk.dma.aiscoverage.event.AisEvent;
import dk.dma.aiscoverage.event.IProjectHandlerListener;
import dk.dma.aiscoverage.event.AisEvent.Event;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.dma.aiscoverage.project.ProjectHandler;


public class CoverageLayer extends OMGraphicHandlerLayer implements Runnable, IProjectHandlerListener {

	private CoverageCalculator calc;
	private static final long serialVersionUID = 1L;
	private OMGraphicList graphics = new OMGraphicList();
	public boolean isRunning = false;
	private int updateDelay;
	private final int defaultUpdatedelay = 100;
	private boolean updateOnce = true;
	private OMRaster raster;
	private boolean drawBorder = true;

	
	public void updateOnce() {
		updateOnce = true;
		updateDelay = 1;
	}
	public CoverageLayer(){	
		new Thread(this).start();
		ProjectHandler.getInstance().addProjectHandlerListener(this);
		doUpdate();
	}

	public void doUpdate() {
		
		if(calc == null)
			return;
		
		BufferedImage bi = new BufferedImage(getProjection().getWidth(), getProjection().getHeight(), BufferedImage.TYPE_INT_ARGB); 
		Graphics2D g = bi.createGraphics();
		int width = getProjection().getWidth();
		int height = getProjection().getHeight();
//		System.out.println("start get map");
		Collection<Cell> cs = calc.getCoverage();
//		System.out.println("end get map");
		for (Cell cell : cs) {
			
			//Convert lat lon coords to x-y pixel coords
//		//Convert lat lon coords to x-y pixel coords
			Point2D point1 = getProjection().forward(cell.getLatitude(), cell.getLongitude());
			
			
			//If cell is visible in current projection, draw polygon
			if(point1.getX() > 0 && point1.getX() < width){
				if(point1.getY() > 0 && point1.getY() < height){
					Point2D point2 = getProjection().forward(cell.getLatitude(), cell.getLongitude()+calc.getLongSize());
					Point2D point3 = getProjection().forward(cell.getLatitude()-calc.getLatSize(), cell.getLongitude()+calc.getLongSize());
					Point2D point4 = getProjection().forward(cell.getLatitude()-calc.getLatSize(), cell.getLongitude());
					
					//create arrays for polygon
					int[] xPoints = {(int)Math.round(point1.getX()),(int) Math.round(point2.getX()),(int) Math.round(point3.getX()),(int)Math.round(point4.getX())};
					int[] yPoints = {(int)Math.round(point1.getY()),(int)Math.round(point2.getY()),(int)Math.round(point3.getY()),(int)Math.round(point4.getY())};
					int nPoints = 4;
					
					Color color = ColorGenerator.getCoverageColor(cell, calc.getHighThreshold(), calc.getLowThreshold());
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
					g.setColor(color);
					g.setBackground(color);
					g.fillPolygon(xPoints, yPoints, nPoints);
					if(drawBorder){
						g.setColor(Color.BLACK);
						g.drawPolygon(xPoints, yPoints, nPoints);
					}
				}
			}
		}

//		System.out.println("end update");
		raster = new OMRaster(0, 0, bi);		
		graphics.clear();
		graphics.add(raster);
//		System.out.println("UPDATING coverage layer");

		doPrepare();
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
					this.calc = project.getCoverageCalculator();
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
		if(event.getEvent() == AisEvent.Event.BS_VISIBILITY_CHANGED){
			updateDelay = 1;
			updateOnce = true;
		}else if(event.getEvent() == AisEvent.Event.ANALYSIS_STARTED){
			updateOnce = true;
		}else if(event.getEvent() == AisEvent.Event.ANALYSIS_STOPPED){
			updateOnce = false;
		}else if(event.getEvent() == Event.PROJECT_CREATED){
			reset();
		}else if(event.getEvent() == Event.PROJECT_LOADED){
			updateOnce();
		}
	}
	
	@Override
	public void projectionChanged(ProjectionEvent e){
		super.projectionChanged(e);
		updateOnce = true;
		updateDelay = 1;
	}
	
	private void reset() {
		graphics.clear();
		updateOnce = true;
		updateDelay = 1;
		
	}
	
	public void setDrawBorder(boolean drawBorder) {
		this.drawBorder = drawBorder;
	}

}
