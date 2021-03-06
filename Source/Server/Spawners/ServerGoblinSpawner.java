package Server.Spawners;
import Server.ServerWorld;
import Server.Buildings.ServerCastle;
import Server.Creatures.ServerPlayer;

/**
 * Spawns a given creature to the world
 * @author Alex Raita & William Xu
 *
 */
public class ServerGoblinSpawner extends ServerSpawner
{

	/**
	 * Team of the spawner
	 */
	private int team;
	
	private ServerCastle castle = null;

	/**
	 * Constructor
	 * @param x the x-coordinate of the spawner
	 * @param y the y-coordinate of the spawner
	 * @param creatureType the type of creature that will be spawned
	 * @param world the world the creature will be added in
	 */
	public ServerGoblinSpawner(double x, double y,
			ServerWorld world, int team)
	{
		super(x, y, world, ServerWorld.GOBLIN_SPAWN_TYPE);
		this.team = team;

		if (team == ServerPlayer.RED_TEAM)
		{
			setImage("RED_GOBLIN_SPAWN");
		}
		else
		{
			setImage("BLUE_GOBLIN_SPAWN");
		}
	}

	public int getTeam()
	{
		return team;
	}

	public void setTeam(int team)
	{
		this.team = team;
	}

	@Override
	public void update()
	{	
		if (castle==null)
		{
			if (getTeam() == ServerPlayer.BLUE_TEAM)
			{
				castle = getWorld().getBlueCastle();
			}
			else
			{
				castle = getWorld().getRedCastle();
			}
			castle.addSpawner(this);
		}	
	}
	
}
