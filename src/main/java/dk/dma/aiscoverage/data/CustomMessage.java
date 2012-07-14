package dk.dma.aiscoverage.data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import dk.frv.ais.message.AisPositionMessage;

public class CustomMessage implements Serializable {
//	public AisPositionMessage message = null;
	public double cog;
	public double sog;
	public double latitude;
	public double longitude;
	public Date timestamp = null;
	public BaseStation grid;
	public Ship ship;
	public Cell cell;
	public long timeSinceLastMsg;

}
