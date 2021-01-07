package ch.correvon.google.calendar.window.mainWindow;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class FilterListener implements DocumentListener, KeyListener
{
	private MainWindow mainWindow;
	
	public FilterListener(MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
	}
	
	@Override public void removeUpdate(DocumentEvent e){}
	@Override public void changedUpdate(DocumentEvent e){}
	@Override public void insertUpdate(DocumentEvent e)
	{
//		Document document = e.getDocument();
//		try
//		{
//			String s = document.getText(0, document.getLength());
//		}
//		catch(BadLocationException e1)
//		{
//			return;
//		}
		this.mainWindow.filterEvents();
	}
	
	@Override public void keyPressed(KeyEvent arg0){}
	@Override public void keyReleased(KeyEvent arg0){}
	@Override public void keyTyped(KeyEvent arg0)
	{
		this.mainWindow.filterEvents();
	}
}