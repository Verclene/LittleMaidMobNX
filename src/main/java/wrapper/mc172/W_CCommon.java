package wrapper.mc172;

import net.minecraft.command.*;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.passive.EntityTameable;
import wrapper.W_ICommon;

import com.mojang.authlib.GameProfile;

public class W_CCommon implements W_ICommon
{
	public void setOwner(EntityTameable entity, String name)
	{
	}
	
	public String getOwnerName(IEntityOwnable entity)
	{
		return null;
	}
	
	public GameProfile newGameProfile(String UUIDid, String name)
	{
		return null;
	}
	
	public void notifyAdmins(ICommandSender p_152374_0_, ICommand p_152374_1_, int p_152374_2_, String p_152374_3_, Object ... p_152374_4_)
	{
	}
}
