/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.aiscoverage;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import dk.dma.aiscoverage.calculator.AbstractCoverageCalculator;
import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.BaseStation;
import dk.dma.aiscoverage.data.BaseStationHandler;
import dk.dma.aiscoverage.data.CustomMessage;
import dk.dma.aiscoverage.data.Ship;
import dk.dma.aiscoverage.geotools.GeoConverter;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.frv.ais.country.Country;
import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisMessage4;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.proprietary.IProprietarySourceTag;
import dk.frv.ais.proprietary.IProprietaryTag;
import dk.frv.ais.reader.AisReader;

/**
 * Class for handling incoming AIS messages
 */
public class MessageHandler implements IAisHandler {
	
	private static Logger LOG = Logger.getLogger(MessageHandler.class);
	private AisCoverageProject project = null;
	private long defaultID;
	
	/*
	 * Timeout is in seconds. 
	 * If timeout is -1 reader will not stop until everything is read
	 * AisReader is only used to stop processing messages
	 * 
	 */
	public MessageHandler(AisCoverageProject project, long defaultID){
		this.project = project;
		this.defaultID = defaultID;
	}


	/**
	 * Message for receiving AIS messages
	 */
	@Override
	public void receive(AisMessage aisMessage) {	
		
		//Check timeout
		Date now = new Date();
		long timeSinceStart = project.getRunningTime();
		if(project.getTimeout() != -1 && timeSinceStart > project.getTimeout())		
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
			bsMmsi = defaultID; //determine id
			timestamp = new Date();
		}

		//It's a base station
		if(aisMessage instanceof AisMessage4){
			AisMessage4 m = (AisMessage4) aisMessage;
			BaseStation b = project.getBaseStationHandler().grids.get(m.getUserId());
			if(b != null){
				b.latitude = m.getPos().getGeoLocation().getLatitude();
				b.longitude = m.getPos().getGeoLocation().getLongitude();
			}
			return;
		}

		// Handle position messages
		if (aisMessage instanceof AisPositionMessage) {
			posMessage = (AisPositionMessage)aisMessage;		
		} else {
			return;
		}
	
		
		// Increment count
		project.incrementMessageCount();
		
		// Validate postion
		if (!posMessage.isPositionValid()) {
			return;
		}
		
		// Get location
		pos = posMessage.getPos().getGeoLocation();
		
		
		
		//calculate lat lon size based on first message
		if(project.getLatSize() == -1){
			double cellInMeters= project.getCellSize(); //cell size in meters
			project.setLatSize(GeoConverter.metersToLatDegree(cellInMeters));
			project.setLongSize(GeoConverter.metersToLonDegree(pos.getLatitude(), cellInMeters));
		}
		
		
//		if(pos.getLatitude() < 37){
//			System.out.println("bsmsi: " + bsMmsi);
//			System.out.println("mmsi: " + posMessage.getUserId());
//			System.out.println("lat: "+ pos.getLatitude());
//			System.out.println("lon: " + pos.getLongitude());
//			System.out.println("cog: " + posMessage.getCog());
//			System.out.println("sog: " + posMessage.getSog());
//			System.out.println();
//		}

		// Check if grid exists (If a message with that bsmmsi has been received before)
		// Otherwise create a grid for corresponding base station
		BaseStationHandler gridHandler = project.getBaseStationHandler();
		BaseStation grid = gridHandler.getGrid(bsMmsi);
		if(grid == null){
			gridHandler.createGrid(bsMmsi);
			grid = gridHandler.getGrid(bsMmsi);
		}
		
		
		// Check which ship sent the message.
		// If it's the first message from that ship, create ship and put it in grid belonging to bsmmsi
		Ship ship = grid.getShip(posMessage.getUserId());
		if(ship == null){
			grid.createShip(posMessage.getUserId());
			ship = grid.getShip(posMessage.getUserId());
		}
	
		CustomMessage newMessage = new CustomMessage();
		newMessage.cog = (double)posMessage.getCog()/10;
		newMessage.sog = (double)posMessage.getSog()/10;
		newMessage.latitude = posMessage.getPos().getGeoLocation().getLatitude();
		newMessage.longitude = posMessage.getPos().getGeoLocation().getLongitude();
		newMessage.timestamp = timestamp;
		newMessage.grid = grid;
		newMessage.ship = ship;
		
		//Calculator takes care of filtering of messages and calculation of coverage
		project.getCalculator().calculateCoverage(newMessage);
			
		//Store received message as lastMessage in ship
		ship.setLastMessage(newMessage);

	}

}
