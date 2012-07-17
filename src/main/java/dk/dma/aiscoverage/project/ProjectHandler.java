package dk.dma.aiscoverage.project;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProjectHandler {

	private List<ProjectHandlerListener> listeners = new ArrayList<ProjectHandlerListener>();
	private AisCoverageProject project = null;
	
	private void terminateProject(){
		if(project != null){
			if(this.project.isRunning()){
				this.project.stopAnalysis();
			}
		}
		project = null;
	}
	public void analysisStopped(){
		for (ProjectHandlerListener listener : listeners) {
			listener.analysisStopped();
		}
	}
	
	public void analysisStarted(){
		for (ProjectHandlerListener listener : listeners) {
			listener.analysisStarted();
		}
	}
	
	public AisCoverageProject createProject(){
		terminateProject();
		this.project = new AisCoverageProject();
		
		for (ProjectHandlerListener listener : listeners) {
			listener.projectCreated();
		}
			
		return project;
	}

	public void addProjectHandlerListener(ProjectHandlerListener listener){
		listeners.add(listener);
	}
	public void saveProject(AisCoverageProject project, String filename){
		try {
			FileOutputStream saveFile = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(project);
			out.close();
			
//			for (AisCoverageListener listener : listeners) {
//				listener.projectSaved();
//			}
			System.out.println("Project saved");
			
		} catch (IOException e) {
			e.printStackTrace();
//			LOG.error("Failed to save settings file");
		}
	}
	public AisCoverageProject loadProject(String filename){
		try {
			terminateProject();
			
			FileInputStream loadFile = new FileInputStream(filename);
			ObjectInputStream in = new ObjectInputStream(loadFile);
			AisCoverageProject project = (AisCoverageProject) in.readObject();
			in.close();

//			
			this.project = project;
			for (ProjectHandlerListener listener : listeners) {
				listener.projectLoaded();
			}
			return project;
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public AisCoverageProject getProject() {
		return project;
	}
	
	//Singleton stuff
		private static ProjectHandler singletonObject;

		private ProjectHandler() {

		}
		
		public static synchronized ProjectHandler getInstance() {
			if (singletonObject == null) {
				singletonObject = new ProjectHandler();
			}
			return singletonObject;
		}
		
		

		public Object clone() throws CloneNotSupportedException {
			throw new CloneNotSupportedException();
		}
}
