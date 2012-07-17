package dk.frv.enav.acv.coverage.layers;

import java.awt.event.MouseEvent;
import java.util.Collection;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;


import dk.dma.aiscoverage.data.BaseStation;
import dk.frv.enav.acv.event.NavigationMouseMode;



public class BaseStationLayer extends OMGraphicHandlerLayer implements MapMouseListener {

	private static final long serialVersionUID = 1L;
	private OMGraphicList graphics = new OMGraphicList();
	private int basestationCount = 0;

	private void updateBasestation(BaseStation basestation){
		
		//If lat/lon isn't set, we can't display base station
		if(basestation.latitude == null)
			return;

		AisTargetGraphic graphic = new AisTargetGraphic(basestation.latitude, basestation.longitude);
		graphics.add(graphic);

	}

	public void doUpdate(Collection<BaseStation> basestations) {
		
		//Only update if new base stations are added
		if(basestations.size() > basestationCount){
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
	}

	
	@Override
	public synchronized OMGraphicList prepare() {
		graphics.project(getProjection());
		return graphics;
	}

	@Override
	public String[] getMouseModeServiceList() {
		String[] ret = new String[1];
		ret[0] = NavigationMouseMode.modeID; // "Gestures"
		
		
		
		return ret;
	}

	@Override
	public boolean mouseClicked(MouseEvent arg0) {
		System.out.println("juhuu");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseDragged(MouseEvent arg0) {
		System.out.println("juhuu");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		System.out.println("juhuu");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		System.out.println("juhuu");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved() {
		System.out.println("juhuu");
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mouseMoved(MouseEvent arg0) {
		System.out.println("juhuu");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousePressed(MouseEvent arg0) {
		System.out.println("juhuu");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseReleased(MouseEvent arg0) {
		System.out.println("juhuu");
		// TODO Auto-generated method stub
		return false;
	}
}
