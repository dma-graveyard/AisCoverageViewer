package dk.frv.enav.acv.coverage.layers;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;

import dk.dma.aiscoverage.calculator.CoverageCalculator;
import dk.dma.aiscoverage.data.BaseStation;
import dk.dma.aiscoverage.project.ProjectHandler;
import dk.dma.aiscoverage.project.ProjectHandlerListener;
import dk.frv.enav.acv.event.AisEvent;
import dk.frv.enav.acv.event.NavigationMouseMode;
import dk.frv.enav.acv.gui.BaseStationInfo;
import dk.frv.enav.acv.gui.MainFrame;


public class BaseStationLayer extends OMGraphicHandlerLayer implements MapMouseListener, Runnable, ProjectHandlerListener {

	private static final long serialVersionUID = 1L;
	private OMGraphicList graphics = new OMGraphicList();
	private OMGraphic current;
	private MainFrame mainFrame;
	BaseStationInfo box = new BaseStationInfo(this, 170, 110);
	private MouseEvent e;
	private CoverageCalculator calc;
	private Map<String, AisTargetGraphic> graphicMap = new ConcurrentHashMap<String, AisTargetGraphic>();
	private int updateDelay;
	private final int defaultUpdatedelay = 100;

	public BaseStationLayer(){
		ProjectHandler.getInstance().addProjectHandlerListener(this);
		new Thread(this).start();
	}

	private void updateBasestation(BaseStation basestation){
		if(basestation == null)
			return;
		
		//If lat/lon isn't set, we can't display base station
		if(basestation.latitude == null)
			return;
				

		// if graphic doesnt exist we create, else we update
		AisTargetGraphic g = graphicMap.get(basestation.identifier);
		if(g == null){ 
			AisTargetGraphic graphic = new AisTargetGraphic(basestation.latitude, basestation.longitude, basestation);
			graphics.add(graphic);
			graphicMap.put(basestation.identifier, graphic);
		}else{
			g.setAwaitingUpdate(true);
		}
		

		

	}

	public void doUpdate() {
		Collection<AisTargetGraphic> col = graphicMap.values();
		for (AisTargetGraphic tar : col) {
			tar.update();
		}
		doPrepare();
	}
	
	@Override
	public void findAndInit(Object obj) {
		//This is used in case we need to communicate with other handler objects such as AIS 
		if (obj instanceof MainFrame) {
			mainFrame = (MainFrame) obj;

		}
	}

	
	@Override
	public synchronized OMGraphicList prepare() {
		graphics.project(getProjection());
		return graphics;
	}

	public MapMouseListener getMapMouseListener(){
		return this;
	}
	
	@Override
	public String[] getMouseModeServiceList() {
		String[] ret = new String[1];
		ret[0] = NavigationMouseMode.modeID; // "Gestures"
		
		
		
		return ret;
	}

	@Override
	public boolean mouseClicked(MouseEvent arg0) {
		return false;
	}

	@Override
	public boolean mouseDragged(MouseEvent arg0) {
		return false;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved() {
	}

	@Override
	public boolean mouseMoved(MouseEvent e) {
		this.e = e;
		OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(), 3.0f);

		if(!allClosest.isEmpty()){
		
			// For some reason it won't cast to AisTargetGraphic - only CenterRaster
			// Hack is to keep a reference to AisTargetGraphic in center raster
			CenterRaster raster = (CenterRaster) allClosest.get(0);
			AisTargetGraphic newGraphic = raster.getAisTargetGraphic();
			if(newGraphic != current){
				current = newGraphic;
				box.setBaseStation(newGraphic.getBasestation());
				box.setVisible(true);	
				box.show(this, e.getX() - 2, e.getY() - 2);
				newGraphic.generate(this.getProjection());
			}
			return true;
		}else{
			box.setVisible(false);
			current = null;
			return true;
		}

	}

	@Override
	public boolean mousePressed(MouseEvent arg0) {
		return false;
	}

	@Override
	public boolean mouseReleased(MouseEvent arg0) {
		return false;
	}

	@Override
	public void run() {
		while(true){
			try {
				while(updateDelay > 0){
					Thread.sleep(100);

					updateDelay--;
				}
				System.out.println("UPDATING Base Stations");
				updateDelay = defaultUpdatedelay; //default update delay
				doUpdate();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	private void forceUpdate(int delay){
		this.updateDelay = delay;
	}


	@Override
	public void aisEventReceived(AisEvent event) {
		if(event.getEvent() == AisEvent.Event.BS_POSITION_FOUND){
			if(event.getSource() instanceof CoverageCalculator){
				BaseStation bs = (BaseStation) event.getEventObject();
				if(graphicMap.get(bs.identifier) == null){
					updateBasestation(bs);
					forceUpdate(1);
				}
			}
			
		}else if(event.getEvent() == AisEvent.Event.BS_VISIBILITY_CHANGED){
			if(event.getSource() instanceof CoverageCalculator){
				BaseStation bs = (BaseStation) event.getEventObject();
				updateBasestation(bs);
				forceUpdate(1);
			}
		}
		
	}
}
