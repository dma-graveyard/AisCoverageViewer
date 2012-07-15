package dk.frv.enav.acv.gui;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.bbn.openmap.MapHandler;
import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.aiscoverage.data.Cell;
import dk.dma.aiscoverage.project.AisCoverageListener;
import dk.dma.aiscoverage.project.ProjectHandler;
import dk.dma.aiscoverage.project.ProjectHandlerListener;
import dk.frv.enav.acv.ACV;
import dk.frv.enav.acv.coverage.layers.CoverageLayer;

import java.awt.AWTEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.GridLayout;


public class AnalysisPanel extends OMComponentPanel implements ActionListener, AisCoverageListener, AWTEventListener, ProjectHandlerListener {

	/**
	 * 
	 */
	private MapHandler mapHandler;
	private static final long serialVersionUID = -5409591947155863462L;
	private JButton btnStartAnalysis;
	private JButton btnStopAnalysis;
	private JLabel totalMessages;
	private JLabel messagesPerSec;
	private CoverageLayer coverageLayer;
	private Thread updateCoverageThread;
	private HashMap<Long, JCheckBox> bsmmsis = new HashMap<Long, JCheckBox>();
	private JPanel baseStationPanel;
	private JScrollPane scrollPane;
	private JPanel baseStationWrapperPanel;
	private JCheckBox chckbxSelectAll;
	private JPanel selectAllPanel;
	private JSeparator separator;
	private boolean updateCoverageLayer = false;
	private int waitUpdate = 0;
	private JPanel bottomPanel;
	private JPanel projectPanel;
	private JLabel lblDuration;
	private JLabel lblCalculator;
	private JLabel lblCellSize;
	private JLabel lblInput;
	private JLabel lblAdvanced;
	private JLabel lblFile;
	private JLabel lblm;
	private JLabel lblh;
	private MainFrame mainFrame;
	private boolean mouseDown = false;
	private static Toolkit tk = Toolkit.getDefaultToolkit();
    private static long eventMask = AWTEvent.MOUSE_EVENT_MASK +AWTEvent.MOUSE_WHEEL_EVENT_MASK;
    private JLabel lblRunningTime;
    private JLabel runningTime;

	
	/**
	 * Create the panel.
	 */
	public AnalysisPanel() {
		mapHandler = ACV.getMapHandler();
		setBorder(null);
		setLayout(new BorderLayout(0, 0));
		
		baseStationWrapperPanel = new JPanel();
		baseStationWrapperPanel.setBorder(new TitledBorder(null, "Base Stations", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(baseStationWrapperPanel, BorderLayout.CENTER);
		
		scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		baseStationWrapperPanel.setLayout(new BorderLayout(0, 0));
		
		selectAllPanel = new JPanel();
		selectAllPanel.setBorder(new MatteBorder(0, 0, 1, 0, (Color) UIManager.getColor("Button.shadow")));
		FlowLayout flowLayout = (FlowLayout) selectAllPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		baseStationWrapperPanel.add(selectAllPanel, BorderLayout.NORTH);
		
		chckbxSelectAll = new JCheckBox("Select all");
		chckbxSelectAll.setSelected(true);
		selectAllPanel.add(chckbxSelectAll);
		
		separator = new JSeparator();
		selectAllPanel.add(separator);
		
		baseStationWrapperPanel.add(scrollPane, BorderLayout.CENTER);
		
		baseStationPanel = new JPanel();
		scrollPane.setViewportView(baseStationPanel);
		baseStationPanel.setBorder(null);
		GridBagLayout gbl_baseStationPanel = new GridBagLayout();
		gbl_baseStationPanel.columnWidths = new int[]{0, 0};
		gbl_baseStationPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_baseStationPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_baseStationPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		baseStationPanel.setLayout(gbl_baseStationPanel);
		
		
		bottomPanel = new JPanel();
		add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		projectPanel = new JPanel();
		projectPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Analysis settings", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		bottomPanel.add(projectPanel);
		
		lblDuration = new JLabel("Duration");
		
		lblCalculator = new JLabel("Calculator");
		
		lblCellSize = new JLabel("Cell size");
		
		lblInput = new JLabel("Input");
		
		lblAdvanced = new JLabel("Advanced3");
		
		lblFile = new JLabel("<html>\r\ndump.txt<br/>\r\nlocalhost:7756\r\n</html>");
		
		lblm = new JLabel("2500m");
		
		lblh = new JLabel("24h");
		GroupLayout gl_projectPanel = new GroupLayout(projectPanel);
		gl_projectPanel.setHorizontalGroup(
			gl_projectPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_projectPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_projectPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblCalculator)
						.addComponent(lblInput)
						.addComponent(lblCellSize)
						.addComponent(lblDuration))
					.addGap(18)
					.addGroup(gl_projectPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblh)
						.addComponent(lblm)
						.addComponent(lblFile)
						.addComponent(lblAdvanced))
					.addContainerGap(96, Short.MAX_VALUE))
		);
		gl_projectPanel.setVerticalGroup(
			gl_projectPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_projectPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_projectPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblInput)
						.addComponent(lblFile))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_projectPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCalculator)
						.addComponent(lblAdvanced))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_projectPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCellSize)
						.addComponent(lblm))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_projectPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDuration)
						.addComponent(lblh))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		projectPanel.setLayout(gl_projectPanel);
		
		JPanel progressPanel = new JPanel();
		bottomPanel.add(progressPanel);
		progressPanel.setBorder(new TitledBorder(null, "Progress", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JLabel lblNewLabel = new JLabel("Total messages");
		
		btnStartAnalysis = new JButton("Start analysis");
		
		btnStopAnalysis = new JButton("Stop Analysis");
		
		JLabel lblNewLabel_1 = new JLabel("Messages/Sec");
		
		totalMessages = new JLabel();
		
		messagesPerSec = new JLabel();
		
		lblRunningTime = new JLabel("Running time");
		
		runningTime = new JLabel("");
		
		GroupLayout gl_progressPanel = new GroupLayout(progressPanel);
		gl_progressPanel.setHorizontalGroup(
			gl_progressPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_progressPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_progressPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_progressPanel.createSequentialGroup()
							.addComponent(btnStartAnalysis)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnStopAnalysis))
						.addGroup(gl_progressPanel.createSequentialGroup()
							.addGroup(gl_progressPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel)
								.addComponent(lblNewLabel_1)
								.addComponent(lblRunningTime))
							.addGap(18)
							.addGroup(gl_progressPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(runningTime)
								.addComponent(totalMessages)
								.addComponent(messagesPerSec))))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_progressPanel.setVerticalGroup(
			gl_progressPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_progressPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_progressPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblRunningTime)
						.addComponent(runningTime))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_progressPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(totalMessages))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_progressPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_progressPanel.createSequentialGroup()
							.addGroup(gl_progressPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel_1)
								.addComponent(messagesPerSec))
							.addGap(29))
						.addGroup(gl_progressPanel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
							.addGroup(gl_progressPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnStopAnalysis)
								.addComponent(btnStartAnalysis))
							.addContainerGap())))
		);
		progressPanel.setLayout(gl_progressPanel);
		
		//add listeners
		btnStartAnalysis.addActionListener(this);
		btnStopAnalysis.addActionListener(this);
		ProjectHandler.getInstance().getProject().addListener(this);
		chckbxSelectAll.addActionListener(this);
		tk.addAWTEventListener(this, eventMask);
		
		ProjectHandler.getInstance().addProjectHandlerListener(this);
