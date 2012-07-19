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
	private String filename = null;
	private String hostPort;
	private int timeout = -1;
	transient private AbstractCoverageCalculator calc = new CoverageCalculatorAdvanced3(true);
	transient private List<AisReader> readers = new ArrayList<AisReader>();
	transient private List<MessageHandler> messageHandlers = new ArrayList<MessageHandler>();
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
		AisReader reader = null;
		try {
			reader = new AisStreamReader(new FileInputStream(filepath));
			
			// Register proprietary handlers (optional)
			reader.addProprietaryFactory(new DmaFactory());
			reader.addProprietaryFactory(new GatehouseFactory());
			readers.add(reader);
			
			// Make handler instance
			MessageHandler messageHandler = new MessageHandler(this, 0l);
			messageHandlers.add(messageHandler);
			// register message handler
			reader.registerHandler(messageHandler);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String getFile()
	{
		return filename;
	}
	public void addHostPort(String port){
		RoundRobinAisTcpReader reader = new RoundRobinAisTcpReader();
		reader.setCommaseparatedHostPort(port);
		
		// Register proprietary handlers (optional)
		reader.addProprietaryFactory(new DmaFactory());
		reader.addProprietaryFactory(new GatehouseFactory());
		
		readers.add(reader);
		
		// Make handler instance
		// We create multiple message handlers because we need a default id
		// if bsmmsi isn't set
		MessageHandler messageHandler = new MessageHandler(this, 123l);
		messageHandlers.add(messageHandler);
		// register message handler
		reader.registerHandler(messageHandler);

	}
	public void startAnalysis() throws FileNotFoundException, InterruptedException{
		DOMConfigurator.configure("log4j.xml");
		LOG = Logger.getLogger(AisCoverageProject.class);
		LOG.info("Starting AisCoverage");
		
		if (readers.size() == 0) {
			LOG.debug("Source missing");
			return;
		}
		
		

		
		for (AisReader reader : readers) {
			
			// start reader
			reader.start();
		}
		
		// Listen for reader to stop
		Thread t = new Thread( new Runnable(){
            public void run(){
        		try {
        			started();
        			readers.get(0).join();
					stopped();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
            }
        });
		t.start();

		
	}
	public void stopAnalysis(){
		for (AisReader reader : readers) {
			reader.stop();
		}
	}
	private void started(){
		starttime = new Date();
		this.isRunning = true;
		ProjectHandler.getInstance().analysisStarted();
	}
	private void stopped(){
		endtime = new Date();
		this.isRunning = false;
		this.isDone = true;
		ProjectHandler.getInstance().analysisStopped();
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
//	public Collection<Cell> getCoverage(List<Long> baseStations){
//		return gridHandler.getCoverage(baseStations);
//	}
	public Collection<Cell> getCoverage(){
		return gridHandler.getCoverage();
	}
	
	public void incrementMessageCount(){
		this.messageCount++;
	}

}
