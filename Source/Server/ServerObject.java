package Server;

import java.util.ArrayList;

import Imports.Images;
import Server.Buildings.ServerBarracks;
import Server.Buildings.ServerCastle;
import Server.Buildings.ServerDefense;
import Server.Buildings.ServerHouse;
import Server.Buildings.ServerMine;
import Server.Creatures.ServerChest;
import Server.Creatures.ServerVendor;
import Server.Spawners.ServerBatSpawner;
import Server.Spawners.ServerGoblinSpawner;
import Server.Spawners.ServerSlimeSpawner;
import Tools.RowCol;

/**
 * A generic object existing somewhere in the world with a unique ID, x,y
 * coordinate, and height and width
 * 
 * @author William Xu & Alex Raita
 *
 */
public abstract class ServerObject implements Comparable<ServerObject>
{
	/**
	 * Unique identifier for the object
	 */
	private int id;

	/**
	 * X-coordinate of the object (left)
	 */
	private double x;

	/**
	 * Y-coordinate of the object (top)
	 */
	private double y;

	/**
	 * Width of the object in pixels
	 */
	private int width;

	/**
	 * Height of the object in pixels
	 */
	private int height;

	/**
	 * The image name for the object (ex. pie)
	 */
	private String image;
	private int imageIndex;

	/**
	 * The horizontal speed of the player (negative -- left, positive -- right)
	 */
	private double hSpeed;

	/**
	 * The vertical speed of the player (negative -- up, positive -- down)
	 */
	private double vSpeed;

	/**
	 * The specific object's gravity (usually the universal gravity)
	 */
	private double gravity;

	/**
	 * Whether or not the object is on top of a surface
	 */
	private boolean onSurface;

	/**
	 * Whether or not the object exists
	 */
	private boolean exists;

	/**
	 * The type of object this is (subclass)
	 */
	private String type;

	/**
	 * Whether or not the MAP can see the object
	 */
	private boolean mapVisible;

	/**
	 * Whether or not the object will collide with tiles
	 */
	private boolean solid;

	/**
	 * An arraylist of indexes (row and column) of the object tiles that this
	 * object is inside
	 */
	private ArrayList<RowCol> objectTiles;

	/**
	 * Whether the object is visible in the game
	 */
	private boolean visible;
	
	/**
	 * Whether or not the object just played a sound (each object can only play one sound at once)
	 */
	private boolean playedSound;

	/**
	 * Constructor for an object
	 * 
	 * @param x
	 * @param y
	 * @param height
	 * @param width
	 * @param ID
	 */
	public ServerObject(double x, double y, int width, int height,
			double gravity, String image, String type,
			ServerEngine engine)
	{
		playedSound = false;
		objectTiles = new ArrayList<RowCol>();
		solid = true;
		mapVisible = true;
		this.type = type;
		exists = true;
		onSurface = false;
		this.visible = true;
		this.gravity = gravity;
		this.x = x;
		this.y = y;
		this.image = image;

		try
		{
			if (type.contains(ServerWorld.BUILDING_TYPE))
			{
				this.id = engine.useNextBuildingID();
			}
			else
			{
				this.id = engine.useNextID();
			}
		}
		catch (NullPointerException E)
		{
			E.printStackTrace();
			System.out.println("ID: " + this.id + " Image: " + image
					+ " ImageIndex: " + this.imageIndex);
		}


		this.imageIndex = Images.getImageIndex(image);


		if (width == -1)
		{
			this.width = Images.getGameImage(image).getWidth();
			this.height = Images.getGameImage(image).getHeight();
		}
		else
		{
			this.width = width;
			this.height = height;
		}
	}

	/**
	 * Constructor for an object
	 * 
	 * @param x
	 * @param y
	 * @param height
	 * @param width
	 * @param ID
	 */
	public ServerObject(double x, double y, int width, int height,
			double gravity, String type, ServerEngine engine)
	{
		objectTiles = new ArrayList<RowCol>();
		solid = true;
		mapVisible = true;
		this.type = type;
		exists = true;
		onSurface = false;
		this.visible = false;
		this.gravity = gravity;
		this.x = x;
		this.y = y;
		try
		{
			if (type.contains(ServerWorld.BUILDING_TYPE))
			{
				this.id = engine.useNextBuildingID();
			}
			else
			{
				this.id = engine.useNextID();
			}
		}
		catch (NullPointerException E)
		{
			E.printStackTrace();
			System.out.println("ID: " + this.id + " Image: " + image
					+ " ImageIndex: " + this.imageIndex);
		}

		if (width == -1)
		{
			this.width = Images.getGameImage(image).getWidth();
			this.height = Images.getGameImage(image).getHeight();
		}
		else
		{
			this.width = width;
			this.height = height;
		}
	}