//		ProjectHandler.getInstance().loadProject("C:\\Users\\Kasper\\Desktop\\save.ana");
		
		updateButtons();
		
		//timers
		startTimers();
		

	}
	
	/*
	 * Starts timers for updating GUI and coverage layer
	 */
	private void startTimers() {
		//This is only for updating GUI elements
		//Don't perform lengthy work
		new Timer(1000, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateProgress();
			}
		}).start();
		
		//This thread updates the coverage layer.
		//We don't use a Swing timer, since this is a bit heavy
		updateCoverageThread = new Thread(new Runnable(){
			

			@Override
			public void run() {
				while(true){
					try {
						
						while(waitUpdate > 0){
							Thread.sleep(1000);
							waitUpdate--;
						}
						waitUpdate = 5; //set standard delay, until next update of coverage
						
						//If mouse is down, we wait a little bit
						while(mouseDown){
							Thread.sleep(500);
						}
						
						//If project is running, or updateCoverageLayer was called, we update layer
						if(updateCoverageLayer || ProjectHandler.getInstance().getProject().isRunning()){

							// Update layer
							List<Long> baseStations = new ArrayList<Long>();
							Collection<JCheckBox> checkboxes = bsmmsis.values();
							for (JCheckBox jCheckBox : checkboxes) {
								if (jCheckBox.isSelected())
									baseStations.add(Long.parseLong(jCheckBox.getText()));
							}
							Collection<Cell> cells = ProjectHandler.getInstance().getProject()
									.getCoverage(baseStations);
							if (coverageLayer != null && cells != null) {
								coverageLayer.doUpdate(cells);
							}
							updateCoverageLayer = false;
						}
						
					} catch (InterruptedException e) {}
				}
			}
		});
		updateCoverageThread.setPriority(1);
		updateCoverageThread.start();

	}
	
	/*
	 * Selects or deselects all base station checkboxes
	 */
	private void selectAll(boolean bool){
		Collection<JCheckBox> boxes = this.bsmmsis.values();
		for (JCheckBox jCheckBox : boxes) {
			jCheckBox.setSelected(bool);
		}
	}
	
	/*
	 * Updates the list of base station
	 */
	private void updateBaseStationList(Long[] bsmmsis){
		Arrays.sort(bsmmsis);
		
		baseStationPanel.removeAll();
		
		int i = 0;
		for(Long bsmmsi : bsmmsis){
			JCheckBox checkbox = this.bsmmsis.get(bsmmsi);
			if(checkbox == null){
				checkbox = new JCheckBox(bsmmsi+"");
				checkbox.setHorizontalAlignment(SwingConstants.LEFT);
				this.bsmmsis.put(bsmmsi, checkbox);
				if(chckbxSelectAll.isSelected()) checkbox.setSelected(true);
				checkbox.addActionListener(this);
			}
			
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.insets = new Insets(5, 25, 0, 0);
			constraints.anchor = GridBagConstraints.WEST;
			constraints.gridx = 0;
			constraints.gridy = i;
			baseStationPanel.add(checkbox, constraints);
			i++;
		}
		
		
	}
	
	private void updateButtons(){
		//Thread safety
		Runnable doWorkRunnable = new Runnable() {
		    public void run() {
		    	if(ProjectHandler.getInstance().getProject().isRunning()){
					btnStartAnalysis.setEnabled(false);
					btnStopAnalysis.setEnabled(true);
				}else if(ProjectHandler.getInstance().getProject().isDone()){
					btnStartAnalysis.setEnabled(false);
					btnStopAnalysis.setEnabled(false);
				}else{
					btnStartAnalysis.setEnabled(true);
					btnStopAnalysis.setEnabled(false);
				}
		    }
		};
		SwingUtilities.invokeLater(doWorkRunnable);
	}
	
	/*
	 * When this method is called, the coverage updater thread
	 * will update the coverage layer.
	 * If it is called multiple times before an update, the layer will
	 * only be updated once.
	 */
	public void updateCoverage(int delay){
		waitUpdate = delay;
		updateCoverageLayer = true;
	}
	
	private String runningTimeToString(Long secondsElapsed){
		String hoursString= null, minutesString = null, secondsString = null;
		int hours = (int) (secondsElapsed/3600);
		int minutes = (int) ((secondsElapsed/60)-(hours*60));
		int seconds = (int) (secondsElapsed-(minutes*60));
		if(seconds < 10) secondsString = "0"+seconds;
		else secondsString = ""+seconds;
		if(minutes < 10) minutesString = "0"+minutes;
		else minutesString = ""+minutes;
		if(hours < 10) hoursString = "0"+hours;
		else hoursString = ""+hours;
		return hoursString + ":"+minutesString+":"+secondsString;
	}
	
	private void updateProgress(){
		if(ProjectHandler.getInstance().getProject() == null) return;
		Long secondsElapsed = ProjectHandler.getInstance().getProject().getRunningTime();
		if(secondsElapsed > 0){
			totalMessages.setText(""+ProjectHandler.getInstance().getProject().getMessageCount());
			messagesPerSec.setText(""+ProjectHandler.getInstance().getProject().getMessageCount()/secondsElapsed);
			updateBaseStationList(ProjectHandler.getInstance().getProject().getBaseStationNames());
			runningTime.setText(runningTimeToString(secondsElapsed));
		}
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		//start the analysis
		if (e.getSource() == btnStartAnalysis) {
			ProjectHandler.getInstance().getProject().setFile("C:\\Users\\Kasper\\Desktop\\aisdump.txt");
			try {
				ProjectHandler.getInstance().getProject().startAnalysis(); //start thread
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		} 
		
		// Stop the analysis
		else if(e.getSource() == btnStopAnalysis){
			ProjectHandler.getInstance().getProject().stopAnalysis();
		}
		
		// Checkbox event, refresh coverageLayer
		else if(e.getSource().getClass() == JCheckBox.class){
			updateCoverage(1);
			if(e.getSource() == chckbxSelectAll) 
				selectAll(chckbxSelectAll.isSelected());
			else
				chckbxSelectAll.setSelected(false);
			
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
		updateButtons();
		updateCoverage(0);		
	}
	
	/*
	 * Global mouselistener
	 * For a more responsive GUI, Don't refresh layer when mouse events occur
	 */
	@Override
	public void eventDispatched(AWTEvent e) {
		if(MouseEvent.MOUSE_PRESSED == e.getID()){
			mouseDown = true;
		}else if(MouseEvent.MOUSE_RELEASED == e.getID()){
            mouseDown = false;
            waitUpdate = 2;
        }else if(MouseEvent.MOUSE_WHEEL == e.getID()){
        	waitUpdate = 2;
        }
	}
	@Override
	public void projectLoaded() {
		updateButtons();
		updateCoverage(0);
	}

	@Override
	public void projectCreated() {
		updateButtons();
		updateCoverage(0);
	}
}
