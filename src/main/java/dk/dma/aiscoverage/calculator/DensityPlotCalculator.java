package dk.dma.aiscoverage.calculator;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import dk.dma.aiscoverage.data.BaseStation;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.CustomMessage;
import dk.dma.aiscoverage.data.Ship;
import dk.dma.aiscoverage.geotools.GeoConverter;
import dk.dma.aiscoverage.geotools.SphereProjection;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.frv.ais.country.Country;
import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisMessage4;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.proprietary.IProprietarySourceTag;

public class DensityPlotCalculator extends AbstractCoverageCalculator {

	transient private SphereProjection projection = new SphereProjection();
	private BaseStation basestation;
	
	public DensityPlotCalculator(AisCoverageProject project, boolean ignoreRotation){
		super(project);

	}
	
	
	/*
	 * This calculator maintains a buffer for each ship. Rotation is determined based on 
	 * difference between cog in first and last message in buffer.
	 * If rotation is ignored, missing points will only be calculated for ships that are NOT rotating.
	 * 
	 */
	@Override
	public void calculateCoverage(CustomMessage message) {
				
		Cell cell = basestation.getCell(message.latitude, message.longitude);
		if(cell == null){
			cell = basestation.createCell(message.latitude, message.longitude);
		}
		cell.ships.put(message.ship.mmsi, message.ship);
		basestation.messageCount++;
		cell.NOofReceivedSignals++;
		
	}
	
	/*
	 * Calculates missing points between two messages
	 */
	private void calculateMissingPoints(CustomMessage m1, CustomMessage m2, boolean rotating){
	

	}
	
	/**Calculates the signed difference between angle A and angle B
    *
     * @param a Angle1 in degrees
    * @param b Angle2 in degrees
    * @return The difference in degrees
    */
    private double angleDiff(double a, double b) {
        double difference = b - a;
        while (difference < -180.0)
            difference+=360.0;
               
        while (difference > 180.0)
            difference-=360.0;
       
        return difference;
    }


	
	/*
	 *
	 * Number of ship in each cell can be used to draw density plot
	 */
	public Collection<Cell> getDensityPlotCoverage() {
		if(basestation == null) return null;
		return basestation.grid.values();
	}
	


	@Override
	public void processMessage(AisMessage aisMessage, long defaultID) {
		// Check timeout
		Date now = new Date();
		long timeSinceStart = project.getRunningTime();
		if (project.getTimeout() != -1 && timeSinceStart > project.getTimeout())
			project.stopAnalysis();

		AisPositionMessage posMessage = null;
		GeoLocation pos = null;
		Long bsMmsi = null;
		Date timestamp = null;
		Country srcCountry = null;

		// Get source tag properties
		IProprietarySourceTag sourceTag = aisMessage.getSourceTag();
		if (sourceTag != null) {
			bsMmsi = sourceTag.getBaseMmsi();
			timestamp = sourceTag.getTimestamp();
			srcCountry = sourceTag.getCountry();
		}

		// What to do if no bsMmsi or timestamp?
		if (bsMmsi == null || timestamp == null) {
			bsMmsi = defaultID; // determine id
			timestamp = new Date();
		}

		// It's a base station
		if (aisMessage instanceof AisMessage4) {
			AisMessage4 m = (AisMessage4) aisMessage;
			BaseStation b = gridHandler.grids.get(m
					.getUserId());
			if (b != null) {
				b.latitude = m.getPos().getGeoLocation().getLatitude();
				b.longitude = m.getPos().getGeoLocation().getLongitude();
			}
			return;
		}

		// Handle position messages
		if (aisMessage instanceof AisPositionMessage) {
			posMessage = (AisPositionMessage) aisMessage;
		} else {
			return;
		}

		// Validate postion
		if (!posMessage.isPositionValid()) {
			return;
		}

		// Get location
		pos = posMessage.getPos().getGeoLocation();
		
		//calculate lat lon size based on first message
		if(getLatSize() == -1){
			double cellInMeters= getCellSize(); //cell size in meters
			setLatSize(GeoConverter.metersToLatDegree(cellInMeters));
			setLongSize(GeoConverter.metersToLonDegree(pos.getLatitude(), cellInMeters));
			
			basestation = getBaseStationHandler().createGrid(0L);
		}

		// Check which ship sent the message.
		// If it's the first message from that ship, create ship and put it in
		// grid belonging to bsmmsi
		Ship ship = basestation.getShip(posMessage.getUserId());
		if (ship == null) {
			basestation.createShip(posMessage.getUserId());
			ship = basestation.getShip(posMessage.getUserId());
		}

		CustomMessage newMessage = new CustomMessage();
		newMessage.cog = (double) posMessage.getCog() / 10;
		newMessage.sog = (double) posMessage.getSog() / 10;
		newMessage.latitude = posMessage.getPos().getGeoLocation()
				.getLatitude();
		newMessage.longitude = posMessage.getPos().getGeoLocation()
				.getLongitude();
		newMessage.timestamp = timestamp;
		newMessage.grid = basestation;
		newMessage.ship = ship;
		
		this.calculateCoverage(newMessage);
		
	}

}
