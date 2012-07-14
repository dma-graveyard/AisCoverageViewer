package dk.dma.aiscoverage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import dk.dma.aiscoverage.calculator.AbstractCoverageCalculator;
import dk.dma.aiscoverage.calculator.CellChangedListener;
import dk.dma.aiscoverage.calculator.CoverageCalculatorAdvanced3;
import dk.frv.ais.proprietary.DmaFactory;
import dk.frv.ais.proprietary.GatehouseFactory;
import dk.frv.ais.reader.AisReader;
import dk.frv.ais.reader.AisStreamReader;
import dk.frv.ais.reader.RoundRobinAisTcpReader;

public class AisCoverageAnalyser {
	
	private static Logger LOG;
	private String filename;
	private String hostPort;
	private int timeout = -1;
	private AbstractCoverageCalculator calc = new CoverageCalculatorAdvanced3(true);
	private AisReader aisReader = null;
	private List<AisCoverageListener> listeners = new ArrayList<AisCoverageListener>();
	private MessageHandler messageHandler = null;
	private boolean analysisStarted = false;
	
	
	public boolean isAnalysisStarted() {
		return analysisStarted;
	}
	public void setAnalysisStarted(boolean analysisStarted) {
		this.analysisStarted = analysisStarted;
	}
	public AbstractCoverageCalculator getCalc() {
		return calc;
	}
	public void setCalc(AbstractCoverageCalculator calc) {
		this.calc = calc;
	}
	public AisCoverageAnalyser(){
		
	}
	public void setFile(String filepath){
		this.filename = filepath; 
	}
	public void setHostPort(int port){
		this.hostPort = port+"";
	}
	public void startAnalysis() throws FileNotFoundException, InterruptedException{
		DOMConfigurator.configure("log4j.xml");
		LOG = Logger.getLogger(AisCoverage.class);
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
		messageHandler = new MessageHandler(timeout, aisReader, calc);

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
		this.analysisStarted = true;
		for (AisCoverageListener listener : listeners) {
			listener.analysisStarted();
		}
	}
	private void stopped(){
		this.analysisStarted = false;
		for (AisCoverageListener listener : listeners) {
			listener.analysisStopped();
		}
	}
	
	public void addListener(AisCoverageListener listener){
		listeners.add(listener);
	}
	public Long getMessageCount(){
		return messageHandler.getCount();
	}
}
