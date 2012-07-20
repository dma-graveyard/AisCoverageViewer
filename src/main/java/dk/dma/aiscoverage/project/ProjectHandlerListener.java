package dk.dma.aiscoverage.project;

public interface ProjectHandlerListener {
	void projectLoaded();
	void projectCreated();
	void analysisStarted();
	void analysisStopped();
	void visibilityChanged(long mmsi);
	void basestationAdded(long mmsi);
}
