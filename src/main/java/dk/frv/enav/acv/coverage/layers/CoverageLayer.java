package dk.frv.enav.acv.coverage.layers;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.aiscoverage.GlobalSettings;
import dk.dma.aiscoverage.MessageHandler;
import dk.dma.aiscoverage.calculator.AbstractCalculator;
import dk.dma.aiscoverage.calculator.CellChangedListener;
import dk.dma.aiscoverage.calculator.CoverageCalculator;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.BaseStation;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.dma.aiscoverage.project.ProjectHandler;
import dk.dma.aiscoverage.project.ProjectHandlerListener;
import dk.frv.enav.acv.event.AisEvent;
import dk.frv.enav.acv.event.AisEvent.Event;


public class CoverageLayer extends OMGraphicHandlerLayer implements Runnable, ProjectHandlerListener {

	private CoverageCalculator calc;
	private static final long serialVersionUID = 1L;
	private OMGraphicList graphics = new OMGraphicList();
	public boolean isRunning = false;
	HashMap<String, GridPolygon> cellMap = new HashMap<String, GridPolygon>();
	private int updateDelay;
	private final int defaultUpdatedelay = 100;
	private boolean updateOnce = true;

	public void updateOnce() {
		updateOnce = true;
	}
	public CoverageLayer(){
		setRenderPolicy(new com.bbn.openmap.layer.policy.BufferedImageRenderPolicy());
		new Thread(this).start();
		ProjectHandler.getInstance().addProjectHandlerListener(this);
		
		
	}
	private void updateCell(Cell cell){
		double longSize = calc.getLongSize();
		double latSize = calc.getLatSize();
		List<LatLonPoint> polygon = new ArrayList<LatLonPoint>();

		polygon.add(new LatLonPoint.Double(cell.latitude, cell.longitude));
		polygon.add(new LatLonPoint.Double(cell.latitude, cell.longitude + longSize));
		polygon.add(new LatLonPoint.Double(cell.latitude + latSize, cell.longitude + longSize));
		polygon.add(new LatLonPoint.Double(cell.latitude + latSize, cell.longitude));

		Color color;
		if (cell.getCoverage() > 0.8) { // green
			color = Color.GREEN;
		} else if (cell.getCoverage() > 0.5) { // orange
			color = Color.ORANGE;
		} else { // red
			color = Color.RED;
		}
		GridPolygon g = new GridPolygon(polygon, color);
		graphics.add(g);
	}

	public void doUpdate() {
		
		if(calc == null)
			return;
		
		Collection<Cell> cells = calc.getCoverage();
		if(cells == null) return;
		
		System.out.println("UPDATING coverage layer");

		graphics.clear();
		
		for (Cell cell : cells) {
			updateCell(cell);
		}
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
						if(updateOnce || (ProjectHandler.getInstance().getProject().isRunning() && isVisible()) ){
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
		}else if(event.getEvent() == AisEvent.Event.ANALYSIS_STARTED){
			updateOnce = true;
		}else if(event.getEvent() == AisEvent.Event.ANALYSIS_STOPPED){
			updateOnce = false;
		}else if(event.getEvent() == Event.PROJECT_CREATED){
			reset();
		}
	
	}
	private void reset() {
		cellMap.clear();
		graphics.clear();
		updateOnce = true;
		updateDelay = 1;
		
	}

}
