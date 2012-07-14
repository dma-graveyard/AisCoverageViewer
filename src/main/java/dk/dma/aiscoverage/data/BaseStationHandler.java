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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class BaseStationHandler implements Serializable {

	public ConcurrentHashMap<Long, BaseStation> grids = new ConcurrentHashMap<Long, BaseStation>();
	private double latSize;
	public double getLatSize() {
		return latSize;
	}

	public void setLatSize(double latSize) {
		this.latSize = latSize;
	}

	public double getLonSize() {
		return lonSize;
	}

	public void setLonSize(double lonSize) {
		this.lonSize = lonSize;
	}

	private double lonSize;
	
	public BaseStationHandler() {
	}
	
	/*
	 * Create grid associated to a specific transponder
	 */
	public void createGrid(Long bsMmsi){
		BaseStation grid = new BaseStation(bsMmsi, latSize, lonSize);
		grids.put(bsMmsi, grid);
	}
	
	public BaseStation getGrid(Long bsMmsi){
		return grids.get(bsMmsi);
	}

	/*
	 * Consider optimizing?
	 *
	 * Returns a combined coverage of cells from selected base stations.
	 * If two base stations cover same area, the best coverage is chosen.
	 */
	public Collection<Cell> getCoverage(List<Long> baseStations) {
		HashMap<String, Cell> cells = new HashMap<String, Cell>();
		
		//For each base station
		for (Long bsmmsi : baseStations) {
			BaseStation bs = grids.get(bsmmsi);
			
			//For each cell
			Collection<Cell> bscells = bs.grid.values();
			for (Cell cell : bscells) {
				Cell existing = cells.get(cell.id);
				if(existing == null)
					cells.put(cell.id, cell);
				else
					if(cell.getCoverage() > existing.getCoverage())
						cells.put(cell.id, cell);
			}
			
		}
		return cells.values();
	}
	
}
