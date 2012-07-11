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

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class Cell {
	
	public ConcurrentHashMap<Long, Ship> ships = new ConcurrentHashMap<Long, Ship>();
	public Long NOofReceivedSignals=0L; 
	public Long NOofMissingSignals=0L;
	public double latitude;
	public double longitude;
	public String id;
	public Grid grid;
	
	public Cell(Grid grid, double lat, double lon, String id){
		this.latitude = lat;
		this.longitude = lon;
		this.grid = grid;
		this.id = id;
	}
	
	public long getTotalNumberOfMessages(){
		return NOofReceivedSignals+NOofMissingSignals;
	}
	public double getCoverage(){
		return (double)NOofReceivedSignals/ (double)getTotalNumberOfMessages();
	}
}
