/*
 * Copyright 2012 Danish Maritime Authority. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
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
package dk.frv.enav.acv;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.bbn.openmap.MapHandler;
import com.bbn.openmap.PropertyConsumer;

import dk.frv.ais.proprietary.GatehouseFactory;
import dk.frv.ais.reader.RoundRobinAisTcpReader;
import dk.frv.enav.acv.ais.AisHandler;
import dk.frv.enav.acv.gui.MainFrame;
import dk.frv.enav.acv.settings.Settings;
import dk.frv.enav.acv.util.OneInstanceGuard;


/**
 * Main class with main method.
 * 
 * Starts up components, bean context and GUI. 
 *
 */
public class ACV {
	
	private static String VERSION;
	private static String MINORVERSION;
	private static Logger LOG;	
	private static MainFrame mainFrame;	
	private static MapHandler mapHandler;
	private static Settings settings;
	private static Properties properties = new Properties();

	private static AisHandler aisHandler;
	private static ExceptionHandler exceptionHandler = new ExceptionHandler();
	
	public static void main(String[] args) {
		// Set up log4j logging
		DOMConfigurator.configure("log4j.xml");
        LOG = Logger.getLogger(ACV.class);
        
        // Set default exception handler        
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
        
        VERSION = "0.1 Alpha";
        LOG.info("Starting ACV version " + VERSION);
        LOG.info("Copyright (C) 2012 Danish Maritime Safety Administration");
        LOG.info("This program comes with ABSOLUTELY NO WARRANTY.");
        LOG.info("This is free software, and you are welcome to redistribute it under certain conditions.");
        LOG.info("For details see LICENSE file.");

        
        // Load properties
        loadProperties();
                
        // Create the bean context (map handler)
        mapHandler = new MapHandler();
        
        // Load settings or get defaults and add to bean context       
        if (args.length > 0) {        	
        	settings = new Settings(args[0]);
        } else {
        	settings = new Settings();
        }       
        LOG.info("Using settings file: " + settings.getSettingsFile());
        settings.loadFromFile();
        mapHandler.add(settings);
        
        // Determine if instance already running and if that is allowed
        OneInstanceGuard guard = new OneInstanceGuard("acv.lock");
        if (guard.isAlreadyRunning()) {
        	JOptionPane.showMessageDialog(null, "One application instance already running. Stop instance or restart computer.", "Error", JOptionPane.ERROR_MESSAGE);
        	System.exit(1);
        }
        
        // Start AIS target monitoring
        aisHandler = new AisHandler();
//        aisHandler.loadView();
        mapHandler.add(aisHandler);
        
        RoundRobinAisTcpReader reader = new RoundRobinAisTcpReader();
        reader.setCommaseparatedHostPort("10.3.246.100:4001");
        
//        reader.setTimeout(getInt("ais_source_timeout." + name, "10"));
//        reader.setReconnectInterval(getInt("ais_source_reconnect_interval." + name, "5") * 1000);

        // Register proprietary handlers
        reader.addProprietaryFactory(new GatehouseFactory());
 
        reader.registerHandler(aisHandler);
        
        // Create plugin components
        createPluginComponents();
        
        // Create and show GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        
        
	}
	

	private static void loadProperties() {
		InputStream in = ACV.class.getResourceAsStream("/acv.properties");		
		try {
			if (in == null) {
				throw new IOException("Properties file not found");
			}
			properties.load(in);
			in.close();
		} catch (IOException e) {
			LOG.error("Failed to load resources: " + e.getMessage());
		}		
	}
	
	private static void createAndShowGUI() {
		// Set the look and feel.
		initLookAndFeel();
		
		// Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        // Create and set up the main window        
		mainFrame = new MainFrame();
		mainFrame.setVisible(true);
		
	    // Create keybinding shortcuts
        makeKeyBindings();
		

	}
	
