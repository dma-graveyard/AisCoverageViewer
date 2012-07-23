package dk.frv.enav.acv.coverage.layers;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Date;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;

import dk.dma.aiscoverage.data.BaseStation;
import dk.frv.enav.acv.event.NavigationMouseMode;
import dk.frv.enav.acv.gui.BaseStationInfo;
import dk.frv.enav.acv.gui.MainFrame;








public class BaseStationLayer extends OMGraphicHandlerLayer implements MapMouseListener {

	private static final long serialVersionUID = 1L;
	private OMGraphicList graphics = new OMGraphicList();
	private int basestationCount = 0;
	private OMGraphic current;
	private MainFrame mainFrame;
	BaseStationInfo box = new BaseStationInfo(this, 170, 110);
	private MouseEvent e;

	public BaseStationLayer(){
	}

	private void updateBasestation(BaseStation basestation){
		
		//If lat/lon isn't set, we can't display base station
		if(basestation.latitude == null)
			return;

		AisTargetGraphic graphic = new AisTargetGraphic(basestation.latitude, basestation.longitude, basestation);
		graphics.add(graphic);

	}

	public void doUpdate(Collection<BaseStation> basestations, boolean forceUpdate) {
		//Only update if new base stations are added
		if(basestations.size() > basestationCount || forceUpdate){
			basestationCount = basestations.size();
			graphics.clear();
			System.out.println("updating base stations");
			
			for (BaseStation basestation : basestations) {
				updateBasestation(basestation);
			}
			doPrepare();
		}
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
//		System.out.println("juhuu");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseDragged(MouseEvent arg0) {
//		System.out.println("juhuu");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
//		System.out.println("juhuu");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
//		System.out.println("juhuu");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved() {
//		System.out.println("juhuu");
		// TODO Auto-generated method stub
		
	}

	public boolean isHighlightable(OMGraphic g){
		return true;
	}
	public String getToolTipTextFor(OMGraphic g){
		System.out.println("ehhsdsf");
		return "fidar";
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
}
