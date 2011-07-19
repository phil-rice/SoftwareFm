package org.arc4eclipse.swtBinding.training;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This class demonstrates TableTreeViewer.
 */
public class PlayerTableTree extends ApplicationWindow {
	// The TableTreeViewer
	private TableTreeViewer ttv;

	/**
	 * PlayerTableTree constructor
	 */
	public PlayerTableTree() {
		super(null);
	}

	/**
	 * Runs the application
	 */
	public void run() {
		// Don't return from open() until window closes
		setBlockOnOpen(true);

		// Open the main window
		open();

		// Dispose the display
		Display.getCurrent().dispose();
	}

	/**
	 * Configures the shell
	 * 
	 * @param shell
	 *            the shell
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Team Tree");
	}

	/**
	 * Creates the main window's contents
	 * 
	 * @param parent
	 *            the main window
	 * @return Control
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected Control createContents(Composite parent) {
		// Create the table viewer to display the players
		ttv = new TableTreeViewer(parent);
		ttv.getTableTree().setLayoutData(new GridData(GridData.FILL_BOTH));

		// Set the content and label providers
		ttv.setContentProvider(new PlayerTreeContentProvider());
		ttv.setLabelProvider(new PlayerTreeLabelProvider());
		ttv.setInput(new PlayerTableModel());

		// Set up the table
		Table table = ttv.getTableTree().getTable();
		new TableColumn(table, SWT.LEFT).setText("First Name");
		new TableColumn(table, SWT.LEFT).setText("Last Name");
		new TableColumn(table, SWT.RIGHT).setText("Points");
		new TableColumn(table, SWT.RIGHT).setText("Rebounds");
		new TableColumn(table, SWT.RIGHT).setText("Assists");

		// Expand everything
		ttv.expandAll();

		// Pack the columns
		for (int i = 0, n = table.getColumnCount(); i < n; i++) {
			table.getColumn(i).pack();
		}

		// Turn on the header and the lines
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Pack the window
		parent.pack();

		// Scroll to top
		ttv.reveal(ttv.getElementAt(0));

		return ttv.getTableTree();
	}

	/**
	 * The application entry point
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		new PlayerTableTree().run();
	}
}

/**
 * This class provides the content for the TableTreeViewer in PlayerTableTree
 */

class PlayerTreeContentProvider implements ITreeContentProvider {
	private static final Object[] EMPTY = new Object[] {};

	/**
	 * Gets the children for a team or player
	 * 
	 * @param arg0
	 *            the team or player
	 * @return Object[]
	 */
	@Override
	public Object[] getChildren(Object arg0) {
		if (arg0 instanceof Team)
			return ((Team) arg0).getPlayers().toArray();
		// Players have no children . . . except Shawn Kemp
		return EMPTY;
	}

	/**
	 * Gets the parent team for a player
	 * 
	 * @param arg0
	 *            the player
	 * @return Object
	 */
	@Override
	public Object getParent(Object arg0) {
		return ((Player) arg0).getTeam();
	}

	/**
	 * Gets whether this team or player has children
	 * 
	 * @param arg0
	 *            the team or player
	 * @return boolean
	 */
	@Override
	public boolean hasChildren(Object arg0) {
		return getChildren(arg0).length > 0;
	}

	/**
	 * Gets the elements for the table
	 * 
	 * @param arg0
	 *            the model
	 * @return Object[]
	 */
	@Override
	public Object[] getElements(Object arg0) {
		// Returns all the teams in the model
		return ((PlayerTableModel) arg0).teams;
	}

	/**
	 * Disposes any resources
	 */
	@Override
	public void dispose() {
		// We don't create any resources, so we don't dispose any
	}

	/**
	 * Called when the input changes
	 * 
	 * @param arg0
	 *            the parent viewer
	 * @param arg1
	 *            the old input
	 * @param arg2
	 *            the new input
	 */
	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// Nothing to do
	}
}

/**
 * This class provides the labels for the PlayerTableTree application
 */

