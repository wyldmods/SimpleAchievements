package com.insane.simpleachievements.common.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.minecraftforge.common.Configuration;

import com.insane.simpleachievements.SimpleAchievements;
import com.insane.simpleachievements.common.data.DataManager;
import com.insane.simpleachievements.common.data.Element;
import com.insane.simpleachievements.common.data.Formatting;

public class ConfigHandler
{
	public static String endStr = "::";
	
	public static List<Element> defaultElements;
	
	public static int standID = 500;
	public static int decorationID = 501;
	
	public static int bookID = 5000;
	
	public static void init(File file)
	{
		Configuration config = new Configuration(file);
		
		endStr = config.get(Configuration.CATEGORY_GENERAL, "endStr", endStr, "The REGEX that deliminates the end of a line, after which you place the div ID").getString();
		
		standID = config.getBlock("stand_ID", standID).getInt();
		bookID = config.getItem("book_ID", bookID).getInt();
		
		decorationID = config.getBlock("decoration_ID", decorationID).getInt();
		
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
				Formatting div = DataManager.instance().getFormat(Integer.parseInt(args[1]));
				
				div.applyTo(ele);
				
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
