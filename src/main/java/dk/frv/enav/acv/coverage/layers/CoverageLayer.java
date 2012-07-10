package dk.frv.enav.acv.coverage.layers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.coords.LatLonPoint;

public class CoverageLayer extends OMGraphicHandlerLayer {

	private static final long serialVersionUID = 1L;
	private OMGraphicList graphics = new OMGraphicList();

	public CoverageLayer() {
		doUpdate();
	}

	public void doUpdate() {
		graphics.clear();

		List<LatLonPoint> polygon = new ArrayList<LatLonPoint>();
		
		polygon.add(new LatLonPoint.Double(0.0, 0.0));
		polygon.add(new LatLonPoint.Double(0.0, 10.0));
		polygon.add(new LatLonPoint.Double(10.0, 10.0));
		polygon.add(new LatLonPoint.Double(10.0, 0.0));

		GridPolygon grid = new GridPolygon(polygon, Color.RED);

		graphics.add(grid);
		
		
		List<LatLonPoint> polygon2 = new ArrayList<LatLonPoint>();
		
		polygon2.add(new LatLonPoint.Double(10.0, 10.0));
		polygon2.add(new LatLonPoint.Double(10.0, 20.0));
		polygon2.add(new LatLonPoint.Double(20.0, 20.0));
		polygon2.add(new LatLonPoint.Double(20.0, 10.0));

		GridPolygon grid2 = new GridPolygon(polygon2, Color.GREEN);

		graphics.add(grid2);

		doPrepare();
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
}
