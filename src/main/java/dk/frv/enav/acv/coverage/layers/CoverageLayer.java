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

import dk.dma.aiscoverage.AisCoverage;
import dk.dma.aiscoverage.GlobalSettings;
import dk.dma.aiscoverage.MessageHandler;
import dk.dma.aiscoverage.calculator.AbstractCoverageCalculator;
import dk.dma.aiscoverage.calculator.CellChangedListener;
import dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced3;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.Grid;
import dk.frv.ais.proprietary.DmaFactory;
import dk.frv.ais.proprietary.GatehouseFactory;
import dk.frv.ais.reader.AisReader;
import dk.frv.ais.reader.AisStreamReader;
import dk.frv.ais.reader.RoundRobinAisTcpReader;
import dk.frv.enav.acv.ACV;

public class CoverageLayer extends OMGraphicHandlerLayer implements CellChangedListener {

	private static final long serialVersionUID = 1L;
	private OMGraphicList graphics = new OMGraphicList();
	public boolean isRunning = false;
	private ConcurrentHashMap<String, Cell> cellsToBeDisplayed = new ConcurrentHashMap<String, Cell>();

	private void updateCell(Cell cell){
		double longSize = GlobalSettings.getInstance().getLonSize();
		double latSize = GlobalSettings.getInstance().getLatSize();
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

	public void doUpdate() {
		graphics.clear();
		System.out.println("update");
		Collection<Cell> cells = cellsToBeDisplayed.values();
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
	public void cellChanged(Cell cell) {
		Cell existing = cellsToBeDisplayed.get(cell.id);
		if(existing == null){
			cellsToBeDisplayed.put(cell.id, cell);
		}
		else{
			if(existing.getCoverage() < cell.getCoverage()){
				cellsToBeDisplayed.put(cell.id, cell);
			}
		}
			
//		System.out.println("update cell");
//		updateCell(cell);		
	}
}
