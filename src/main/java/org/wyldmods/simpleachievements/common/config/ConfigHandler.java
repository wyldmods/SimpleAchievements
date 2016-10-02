package org.wyldmods.simpleachievements.common.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import net.minecraftforge.common.config.Configuration;

import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.common.data.DataManager;
import org.wyldmods.simpleachievements.common.data.Element;
import org.wyldmods.simpleachievements.common.data.Formatting;

import com.google.common.collect.Maps;

public class ConfigHandler
{
    public static String endStr = "::";

    public static List<Element> defaultElements;
    public static Map<String, Element> idMap = Maps.newHashMap();

    public static int standID = 500;
    public static int decorationID = 501;

    public static int bookID = 5000;

    public static void init(File file)
    {
        Configuration config = new Configuration(file);

        endStr = config.get(Configuration.CATEGORY_GENERAL, "endStr", endStr,
                "The REGEX that deliminates the end of a line, after which you place the div ID").getString();

        config.save();
        
        initDefaultElements();
    }

    public static void initDefaultElements()
    {
        try
        {
            Scanner scan = new Scanner(SimpleAchievements.achievementConfig);
            defaultElements = new ArrayList<Element>();
            while (scan.hasNextLine())
            {
                String s = scan.nextLine();
                String[] args = s.split(endStr);
                if (args.length != 2)
                {
                    scan.close();
                    throw new IllegalArgumentException("Illegal format \"" + s + "\". Format must be [text]" + endStr + "[divClass]");
                }
                Element ele = new Element(args[0].trim());
                Formatting div = DataManager.INSTANCE.getFormat(Integer.parseInt(args[1].trim()));

                div.applyTo(ele);

                defaultElements.add(ele);
            }

            scan.close();

            for (Element ele : defaultElements)
            {
                idMap.put(ele.text, ele);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void flush()
    {
        DataManager.INSTANCE.initFormatting();
        initDefaultElements();
    }
}
