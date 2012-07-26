package dk.dma.aiscoverage.gui;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
//<<<<<<< HEAD
import javax.swing.JOptionPane;
//=======
import javax.swing.JPanel;
//>>>>>>> acf3d52844b593fb2667a0ca28eb8be8299590fb
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import dk.dma.aiscoverage.data.BaseStation;
import dk.dma.aiscoverage.data.BaseStationHandler;
import dk.dma.aiscoverage.export.ImageGenerator;
import dk.dma.aiscoverage.export.KMLGenerator;
import dk.dma.aiscoverage.project.ProjectHandler;


public class GUIHelper {
	
	
	JFileChooser fileChooser = new JFileChooser(){
		@Override
		public void approveSelection(){
		    File f = getSelectedFile();
		    if(f.exists() && getDialogType() == SAVE_DIALOG){
		        int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
		        switch(result){
		            case JOptionPane.YES_OPTION:
		                super.approveSelection();
		                return;
		            case JOptionPane.NO_OPTION:
		                return;
		            case JOptionPane.CLOSED_OPTION:
		                return;
		            case JOptionPane.CANCEL_OPTION:
		                cancelSelection();
		                return;
		        }
		    }
		    super.approveSelection();
		}
	};
	

	//JFileChooser fileChooser = null;
	FileFilter internalFilter = null;
	FileFilter kmlFilter = null;
	FileFilter shapeFilter = null;
	FileFilter pngFilter = null;
	ProjectHandler projectHandler = ProjectHandler.getInstance();
	
	
	public GUIHelper()
	{
		internalFilter = new FileNameExtensionFilter("aiscoverage", "aiscoverage"); 
		kmlFilter = new FileNameExtensionFilter("kml", "kml"); 
//<<<<<<< HEAD
		//shapeFilter = new FileNameExtensionFilter("shape", "shape"); 	
		//fileChooser.setBounds(0, 0, 600, 400);
//=======
		shapeFilter = new FileNameExtensionFilter("shape", "shape"); 
		pngFilter = new FileNameExtensionFilter("png", "png"); 
		fileChooser.setBounds(0, 0, 600, 400);
//>>>>>>> acf3d52844b593fb2667a0ca28eb8be8299590fb
	}
	
	
	public String openShapeFileDialog()
	{
		String fileUrl = null;
		//fileChooser = new JFileChooser();
		//fileChooser.setBounds(0, 0, 600, 400);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.removeChoosableFileFilter(internalFilter);
		fileChooser.removeChoosableFileFilter(shapeFilter);
		fileChooser.removeChoosableFileFilter(kmlFilter);
		fileChooser.setFileFilter(shapeFilter);

		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			fileUrl = fileChooser.getSelectedFile().getPath();
			System.out.println(fileUrl);
		}		
		return fileUrl;
	}
	
	public String openAISFileDialog()
	{
		String fileUrl = null;
		//fileChooser = new JFileChooser();
		//fileChooser.setBounds(0, 0, 600, 400);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.removeChoosableFileFilter(internalFilter);
		fileChooser.removeChoosableFileFilter(shapeFilter);
		fileChooser.removeChoosableFileFilter(kmlFilter);
		//fileChooser.setFileFilter(internalFilter);

		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			fileUrl = fileChooser.getSelectedFile().getPath();
			System.out.println(fileUrl);
		}		
		return fileUrl;
	}
	
	public void openFileDialog()
	{
		String fileUrl = null;
		//fileChooser = new JFileChooser();
		//fileChooser.setBounds(0, 0, 600, 400);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.removeChoosableFileFilter(internalFilter);
		fileChooser.removeChoosableFileFilter(shapeFilter);
		fileChooser.removeChoosableFileFilter(kmlFilter);
		fileChooser.setFileFilter(internalFilter);

		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			fileUrl = fileChooser.getSelectedFile().getPath();
			System.out.println(fileUrl);
			projectHandler.loadProject(fileUrl);
		}		
	}
	
	public void saveFileDialog()
	{
		//fileChooser = new JFileChooser();
		//fileChooser.setBounds(0, 0, 600, 400);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.removeChoosableFileFilter(internalFilter);
		fileChooser.removeChoosableFileFilter(shapeFilter);
		fileChooser.removeChoosableFileFilter(kmlFilter);
		fileChooser.setFileFilter(internalFilter);

		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			String fileUrl = fileChooser.getSelectedFile().getPath();
			System.out.println(""+fileUrl);
			
			if(!fileUrl.endsWith(".aiscoverage"))
			{
			    fileUrl = (fileUrl + ".aiscoverage");
			}
			
			
			projectHandler.saveProject(projectHandler.getProject(), fileUrl+"");
			//.aiscoverage
		}
	}
	
	/*
	 * not working properly
	 */
	public void saveKMLDialog()
	{
		//fileChooser = new JFileChooser();
		//fileChooser.setBounds(0, 0, 600, 400);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.removeChoosableFileFilter(internalFilter);
		fileChooser.removeChoosableFileFilter(shapeFilter);
		fileChooser.removeChoosableFileFilter(kmlFilter);
		fileChooser.setFileFilter(kmlFilter);

		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			String fileUrl = fileChooser.getSelectedFile().getPath();
			System.out.println(""+fileUrl);
			
//<<<<<<< HEAD
			KMLGenerator.generateKML(ProjectHandler.getInstance().getProject().getCoverageCalculator() , fileUrl+".kml");
//=======
			if(!fileUrl.endsWith(".kml"))
			{
			    fileUrl = (fileUrl + ".kml");
			}
			
			//ArrayList<BaseStation> list = new ArrayList<BaseStation>(projectHandler.getProject().getBaseStationHandler().grids.values());
			//KMLGenerator.generateKML(list , fileUrl+".kml");
//>>>>>>> merged
			
			System.out.println(fileUrl + " printet");
		}
	}
	public void savePNGDialog(JPanel panel){
		fileChooser = new JFileChooser();
		fileChooser.setBounds(0, 0, 600, 400);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(pngFilter);

		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			String fileUrl = fileChooser.getSelectedFile().getPath();
			System.out.println(""+fileUrl);
			
			ImageGenerator.generatePNG(panel , fileUrl+".png");
			
			System.out.println(fileUrl + " saved");
		}
	}
	
	
	public void saveShapeDialog()
	{
		//fileChooser = new JFileChooser();
		//fileChooser.setBounds(0, 0, 600, 400);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.removeChoosableFileFilter(internalFilter);
		fileChooser.removeChoosableFileFilter(shapeFilter);
		fileChooser.removeChoosableFileFilter(kmlFilter);
		fileChooser.setFileFilter(shapeFilter);

		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			String fileUrl = fileChooser.getSelectedFile().getPath();
			System.out.println(""+fileUrl);
			
			if(!fileUrl.endsWith(".shape"))
			{
			    fileUrl = (fileUrl + ".shape");
			}
			
			
			File fileName = new File( fileChooser.getSelectedFile( ) + ".shape" );
			try {
				BufferedWriter outFile = new BufferedWriter( new FileWriter( fileName ) );
				/*
				 * write polygon layer data to file
				 */
				//outFile.write( getTxtArea_log().getText( ) ); //put in textfile
				outFile.flush( ); // redundant, done by close()
				outFile.close( );
				
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	

}
	


