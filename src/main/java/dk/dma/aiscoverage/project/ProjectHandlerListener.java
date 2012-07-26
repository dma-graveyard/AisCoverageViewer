package dk.dma.aiscoverage.project;

import dk.dma.aiscoverage.event.AisEvent;

public interface ProjectHandlerListener {
	void aisEventReceived(AisEvent event);
}
