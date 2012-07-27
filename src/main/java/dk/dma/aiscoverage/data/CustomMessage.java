package dk.dma.aiscoverage.data;

import java.io.Serializable;
import java.util.Date;

public class CustomMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private double cog;
	private double sog;
	private double latitude;
	private double longitude;
	private Date timestamp = null;
	private BaseStation grid;
	private Ship ship;
	private long timeSinceLastMsg;
	
	public double getCog() {
		return cog;
	}
	public void setCog(double cog) {
		this.cog = cog;
	}
	public double getSog() {
		return sog;
	}
	public void setSog(double sog) {
		this.sog = sog;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public BaseStation getGrid() {
		return grid;
	}
	public void setGrid(BaseStation grid) {
		this.grid = grid;
	}
	public Ship getShip() {
		return ship;
	}
	public void setShip(Ship ship) {
		this.ship = ship;
	}
	public long getTimeSinceLastMsg() {
		return timeSinceLastMsg;
	}
	public void setTimeSinceLastMsg(long timeSinceLastMsg) {
		this.timeSinceLastMsg = timeSinceLastMsg;
	}

}
