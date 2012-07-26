package dk.dma.aiscoverage.calculator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import dk.dma.aiscoverage.calculator.geotools.GeoConverter;
import dk.dma.aiscoverage.calculator.geotools.SphereProjection;
import dk.dma.aiscoverage.data.BaseStation;
import dk.dma.aiscoverage.data.BaseStationHandler;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.CustomMessage;
import dk.dma.aiscoverage.data.Ship;
import dk.dma.aiscoverage.data.BaseStation.ReceiverType;
import dk.dma.aiscoverage.data.Ship.ShipClass;
import dk.dma.aiscoverage.event.AisEvent;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.dma.aiscoverage.project.ProjectHandler;
import dk.frv.ais.country.Country;
import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisMessage4;
import dk.frv.ais.message.AisMessage5;
import dk.frv.ais.message.IGeneralPositionMessage;
import dk.frv.ais.message.ShipTypeCargo;
import dk.frv.ais.message.ShipTypeCargo.ShipType;
import dk.frv.ais.proprietary.IProprietarySourceTag;

/*
 * See CoverageCalculator and DensityPlotCalculator for examples of how to extend this class.
 * When a calculator is added to an AisCoverageProject instance, the calculator automatically receives
 * ais messages.
 * 
 */
public abstract class AbstractCalculator implements Serializable {

	transient protected SphereProjection projection = new SphereProjection();
	protected BaseStationHandler gridHandler = new BaseStationHandler(this);
	private double latSize = -1;
	private double longSize = -1;
	private int cellSize = 2500;
	protected AisCoverageProject project;
	protected Map<ShipClass, ShipClass> allowedShipClasses = new ConcurrentHashMap<ShipClass, ShipClass>();
	protected Map<ShipType, ShipType> allowedShipTypes = new ConcurrentHashMap<ShipType, ShipType>();
	protected Map<Long, Boolean> allowedShips = new ConcurrentHashMap<Long, Boolean>();
	

	/*
	 * This is called by message handlers whenever a new message is received.
	 */
	abstract public void processMessage(AisMessage message, String defaultID);
	
	
	
	/*
	 * Determines the expected transmitting frequency, based on speed over ground(sog),
	 * whether the ship is rotating and ship class.
	 * This can be used to calculate coverage.
	 */
	public double getExpectedTransmittingFrequency(double sog, boolean rotating, ShipClass shipClass){
		double expectedTransmittingFrequency;
		if(shipClass == ShipClass.CLASS_A){		
			if(rotating){
				if(sog < .2)
					expectedTransmittingFrequency = 180;
				else if(sog < 14)
					expectedTransmittingFrequency = 3.33;
				else if(sog < 23)
					expectedTransmittingFrequency = 2;
				else 
					expectedTransmittingFrequency = 2;
			}else{
				if(sog < .2)
					expectedTransmittingFrequency = 180;
				else if(sog < 14)
					expectedTransmittingFrequency = 10;
				else if(sog < 23)
					expectedTransmittingFrequency = 6;
				else 
					expectedTransmittingFrequency = 2;
			}
		}
		else{
			if(sog <= 2)
				expectedTransmittingFrequency = 180;
			else
				expectedTransmittingFrequency = 30;
		}
		
		return expectedTransmittingFrequency;
		
	}
	
	/*
	 * Use this method to filter out unwanted messages. 
	 * The filtering is based on rules of thumbs. For instance, if a distance 
	 * between two messages is over 2000m, we filter
	 */
	public boolean filterMessage(CustomMessage customMessage){

		if(customMessage.sog < 3 || customMessage.sog > 50)
			return true;
		if(customMessage.cog == 360)
			return true;

		// check distance from last message to new message
		CustomMessage lastMessage = customMessage.ship.getLastMessage();
		if(lastMessage != null){
			double distance = projection.distBetweenPoints(customMessage.longitude, customMessage.latitude, lastMessage.longitude, lastMessage.latitude);
			if(distance > 2000)
				return true;
		}
		return false;
		
	}
	

