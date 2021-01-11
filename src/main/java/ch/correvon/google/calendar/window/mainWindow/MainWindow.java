package ch.correvon.google.calendar.window.mainWindow;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ch.correvon.google.calendar.GoogleCalendarService;
import ch.correvon.google.calendar.components.TimedLabel;
import ch.correvon.google.calendar.helper.BrowserHelper;
import ch.correvon.google.calendar.helper.DateHelper;
import ch.correvon.google.calendar.helper.PreferencesBundle;
import ch.correvon.google.calendar.object.MyCalendar;
import ch.correvon.google.calendar.object.MyEvent;
import ch.correvon.google.calendar.window.mainWindow.jtable.CalendarModel;
import ch.correvon.google.calendar.window.mainWindow.jtable.ColorColumnCellRenderer;
import ch.correvon.google.calendar.window.mainWindow.jtable.EventModel;
import ch.correvon.google.calendar.window.mainWindow.jtable.MySorter;

/**
 * JCalendar : http://toedter.com/jcalendar/
 * @author Loïc Correvon
 */
public class MainWindow extends JFrame implements WindowListener
{
	/* ------------------------------------------------------------------------ *\
	|* 		  						Constructeur								*|
	\* ------------------------------------------------------------------------ */
	public MainWindow(String title)
	{
		super(title);
		this.addWindowListener(this);

		this.initComponents();
		this.myInit();

		this.loadPref();

		super.setVisible(false);
	}

	/* ------------------------------------------------------------------------ *\
	|* 		  					Méthodes publiques								*|
	\* ------------------------------------------------------------------------ */
	public void run()
	{
		super.setVisible(true);
	}

