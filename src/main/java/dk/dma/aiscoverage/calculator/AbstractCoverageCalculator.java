package dk.dma.aiscoverage.calculator;

import java.util.ArrayList;
import java.util.List;

import dk.dma.aiscoverage.CustomMessage;
import dk.dma.aiscoverage.data.Cell;

public abstract class AbstractCoverageCalculator {

	private List<CellChangedListener> listeners = new ArrayList<CellChangedListener>();
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
		boolean filterMessage = false;
		if(customMessage.message.getSog()/10 < 3 || customMessage.message.getSog()/10 > 50)
			filterMessage = true;
		if(customMessage.message.getCog() == 360)
			filterMessage = true;
		
		//if this is the first message for a ship, we don't calculate coverage
		if(customMessage.ship.getLastMessage() == null) {
			filterMessage = true;
		}else{

			//If time since last message is > 30 minutes, we filter
//			if(customMessage.timeSinceLastMsg > 1800)
//				filterMessage = true;
//				
			}
		
		return filterMessage;
	}
	protected void cellChanged(Cell cell){
		for (CellChangedListener listener : listeners) {
			listener.cellChanged(cell);
		}
	}
	public void addCellChangedListener(CellChangedListener listener){
		listeners.add(listener);
	}
}
