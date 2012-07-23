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
package dk.dma.aiscoverage.data;

import java.io.Serializable;
import java.util.LinkedList;


public class Ship implements Serializable {
	
	public Long mmsi;
	private CustomMessage lastMessage = null;
	private LinkedList<CustomMessage> messageBuffer = new LinkedList<CustomMessage>();
	private Cell lastCell = null;
	

	public Cell getLastCell() {
		return lastCell;
	}

	public void setLastCell(Cell lastCell) {
		this.lastCell = lastCell;
	}

	public Ship(Long mmsi) {
		this.mmsi = mmsi;
	}
	
	public void setLastMessage(CustomMessage message){
		this.lastMessage = message;
	}
	public CustomMessage getLastMessage(){
		return lastMessage;
	}
	public void addToBuffer(CustomMessage m){
		messageBuffer.add(m);
	}
	public LinkedList<CustomMessage> getMessages(){
		return messageBuffer;
	}
	public void emptyBuffer(){
		CustomMessage last = messageBuffer.getLast();
		messageBuffer.clear();
		messageBuffer.add(last); //We still want the last message in the buffer
	}
	


}