	/* ------------------------------------------------------------------------ *\
	|* 		  					Méthodes privées								*|
	\* ------------------------------------------------------------------------ */
	private void exit()
	{
		this.writePref();
		super.dispose();
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Window listener							*|
	\* ------------------------------------------------------------ */
	@Override public void windowActivated(WindowEvent e){}
	@Override public void windowClosed(WindowEvent e){}
	@Override public void windowDeactivated(WindowEvent e){}
	@Override public void windowDeiconified(WindowEvent e){}
	@Override public void windowIconified(WindowEvent e){}
	@Override public void windowOpened(WindowEvent e){}
	@Override public void windowClosing(WindowEvent e)
	{
		this.exit();
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Key listener							*|
	\* ------------------------------------------------------------ */

	/* ------------------------------------------------------------ *\
	|* 		  				GUI Events								*|
	\* ------------------------------------------------------------ */
	private void buttonConnectMouseClicked(MouseEvent e)
	{
		if(!this.connected)
			this.connect();
		else
			this.disconnect();
	}

	private void menuFileQuitActionPerformed(ActionEvent e)
	{
		this.exit();
	}
	
//	private void startEditingTable(final JTable table)
//	{
//		TableModel model = table.getModel();
//		final int row = model.getRowCount() - 1;
//		final int col = 1; // Colone du nom
//
//		SwingUtilities.invokeLater(new Runnable() // Invoke later to let current table event to consume
//			{
//				@Override public void run()
//				{
//					// http://www.java2s.com/Code/Java/Swing-JFC/ProgrammaticallyStartingCellEditinginaJTableComponent.htm
//					boolean success = table.editCellAt(row, col); // Start editing
//					if(!success)
//						return;
//
//					MainWindow.this.tableSchedule.changeSelection(row, col, false, false);
//					Component editorComponent = table.getEditorComponent();
//					if(editorComponent == null)
//						return;
//					editorComponent.requestFocusInWindow(); // Request focus to show editing cursor
//
//					if(!(editorComponent instanceof JTextComponent)) // Select all text
//						return;
//					JTextComponent txt = (JTextComponent)editorComponent;
//					txt.setSelectionStart(0);
//					txt.setSelectionEnd(txt.getText().length());
//				}
//			});
//	}

	/* ------------------------------------------------------------ *\
	|* 		  				File methods							*|
	\* ------------------------------------------------------------ */
	private void loadPref()
	{
		PreferencesBundle.readPref();
		Map<String, String> metadata = PreferencesBundle.getPref();

		try
		{
			int x = Integer.parseInt(metadata.get(PREF_MAIN_WINDOW_X));
			int y = Integer.parseInt(metadata.get(PREF_MAIN_WINDOW_Y));
			int w = Integer.parseInt(metadata.get(PREF_MAIN_WINDOW_W));
			int h = Integer.parseInt(metadata.get(PREF_MAIN_WINDOW_H));
			super.setLocation(x, y);
			super.setSize(w, h);
			
			// TODO timeinterval, agendas sélectionnés
		}
		catch(NumberFormatException e)
		{
			s_logger.warn("Error setting window localization preferences", e);
		}
	}

	private void writePref()
	{
		PreferencesBundle.writePref(this.dumpPref());
	}

	private Map<String, String> dumpPref()
	{
		Map<String, String> metadata = new HashMap<String, String>(20);

		metadata.put(PREF_MAIN_WINDOW_X, "" + super.getLocation().x);
		metadata.put(PREF_MAIN_WINDOW_Y, "" + super.getLocation().y);
		metadata.put(PREF_MAIN_WINDOW_W, "" + super.getSize().width);
		metadata.put(PREF_MAIN_WINDOW_H, "" + super.getSize().height);
		
		// TODO timeinterval, agendas sélectionnés

		return metadata;
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Google events							*|
	\* ------------------------------------------------------------ */
	private void connect()
	{
//		this.connected = false;

		this.buttonRefresh.setEnabled(false);		
		this.labelAccountValue.setText("Connexion en cours");
		this.eventModel.clear();

		new SwingWorker<String, CalendarListEntry>()
			{
				@Override protected String doInBackground()
				{
					// Connexion au service google
					MainWindow.this.service = GoogleCalendarService.getService();
					if(MainWindow.this.service == null)
					{
						MainWindow.this.labelAccountValue.setText("Impossible de se connecter à l'agenda, consultez les logs");
						s_logger.error("Impossible de se connecter à l'agenda");
						return null;
					}

					// Récupération de tous les calendriers
					String mainCalendarName = null;
					List<CalendarListEntry> calendars = MainWindow.this.service.getAllCalendar();
					for(CalendarListEntry calendar:calendars)
					{
						if(calendar.getPrimary() != null && calendar.getPrimary() == true)
							mainCalendarName = calendar.getSummary() + " (" + calendar.getId() + ")";
						publish(calendar); // Va appeler la méthode process du SwingWorker (ci-dessous)
					}
					return mainCalendarName;
				}

				@Override protected void process(List<CalendarListEntry> calendars) // Appelé (quand cela est possible) par publish.
				{
					// Comme cette méthode n'est pas appelée systematiquement lors de l'appel à publish
					// il faut s'assurer d'entrer toutes les valeurs (calendars est peuplé correctement).
					// En pratique, pour des si petites quantité de calendrier, elle est appellée peu souvent (1x, en fait...),
					// on peut donc se permettre de tout supprimer et tout ré-importer
					MainWindow.this.calendarModel.clear();
					for(CalendarListEntry calendar:calendars)
						MainWindow.this.calendarModel.add(new MyCalendar(calendar, true));
				}

				@Override protected void done() // Appelé à la fin de doInBackground
				{
					try
					{
						String mainCalendarName = get(); // Méthode bloquante qui attend la fin de doInBackground et récupère son résultat.
						MainWindow.this.labelAccountValue.setText(mainCalendarName);

						MainWindow.this.go();
						
//						MainWindow.this.connected = true; // La connexion sera déterminée comme "établie" une fois le calendrier sélectionné (c'est une nouvelle requête Google)
//						MainWindow.this.labelMessage.showWarning("Veuillez sélectionner un calendrier");
					}
					catch(InterruptedException | ExecutionException e)
					{
						s_logger.error(e);
					}
				}
			}.execute();
	}
	
	private void consumeSelectedCalendars()
	{
		if(this.selectedCalendars == null || this.selectedCalendars.isEmpty())
		{
			this.buttonConnect.setEnabled(true);
			this.buttonConnect.setText("Déconnexion");
			this.buttonConnect.setForeground(new Color(186, 101, 16));
			this.labelMessage.hideMessage();

			this.connected = true; // La connexion sera déterminée comme "établie" une fois le dernier calendrier sélectionné (c'est une nouvelle requête Google)
			this.buttonRefresh.setEnabled(true);
			
			this.labelMessage.showMessage("Recherche terminée");
			
			this.filterEvents();
			
			return;
		}
		
		this.selectCalendar(this.selectedCalendars.remove(0));
	}
	
	private void updateLabelNbEvents()
	{
		int nbTot = this.allEvents.size();
		int nbFiltered = this.eventModel.getRowCount();
		String strNb = nbTot + "";
		if(nbTot != nbFiltered)
			strNb = nbFiltered + " / " + nbTot;
		this.labelNbEvents.setText(strNb);
	}
	
	private void disconnect()
	{
		this.connected = false;
		this.buttonRefresh.setEnabled(false);
		this.buttonConnect.setText("Connexion");
		this.buttonConnect.setForeground(new Color(0, 134, 175));
		this.calendarModel.clear();
		this.eventModel.clear();
		this.updateLabelNbEvents();
	}
	
	public void setDatePicker()
	{
		try
		{
			this.datePickerFrom.setDate(DateHelper.dateToLocalDate(DATE_SDF.parse(this.txtDateFrom.getText())));
			this.datePickerTo.setDate(DateHelper.dateToLocalDate(DATE_SDF.parse(this.txtDateTo.getText())));
		}
		catch(ParseException e)
		{
			s_logger.debug("rien");
		}
	}
	
	public void filterEvents()
	{
		String filter = this.txtFilter.getText();
		if(filter == null)
			filter = "";
		filter = filter.trim().toLowerCase();
		
		Date minDate = null;
		Date maxDate = null;
		try
		{
			minDate = DATE_SDF.parse(MainWindow.this.txtDateFrom.getText());
			maxDate = DATE_SDF.parse(MainWindow.this.txtDateTo.getText());
			maxDate.setTime(maxDate.getTime() + + 24 * 3600 * 1000 - 1); // Pour aller jusqu'à la fin de la journée, sinon on ne voit pas les events du dernier jour du mois
		}
		catch(ParseException e)
		{
		}

		this.eventModel.clear();
		for(MyEvent event:this.allEvents)
		{
			if(!event.getEvent().getSummary().toLowerCase().contains(filter))
				continue;
			
			Date eventDate;
			DateTime dateTime = event.getEvent().getStart().getDate();
			if(dateTime == null)
				dateTime = event.getEvent().getStart().getDateTime();
			if(dateTime == null)
				eventDate = minDate = maxDate = null; // Si l'event n'a pas de date on set les filtres à null pour ne pas les tester et ajouter d'office l'event
			else
				eventDate = new Date(dateTime.getValue());
			
			if(minDate != null)
				if(eventDate.before(minDate))
					continue;
			if(maxDate != null)
				if(eventDate.after(maxDate))
					continue;
			
			this.eventModel.add(event.getCalendar(), event.getEvent());
		}
		this.updateLabelNbEvents();
		this.eventModel.sortByColumn();
	}

	private void getEvents(final MyCalendar calendar)
	{
//		if(!this.connected)
//			return;
		this.googleEvents = null;

		if(this.eventSwingWorker != null && this.eventSwingWorker.getState() != SwingWorker.StateValue.DONE) // Si il y a déjà un thread en cours
		{
//			synchronized(this)
//			{
//				this.refreshEvent = true;
//			}
			return;
		}

		this.eventSwingWorker = new SwingWorker<Void, Void>()
			{
				@Override protected Void doInBackground()
				{
					Date minDate = null;
					Date maxDate = null;
					try
					{
						minDate = DATE_SDF.parse(MainWindow.this.txtDateFrom.getText());
						maxDate = DATE_SDF.parse(MainWindow.this.txtDateTo.getText());
						maxDate.setTime(maxDate.getTime() + + 24 * 3600 * 1000 - 1); // Pour aller jusqu'à la fin de la journée, sinon on ne voit pas les events du dernier jour du mois
					}
					catch(ParseException e)
					{
						return null;
					}

					String filter = MainWindow.this.txtFilter.getText();
					MainWindow.this.googleEvents = MainWindow.this.service.getNextEvents(-1, minDate, maxDate, filter); // TODO combien d'event max on reçoit ? Faut-il parcourir des "pages" ?
					return null;
				}

				@Override protected void done() // Appelé à la fin de doInBackground
				{
					for(Event event:MainWindow.this.googleEvents)
					{
						MainWindow.this.allEvents.add(new MyEvent(calendar, event));						
						MainWindow.this.eventModel.add(calendar, event); // trick pour voir la liste se remplir en temps réel
					}
					MainWindow.this.updateLabelNbEvents(); // same trick
					MainWindow.this.consumeSelectedCalendars();
				}
			};

		this.eventSwingWorker.execute();
	}
	
	private void selectCalendar(final MyCalendar calendar)
	{
		final String calendarName = calendar.getSummary();
		if(calendarName == null || calendarName.isEmpty())
			return;

		new SwingWorker<String, Void>()
			{
				@Override protected String doInBackground()
				{
					return MainWindow.this.service.selectCalendar(calendarName, MainWindow.this.calendarModel.getGoogleCalendars());
				}

				@Override protected void done() // Appelé à la fin de doInBackground
				{
					String calendarId = null;
					try
					{
						calendarId = get();
					}
					catch(InterruptedException | ExecutionException e)
					{
						s_logger.error("Impossible de sélectionner l'agenda '"+calendarName+"'", e);
						MainWindow.this.consumeSelectedCalendars();
						return;
					}
					
					if(calendarId == null)
					{
						s_logger.error("Aucun agenda '"+calendarName+"' n'a été trouvé");
						MainWindow.this.consumeSelectedCalendars();
						return;
					}
					
					MainWindow.this.getEvents(calendar);
				}
			}.execute();
	}

	/* ------------------------------------------------------------ *\
	|* 		  				GUI modification						*|
	\* ------------------------------------------------------------ */
	private void buttonExitActionPerformed(ActionEvent e) {
		this.exit();
	}

	private void buttonRefreshActionPerformed(ActionEvent e)
	{
		if(!this.connected)
			return;
		
		this.eventModel.clear();
		this.allEvents.clear();
		this.go();
	}
	
	private void go()
	{
		this.selectedCalendars = MainWindow.this.calendarModel.getSelectedCalendars();
		this.labelMessage.showMessage("Recherche en cours");
		this.consumeSelectedCalendars();
	}

	private void tableEventsKeyTyped(KeyEvent e)
	{
		int keycode = e.getKeyCode();
//		int modifier = e.getModifiers();
		e.consume();

		if(keycode == KeyEvent.VK_DELETE)
		{
			int selectedRow = this.tableEvents.getSelectedRow();
			if(selectedRow < 0)
				return;
			
			final MyEvent event = this.eventModel.getEvent(selectedRow);
			if(event == null)
				return;
			
			s_logger.debug("delete event["+selectedRow+"] : "+event.getEvent().getSummary());
			
			final String calendarName = event.getCalendar().getSummary();
			s_logger.debug("selection du calendrier "+calendarName);
			
			new SwingWorker<String, Void>()
			{
				@Override protected String doInBackground()
				{
					return MainWindow.this.service.selectCalendar(calendarName, MainWindow.this.calendarModel.getGoogleCalendars());
				}

				@Override protected void done() // Appelé à la fin de doInBackground
				{
					String calendarId = null;
					try
					{
						calendarId = get();
					}
					catch(InterruptedException | ExecutionException e)
					{
						s_logger.error("Impossible de sélectionner l'agenda '"+calendarName+"'", e);
						return;
					}
					
					if(calendarId == null)
					{
						s_logger.error("Aucun agenda '"+calendarName+"' n'a été trouvé");
						return;
					}
					
					s_logger.debug("delete event "+event.getEvent().getId());
					MainWindow.this.service.deleteEvent(event.getEvent().getId());
				}
			}.execute();			
		}
	}

	private void radioMonthActionPerformed(ActionEvent e) {
		this.timeIntervalChanged();
	}

	private void radioWeekActionPerformed(ActionEvent e) {
		this.timeIntervalChanged();
	}

	private void radioTodayActionPerformed(ActionEvent e) {
		this.timeIntervalChanged();
	}
	
	private void timeIntervalChanged()
	{
		// get today and clear time of day
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		if(this.radioToday.isSelected())
		{
//			Date today = new Date();
			this.datePickerFrom.setDateToToday();
			this.datePickerTo.setDateToToday();
			return;
		}

		if(this.radioWeek.isSelected())
		{
			// https://stackoverflow.com/questions/2937086/how-to-get-the-first-day-of-the-current-week-and-month
			// get start of this week in milliseconds
			cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			s_logger.debug("Start of this week:       " + cal.getTime());
			this.datePickerFrom.setDate(DateHelper.dateToLocalDate(cal.getTime()));

			// start of the next week
			cal.add(Calendar.DAY_OF_WEEK, 6);
			s_logger.debug("End of this week:   " + cal.getTime());
			this.datePickerTo.setDate(DateHelper.dateToLocalDate(cal.getTime()));
			
			return;
		}

		if(this.radioMonth.isSelected())
		{
			// get start of this week in milliseconds
			cal.set(Calendar.DAY_OF_MONTH, 1);
			s_logger.debug("Start of this month:       " + cal.getTime());
			this.datePickerFrom.setDate(DateHelper.dateToLocalDate(cal.getTime()));

			// start of the next week
			cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH));
			s_logger.debug("End of this month:   " + cal.getTime());
			this.datePickerTo.setDate(DateHelper.dateToLocalDate(cal.getTime()));

			return;
		}
	}

	/* ------------------------------------------------------------ *\
	|* 		  			GUI Creation & init							*|
	\* ------------------------------------------------------------ */
	private void initComponents()
	{
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - yggpuduku
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu();
		JMenuItem menuFileQuit = new JMenuItem();
		this.labelMessage = new TimedLabel();
		this.buttonConnect = new JLabel();
		this.labelAccountValue = new JLabel();
		this.radioToday = new JRadioButton();
		JLabel labelRDV = new JLabel();
		this.labelNbEvents = new JLabel();
		JLabel labelFilter = new JLabel();
		this.txtFilter = new JTextField();
		this.labelCalendar = new JLabel();
		this.radioWeek = new JRadioButton();
		this.scrollEvents = new JScrollPane();
		this.tableEvents = new JTable();
		this.scrollCalendars = new JScrollPane();
		this.tableCalendars = new JTable();
		this.radioMonth = new JRadioButton();
		JLabel labellDateFrom = new JLabel();
		this.txtDateFrom = new JTextField();
		JLabel labelDateTo = new JLabel();
		this.txtDateTo = new JTextField();
		this.buttonRefresh = new JButton();
		JButton buttonExit = new JButton();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, $lcgap, right:default, $lcgap, 50dlu, 4*($lcgap, default), $lcgap, default:grow, $lcgap, default, $lcgap, default:grow(0.5), $lcgap, $ugap",
			"7*(default, $lgap), fill:default:grow(0.33), $lgap, fill:default:grow, $lgap, default, $lgap, $ugap"));
		((FormLayout)contentPane.getLayout()).setColumnGroups(new int[][] {{15, 19}});