class PlayerTreeLabelProvider extends PlayerLabelProvider {
	/**
	 * Gets the image for the specified column
	 * 
	 * @param arg0
	 *            the player or team
	 * @param arg1
	 *            the column
	 * @return Image
	 */
	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		// Teams have no image
		if (arg0 instanceof Player)
			return super.getColumnImage(arg0, arg1);
		return null;
	}

	/**
	 * Gets the text for the specified column
	 * 
	 * @param arg0
	 *            the player or team
	 * @param arg1
	 *            the column
	 * @return String
	 */
	@Override
	public String getColumnText(Object arg0, int arg1) {
		if (arg0 instanceof Player)
			return super.getColumnText(arg0, arg1);
		Team team = (Team) arg0;
		return arg1 == 0 ? team.getYear() + " " + team.getName() : "";
	}
}

/**
 * This class contains the data model for the PlayerTable
 */

class PlayerTableModel {
	public Team[] teams;

	/**
	 * Constructs a PlayerTableModel Fills the model with data
	 */
	public PlayerTableModel() {
		teams = new Team[3];

		teams[0] = new Team("Celtics", "1985-86");
		teams[0].add(new Player("Larry", "Bird", 25.8f, 9.8f, 6.8f));
		teams[0].add(new Player("Kevin", "McHale", 21.3f, 8.1f, 2.7f));
		teams[0].add(new Player("Robert", "Parish", 16.1f, 9.5f, 1.8f));
		teams[0].add(new Player("Dennis", "Johnson", 15.6f, 3.4f, 5.8f));
		teams[0].add(new Player("Danny", "Ainge", 10.7f, 2.9f, 5.1f));
		teams[0].add(new Player("Scott", "Wedman", 8.0f, 2.4f, 1.1f));
		teams[0].add(new Player("Bill", "Walton", 7.6f, 6.8f, 2.1f));
		teams[0].add(new Player("Jerry", "Sichting", 6.5f, 1.3f, 2.3f));
		teams[0].add(new Player("David", "Thirdkill", 3.3f, 1.4f, 0.3f));
		teams[0].add(new Player("Sam", "Vincent", 3.2f, 0.8f, 1.2f));
		teams[0].add(new Player("Sly", "Williams", 2.8f, 2.5f, 0.3f));
		teams[0].add(new Player("Rick", "Carlisle", 2.6f, 1.0f, 1.4f));
		teams[0].add(new Player("Greg", "Kite", 1.3f, 2.0f, 1.3f));

		teams[1] = new Team("Bulls", "1995-96");
		teams[1].add(new Player("Michael", "Jordan", 30.4f, 6.6f, 4.3f));
		teams[1].add(new Player("Scottie", "Pippen", 19.4f, 6.4f, 5.9f));
		teams[1].add(new Player("Toni", "Kukoc", 13.1f, 4.0f, 3.5f));
		teams[1].add(new Player("Luc", "Longley", 9.1f, 5.1f, 1.9f));
		teams[1].add(new Player("Steve", "Kerr", 8.4f, 1.3f, 2.3f));
		teams[1].add(new Player("Ron", "Harper", 7.4f, 2.7f, 2.6f));
		teams[1].add(new Player("Dennis", "Rodman", 5.5f, 14.9f, 2.5f));
		teams[1].add(new Player("Bill", "Wennington", 5.3f, 2.5f, 0.6f));
		teams[1].add(new Player("Jack", "Haley", 5.0f, 2.0f, 0.0f));
		teams[1].add(new Player("John", "Salley", 4.4f, 3.3f, 1.3f));
		teams[1].add(new Player("Jud", "Buechler", 3.8f, 1.5f, 0.8f));
		teams[1].add(new Player("Dickey", "Simpkins", 3.6f, 2.6f, 0.6f));
		teams[1].add(new Player("James", "Edwards", 3.5f, 1.4f, 0.4f));
		teams[1].add(new Player("Jason", "Caffey", 3.2f, 1.9f, 0.4f));
		teams[1].add(new Player("Randy", "Brown", 2.7f, 1.0f, 1.1f));

		teams[2] = new Team("Lakers", "1987-1988");
		teams[2].add(new Player("Magic", "Johnson", 23.9f, 6.3f, 12.2f));
		teams[2].add(new Player("James", "Worthy", 19.4f, 5.7f, 2.8f));
		teams[2].add(new Player("Kareem", "Abdul-Jabbar", 17.5f, 6.7f, 2.6f));
		teams[2].add(new Player("Byron", "Scott", 17.0f, 3.5f, 3.4f));
		teams[2].add(new Player("A.C.", "Green", 10.8f, 7.8f, 1.1f));
		teams[2].add(new Player("Michael", "Cooper", 10.5f, 3.1f, 4.5f));
		teams[2].add(new Player("Mychal", "Thompson", 10.1f, 4.1f, 0.8f));
		teams[2].add(new Player("Kurt", "Rambis", 5.7f, 5.8f, 0.8f));
		teams[2].add(new Player("Billy", "Thompson", 5.6f, 2.9f, 1.0f));
		teams[2].add(new Player("Adrian", "Branch", 4.3f, 1.7f, 0.5f));
		teams[2].add(new Player("Wes", "Matthews", 4.2f, 0.9f, 2.0f));
		teams[2].add(new Player("Frank", "Brickowski", 4.0f, 2.6f, 0.3f));
		teams[2].add(new Player("Mike", "Smrek", 2.2f, 1.1f, 0.1f));
	}
}