	private static void makeKeyBindings(){
	      JPanel content = (JPanel) mainFrame.getContentPane();
	      InputMap inputMap = content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		
	    @SuppressWarnings("serial")
		Action zoomIn = new AbstractAction() {
	        public void actionPerformed(ActionEvent actionEvent) {
	        	mainFrame.getChartPanel().doZoom(0.5f);
	        }
	      };
	      
		@SuppressWarnings("serial")
		Action zoomOut = new AbstractAction() {
		    public void actionPerformed(ActionEvent actionEvent) {
		    	mainFrame.getChartPanel().doZoom(2f);
		        }
		      };	      
			
		@SuppressWarnings("serial")
		Action panUp = new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				mainFrame.getChartPanel().pan(1);
				}
			};
		@SuppressWarnings("serial")
		Action panDown = new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				mainFrame.getChartPanel().pan(2);
				}
			};			
			
		@SuppressWarnings("serial")
		Action panLeft = new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				mainFrame.getChartPanel().pan(3);
				}
			};
		@SuppressWarnings("serial")
		Action panRight = new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				mainFrame.getChartPanel().pan(4);
				}
			};			
	   			
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, 0), "ZoomIn");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PLUS, 0), "ZoomIn");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, 0), "ZoomOut");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, 0), "ZoomOut");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, 0), "panUp");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, 0), "panDown");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, 0), "panLeft");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, 0), "panRight");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_UP, 0), "panUp");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_DOWN, 0), "panDown");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_LEFT, 0), "panLeft");
	      inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_RIGHT, 0), "panRight");	      
     
	      
	      content.getActionMap().put("ZoomOut", zoomOut);
	      content.getActionMap().put("ZoomIn", zoomIn);
	      content.getActionMap().put("panUp", panUp);	
	      content.getActionMap().put("panDown", panDown);	
	      content.getActionMap().put("panLeft", panLeft);	
	      content.getActionMap().put("panRight", panRight);	
	      
	}
	
	private static void initLookAndFeel() {
		try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { 
        	LOG.error("Failed to set look and feed: " + e.getMessage());
        }

	}
	
	public static void closeApp() {
		closeApp(false);
	}
	
	public static void closeApp(boolean restart) {
		// Shutdown routine
		mainFrame.saveSettings();
		settings.saveToFile();
//		routeManager.saveToFile();
//		msiHandler.saveToFile();
//		aisHandler.saveView();
		LOG.info("Closing ACV");
		System.exit(restart ? 2 : 0);
	}
	
	private static void createPluginComponents() {
		Properties props = getProperties();
		String componentsValue = props.getProperty("acv.plugin_components");
		if (componentsValue == null) {
			return;
		}
		String[] componentNames = componentsValue.split(" ");
		for (String compName : componentNames) {
			String classProperty = compName + ".class";
			String className = props.getProperty(classProperty);
			if (className == null) {
				LOG.error("Failed to locate property " + classProperty);
				continue;
			}
			// Create it if you do...
			try {
				Object obj = java.beans.Beans.instantiate(null, className);
				if (obj instanceof PropertyConsumer) {
					PropertyConsumer propCons = (PropertyConsumer) obj;
					propCons.setProperties(compName, props);
				}
				mapHandler.add(obj);
			} catch (IOException e) {
				LOG.error("IO Exception instantiating class \"" + className + "\"");
			} catch (ClassNotFoundException e) {
				LOG.error("Component class not found: \"" + className + "\"");
			}
		}
	}
	
	public static Properties getProperties() {
		return properties;
	}
	
	public static String getVersion() {
		return VERSION;
	}
	
	public static String getMinorVersion() {
		return MINORVERSION;
	}
	
	public static Settings getSettings() {
		return settings;
	}
	
	public static MainFrame getMainFrame() {
		return mainFrame;
	}
	
	public static AisHandler getAisHandler() {
		return aisHandler;
	}
	
	public static MapHandler getMapHandler() {
		return mapHandler;
	}
	
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
	}
	
	public static void startThread(Runnable t, String name) {
		Thread thread = new Thread(t);
		thread.setName(name);
		thread.start();
	}
	
	public static double elapsed(long start) {
		double elapsed = System.nanoTime() - start;
		return elapsed / 1000000.0;
	}


}
