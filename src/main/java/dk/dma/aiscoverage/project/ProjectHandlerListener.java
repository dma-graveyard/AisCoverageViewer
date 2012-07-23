package dk.dma.aiscoverage.project;

public interface ProjectHandlerListener {
	void projectLoaded();
	void projectCreated();
	void analysisStarted();
	void analysisStopped();
	void visibilityChanged(String mmsi);
	void basestationAdded(String mmsi);
}
