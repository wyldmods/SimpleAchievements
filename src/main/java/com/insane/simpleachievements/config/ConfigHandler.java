package com.insane.simpleachievements.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.minecraftforge.common.Configuration;

import com.insane.simpleachievements.SimpleAchievements;
import com.insane.simpleachievements.data.Element;

public class ConfigHandler
{
	public static String endStr = "::";
	
	public static List<Element> defaultElements;
	
	public static void init(File file)
	{
		Configuration config = new Configuration(file);
		
		config.get(Configuration.CATEGORY_GENERAL, "endStr", endStr, "The REGEX that deliminates the end of a line, after which you place the div ID").getString();
		
		defaultElements = getDefaultElements();
	}
	
	public static List<Element> getDefaultElements()
	{
		try
		{
			Scanner scan = new Scanner(SimpleAchievements.achievementConfig);
			ArrayList<Element> ret = new ArrayList<Element>();
			while (scan.hasNextLine())
			{
				String s = scan.nextLine();
				String[] args = s.split(endStr);
				if (args.length != 2)
				{
					scan.close();
					throw new IllegalArgumentException("Illegal format \"" + s + "\". Format must be [text]" + endStr + "[divClass]");
				}
				Element ele = new Element(args[0]);
				// TODO get div class and apply to element
				ret.add(ele);
			}

			scan.close();
			return ret;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