		//======== menuBar ========

		//======== menuFile ========
		menuFile.setText("Fichier");

		//---- menuFileQuit ----
		menuFileQuit.setText("Quitter");
		menuFileQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuFileQuitActionPerformed(e);
			}
		});
		menuFile.add(menuFileQuit);
		menuBar.add(menuFile);
		setJMenuBar(menuBar);
		contentPane.add(this.labelMessage, CC.xywh(3, 1, 13, 1));

		//---- buttonConnect ----
		this.buttonConnect.setText("Connexion");
		this.buttonConnect.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.buttonConnect.setForeground(new Color(0, 134, 175));
		this.buttonConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				buttonConnectMouseClicked(e);
			}
		});
		contentPane.add(this.buttonConnect, CC.xy(17, 1));

		//---- labelAccountValue ----
		this.labelAccountValue.setText("text");
		contentPane.add(this.labelAccountValue, CC.xywh(19, 1, 3, 1));

		//---- radioToday ----
		this.radioToday.setText("Aujourd'hui");
		this.radioToday.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				radioTodayActionPerformed(e);
			}
		});
		contentPane.add(this.radioToday, CC.xywh(3, 3, 3, 1));

		//---- labelRDV ----
		labelRDV.setText("Rendez-vous :");
		contentPane.add(labelRDV, CC.xy(7, 3));

		//---- labelNbEvents ----
		this.labelNbEvents.setText("0");
		contentPane.add(this.labelNbEvents, CC.xy(9, 3));

		//---- labelFilter ----
		labelFilter.setText("Filtre");
		contentPane.add(labelFilter, CC.xy(13, 3));
		contentPane.add(this.txtFilter, CC.xy(15, 3));

		//---- labelCalendar ----
		this.labelCalendar.setText("Agendas : ");
		contentPane.add(this.labelCalendar, CC.xy(17, 3));

		//---- radioWeek ----
		this.radioWeek.setText("Cette semaine");
		this.radioWeek.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				radioWeekActionPerformed(e);
			}
		});
		contentPane.add(this.radioWeek, CC.xywh(3, 5, 3, 1));

		//======== scrollEvents ========
		this.scrollEvents.setViewportView(this.tableEvents);
		contentPane.add(this.scrollEvents, CC.xywh(7, 5, 9, 15));

		//======== scrollCalendars ========
		this.scrollCalendars.setViewportView(this.tableCalendars);
		contentPane.add(this.scrollCalendars, CC.xywh(17, 5, 3, 11));

		//---- radioMonth ----
		this.radioMonth.setText("Ce mois");
		this.radioMonth.setSelected(true);
		this.radioMonth.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				radioMonthActionPerformed(e);
			}
		});
		contentPane.add(this.radioMonth, CC.xywh(3, 7, 3, 1));

		//---- labellDateFrom ----
		labellDateFrom.setText("De :");
		contentPane.add(labellDateFrom, CC.xy(3, 9));
		contentPane.add(this.txtDateFrom, CC.xy(5, 9));

		//---- labelDateTo ----
		labelDateTo.setText("A :");
		contentPane.add(labelDateTo, CC.xy(3, 11));
		contentPane.add(this.txtDateTo, CC.xy(5, 11));

		//---- buttonRefresh ----
		this.buttonRefresh.setText("Actualiser");
		this.buttonRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonRefreshActionPerformed(e);
			}
		});
		contentPane.add(this.buttonRefresh, CC.xywh(3, 13, 3, 1));

		//---- buttonExit ----
		buttonExit.setText("Quitter");
		buttonExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonExitActionPerformed(e);
			}
		});
		contentPane.add(buttonExit, CC.xy(19, 19));
		setSize(645, 390);
		setLocationRelativeTo(getOwner());

		//---- radioGroupTimeInterval ----
		ButtonGroup radioGroupTimeInterval = new ButtonGroup();
		radioGroupTimeInterval.add(this.radioToday);
		radioGroupTimeInterval.add(this.radioWeek);
		radioGroupTimeInterval.add(this.radioMonth);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	private void myInit()
	{
		// Vire le fuckin watermark
//		for(PropertyChangeListener listener:this.panelLeft.getPropertyChangeListeners())
//			this.panelLeft.removePropertyChangeListener(listener);
//		this.panelLeft.setBorder(null);
		
		// Cache les labels inutils lorsqu'on n'est pas encore connecté
//		this.labelAccount.setVisible(false);
//		this.labelAccountValue.setVisible(false);
//		this.labelCalendar.setVisible(false);
//		this.comboCalendars.setVisible(false);
		
		this.allEvents = new ArrayList<>();
		
		// Dates
		LocalDate now = LocalDate.now();
		Date monthStart = Date.from(now
				.withDayOfMonth(1)
				.atStartOfDay()
			    .atZone(ZoneId.systemDefault())
			    .toInstant());
		
		Date monthEnd = Date.from(now
				.withDayOfMonth(now.lengthOfMonth())
				.atStartOfDay()
			    .atZone(ZoneId.systemDefault())
			    .toInstant());
		
		this.txtDateFrom.setText(DATE_SDF.format(monthStart.getTime()));
		this.txtDateTo.setText(DATE_SDF.format(monthEnd.getTime()));

		this.eventSwingWorker = null;
//		this.refreshEvent = false;

		this.labelAccountValue.setText("Non connecté");
		this.connected = false;
		this.buttonRefresh.setEnabled(false);
		
		// Models
		this.calendarModel = new CalendarModel();
		this.tableCalendars.setModel(this.calendarModel);
		ColorColumnCellRenderer calendarCellRenderer = new ColorColumnCellRenderer();
		TableColumnModel model = this.tableCalendars.getColumnModel();
		for(int i = 1; i < model.getColumnCount(); i++)
			model.getColumn(i).setCellRenderer(calendarCellRenderer);
		TableColumn col0 = model.getColumn(0);
		col0.setPreferredWidth(25);
		col0.setMaxWidth(25);

		this.eventModel = new EventModel();
		this.tableEvents.setModel(this.eventModel);
		ColorColumnCellRenderer rdvCellRenderer = new ColorColumnCellRenderer();
		model = this.tableEvents.getColumnModel();
		for(int i = 0; i < model.getColumnCount(); i++)
			model.getColumn(i).setCellRenderer(rdvCellRenderer);
		col0 = model.getColumn(0);
		col0.setPreferredWidth(75);
		col0.setMaxWidth(200);
		TableColumn col1 = model.getColumn(1);
		col1.setPreferredWidth(100);
		col1.setMaxWidth(200);
		
		// Swing listeners
		this.tableEvents.addMouseListener(new MouseAdapter() {
		    @Override public void mousePressed(MouseEvent mouseEvent) {
		        JTable table =(JTable) mouseEvent.getSource();
		        Point point = mouseEvent.getPoint();
		        int row = table.rowAtPoint(point);
		        int selectedRow = table.getSelectedRow();
		        if (mouseEvent.getClickCount() == 2 && selectedRow != -1 && row != -1) {
					MyEvent selectedEvent = MainWindow.this.eventModel.getEvent(selectedRow);
					if(selectedEvent ==  null)
						return;
					String htmlLink = selectedEvent.getEvent().getHtmlLink();
					if(htmlLink == null || htmlLink.trim().isEmpty())
						return;
					BrowserHelper.openWebpage(htmlLink);
		        }
		    }
		});
		
		this.tableEvents.addKeyListener((new KeyListener()
		{
			@Override public void keyTyped(KeyEvent keyevent){}
			@Override public void keyReleased(KeyEvent keyevent){}
			@Override public void keyPressed(KeyEvent keyevent) {
				tableEventsKeyTyped(keyevent);
			}
		}));
		
		FilterListener filterListener = new FilterListener(this);
		this.txtFilter.addKeyListener(filterListener);
		this.txtFilter.getDocument().addDocumentListener(filterListener);
		this.txtDateFrom.addKeyListener(filterListener);
		this.txtDateFrom.getDocument().addDocumentListener(filterListener);
		this.txtDateTo.addKeyListener(filterListener);
		this.txtDateTo.getDocument().addDocumentListener(filterListener);
		
//		Sorting Automatique.
//		https://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
//		Ne marche pas dans notre cas car on aimerait que le model soit aussi trié (et pas uniquement la vue comme le fait setAutoCreateRowSorter)
//		Pour que les lignes soient coloriées correctement		
//		this.tableEvents.setAutoCreateRowSorter(true);
		
//		Sorting manuel
//		http://www.javapractices.com/topic/TopicAction.do?Id=162
		this.tableEvents.getTableHeader().addMouseListener(new MySorter(this.tableEvents, this.eventModel));
		
		// Fonts
		Font font = this.buttonConnect.getFont();
		@SuppressWarnings("unchecked") Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>)font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		this.buttonConnect.setFont(font.deriveFont(attributes));
		
		// DatePicker
		DatePickerSettings dateSettingsFrom = new DatePickerSettings();
		dateSettingsFrom.setVisibleDateTextField(false);
		dateSettingsFrom.setGapBeforeButtonPixels(0);
        
        this.datePickerFrom = new DatePicker(dateSettingsFrom);
        this.datePickerFrom.addDateChangeListener(new DateChangeListener()
		{
			@Override public void dateChanged(DateChangeEvent e) {
				Date datePickerDate =  DateHelper.localDateToDate(e.getNewDate());
				try
				{
					Date txtDate = DATE_SDF.parse(MainWindow.this.txtDateFrom.getText());
					if(txtDate.compareTo(datePickerDate) == 0)
						return;
					MainWindow.this.txtDateFrom.setText(DATE_SDF.format(DateHelper.localDateToDate(e.getNewDate())));
				}
				catch(ParseException e1){}
			}
		});
        getContentPane().add(this.datePickerFrom, CC.xy(7, 11));
        
		DatePickerSettings dateSettingsTo = new DatePickerSettings();
		dateSettingsTo.setVisibleDateTextField(false);
		dateSettingsTo.setGapBeforeButtonPixels(0);
		this.datePickerTo = new DatePicker(dateSettingsTo);
		this.datePickerTo.addDateChangeListener(new DateChangeListener()
		{
			@Override public void dateChanged(DateChangeEvent e) {
				Date datePickerDate =  DateHelper.localDateToDate(e.getNewDate());
				try
				{
					Date txtDate = DATE_SDF.parse(MainWindow.this.txtDateTo.getText());
					if(txtDate.compareTo(datePickerDate) == 0)
						return;
					MainWindow.this.txtDateTo.setText(DATE_SDF.format(DateHelper.localDateToDate(e.getNewDate())));
				}
				catch(ParseException e1){}
			}
		});
        getContentPane().add(this.datePickerTo, CC.xy(7, 13));

	}

	/* ------------------------------------------------------------------------ *\
	|* 		  					Attributs privés								*|
	\* ------------------------------------------------------------------------ */
	private CalendarModel calendarModel;
	private EventModel eventModel;
	private List<MyEvent> allEvents;
	private GoogleCalendarService service;
	private List<MyCalendar> selectedCalendars;
	private boolean connected;
	private SwingWorker<Void, Void> eventSwingWorker;
