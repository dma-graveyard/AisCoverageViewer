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
import dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced3;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.BaseStation;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.dma.aiscoverage.project.ProjectHandler;


public class DensityPlotLayer extends OMGraphicHandlerLayer {

	private static final long serialVersionUID = 1L;
	private OMGraphicList graphics = new OMGraphicList();
	public boolean isRunning = false;
	HashMap<String, GridPolygon> cellMap = new HashMap<String, GridPolygon>(); 

	public DensityPlotLayer(){
//		setRenderPolicy(new com.bbn.openmap.layer.policy.BufferedImageRenderPolicy());
	}
	private void updateCell(Cell cell){
		double longSize = ProjectHandler.getInstance().getProject().getLongSize();
		double latSize = ProjectHandler.getInstance().getProject().getLatSize();
		List<LatLonPoint> polygon = new ArrayList<LatLonPoint>();

		polygon.add(new LatLonPoint.Double(cell.latitude, cell.longitude));
		polygon.add(new LatLonPoint.Double(cell.latitude, cell.longitude + longSize));
		polygon.add(new LatLonPoint.Double(cell.latitude + latSize, cell.longitude + longSize));
		polygon.add(new LatLonPoint.Double(cell.latitude + latSize, cell.longitude));

		GridPolygon g = new GridPolygon(polygon, Color.BLACK);
		graphics.add(g);
	}

	public void doUpdate(Collection<Cell> cells) {
		System.out.println("density update");
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

}
