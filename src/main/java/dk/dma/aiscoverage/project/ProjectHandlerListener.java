package dk.dma.aiscoverage.project;

import dk.frv.enav.acv.event.AisEvent;

public interface ProjectHandlerListener {
	void aisEventReceived(AisEvent event);
}
