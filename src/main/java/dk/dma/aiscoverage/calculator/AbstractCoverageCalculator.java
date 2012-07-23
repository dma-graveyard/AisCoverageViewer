package dk.dma.aiscoverage.calculator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dk.dma.aiscoverage.data.BaseStationHandler;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.CustomMessage;
import dk.dma.aiscoverage.geotools.SphereProjection;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.frv.ais.message.AisMessage;

public abstract class AbstractCoverageCalculator implements Serializable {

	transient protected SphereProjection projection = new SphereProjection();
	transient private List<CellChangedListener> listeners = new ArrayList<CellChangedListener>();
	protected BaseStationHandler gridHandler = new BaseStationHandler();
	private double latSize = -1;
	private double longSize = -1;
	private int cellSize = 2500;
	protected AisCoverageProject project;
	
	public AbstractCoverageCalculator(AisCoverageProject project){
		this.project = project;
	}
	abstract public void processMessage(AisMessage message, String defaultID);
	abstract public void calculateCoverage(CustomMessage message);
	
	/*
	 * Time difference between two messages in seconds
	 */
	public double getTimeDifference(CustomMessage m1, CustomMessage m2){
		return (double) ((m2.timestamp.getTime() - m1.timestamp.getTime()) / 1000);
	}
	public double getTimeDifference(Long m1, Long m2){
		return  ((double)(m2 - m1) / 1000);
	}
	public double getExpectedTransmittingFrequency(int sog, boolean rotating){
		double expectedTransmittingFrequency;
		//Determine expected transmitting frequency
		if(rotating){
			if(sog < 14)
				expectedTransmittingFrequency = 3.33;
			else if(sog < 23)
				expectedTransmittingFrequency = 2;
			else 
				expectedTransmittingFrequency = 2;
		}else{
			
			if(sog < 14)
				expectedTransmittingFrequency = 10;
			else if(sog < 23)
				expectedTransmittingFrequency = 6;
			else 
				expectedTransmittingFrequency = 2;
		}
		
		return expectedTransmittingFrequency;
	}
	
	public boolean filterMessage(CustomMessage customMessage){

		if(customMessage.sog < 3 || customMessage.sog > 50)
			return true;
		if(customMessage.cog == 360)
			return true;

		CustomMessage lastMessage = customMessage.ship.getLastMessage();
		if(lastMessage != null){
			double distance = projection.distBetweenPoints(customMessage.longitude, customMessage.latitude, lastMessage.longitude, lastMessage.latitude);
			if(distance > 2000)
				return true;
		}
		return false;
		
	}
	protected void cellChanged(Cell cell){
		for (CellChangedListener listener : listeners) {
			listener.cellChanged(cell);
		}
	}
	public void addCellChangedListener(CellChangedListener listener){
		listeners.add(listener);
	}
	
	public double getLatSize() {
		return latSize;
	}
	public void setLatSize(double latSize) {
		this.latSize = latSize;
		gridHandler.setLatSize(latSize);
	}
	public double getLongSize() {
		return longSize;
	}
	public void setLongSize(double longSize) {
		this.longSize = longSize;
		gridHandler.setLonSize(longSize);
	}
	public BaseStationHandler getBaseStationHandler(){
		return gridHandler;
	}
	public String[] getBaseStationNames(){
		Set<String> set = gridHandler.grids.keySet();
		String[] bssmsis = new String[set.size()];
		int i = 0;
		for (String s : set) {
			bssmsis[i] = s;
			i++;
		}
		
		return bssmsis;
	}
	public int getCellSize() {
		return cellSize;
	}
	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}
}
