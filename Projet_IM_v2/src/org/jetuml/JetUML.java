
package org.jetuml;

import static org.jetuml.application.ApplicationResources.RESOURCES;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.Version;
import org.jetuml.geom.Rectangle;
import org.jetuml.gui.EditorFrame;
import org.jetuml.gui.GuiUtils;
import org.jetuml.gui.tips.TipDialog;
import org.json.JSONException;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Entry point for launching JetUML.
 */
public final class JetUML extends Application
{
	@SuppressWarnings("exports")
	public static final Version VERSION = Version.create(3, 5);
	
	private static HostServices aHostServices; // Required to open a browser page.
	
	/**
	 * @param pArgs Not used.
	 */
	public static void main(String[] pArgs)
	{
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		launch(pArgs);
	}
	
	@Override
	public void start(Stage pStage) throws Exception ,IOException
	{
		aHostServices = getHostServices();
		setStageBoundaries(pStage);

		pStage.setTitle(RESOURCES.getString("application.name"));
		pStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));

		pStage.setScene(new Scene(new EditorFrame(pStage, openWith())));
		pStage.getScene().getStylesheets().add(getClass().getResource("JetUML.css").toExternalForm());

		pStage.setOnCloseRequest(pWindowEvent -> 
		{
			pWindowEvent.consume();
			((EditorFrame)((Stage)pWindowEvent.getSource()).getScene().getRoot()).exit();
		});
		pStage.show();
		
		if(UserPreferences.instance().getBoolean(UserPreferences.BooleanPreference.showTips))
		{
			new TipDialog(pStage).show();
		}

		
	}
	
	// If the first argument passed to the application is a valid file, open it.
	private Optional<File> openWith()
	{
		List<String> parameters = getParameters().getUnnamed();
		if( parameters.isEmpty() )
		{
			return Optional.empty();
		}
		File file = new File(parameters.get(0));
		if(file.exists() && !file.isDirectory())
		{
			return Optional.of(file);
		}
		else
		{
			return Optional.empty();
		}
	}
	
	/**
	 * Open pUrl in the default system browser.
	 * 
	 * @param pUrl The url to open.
	 * @pre pUrl != null
	 */
	public static void openBrowser(String pUrl)
	{
		assert pUrl != null;
		aHostServices.showDocument(pUrl);
	}

	private static void setStageBoundaries(Stage pStage)
	{
		Rectangle defaultStageBounds = GuiUtils.defaultStageBounds();
		pStage.setX(defaultStageBounds.getX());
		pStage.setY(defaultStageBounds.getY());
		pStage.setWidth(defaultStageBounds.getWidth());
		pStage.setHeight(defaultStageBounds.getHeight());
	}
}