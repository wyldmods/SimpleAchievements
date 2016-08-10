package org.wyldmods.simpleachievements.common.data;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandFlush extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "flushAchievements";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/flushAchievements - Clears all cached achievement data";
	}

	@Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		DataManager.INSTANCE.flush();
	}
}
