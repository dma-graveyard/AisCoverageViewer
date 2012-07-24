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

import java.util.List;

import org.apache.log4j.Logger;

import dk.dma.aiscoverage.calculator.AbstractCalculator;
import dk.dma.aiscoverage.project.AisCoverageProject;
import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.message.AisMessage;

/**
 * Class for handling incoming AIS messages
 */
public class MessageHandler implements IAisHandler {
	
	private static Logger LOG = Logger.getLogger(MessageHandler.class);
	private AisCoverageProject project = null;
	private String defaultID;
	

	public MessageHandler(AisCoverageProject project, String defaultID){
		this.project = project;
		this.defaultID = defaultID;
	}


	/**
	 * Message for receiving AIS messages
	 */
	@Override
	public void receive(AisMessage aisMessage) {	
		
		// Increment count
		project.incrementMessageCount();
		
		// Notify each calculator
		List<AbstractCalculator> calculators = project.getCalculators();	
		for (AbstractCalculator abstractCoverageCalculator : calculators) {
			//Calculator takes care of filtering of messages and calculation of coverage
			abstractCoverageCalculator.processMessage(aisMessage, defaultID);
		}
	}

}