/**
 * This class represents a player
 */

class Player {
	private Team team;

	private String lastName;

	private String firstName;

	private float points;

	private float rebounds;

	private float assists;

	/**
	 * Constructs an empty Player
	 */
	public Player() {
		this(null, null, 0.0f, 0.0f, 0.0f);
	}

	/**
	 * Constructs a Player
	 * 
	 * @param firstName
	 *            the first name
	 * @param lastName
	 *            the last name
	 * @param points
	 *            the points
	 * @param rebounds
	 *            the rebounds
	 * @param assists
	 *            the assists
	 */
	public Player(String firstName, String lastName, float points, float rebounds, float assists) {
		setFirstName(firstName);
		setLastName(lastName);
		setPoints(points);
		setRebounds(rebounds);
		setAssists(assists);
	}

	/**
	 * Sets the team for theo player
	 * 
	 * @param team
	 *            the team
	 */
	public void setTeam(Team team) {
		this.team = team;
	}

	/**
	 * Gets the assists
	 * 
	 * @return float
	 */
	public float getAssists() {
		return assists;
	}

	/**
	 * Sets the assists
	 * 
	 * @param assists
	 *            The assists to set.
	 */
	public void setAssists(float assists) {
		this.assists = assists;
	}

	/**
	 * Gets the first name
	 * 
	 * @return String
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name
	 * 
	 * @param firstName
	 *            The firstName to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the last name
	 * 
	 * @return String
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name
	 * 
	 * @param lastName
	 *            The lastName to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets the points
	 * 
	 * @return float
	 */
	public float getPoints() {
		return points;
	}

	/**
	 * Sets the points
	 * 
	 * @param points
	 *            The points to set.
	 */
	public void setPoints(float points) {
		this.points = points;
	}

	/**
	 * Gets the rebounds
	 * 
	 * @return float
	 */
	public float getRebounds() {
		return rebounds;
	}

	/**
	 * Sets the rebounds
	 * 
	 * @param rebounds
	 *            The rebounds to set.
	 */
	public void setRebounds(float rebounds) {
		this.rebounds = rebounds;
	}

	/**
	 * Gets the team
	 * 
	 * @return Team
	 */
	public Team getTeam() {
		return team;
	}

	/**
	 * Returns whether this player led the team in the specified category
	 * 
	 * @param column
	 *            the column (category)
	 * @return boolean
	 */
	public boolean ledTeam(int column) {
		return team.led(this, column);
	}
}

/**
 * This class represents a team
 */

@SuppressWarnings("rawtypes")
class Team {
	private final String name;

	private final String year;

	private final List players;

	/**
	 * Constructs a Team
	 * 
	 * @param name
	 *            the name
	 * @param year
	 *            the year
	 */
	public Team(String name, String year) {
		this.name = name;
		this.year = year;
		players = new LinkedList();
	}

	/**
	 * Gets the name
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the year
	 * 
	 * @return String
	 */
	public String getYear() {
		return year;
	}

	/**
	 * Adds a player
	 * 
	 * @param player
	 *            the player to add
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean add(Player player) {
		boolean added = players.add(player);
		if (added)
			player.setTeam(this);
		return added;
	}

	/**
	 * Gets the players
	 * 
	 * @return List
	 */

