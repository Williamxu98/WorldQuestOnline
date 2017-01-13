package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import Imports.Images;
import Server.ServerWorld;
import Server.Buildings.ServerCastle;
import Server.Items.ServerBuildingItem;

public class ClientCastleShopItem extends JButton implements ActionListener{

	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;
	private int cost;
	private String type;
	private ClientCastleShop shop;

	public ClientCastleShopItem(String type, ClientCastleShop shop)
	{
		this.type = type;
		this.shop = shop;
		setSize(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT));
		
		//Add tooltips and set location:
		switch(type) {
		case ServerWorld.BASIC_BARRACKS_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("BARRACKS_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(100,100);
			cost = ServerBuildingItem.BASIC_BARRACKS_COST;
			setToolTipText("Barracks (Spawns two soldiers and one archer each tick)");
			break;
		case ServerWorld.ADV_BARRACKS_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("ADV_BARRACKS_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(100,200);
			cost = ServerBuildingItem.ADV_BARRACKS_COST;
			setToolTipText("Advanced Barracks (Spawns two knights and one wizard each tick");
			break;
		case ServerWorld.GIANT_FACTORY_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("GIANT_FACTORY_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(100,300);
			cost = ServerBuildingItem.GIANT_FACTORY_COST;
			setToolTipText("Giant Factory (Spawns two knights and one wizard");
			break;
		case ServerWorld.WOOD_HOUSE_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("WOOD_HOUSE_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(200,100);
			cost = ServerBuildingItem.WOOD_HOUSE_COST;
			setToolTipText("Wooden house (+10 housing space)");
			break;
		case ServerWorld.INN_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("INN_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(200,200);
			cost = ServerBuildingItem.INN_COST;
			setToolTipText("Inn (+25 housing space)");
			break;
		case ServerWorld.TOWER_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("TOWER_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(300,100);
			cost = ServerBuildingItem.TOWER_COST;
			setToolTipText("Arrow Tower");
			break;
		case ServerWorld.GOLD_MINE_ITEM_TYPE:
			setIcon(new ImageIcon(Images.getImage("GOLD_MINE_ICON").getScaledInstance(ClientFrame.getScaledWidth(WIDTH), ClientFrame.getScaledHeight(HEIGHT), 0)));
			setLocation(400,100);
			cost = ServerBuildingItem.GOLD_MINE_COST;
			setToolTipText("Gold Mine");
			break;
		}
		setVisible(true);
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent arg0) {
		if(shop.getMoney() >= cost)
			shop.buy(type, cost);
	}
}
