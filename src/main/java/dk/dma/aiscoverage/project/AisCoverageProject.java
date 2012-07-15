package dk.dma.aiscoverage.project;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import dk.dma.aiscoverage.GlobalSettings;
import dk.dma.aiscoverage.MessageHandler;
import dk.dma.aiscoverage.calculator.AbstractCoverageCalculator;
import dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced3;
import dk.dma.aiscoverage.data.BaseStationHandler;
import dk.dma.aiscoverage.data.Cell;
import dk.frv.ais.proprietary.DmaFactory;
import dk.frv.ais.proprietary.GatehouseFactory;
import dk.frv.ais.reader.AisReader;
import dk.frv.ais.reader.AisStreamReader;
import dk.frv.ais.reader.RoundRobinAisTcpReader;

public class AisCoverageProject implements Serializable {
	transient private static Logger LOG;
	private String filename = "C:\\Users\\silentk\\Desktop\\aisdump.txt";
	private String hostPort;
	private int timeout = -1;
	transient private AbstractCoverageCalculator calc = new CoverageCalculatorAdvanced3(true);
	transient private AisReader aisReader = null;
	transient private List<AisCoverageListener> listeners = new ArrayList<AisCoverageListener>();
	transient private MessageHandler messageHandler = null;
	private BaseStationHandler gridHandler = new BaseStationHandler();
	private Date starttime;
	private Date endtime;
	private boolean isRunning = false;
	private boolean isDone = false;
	private long messageCount = 0;
	private double latSize = -1;
	private double longSize = -1;
	private int cellSize = 2500;
	
	
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
	public int getCellSize() {
		return cellSize;
	}
	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}
	public boolean isRunning() {
		return isRunning;
	}
	public AbstractCoverageCalculator getCalculator() {
		return calc;
	}
	public void setCalculator(AbstractCoverageCalculator calc) {
		this.calc = calc;
	}
	public AisCoverageProject(){
		
	}
	public void setFile(String filepath){
		this.filename = filepath;
		//this.filename = "C:\\Users\\silentk\\Desktop\\aisdump.txt"; 
	}
	
	public String getFile()
	{
		return filename;
	}
	public void setHostPort(int port){
		this.hostPort = port+"";
	}
	public void startAnalysis() throws FileNotFoundException, InterruptedException{
		DOMConfigurator.configure("log4j.xml");
		LOG = Logger.getLogger(AisCoverageProject.class);
		LOG.info("Starting AisCoverage");
		
		if (filename == null && hostPort == null) {
			LOG.debug("Source missing");
			return;
		}
		
		// Use TCP or file as source
		if (filename != null) {
			LOG.debug("Using file source: " + filename);
			aisReader = new AisStreamReader(new FileInputStream(filename));
		} else {
			LOG.debug("Using TCP source: " + hostPort);
			RoundRobinAisTcpReader rrAisReader = new RoundRobinAisTcpReader();
			rrAisReader.setCommaseparatedHostPort(hostPort);
			aisReader = rrAisReader;
		}
		
		// Register proprietary handlers (optional)
		aisReader.addProprietaryFactory(new DmaFactory());
		aisReader.addProprietaryFactory(new GatehouseFactory());

		// Make handler instance
		messageHandler = new MessageHandler(this);

		// Register handler and start reader
		aisReader.registerHandler(messageHandler);
		aisReader.start();
		
		Thread t = new Thread( new Runnable(){
            public void run(){
        		try {
        			started();
					aisReader.join();
					stopped();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
            }
        });
		t.start();

		
	}
	public void stopAnalysis(){
		aisReader.stop();
	}
	private void started(){
		starttime = new Date();
		this.isRunning = true;
		for (AisCoverageListener listener : listeners) {
			listener.analysisStarted();
		}
	}
	private void stopped(){
		endtime = new Date();
		this.isRunning = false;
		this.isDone = true;
		for (AisCoverageListener listener : listeners) {
			listener.analysisStopped();
		}
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public void addListener(AisCoverageListener listener){
		listeners.add(listener);
	}
	public Long getMessageCount(){
		return messageCount;
	}
	public BaseStationHandler getBaseStationHandler(){
		return gridHandler;
	}
	public Long[] getBaseStationNames(){
		Set<Long> set = gridHandler.grids.keySet();
		Long[] bssmsis = new Long[set.size()];
		int i = 0;
		for (Long long1 : set) {
			bssmsis[i] = long1;
			i++;
		}
		return bssmsis;
	}
	public Long getRunningTime(){
		
		if(starttime == null) return -1L;
		if(isRunning) 
			return (new Date().getTime() - starttime.getTime()) /1000;
		else
			return (endtime.getTime() - starttime.getTime()) /1000;
	}
	/*
	 * Returns a combined coverage of cells from selected base stations.
	 * If two base stations cover same area, the best coverage is chosen.
	 */
	public Collection<Cell> getCoverage(List<Long> baseStations){
		return gridHandler.getCoverage(baseStations);
	}
	public void incrementMessageCount(){
		this.messageCount++;
	}

}
