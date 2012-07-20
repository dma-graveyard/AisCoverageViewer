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
import dk.dma.aiscoverage.calculator.AbstractCoverageCalculator;
import dk.dma.aiscoverage.calculator.CellChangedListener;
import dk.dma.aiscoverage.calculator.CoverageCalculator;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.BaseStation;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.dma.aiscoverage.project.ProjectHandler;


public class CoverageLayer extends OMGraphicHandlerLayer {

	private CoverageCalculator calc;
	private static final long serialVersionUID = 1L;
	private OMGraphicList graphics = new OMGraphicList();
	public boolean isRunning = false;
	HashMap<String, GridPolygon> cellMap = new HashMap<String, GridPolygon>(); 

	public CoverageLayer(){
		setRenderPolicy(new com.bbn.openmap.layer.policy.BufferedImageRenderPolicy());
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

	public void doUpdate(CoverageCalculator calc) {
		this.calc = calc;
		Collection<Cell> cells = calc.getCoverage();
		if(cells == null) return;
		
		graphics.clear();
		
		for (Cell cell : cells) {
			updateCell(cell);
		}
		System.out.println("start update");
		doPrepare();
		System.out.println("update ended");
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

}
