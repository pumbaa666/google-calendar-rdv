package ch.correvon.google.calendar.helper;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BrowserHelper
{
	public static boolean openWebpage(String strUrl)
	{
		try
		{
			return openWebpage(new URI(strUrl));
		}
		catch(URISyntaxException e)
		{
			s_logger.error("URL incorrecte", e);
		}
		
		return false;
	}
	
	public static boolean openWebpage(URI uri)
	{
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
		{
			try
			{
				desktop.browse(uri);
				return true;
			}
			catch(Exception e)
			{
				s_logger.error("Impossible d'ouvrir le navigateur par défaut", e);
			}
		}
		else
			s_logger.error("Impossible d'ouvrir le navigateur par défaut sur ce système");

		return false;
	}

	public static boolean openWebpage(URL url)
	{
		try
		{
			return openWebpage(url.toURI());
		}
		catch(URISyntaxException e)
		{
			s_logger.error("Impossible d'ouvrir le navigateur par défaut", e);
		}
		
		return false;
	}
	
	private static Log s_logger = LogFactory.getLog(BrowserHelper.class);
}
