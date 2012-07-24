
/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Authority.
 *
 */

package dk.frv.enav.acv.coverage.layers;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;

import dk.dma.aiscoverage.data.BaseStation;
import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.EeINS;

public class AisTargetGraphic extends OMGraphicList {
	private static final long serialVersionUID = 1L;
	CenterRaster selectionGraphics;
	ImageIcon targetImage;
	int imageWidth;
	int imageHeight;
	private BaseStation basestation;
	private double latitude;
	private double longitude;
	private boolean awaitingUpdate = true;



	public boolean isAwaitingUpdate() {
		return awaitingUpdate;
	}


	public void setAwaitingUpdate(boolean awaitingUpdate) {
		this.awaitingUpdate = awaitingUpdate;
	}


	public void update() {
		if(awaitingUpdate == true){
			if(basestation.isVisible()){
				targetImage = new ImageIcon(EeINS.class.getResource("/images/atonActive.png"));
			}else{
				targetImage = new ImageIcon(EeINS.class.getResource("/images/aton.png"));
	
			}
	
			remove(selectionGraphics);
			selectionGraphics = new CenterRaster(latitude,
						longitude, imageWidth, imageHeight, targetImage, this);
			add(selectionGraphics);
			
			awaitingUpdate = true;
		}

	}
	
	
	public boolean generate(Projection proj){
		boolean result = super.generate(proj);
		selectionGraphics.generate(proj);
		return result;
		
	}
	


	public BaseStation getBasestation() {
		return basestation;
	}
	

	public CenterRaster getCenterRaster(){
		return selectionGraphics;
	}
	public void setBasestation(BaseStation basestation) {
		this.basestation = basestation;
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		attributes.put("basestation", basestation);
		selectionGraphics.setAttributes(attributes);
	}

	public AisTargetGraphic(double latitude, double longitude, BaseStation bs) {
		super();
		this.latitude=latitude;
		this.longitude=longitude;
		this.basestation = bs;
		createGraphics();
		selectionGraphics = new CenterRaster(latitude,
				longitude, imageWidth, imageHeight, targetImage, this);
		add(selectionGraphics);
	}

	private void createGraphics() {

		if(basestation.isVisible()){
			targetImage = new ImageIcon(EeINS.class.getResource("/images/atonActive.png"));
		}else{
			targetImage = new ImageIcon(EeINS.class.getResource("/images/aton.png"));
		}
		
		imageWidth = targetImage.getIconWidth();
		imageHeight = targetImage.getIconHeight();

		selectionGraphics = new CenterRaster(0, 0, imageWidth, imageHeight,
				targetImage, this);

	}

	public void moveSymbol(GeoLocation pos) {
		remove(selectionGraphics);
		selectionGraphics = new CenterRaster(pos.getLatitude(),
				pos.getLongitude(), imageWidth, imageHeight, targetImage, this);
		add(selectionGraphics);
	}

	public void removeSymbol() {
		remove(selectionGraphics);
	}

}