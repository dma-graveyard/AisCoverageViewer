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
import dk.dma.aiscoverage.calculator.AbstractCalculator;
import dk.dma.aiscoverage.calculator.CoverageCalculator;
import dk.dma.aiscoverage.calculator.DensityPlotCalculator;
import dk.dma.aiscoverage.data.BaseStationHandler;
import dk.dma.aiscoverage.data.Cell;
import dk.frv.ais.proprietary.DmaFactory;
import dk.frv.ais.proprietary.GatehouseFactory;
import dk.frv.ais.reader.AisReader;
import dk.frv.ais.reader.AisStreamReader;
import dk.frv.ais.reader.RoundRobinAisTcpReader;
import dk.frv.enav.acv.event.AisEvent;

public class AisCoverageProject implements Serializable {
	transient private static Logger LOG;
	private String filename = null;
	private String hostPort;
	private int timeout = -1;
	private List<AbstractCalculator> calculators = new ArrayList<AbstractCalculator>();
	transient private List<AisReader> readers = new ArrayList<AisReader>();
	transient private List<MessageHandler> messageHandlers = new ArrayList<MessageHandler>();
//	private BaseStationHandler gridHandler = new BaseStationHandler();
	private Date starttime;
	private Date endtime;
	private boolean isRunning = false;
	private boolean isDone = false;
	private long messageCount = 0;

	public boolean isRunning() {
		return isRunning;
	}
	public List<AbstractCalculator> getCalculators() {
		return calculators;
	}
	public void addCalculator(AbstractCalculator calc){
		calculators.add(calc);
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
			MessageHandler messageHandler = new MessageHandler(this, "Unidentified");
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
	public void addHostPort(String port, String defaultID){
		RoundRobinAisTcpReader reader = new RoundRobinAisTcpReader();
		reader.setCommaseparatedHostPort(port);
		
		// Register proprietary handlers (optional)
		reader.addProprietaryFactory(new DmaFactory());
		reader.addProprietaryFactory(new GatehouseFactory());
		
		readers.add(reader);
		
		// Make handler instance
		// We create multiple message handlers because we need a default id
		// if bsmmsi isn't set
		MessageHandler messageHandler = new MessageHandler(this, defaultID);
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
		
		AisEvent event = new AisEvent();
		event.setEvent(AisEvent.Event.ANALYSIS_STARTED);
		ProjectHandler.getInstance().broadcastEvent(event);
	}
	private void stopped(){
		endtime = new Date();
		this.isRunning = false;
		this.isDone = true;
		
		AisEvent event = new AisEvent();
		event.setEvent(AisEvent.Event.ANALYSIS_STOPPED);
		ProjectHandler.getInstance().broadcastEvent(event);
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
	
	
	public void incrementMessageCount(){
		this.messageCount++;
	}
	public CoverageCalculator getCoverageCalculator(){
		for (AbstractCalculator abstractCalc : getCalculators()) {
			if(abstractCalc instanceof CoverageCalculator)
				return (CoverageCalculator) abstractCalc;
		}
		return null;
	}
	public DensityPlotCalculator getDensityPlotCalculator(){
		for (AbstractCalculator abstractCalc : getCalculators()) {
			if(abstractCalc instanceof DensityPlotCalculator)
				return (DensityPlotCalculator) abstractCalc;
		}
		return null;
	}

}