//	private boolean refreshEvent;
	private DatePicker datePickerFrom;
	private DatePicker datePickerTo;

	private List<Event> googleEvents;

	/* ---------------------------------------- *\
	|* 		  			Static					*|
	\* ---------------------------------------- */
	private static Log s_logger = LogFactory.getLog(MainWindow.class);

	private static final String DATA_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator;
	public static final String PREF_FILE_PATH = DATA_PATH + "preferences.dat";

	private static final SimpleDateFormat DAY_SDF = new SimpleDateFormat("dd");
	private static final SimpleDateFormat MONTH_SDF = new SimpleDateFormat("MM");
	private static final SimpleDateFormat YEAR_SDF = new SimpleDateFormat("yyyy");
	public static final SimpleDateFormat DATE_SDF = new SimpleDateFormat("dd.MM.yyyy");

	private static final String PREF_MAIN_WINDOW_X = "MAIN_WINDOW_X";
	private static final String PREF_MAIN_WINDOW_Y = "MAIN_WINDOW_Y";
	private static final String PREF_MAIN_WINDOW_W = "MAIN_WINDOW_W";
	private static final String PREF_MAIN_WINDOW_H = "MAIN_WINDOW_H";
	private static final String PREF_SELECTED_CALENDAR = "PREF_SELECTED_CALENDAR";
	private static final String NEW_LINE = System.getProperty("line.separator");

	/* ---------------------------------------- *\
	|* 		  			GUI						*|
	\* ---------------------------------------- */
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - yggpuduku
	private TimedLabel labelMessage;
	private JLabel buttonConnect;
	private JLabel labelAccountValue;
	private JRadioButton radioToday;
	private JLabel labelNbEvents;
	private JTextField txtFilter;
	private JLabel labelCalendar;
	private JRadioButton radioWeek;
	private JScrollPane scrollEvents;
	private JTable tableEvents;
	private JScrollPane scrollCalendars;
	private JTable tableCalendars;
	private JRadioButton radioMonth;
	private JTextField txtDateFrom;
	private JTextField txtDateTo;
	private JButton buttonRefresh;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
