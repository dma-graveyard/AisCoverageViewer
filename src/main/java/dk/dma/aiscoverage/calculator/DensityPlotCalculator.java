package dk.dma.aiscoverage.calculator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dk.dma.aiscoverage.data.BaseStation;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.CustomMessage;
import dk.dma.aiscoverage.data.Ship;
import dk.dma.aiscoverage.data.BaseStation.ReceiverType;
import dk.dma.aiscoverage.data.Ship.ShipClass;
import dk.dma.aiscoverage.geotools.GeoConverter;
import dk.dma.aiscoverage.geotools.SphereProjection;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.dma.aiscoverage.project.ProjectHandler;
import dk.frv.ais.country.Country;
import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisMessage4;
import dk.frv.ais.message.AisMessage5;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.message.IGeneralPositionMessage;
import dk.frv.ais.message.ShipTypeCargo;
import dk.frv.ais.proprietary.IProprietarySourceTag;
import dk.frv.enav.acv.event.AisEvent;

public class DensityPlotCalculator extends AbstractCalculator {

	
	private BaseStation basestation;
	private Cell minShips = null;
	private Cell maxShips = null;
	private CustomMessage firstMessage = null;
	private CustomMessage currentMessage = null;
	


	public CustomMessage getCurrentMessage() {
		return currentMessage;
	}


	public void setCurrentMessage(CustomMessage currentMessage) {
		this.currentMessage = currentMessage;
	}


	public CustomMessage getFirstMessage() {
		return firstMessage;
	}


	public void setFirstMessage(CustomMessage firstMessage) {
		this.firstMessage = firstMessage;
	}


	public Cell getMinShips() {
		return minShips;
	}


	public void setMinShips(Cell minShips) {
		this.minShips = minShips;
	}


	public Cell getMaxShips() {
		return maxShips;
	}


	public void setMaxShips(Cell maxShips) {
		this.maxShips = maxShips;
	}


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
		CustomMessage lastMessage = message.ship.getLastMessage();
		if(lastMessage == null){
			message.ship.setLastMessage(message);
			return;
		}
		
//		double x0 = this.projection.lon2x(11, 55);
//		double y0 = this.projection.lat2y(11, 55);
//		double x1 = this.projection.lon2x(12, 55);
//		double y1 = this.projection.lat2y(12, 55);
//		
		double x0 = this.projection.lon2x(lastMessage.longitude, lastMessage.latitude);
		double y0 = this.projection.lat2y(lastMessage.longitude, lastMessage.latitude);
		double x1 = this.projection.lon2x(message.longitude, message.latitude);
		double y1 = this.projection.lat2y(message.longitude, message.latitude);
		Ship ship = message.ship;
		
		if(!filterMessage(message)){
			calculateLine(x0, y0, x1, y1, ship);
		}
		