	/**
	 * Get the base image for the object (ex. the base name for player right is
	 * player) This is useful for changing the direction/animation of the object
	 * but not the image itself
	 * 
	 * @return the base image
	 */
	public String getBaseImage()
	{
		// The first word is always the base image
		String[] tokens = image.split("_");
		return tokens[0];
	}

	/**
	 * Check for a collision between the two objects
	 * 
	 * @param other
	 * @return whether or not the two objects are colliding
	 */
	public boolean collidesWith(ServerObject other)
	{
		if (exists && x <= other.getX() + other.getWidth()
				&& (x + width) >= other.getX()
				&& y <= other.getY() + other.getHeight()
				&& (y + height) >= other.getY())
		{
			return true;
		}
		return false;
	}

	/**
	 * Check for a collision between an object and a hitbox
	 * 
	 * @param other
	 * @return whether or not the two objects are colliding
	 */
	public boolean collidesWith(int x1, int y1, int x2, int y2)
	{
		if (exists && x <= x2 && (x + width) >= x1 && y <= y2
				&& (y + height) >= y1)
		{
			return true;
		}
		return false;
	}

	/**
	 * Checks whether or not the other object is in range of this object
	 * 
	 * @param other
	 * @param distance
	 * @return
	 */
	public boolean inRange(ServerObject other, double distance)
	{
		double distanceBetween = distanceBetween(other);

		if (distanceBetween <= distance)
		{
			return true;
		}
		return false;
	}

	/**
	 * Quickly finds whether or not the other object is vertically or
	 * horizontally within range without actually calculating the distance
	 * between
	 * 
	 * @param other
	 * @param distance
	 * @return
	 */
	public boolean quickInRange(ServerObject other, double distance)
	{
		// Create a big hitbox and see if the other object touches it,
		// essentially
		if (other.getX() <= x + width + distance
				&& other.getX() + other.getWidth() >= x - distance
				&& other.getY() <= y + height + distance
				&& other.getY() + other.getHeight() >= y - distance)
		{
			return true;
		}
		return false;
	}

	/**
	 * Set the image for the object
	 * 
	 * @param image
	 */
	public void setImage(String image)
	{
		this.image = image;
		this.imageIndex = Images.getImageIndex(image);
	}

	/**
	 * Find the minimum distance between two objects
	 * 
	 * @param other the other object
	 * @return the distance between this and the other object
	 */
	public double distanceBetween(ServerObject other)
	{
		// The specific sides of each object to calculate distance between (top,
		// bottom, left, right)
		double thisX = 0;
		double otherX = 0;
		double thisY = 0;
		double otherY = 0;

		if (x - (other.getX() + other.getWidth()) > 0)
		{
			otherX = other.getX() + other.getWidth();
			thisX = getX();
		}
		else if (other.getX() - (x + width) > 0)
		{
			thisX = x + width;
			otherX = other.getX();
		}

		if (y - (other.getY() + other.getHeight()) > 0)
		{
			otherY = other.getY() + other.getHeight();
			thisY = getY();
		}
		else if (other.getY() - (y + height) > 0)
		{
			thisY = y + height;
			otherY = other.getY();
		}

		return Math.sqrt((thisX - otherX) * (thisX - otherX) + (thisY - otherY)
				* (thisY - otherY));
	}

