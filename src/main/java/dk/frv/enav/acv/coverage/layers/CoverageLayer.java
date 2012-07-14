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


public class CoverageLayer extends OMGraphicHandlerLayer {

	private static final long serialVersionUID = 1L;
	private OMGraphicList graphics = new OMGraphicList();
	public boolean isRunning = false;
	private AisCoverageProject project;

	private void updateCell(Cell cell){
		double longSize = ProjectHandler.getInstance().getProject().getLongSize();
		double latSize = ProjectHandler.getInstance().getProject().getLatSize();
		List<LatLonPoint> polygon = new ArrayList<LatLonPoint>();
		//LatLonPoint.Double parameters are swapped
		polygon.add(new LatLonPoint.Double(cell.longitude, cell.latitude));
		polygon.add(new LatLonPoint.Double(cell.longitude + longSize, cell.latitude));
		polygon.add(new LatLonPoint.Double(cell.longitude + longSize, cell.latitude + latSize));
		polygon.add(new LatLonPoint.Double(cell.longitude, cell.latitude + latSize));

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

	public void doUpdate(Collection<Cell> cells) {
		graphics.clear();
		System.out.println("update");
		for (Cell cell : cells) {
			updateCell(cell);
		}
		doPrepare();
	}
	
	@Override
	public void findAndInit(Object obj) {
		//This is used in case we need to communicate with other handler objects such as AIS 
		if (obj instanceof AisCoverageProject) {
			project = (AisCoverageProject) obj;
		}
	}

	
	@Override
	public synchronized OMGraphicList prepare() {
		graphics.project(getProjection());
		return graphics;
	}

}