	@SuppressWarnings("unchecked")
	public List getPlayers() {
		return Collections.unmodifiableList(players);
	}

	/**
	 * Returns whether the specified player led his team in the specified category
	 * 
	 * @param player
	 *            the player
	 * @param column
	 *            the category
	 * @return boolean
	 */
	public boolean led(Player player, int column) {
		boolean led = true;

		// Go through all the players on the team, comparing the specified
		// player's
		// stats with each other player.
		for (int i = 0, n = players.size(); i < n && led; i++) {
			Player test = (Player) players.get(i);
			if (player == test)
				continue;
			switch (column) {
			case PlayerConst.COLUMN_POINTS:
				if (player.getPoints() < test.getPoints())
					led = false;
				break;
			case PlayerConst.COLUMN_REBOUNDS:
				if (player.getRebounds() < test.getRebounds())
					led = false;
				break;
			case PlayerConst.COLUMN_ASSISTS:
				if (player.getAssists() < test.getAssists())
					led = false;
				break;
			}
		}
		return led;
	}
}

/**
 * This class contains constants for the PlayerTable application
 */

class PlayerConst {
	// Column constants
	public static final int COLUMN_FIRST_NAME = 0;

	public static final int COLUMN_LAST_NAME = 1;

	public static final int COLUMN_POINTS = 2;

	public static final int COLUMN_REBOUNDS = 3;

	public static final int COLUMN_ASSISTS = 4;
}

/**
 * This class provides the labels for PlayerTable
 */

class PlayerLabelProvider implements ITableLabelProvider {
	// Image to display if the player led his team
	private Image ball;

	// Constructs a PlayerLabelProvider
	public PlayerLabelProvider() {
		// Create the image
		try {
			ball = new Image(null, new FileInputStream("images/ball.png"));
		} catch (FileNotFoundException e) {
			// Swallow it
		}
	}

	/**
	 * Gets the image for the specified column
	 * 
	 * @param arg0
	 *            the player
	 * @param arg1
	 *            the column
	 * @return Image
	 */
	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		Player player = (Player) arg0;
		Image image = null;
		switch (arg1) {
		// A player can't lead team in first name or last name
		case PlayerConst.COLUMN_POINTS:
		case PlayerConst.COLUMN_REBOUNDS:
		case PlayerConst.COLUMN_ASSISTS:
			if (player.ledTeam(arg1))
				// Set the image
				image = ball;
			break;
		}
		return image;
	}

	/**
	 * Gets the text for the specified column
	 * 
	 * @param arg0
	 *            the player
	 * @param arg1
	 *            the column
	 * @return String
	 */
	@Override
	public String getColumnText(Object arg0, int arg1) {
		Player player = (Player) arg0;
		String text = "";
		switch (arg1) {
		case PlayerConst.COLUMN_FIRST_NAME:
			text = player.getFirstName();
			break;
		case PlayerConst.COLUMN_LAST_NAME:
			text = player.getLastName();
			break;
		case PlayerConst.COLUMN_POINTS:
			text = String.valueOf(player.getPoints());
			break;
		case PlayerConst.COLUMN_REBOUNDS:
			text = String.valueOf(player.getRebounds());
			break;
		case PlayerConst.COLUMN_ASSISTS:
			text = String.valueOf(player.getAssists());
			break;
		}
		return text;
	}

	/**
	 * Adds a listener
	 * 
	 * @param arg0
	 *            the listener
	 */
	@Override
	public void addListener(ILabelProviderListener arg0) {
		// Throw it away
	}

	/**
	 * Dispose any created resources
	 */
	@Override
	public void dispose() {
		// Dispose the image
		if (ball != null)
			ball.dispose();
	}

	/**
	 * Returns whether the specified property, if changed, would affect the label
	 * 
	 * @param arg0
	 *            the player
	 * @param arg1
	 *            the property
	 * @return boolean
	 */
	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	/**
	 * Removes the specified listener
	 * 
	 * @param arg0
	 *            the listener
	 */
	@Override
	public void removeListener(ILabelProviderListener arg0) {
		// Do nothing
	}
}
