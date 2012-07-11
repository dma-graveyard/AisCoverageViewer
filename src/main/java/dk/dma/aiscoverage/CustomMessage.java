package dk.dma.aiscoverage;

import java.util.Date;
import java.util.HashMap;

import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.data.Grid;
import dk.dma.aiscoverage.data.Ship;
import dk.frv.ais.message.AisPositionMessage;

public class CustomMessage {
	public AisPositionMessage message = null;
	public Date timestamp = null;
	public Grid grid;
	public Ship ship;
	public Cell cell;
	public long timeSinceLastMsg;

}
