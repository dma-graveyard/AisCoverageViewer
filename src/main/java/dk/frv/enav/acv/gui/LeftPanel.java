package dk.frv.enav.acv.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.aiscoverage.AisCoverageAnalyser;
import dk.dma.aiscoverage.AisCoverageListener;
import dk.frv.enav.acv.ACV;
import dk.frv.enav.acv.coverage.layers.CoverageLayer;

public class LeftPanel extends OMComponentPanel implements ActionListener, AisCoverageListener {
	
	private JButton startAnalysis = new JButton("Start Analysis");
	private JButton stopAnalysis = new JButton("Stop Analysis");
	private MainFrame mainFrame;
	private CoverageLayer coverageLayer;
	private Thread refresherThread;
	private AisCoverageAnalyser analyser = new AisCoverageAnalyser();
	
	public LeftPanel(){
		super();
		
		// Display buttons
		add(startAnalysis);
		add(stopAnalysis);
		
		//add listeners
		startAnalysis.addActionListener(this);
		stopAnalysis.addActionListener(this);
		analyser.addListener(this);
		
		updateButtons();
	}
	
	public void updateButtons(){
		if(analyser.isAnalysisStarted()){
			startAnalysis.setEnabled(false);
			stopAnalysis.setEnabled(true);
		}else{
			startAnalysis.setEnabled(true);
			stopAnalysis.setEnabled(false);
		}
	}
	
	public void stopAnalysis(){
		analyser.stopAnalysis();
	}
	
	public void startAnalysis(){
		analyser.setFile("C:\\Users\\Kasper\\Desktop\\aisdump.txt");
		analyser.getCalc().addCellChangedListener(coverageLayer);
		try {
			analyser.startAnalysis(); //start thread
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		refresherThread = new Thread(new Runnable(){
			@Override
			public void run() {
				while(true){
					try {
						Thread.sleep(5000);
						System.out.println(analyser.getMessageCount());
						coverageLayer.doUpdate();
					} catch (InterruptedException e) {}
				}
			}
		});
		refresherThread.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startAnalysis) {
			startAnalysis();
		} 
		else if(e.getSource() == stopAnalysis){
			stopAnalysis();
		}
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MainFrame) {
			mainFrame = (MainFrame) obj;
		}else if (obj instanceof CoverageLayer) {
			coverageLayer = (CoverageLayer) obj;
		}
	}

	@Override
	public void analysisStarted() {
		System.out.println("analysis started");
		updateButtons();
		
	}

	@Override
	public void analysisStopped() {
		System.out.println("analysis stopped");
		refresherThread.stop();
		updateButtons();
		coverageLayer.doUpdate();
		
	}
	
}
