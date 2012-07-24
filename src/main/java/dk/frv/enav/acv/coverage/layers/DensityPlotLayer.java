package dk.frv.enav.acv.coverage.layers;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMRect;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.aiscoverage.GlobalSettings;
import dk.dma.aiscoverage.MessageHandler;
import dk.dma.aiscoverage.calculator.AbstractCalculator;
import dk.dma.aiscoverage.calculator.CellChangedListener;
import dk.dma.aiscoverage.calculator.CoverageCalculator;
import dk.dma.aiscoverage.calculator.DensityPlotCalculator;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.BaseStation;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.dma.aiscoverage.project.ProjectHandler;
import dk.dma.aiscoverage.project.ProjectHandlerListener;
import dk.frv.enav.acv.event.AisEvent;


public class DensityPlotLayer extends OMGraphicHandlerLayer implements Runnable, ProjectHandlerListener {

	private DensityPlotCalculator calc;
	private static final long serialVersionUID = 1L;
	public boolean isRunning = false;
	HashMap<String, Color> cellColor = new HashMap<String, Color>(); 
	HashMap<String, OMRect> addedCell = new HashMap<String, OMRect>(); 
	private OMGraphicList graphicslist = new OMGraphicList();
	private Color[] colors = new Color[10];
	private int updateDelay;
	private final int defaultUpdatedelay = 100;
	private boolean updateOnce = true;

	

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
	
	public Color getColor(Cell c){
		try{

			double seconds = calc.getTimeDifference(calc.getFirstMessage().timestamp.getTime(), calc.getCurrentMessage().timestamp.getTime());
			int shipsPerDay = (int) ((double)c.shipCount/seconds*86400);

			Color color;
			if(shipsPerDay < 2)
				color = colors[0];
			else if(shipsPerDay < 3)
				color = colors[1];
			else if(shipsPerDay < 4)
				color = colors[2];
			else if(shipsPerDay < 5)
				color = colors[3];
			else if(shipsPerDay < 6)
				color = colors[4];
			else if(shipsPerDay < 8)
				color = colors[5];
			else if(shipsPerDay < 10)
				color = colors[6];
			else if(shipsPerDay < 15)
				color = colors[7];
			else if(shipsPerDay < 30)
				color = colors[8];
			else
				color = colors[9];
			return color;
		}catch(Exception e){
			System.out.println("exception");
			return Color.WHITE;
		}
		
	}

	private void updateCell(Cell cell){
		double longSize = calc.getLongSize();
		double latSize = calc.getLatSize();
		
		OMRect rect = new OMRect(cell.latitude, cell.longitude, cell.latitude + latSize, cell.longitude + longSize, OMGraphic.LINETYPE_STRAIGHT);
		
		Color color = this.getColor(cell);
		
		
		rect.setFillColor(color);
		rect.setLineColor(color);
		graphicslist.add(rect);
		cellColor.put(cell.id, color);
		this.addedCell.put(cell.id, rect);
		
		
	}
	private OMRect createRect(Cell cell, Color color){
		double longSize = calc.getLongSize();
		double latSize = calc.getLatSize();
		OMRect rect = new OMRect(cell.latitude, cell.longitude, cell.latitude + latSize, cell.longitude + longSize, OMGraphic.LINETYPE_STRAIGHT);
		rect.setFillColor(color);
		rect.setLineColor(color);
		graphicslist.add(rect);
		cellColor.put(cell.id, color);
		this.addedCell.put(cell.id, rect);
		return rect;
	}

	public void doUpdate() {
		System.out.println("UPDATING Density Plot");
//		System.out.println("max ships: "+calc.getMaxShips().shipCount);
//		System.out.println("min ships: "+calc.getMinShips().shipCount);


		Collection<Cell> cells = calc.getDensityPlotCoverage();
		if(cells== null) return;
		for (Cell cell : cells) {
			
			if(!addedCell.containsKey(cell.id)){
				updateCell(cell);
			}else{
				
				Color existingColor = cellColor.get(cell.id);
				Color newColor = this.getColor(cell);
				if(existingColor != newColor){
					OMRect rect = this.addedCell.get(cell.id);
					cellColor.put(cell.id, newColor);
					rect.setFillColor(newColor);
					rect.setLineColor(newColor);
				}
			}
		}

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
		graphicslist.project(getProjection());
		return graphicslist;
	}
	public void reset() {
		graphicslist.clear();
		this.addedCell.clear();
		this.cellColor.clear();
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
		if(event.getEvent() == AisEvent.Event.ANALYSIS_STOPPED){
			updateOnce = false;
		}
	}

	public void updateOnce() {
		this.updateOnce = true;
	}

}
