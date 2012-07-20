package dk.dma.aiscoverage.calculator;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import dk.dma.aiscoverage.data.BaseStation;
import dk.dma.aiscoverage.data.BaseStationHandler;
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

public class CoverageCalculator extends AbstractCoverageCalculator {

	private SphereProjection projection = new SphereProjection();
	private int bufferInSeconds = 20;
	private int degreesPerMinute = 20;
	private boolean ignoreRotation = true;
	
	public CoverageCalculator(AisCoverageProject project, boolean ignoreRotation){
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
		
		
		//put message in ships' buffer
		message.ship.addToBuffer(message);
		
		
		
		// If this message is filtered, we empty the ships' buffer and returns
		if( filterMessage(message) ){
			message.ship.emptyBuffer();
			return;
		}
		
		
		
		//Time difference between first and last message in buffer
		CustomMessage firstMessage = message.ship.getMessages().peekFirst();
		CustomMessage lastMessage = message.ship.getMessages().peekLast();
		double timeDifference = this.getTimeDifference(firstMessage, lastMessage);
		
		//
		if(timeDifference >= bufferInSeconds){
			
			if(timeDifference < 1800){
				LinkedList<CustomMessage> buffer = message.ship.getMessages();
				double rotation = Math.abs( angleDiff((double)firstMessage.cog, (double)lastMessage.cog) );
				
				//Ship is rotating
				if(rotation > ((double)degreesPerMinute/60)*timeDifference){
					if(!ignoreRotation){
						for (int i = 0; i < message.ship.getMessages().size()-1; i++) {
							calculateMissingPoints(buffer.get(i), buffer.get(i+1), true);
						}
					}
				}
				else{
					for (int i = 0; i < message.ship.getMessages().size()-1; i++) {
						calculateMissingPoints(buffer.get(i), buffer.get(i+1), false);
					}
				}
			}
			
			//empty buffer
			message.ship.emptyBuffer();
		}
		
	}
	
