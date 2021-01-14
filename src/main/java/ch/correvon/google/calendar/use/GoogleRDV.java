package ch.correvon.google.calendar.use;

import java.text.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ch.correvon.google.calendar.window.mainWindow.MainWindow;

public class GoogleRDV
{
	private static Log s_logger = LogFactory.getLog(GoogleRDV.class);
	public static final String APPLICATION_NAME = "PUMBAA google-cal RENDEZ-VOUS";
	private static final String VERSION = "0.1";

	/*
	 * https://console.developers.google.com/apis/credentials?project=medical-calendar-1125
	 */
	public static void main(String[] args) throws ParseException, InterruptedException
	{
		s_logger.info("Démarrage du programme");
		new MainWindow(APPLICATION_NAME + " (" + VERSION + ")").run();
	}

	/*
	 *	TODO
	 *	Better Readme
	 *	batch delete
	 *
	 *	Version NodeJS ? (branche séparée)
	 */
}