package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerPlayer;

@SuppressWarnings("serial")
public class ClientItem extends JButton implements MouseListener{

	private Image image;
	private String imageName;
	private boolean selected = false;
	private int equipSlot = -1;
	private int row;
	private int col;
	private ClientInventory inventory;
	private String type;
	private int amount = 1;

	public ClientItem(String imageName, String type,int row, int col, ClientInventory inventory)
	{
		super(new ImageIcon(Images.getImage(imageName.substring(0,imageName.length()-4)+"_ICON.png")));
		this.row = row;
		this.col = col;
		this.type = type;
		this.inventory = inventory;
		this.imageName = imageName;
		image = Images.getImage(imageName.substring(0,imageName.length()-4)+"_ICON.png");

		setSize(Images.INVENTORY_IMAGE_SIDELENGTH,Images.INVENTORY_IMAGE_SIDELENGTH);
		setLocation(col*Images.INVENTORY_IMAGE_SIDELENGTH+(col+1)*20,row*Images.INVENTORY_IMAGE_SIDELENGTH+row*20+50);
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addMouseListener(this);
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		graphics.setColor(Color.white);
		if(amount > 10)
			graphics.drawString(amount+"", getWidth()-16, 10);
		else if(amount > 1)
			graphics.drawString(amount+"", getWidth()-8, 10);
	}
	public int getEquipSlot()
	{
		return equipSlot;
	}
	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void increaseAmount()
	{
		amount++;
	}

	public int getAmount()
	{
		return amount;
	}

	public void decreaseAmount()
	{
		amount--;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton()== MouseEvent.BUTTON1)
		{
			//If it can be equipped
			if(type.charAt(1) == ServerWorld.EQUIP_TYPE.charAt(1))
			{
				if(selected)
				{
					//Move back to inventory
					inventory.getClient().print("MI "+equipSlot);
					selected = false;

					ClientItem[][] invGrid = inventory.getInventory();
					for(int row = 0; row < invGrid.length;row++)
						for(int col = 0;col < invGrid[row].length;col++)
							if(invGrid[row][col] == null)
							{
								invGrid[row][col] = this;
								this.row = row;
								this.col = col;
								inventory.getEquippedWeapons()[equipSlot] = null;
								equipSlot = -1;
								setLocation(col*Images.INVENTORY_IMAGE_SIDELENGTH+(col+1)*20,row*Images.INVENTORY_IMAGE_SIDELENGTH+row*20+50);
								return;
							}
				}
				else
				{
					//Only move to weapons if there is room
					int pos = 0;
					for(;pos < ServerPlayer.MAX_WEAPONS;pos++)
					{
						if(inventory.getEquippedWeapons()[pos] == null)
							break;
					}

					if(pos == ServerPlayer.MAX_WEAPONS)
						return;

					inventory.getClient().print("MW "+type);
					selected = true;
					inventory.getInventory()[row][col] = null;
					inventory.getEquippedWeapons()[pos] = this;
					equipSlot = pos;
					row = -1;
					col = -1;
					setLocation(equipSlot*Images.INVENTORY_IMAGE_SIDELENGTH+equipSlot*20+80,500);
					repaint();


				}
			}
			//System.out.println("Selected this item");
		}
		else if(e.getButton() == MouseEvent.BUTTON3)
		{
			//Remove from equipment
			inventory.removeItem(this,equipSlot);
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
