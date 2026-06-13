package com.sleaky.invRestrictor.Commands;

import com.sleaky.invRestrictor.gui.PlayerSelectGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RestrictCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "You are not a player");
            return true;
        }

        PlayerSelectGUI gui = new PlayerSelectGUI(p);
        gui.openGUIFor(p);
        return true;
    }
}