		message.ship.setLastMessage(message);

		
		
	}
	
	public boolean filterMessage(CustomMessage message){
		boolean filterMessage = false;
		CustomMessage lastMessage = message.ship.getLastMessage();
//		System.out.println(message.latitude);
//		System.out.println(lastMessage.latitude);
//		System.out.println();
		double distance = projection.distBetweenPoints(message.longitude, message.latitude, lastMessage.longitude, lastMessage.latitude);
//		System.out.println(distance);
		if(distance > 2000)
			filterMessage = true;
		
		return filterMessage;
	}
	private void addToShipToCell(Cell c, Ship s){
//		System.out.println(c.id);
		c.ships.put(s.mmsi, s);
		basestation.messageCount++;
		
		// If ship is not in same cell anymore, we increment shipcount for  cell
		if(c.shipCount == 0)
			c.shipCount++;
		else if(s.getLastCell() == null)
			c.shipCount++;
		else if(s.getLastCell() != c)
			c.shipCount++;
		
		s.setLastCell(c);
		int numberOfShips = c.shipCount;
		if(minShips == null)
			minShips = c;
		if(maxShips == null)
			maxShips = c;
		if(minShips.shipCount > numberOfShips)
			minShips = c;
		
		if(maxShips.shipCount < numberOfShips)
			maxShips = c;
	}
	/*
	 * Bresenham's line algorithm
	 */
	public void calculateLine(double x0, double y0, double x1, double y1, Ship ship){
//		System.out.println("yeah");
		boolean steep = false;
		if(Math.abs(y1 - y0) > Math.abs(x1 - x0))
			steep = true;
			
		if(steep){
			//swap x0, y0
			double newX0 = y0;
			double newY0 = x0;
			x0 = newX0;
			y0 = newY0;
			//swap x1, y1
			double newX1 = y1;
			double newY1 = x1;
			x1 = newX1;
			y1 = newY1;
		}
		if(x0 > x1){
			//swap x0, x1
			double newX0 = x1;
			double newX1 = x0;
			x0 = newX0;
			x1 = newX1;
			//swap y0, y1
			double newY0 = y1;
			double newY1 = y0;
			y0 = newY0;
			y1 = newY1;
		}		     
		double deltax = x1 - x0;		     
		double deltay = Math.abs(y1 - y0);		     
		double error = 0;		     
		double deltaerr = deltay / deltax;		     
		double ystep;   
		double y = y0;
		if(y0 < y1)
			ystep = getCellSize();
		else
			ystep = -1*getCellSize();
			 
		for (double x = x0; x < x1; x += getCellSize()) {
			double lon;
			double lat;
			if(steep){
				lon = projection.x2Lon(y, x);
				lat = projection.y2Lat(y, x);
			}else{
				lon = projection.x2Lon(x, y);
				lat = projection.y2Lat(x, y);
			}
			Cell cell = basestation.getCell(lat, lon);
			if(cell == null){
				cell = basestation.createCell(lat, lon);
			}
			addToShipToCell(cell, ship);
			error +=deltaerr;
			if(error >= getCellSize()/2){
				y += ystep;
				error = error - getCellSize();
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


	
	/*
	 *
	 * Number of ship in each cell can be used to draw density plot
	 */
	public Collection<Cell> getDensityPlotCoverage() {
		if(basestation == null) return null;
		return basestation.grid.values();
	}
	


	/*
	 * In this calculator, we always use the same base station
	 */
	@Override
	public void processMessage(AisMessage aisMessage, String defaultID) {
		
		CustomMessage newMessage = aisToCustom(aisMessage, defaultID);
		if(newMessage != null){
			calculateCoverage(newMessage);
		}
		
	}
	
	@Override
	public CustomMessage aisToCustom(AisMessage aisMessage, String defaultID){
		long timeSinceStart = project.getRunningTime();
		if (project.getTimeout() != -1 && timeSinceStart > project.getTimeout())
			project.stopAnalysis();

		String identifier = null;
		ReceiverType receiverType = ReceiverType.NOTDEFINED;
		IGeneralPositionMessage posMessage = null;
		GeoLocation pos = null;
		Date timestamp = null;
		Country srcCountry = null;
		ShipClass shipClass = null;


		// Get source tag properties
		IProprietarySourceTag sourceTag = aisMessage.getSourceTag();
		if (sourceTag != null) {
			Long bsmmsi = sourceTag.getBaseMmsi();
			timestamp = sourceTag.getTimestamp();
			srcCountry = sourceTag.getCountry();
			String region = sourceTag.getRegion();
			if(bsmmsi == null){
				if(!region.equals("")){
					identifier = region;
					receiverType = ReceiverType.REGION;
				}
			}else{
				identifier = bsmmsi+"";
				receiverType = ReceiverType.BASESTATION;
			}
		}

		//Checks if its neither a basestation nor a region
		if (identifier == null){
			identifier = defaultID;
		}
		
		// If time stamp is not present, we add one
		if(timestamp == null){
			timestamp = new Date();
		}
		

		// It's a base station
		if (aisMessage instanceof AisMessage4) {
			return null;
		}
		
		// if no allowed ship types has been set, we process all ship types
		if(allowedShipTypes.size() > 0){

			// Ship type message
			if(aisMessage instanceof AisMessage5){

				//if ship type is allowed, we add ship mmsi to allowedShips map
				AisMessage5 m = (AisMessage5) aisMessage;
				ShipTypeCargo shipTypeCargo = new ShipTypeCargo(m.getShipType());
				if(allowedShipTypes.containsKey(shipTypeCargo.getShipType())){
					allowedShips.put(m.getUserId(), true);
				}

				return null;

			}	

			// if ship isn't in allowedShips we don't process the message
			if(!allowedShips.containsKey(aisMessage.getUserId()) ){
				return null;
			}
		}

		// Handle position messages
		if (aisMessage instanceof IGeneralPositionMessage) {
			posMessage = (IGeneralPositionMessage) aisMessage;
		} else {
			return null;
		}
		
		if (aisMessage.getMsgId() == 18) {
			// class B
			shipClass = Ship.ShipClass.CLASS_B;
		} else {
			// class A
			shipClass = Ship.ShipClass.CLASS_A;
		}
		if(!allowedShipClasses.containsKey(shipClass))
			return null;

		// Validate postion
		if (!posMessage.isPositionValid()) {
			return null;
		}

		// Get location
		pos = posMessage.getPos().getGeoLocation();
		
		//calculate lat lon size based on first message
		if(getLatSize() == -1){
			double cellInMeters= getCellSize(); //cell size in meters
			setLatSize(GeoConverter.metersToLatDegree(cellInMeters));
			setLongSize(GeoConverter.metersToLonDegree(pos.getLatitude(), cellInMeters));
							
			basestation = getBaseStationHandler().createGrid("density");
		}


		// Check if grid exists (If a message with that bsmmsi has been received
		// before)
		// Otherwise create a grid for corresponding base station
		BaseStation grid = gridHandler.getGrid(identifier);
		if (grid == null) {
			grid = gridHandler.createGrid(identifier);
			grid.setReceiverType(receiverType);
		}

		// Check which ship sent the message.
		// If it's the first message from that ship, create ship and put it in
		// grid belonging to bsmmsi
		Ship ship = grid.getShip(aisMessage.getUserId());
		if (ship == null) {
			grid.createShip(aisMessage.getUserId(), shipClass);
			ship = grid.getShip(aisMessage.getUserId());
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
		if(firstMessage == null)
			firstMessage = newMessage;
		currentMessage = newMessage;
		
		return newMessage;
	}

}