	protected void extractBaseStationPosition(AisMessage4 m){
		BaseStation b = gridHandler.grids.get(m.getUserId()+"");
		if (b != null) {
			b.latitude = m.getPos().getGeoLocation().getLatitude();
			b.longitude = m.getPos().getGeoLocation().getLongitude();

			ProjectHandler.getInstance().broadcastEvent(new AisEvent(AisEvent.Event.BS_POSITION_FOUND, this, b));
		}
	}
	protected boolean isShipAllowed(AisMessage aisMessage){
		if(allowedShipTypes.size() > 0){
			
			// Ship type message
			if(aisMessage instanceof AisMessage5){
				
				//if ship type is allowed, we add ship mmsi to allowedShips map
				AisMessage5 m = (AisMessage5) aisMessage;
				ShipTypeCargo shipTypeCargo = new ShipTypeCargo(m.getShipType());
				if(allowedShipTypes.containsKey(shipTypeCargo.getShipType())){
					allowedShips.put(m.getUserId(), true);
				}
				// It's not a position message, so we return false
				return false;

			}	
			
			// if ship isn't in allowedShips we don't process the message
			if(!allowedShips.containsKey(aisMessage.getUserId()) ){
				return false;
			}
		}
		return true;
	}
	protected ShipClass extractShipClass(AisMessage aisMessage){
		if (aisMessage.getMsgId() == 18) {
			// class B
			return Ship.ShipClass.CLASS_B;
		} else {
			// class A
			return Ship.ShipClass.CLASS_A;
		}
	}
	
	/* The aisToCustom method is used to map AisMessages to CustomMessages. It also takes care of creating base station instances,
	 * ship instances and to set up references between these. Override it if you want to handle this in a different way.
	 */
	public CustomMessage aisToCustom(AisMessage aisMessage, String defaultID){
		
		//Stops analysis if project has been running longer than timeout
		long timeSinceStart = project.getRunningTime();
		if (project.getTimeout() != -1 && timeSinceStart > project.getTimeout())
			project.stopAnalysis();

		String baseId = null;
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
					baseId = region;
					receiverType = ReceiverType.REGION;
				}
			}else{
				baseId = bsmmsi+"";
				receiverType = ReceiverType.BASESTATION;
			}
		}

		//Checks if its neither a basestation nor a region
		if (baseId == null){
			baseId = defaultID;
		}
		
		// If time stamp is not present, we add one
		if(timestamp == null){
			timestamp = new Date();
		}
		

		// It's a base station positiion message
		if (aisMessage instanceof AisMessage4) {
			extractBaseStationPosition((AisMessage4) aisMessage);	
			return null;
		}
		
		
		// if no allowed ship types has been set, we process all ship types
		if(!isShipAllowed(aisMessage))
			return null;

		// Handle position messages
		if (aisMessage instanceof IGeneralPositionMessage) {
			posMessage = (IGeneralPositionMessage) aisMessage;
		} else {
			return null;
		}
		
		shipClass = extractShipClass(aisMessage);
		
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
		}


		// Check if grid exists (If a message with that bsmmsi has been received
		// before)
		// Otherwise create a grid for corresponding base station
		BaseStation grid = gridHandler.getGrid(baseId);
		if (grid == null) {
			grid = gridHandler.createGrid(baseId);
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

		return newMessage;
	}
	
	
	
	//getters and setters
	
	
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
	
	/*
	 * Time difference between two messages in seconds
	 */
	public double getTimeDifference(CustomMessage m1, CustomMessage m2){
		return (double) ((m2.timestamp.getTime() - m1.timestamp.getTime()) / 1000);
	}
	public double getTimeDifference(Long m1, Long m2){
		return  ((double)(m2 - m1) / 1000);
	}
	public Map<ShipClass, ShipClass> getAllowedShipClasses() {
		return allowedShipClasses;
	}
	public void setAllowedShipClasses(Map<ShipClass, ShipClass> allowedShipClasses) {
		this.allowedShipClasses = allowedShipClasses;
	}
	public Map<ShipType, ShipType> getAllowedShipTypes() {
		return allowedShipTypes;
	}
	public void setAllowedShipTypes(Map<ShipType, ShipType> allowedShipTypes) {
		this.allowedShipTypes = allowedShipTypes;
	}
	public AbstractCalculator(AisCoverageProject project){
		this.project = project;
	}
}
