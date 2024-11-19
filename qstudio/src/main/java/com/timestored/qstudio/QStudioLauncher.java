/*
 * qStudio - Free SQL Analysis Tool
 * Copyright C 2013-2023 TimeStored
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.timestored.qstudio;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import com.timestored.TimeStored;
import com.timestored.connections.ConnectionManager;
import com.timestored.docs.OpenDocumentsModel;
import com.timestored.misc.AppLaunchHelper;
import com.timestored.misc.ErrorReporter;
import com.timestored.plugins.PluginLoader;
import com.timestored.swingxx.ApplicationInstanceListener;
import com.timestored.swingxx.ApplicationInstanceManager;
import com.timestored.swingxx.SwingUtils;

/**
 * Launches the qStudio Application. Checks licensing.
 */
public class QStudioLauncher  {

	private static final Color BLUE_LOGO_BG = new Color(0, 124, 195);
	
	private static QStudioFrame appFrame;
	private static final Logger LOG = Logger.getLogger(QStudioLauncher.class.getName());

	// catch all errors and allow user to report
	private static final String ERR_URL = TimeStored.getContactUrl("qStudio Error Report");
	private static final int MINS_BETWEEN = 60*12;
	
	public static final ErrorReporter ERR_REPORTER = new ErrorReporter(ERR_URL, 
			TimeStored.TECH_EMAIL_ADDRESS, "qStudio Bug Report " + QStudioFrame.VERSION, MINS_BETWEEN);

	
	public static void main(final String... args) throws InterruptedException, InvocationTargetException {
        
		/*
		 * If filename passed, send to existing instance where possible.
		 * Otherwise start a new instance. Put this first in main(..) so that file opens are quick.
		 */
		boolean firstInstance = ApplicationInstanceManager.registerInstance(args);
		
		if(!firstInstance) {
			System.out.println("Not the first instance.");
			if (args.length>0) {
				System.out.println("I had arguments, they were handled so EXIT.");
				return;
			}
		}
		
		// if appframe already send it args to process
		ApplicationInstanceManager.setApplicationInstanceListener(new ApplicationInstanceListener() {
			@Override public void newInstanceCreated(List<String> args) {
				System.out.println("New instance detected...");
				if(args.size()>0 && appFrame!= null) {
					appFrame.handleArgs(args);
					EventQueue.invokeLater(new Runnable() {
						@Override public void run() {
							SwingUtils.forceToFront(appFrame);
						}
					});
				}
			}
		});

		try {
			boolean foundPlugins = PluginLoader.loadPlugins(QStudioModel.APP_TITLE.toLowerCase());
		} catch(Throwable ucve) {
			LOG.severe("Could not load a plugin due to " + ucve.getLocalizedMessage());
		}

        SwingUtilities.invokeAndWait(new Runnable(){
            @Override public void run(){
        		launch(args);
            }



			private void launch(final String... args) {
		        
				OpenDocumentsModel openDocumentsModel = OpenDocumentsModel.newInstance();
				String title = QStudioModel.APP_TITLE;
                Persistance persistance = Persistance.INSTANCE;
                
                // persist first ever run date for license purposes
                // they get 4 weeks trial then forced to buy
                long firstEverRun = persistance.getLong(Persistance.Key.FERDB, -1);
                if(firstEverRun == -1) {
                	firstEverRun = System.currentTimeMillis();
                	persistance.putLong(Persistance.Key.FERDB, firstEverRun);
                }

				AppLaunchHelper.setMacAndWindowsAppearance(title);
				AppLaunchHelper.setTheme(MyPreferences.INSTANCE.getCodeTheme());
				AppLaunchHelper.logToUsersFolder(QStudioModel.LEGACY_FOLDER_NAME);
				LOG.info("Starting QStudioLauncher  launch() ###################################");
				LOG.info("version = " + QStudioFrame.VERSION);
				LOG.info("current dir = " + new File(".").getAbsolutePath());
				LOG.info("PATH = " + System.getenv("PATH"));
				LOG.info("JAVA_HOME = " + System.getenv("JAVA_HOME"));
				LOG.info("java.version =" + System.getProperty("java.version"));
				LOG.info("os.name =" + System.getProperty("os.name"));
				LOG.info("user.home =" + System.getProperty("user.home"));
				LOG.info("user.dir =" + System.getProperty("user.dir"));


				Thread.setDefaultUncaughtExceptionHandler(ERR_REPORTER.getUncaughtExceptionHandler());

				// if not using the built-in java SE6 splash, create our own
//				JDialog dialog = null;
//				if(SplashScreen.getSplashScreen() == null) {
//					URL r = QStudioLauncher.class.getResource("splash.png");
//					dialog = SwingUtils.showSplashDialog(r, BLUE_LOGO_BG, "        Version: " + QStudioFrame.VERSION);
//				}

        		ConnectionManager conMan = ConnectionManager.newInstance();
        		
        		// only set a default if one was truly set.
        		MyPreferences my = MyPreferences.INSTANCE;
        		String u = my.getDefaultLoginUsername();
        		String p = my.getDefaultLoginPassword();
        		if(u != null || p != null) {
        			conMan.setDefaultLogin(u, p);
        		}
        		
        		conMan.setPreferenceStore(persistance.getPref(), Persistance.Key.CONNECTIONS.name());
        		QStudioModel qStudioModel = new QStudioModel(conMan, persistance, openDocumentsModel);

        		try {
        			Class<?> clazz = Class.forName("com.timestored.pro.kdb.PluginInitialiser");
        			clazz.getDeclaredMethod("init", QStudioModel.class).invoke(null, qStudioModel);
        		} catch(Throwable ucve) {
        			LOG.severe("Could not load a plugin due to " + ucve.getLocalizedMessage());
        		}
        		
            	appFrame = new QStudioFrame(qStudioModel);
            	appFrame.setVisible(true);

//            	// if we showed our own dialog, hide it now
//				if(dialog != null) {
//		            dialog.setVisible(false);
//		            dialog.dispose();
//				}
				
            	appFrame.handleArgs(Arrays.asList(args));

				LOG.info("Ending QStudioLauncher launch()");
			}
        });
	}

}