	/**
	 * Makes a copy of a given object
	 * 
	 * @param original the original object
	 * @return a copy of the original object
	 */
	public static ServerObject copy(ServerObject original)
	{
		switch (original.getType())
		{
		case ServerWorld.CASTLE_TYPE:
			ServerCastle newCastle = (ServerCastle) original;
			return new ServerCastle(newCastle.getX(), newCastle.getY(),
					newCastle.getTeam(), newCastle.getWorld());
		case ServerWorld.CHEST_TYPE:
			ServerChest newChest = (ServerChest) original;
			return new ServerChest(newChest.getX(), newChest.getY(),
					newChest.getWorld());
		case ServerWorld.VENDOR_TYPE:
			ServerVendor newVendor = (ServerVendor) original;
			return new ServerVendor(newVendor.getX(), newVendor.getY(),
					newVendor.getWorld());
		case ServerWorld.GOBLIN_SPAWN_TYPE:
			ServerGoblinSpawner newSpawner = (ServerGoblinSpawner) original;
			return new ServerGoblinSpawner(original.getX(), original.getY(),
					newSpawner.getWorld(),
					newSpawner.getTeam());
		case ServerWorld.SLIME_SPAWN_TYPE:
			ServerSlimeSpawner newSlimeSpawner = (ServerSlimeSpawner) original;
			return new ServerSlimeSpawner(original.getX(), original.getY(),
					newSlimeSpawner.getWorld());
		case ServerWorld.BAT_SPAWN_TYPE:
			ServerBatSpawner newBatSpawner = (ServerBatSpawner) original;
			return new ServerBatSpawner(original.getX(), original.getY(),
					newBatSpawner.getWorld());
		case ServerWorld.BASIC_BARRACKS_TYPE:
		case ServerWorld.ADV_BARRACKS_TYPE:
		case ServerWorld.GIANT_FACTORY_TYPE:
			ServerBarracks barracks = (ServerBarracks) original;
			return new ServerBarracks(barracks.getX(), barracks.getY(), barracks.getType(), barracks.getTeam(), barracks.getWorld());
		case ServerWorld.GOLD_MINE_TYPE:
			ServerMine mine = (ServerMine) original;
			return new ServerMine(mine.getX(), mine.getY(), mine.getType(), mine.getTeam(), mine.getWorld());
		case ServerWorld.WOOD_HOUSE_TYPE:
		case ServerWorld.INN_TYPE:
			ServerHouse house = (ServerHouse) original;
			return new ServerHouse(house.getX(), house.getY(), house.getType(), house.getTeam(), house.getWorld());
		case ServerWorld.TOWER_TYPE:
			ServerDefense defense = (ServerDefense) original;
			return new ServerDefense(defense.getX(), defense.getY(), defense.getType(), defense.getTeam(), defense.getWorld());
		}

		// case ServerWorld.SLIME_TYPE:
		// ServerSlime newSlime = (ServerSlime)original;
		// return new
		// ServerSlime(newSlime.getX(),newSlime.getY(),newSlime.getWorld());
		// }
		//
		// //Special case if we have a goblin type
		// if(original.getType().contains(ServerWorld.GOBLIN_TYPE))
		// {
		// ServerGoblin newGoblin = (ServerGoblin)original;
		// return new
		// ServerGoblin(newGoblin.getX(),newGoblin.getY(),newGoblin.getWorld(),newGoblin.getTeam());
		// }

		return null;
	}

	@Override
	public int compareTo(ServerObject other)
	{
		return other.getID() - getID();
	}

	/**
	 * For overriding
	 */
	public abstract void update();

	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////

	public ArrayList<RowCol> getObjectTiles()
	{
		return objectTiles;
	}

	public void setObjectTiles(ArrayList<RowCol> objectTiles)
	{
		this.objectTiles = objectTiles;
	}

	public boolean exists()
	{
		return exists;
	}

	public void destroy()
	{
		exists = false;
	}

	public double getGravity()
	{
		return gravity;
	}

	public void makeExist()
	{
		exists = true;
	}

	public void setGravity(double gravity)
	{
		this.gravity = gravity;
	}

	public boolean isOnSurface()
	{
		return onSurface;
	}

	public void setOnSurface(boolean onSurface)
	{
		this.onSurface = onSurface;
	}

	public double getHSpeed()
	{
		return hSpeed;
	}

	public void setHSpeed(double hSpeed)
	{
		this.hSpeed = hSpeed;
	}

	public double getVSpeed()
	{
		return vSpeed;
	}

	public void setVSpeed(double vSpeed)
	{
		this.vSpeed = vSpeed;
	}

	public int getID()
	{
		return id;
	}

	public void setID(int iD)
	{
		id = iD;
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		if (height == -1)
		{
			this.height = Images.getGameImage(image).getHeight();
		}
		else
		{
			this.height = height;
		}
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		if (width == -1)
		{
			this.width = Images.getGameImage(image).getWidth();
		}
		else
		{
			this.width = width;
		}
	}

	public String getImage()
	{
		return image;
	}

	public int getImageIndex()
	{
		return imageIndex;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public boolean isMapVisible()
	{
		return mapVisible;
	}

	public void setMapVisible(boolean mapVisible)
	{
		this.mapVisible = mapVisible;
	}

	public boolean isSolid()
	{
		return solid;
	}

	public void setSolid(boolean solid)
	{
		this.solid = solid;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	public boolean isPlayedSound() {
		return playedSound;
	}

	public void setPlayedSound(boolean playedSound) {
		this.playedSound = playedSound;
	}
}
