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
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import dk.dma.aiscoverage.GlobalSettings;


public class BaseStation implements Serializable {
	
	public ConcurrentHashMap<String, Cell> grid = new ConcurrentHashMap<String, Cell>();
	public ConcurrentHashMap<Long, Ship> ships = new ConcurrentHashMap<Long, Ship>();
	public Long bsMmsi;
	private double latSize;
	private double lonSize;

	
	public BaseStation(long bsMmsi, double latSize, double lonSize) {
		this.bsMmsi = bsMmsi;
		this.latSize = latSize;
		this.lonSize = lonSize;
	}
	

	
	/*
	 * 
	 */
	public Cell getCell(double latitude, double longitude){
		return grid.get(getCellId(latitude, longitude));
	}
	
	/*
	 * latitude is rounded down
	 * longitude is rounded up.
	 * The id is lat-lon-coords representing top-left point in cell
	 */
	public String getCellId(double latitude, double longitude){

		double lat;
		double lon;
		if(latitude < 0){
			latitude +=latSize;
			lat = (double)((int)(10000*((latitude)- (latitude % latSize))))/10000;
			
		}else{
			lat = (double)((int)(10000*((latitude)- (latitude % latSize))))/10000;
		}
		
		if(longitude < 0){
			lon = (double)((int)(10000*(longitude - (longitude % lonSize))))/10000;
			
		}else{
			longitude -=lonSize;
			lon = (double)((int)(10000*(longitude - (longitude % lonSize))))/10000;
		}
		
		String cellId =  lat+"_"+lon;	
		return cellId;
	}
	
	public Cell createCell(double latitude, double longitude){
		String id = getCellId(latitude, longitude);
		double lat = (double)((int)(10000*(latitude - (latitude % latSize))))/10000;
		double lon = (double)((int)(10000*(longitude - (longitude % lonSize))))/10000;
		Cell cell = new Cell(this, lat, lon, id);
		grid.put(cell.id, cell);
		
		return cell;
	}
	
	/*
	 * Create ship
	 */
	public void createShip(Long mmsi){
		Ship ship = new Ship(mmsi);
		ships.put(mmsi, ship);
	}
	
	public Ship getShip(Long mmsi){
		return ships.get(mmsi);
	}
}
