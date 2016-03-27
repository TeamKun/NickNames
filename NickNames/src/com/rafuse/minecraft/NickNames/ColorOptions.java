package com.rafuse.minecraft.NickNames;

import org.bukkit.ChatColor;

/**
 * Created by Sierra6767 on 3/27/2016.
 */
public enum ColorOptions
{
    BLACK("black", ChatColor.BLACK),
    DARK_BLUE("dark-blue", ChatColor.DARK_BLUE),
    DARK_GREEN("dark-gereen", ChatColor.DARK_GREEN),
    DARK_AQUA("dark-aqua", ChatColor.DARK_AQUA),
    DARK_RED("dark-red", ChatColor.DARK_RED),
    DARK_PURPLE("dark-purple", ChatColor.DARK_PURPLE),
    GOLD("gold", ChatColor.GOLD),
    GRAY("gray", ChatColor.GRAY),
    DARK_GRAY("dark-gray", ChatColor.DARK_GRAY),
    BLUE("blue", ChatColor.BLUE),
    GREEN("green", ChatColor.GREEN),
    AQUA("aqua", ChatColor.AQUA),
    RED("red", ChatColor.RED),
    LIGHT_PURPLE("light-purple", ChatColor.LIGHT_PURPLE),
    YELLOW("yellow", ChatColor.YELLOW),
    WHITE("white", ChatColor.WHITE),
    STRIKETHROUGH("strikethrough", ChatColor.STRIKETHROUGH),
    UNDERLINE("underline", ChatColor.UNDERLINE),
    BOLD("bold", ChatColor.BOLD),
    ITALIC("italic", ChatColor.ITALIC),
    RESET("reset", ChatColor.RESET);
    private ColorOptions(String name, ChatColor color)
    {
        this.name = name;
        this.color = color;
    }
    private final String name;
    private final ChatColor color;

    public char getChar() {return this.color.getChar();}
    @Override
    public String toString() {return this.color.toString();}
    public String getName() {return this.name;}
}