	/*
	 * Calculates missing points between two messages and add them to corresponding cells
	 */
	private void calculateMissingPoints(CustomMessage m1, CustomMessage m2, boolean rotating){
		
		//WHERE TO PUT THIS??
		Cell cell = m1.grid.getCell(m1.latitude, m1.longitude);
		if(cell == null){
			cell = m1.grid.createCell(m1.latitude, m1.longitude);
		}
		cell.ships.put(m1.ship.mmsi, m1.ship);
		m1.grid.messageCount++;
		cell.NOofReceivedSignals++;
		this.cellChanged(cell);
		
		Long p1Time = m1.timestamp.getTime();
		Long p2Time = m2.timestamp.getTime();
		double p1Lat = m1.latitude;
		double p1Lon = m1.longitude;
		double p2Lat = m2.latitude;
		double p2Lon = m2.longitude;
		double p1X = projection.lon2x(p1Lon, p1Lat);
		double p1Y = projection.lat2y(p1Lon, p1Lat);
		double p2X = projection.lon2x(p2Lon, p2Lat);
		double p2Y = projection.lat2y(p2Lon, p2Lat);
		
		double timeSinceLastMessage = getTimeDifference(p1Time, p2Time);
		int sog = (int) m2.sog;
		double expectedTransmittingFrequency = getExpectedTransmittingFrequency(sog, rotating);
		
		// Calculate missing messages
		// A Parametric equation is used to find missing points' lat-lon coordinates between point1 and point2.
		// These points are not converted to metric x-y coordinates before calculating missing points.
		int missingMessages; 
		if(timeSinceLastMessage > expectedTransmittingFrequency) {

			// Number of missing points between the two points
			missingMessages = (int) (Math.round((double)timeSinceLastMessage/(double)expectedTransmittingFrequency)-1);

			// Finds lat/lon of each missing point and adds "missing signal" to corresponding cell
			for (int i = 1; i <= missingMessages; i++) {

				double xMissing = getX((i*expectedTransmittingFrequency),p1Time, p2Time, p1X, p2X);
				double yMissing = getY(i*expectedTransmittingFrequency,p1Time, p2Time, p1Y, p2Y);

				//Add number of missing messages to cell
				Cell c = m2.grid.getCell(projection.y2Lat(xMissing, yMissing), projection.x2Lon(xMissing, yMissing));
				if(c == null){
					c = m2.grid.createCell(projection.y2Lat(xMissing, yMissing), projection.x2Lon(xMissing, yMissing));
				}
				c.ships.put(m1.ship.mmsi, m1.ship);
				c.NOofMissingSignals++;
				this.cellChanged(c);
			}

		}
		
		
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

	
	
	private double getY(double seconds, Long p1Time, Long p2Time, double p1y, double p2y){
		double distanceInMeters = p2y-p1y;
		double timeDiff = getTimeDifference(p1Time, p2Time);
		double metersPerSec = distanceInMeters/timeDiff;
		return p1y + (metersPerSec * seconds);
	}
	private double getX(double seconds, Long p1Time, Long p2Time, double p1x, double p2x){
		double distanceInMeters = p2x-p1x;
		double timeDiff = getTimeDifference(p1Time, p2Time);
		double metersPerSec = distanceInMeters/timeDiff;
		return p1x + (metersPerSec * seconds);
	}
	
	/*
	 * Consider optimizing?
	 *
	 * Returns a combined coverage of cells from selected base stations.
	 * If two base stations cover same area, the best coverage is chosen.
	 */
	public Collection<Cell> getCoverage() {
		HashMap<String, Cell> cells = new HashMap<String, Cell>();
		//For each base station
		Collection<BaseStation> basestations = gridHandler.grids.values();
		for (BaseStation basestation : basestations) {

			if(basestation.isVisible()){
				//For each cell
				Collection<Cell> bscells = basestation.grid.values();
				for (Cell cell : bscells) {
					Cell existing = cells.get(cell.id);
					if(existing == null)
						cells.put(cell.id, cell);
					else
						if(cell.getCoverage() > existing.getCoverage())
							cells.put(cell.id, cell);
				}
			}
			
		}
		return cells.values();
	}
	
	
	public int getBufferInSeconds() {
		return bufferInSeconds;
	}
	public void setBufferInSeconds(int bufferInSeconds) {
		this.bufferInSeconds = bufferInSeconds;
	}
	public int getDegreesPerMinute() {
		return degreesPerMinute;
	}
	public void setDegreesPerMinute(int degreesPerMinute) {
		this.degreesPerMinute = degreesPerMinute;
	}
	public boolean isIgnoreRotation() {
		return ignoreRotation;
	}
	public void setIgnoreRotation(boolean ignoreRotation) {
		this.ignoreRotation = ignoreRotation;
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
		}


		// Check if grid exists (If a message with that bsmmsi has been received
		// before)
		// Otherwise create a grid for corresponding base station
		BaseStation grid = gridHandler.getGrid(bsMmsi);
		if (grid == null) {
			gridHandler.createGrid(bsMmsi);
			grid = gridHandler.getGrid(bsMmsi);
		}

		// Check which ship sent the message.
		// If it's the first message from that ship, create ship and put it in
		// grid belonging to bsmmsi
		Ship ship = grid.getShip(posMessage.getUserId());
		if (ship == null) {
			grid.createShip(posMessage.getUserId());
			ship = grid.getShip(posMessage.getUserId());
		}

		CustomMessage newMessage = new CustomMessage();
		newMessage.cog = (double) posMessage.getCog() / 10;
		newMessage.sog = (double) posMessage.getSog() / 10;
		newMessage.latitude = posMessage.getPos().getGeoLocation()
				.getLatitude();
		newMessage.longitude = posMessage.getPos().getGeoLocation()
				.getLongitude();
		newMessage.timestamp = timestamp;
		newMessage.grid = grid;
		newMessage.ship = ship;
		
		this.calculateCoverage(newMessage);
		
	}

}
