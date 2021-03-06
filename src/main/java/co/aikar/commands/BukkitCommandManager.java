/*
 * Copyright (c) 2016-2017 Daniel Ennis (Aikar) - MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package co.aikar.commands;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class BukkitCommandManager implements CommandManager {

    @SuppressWarnings("WeakerAccess")
    protected final Plugin plugin;
    private final CommandMap commandMap;
    protected CommandContexts contexts;
    protected CommandCompletions completions;

    public BukkitCommandManager(Plugin plugin) {
        this.plugin = plugin;
        CommandMap commandMap = null;
        try {
            Server server = Bukkit.getServer();
            Method getCommandMap = server.getClass().getDeclaredMethod("getCommandMap");
            getCommandMap.setAccessible(true);
            commandMap = (CommandMap) getCommandMap.invoke(server);
        } catch (Exception e) {
            ACFLog.severe("Failed to get Command Map. ACF will not function.");
            ACFUtil.sneaky(e);
        }
        this.commandMap = commandMap;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    public CommandMap getCommandMap() {
        return commandMap;
    }

    @Override
    public CommandContexts getCommandContexts() {
        if (this.contexts == null) {
            this.contexts = new BukkitCommandContexts();
        }
        return contexts;
    }

    @Override
    public CommandCompletions getCommandCompletions() {
        if (this.completions == null) {
            this.completions = new BukkitCommandCompletions();
        }
        return completions;
    }

    @Override
    public boolean registerCommand(BaseCommand command) {
        final String plugin = this.plugin.getName().toLowerCase();
        command.onRegister(this);
        boolean allSuccess = true;
        for (Map.Entry<String, Command> entry : command.registeredCommands.entrySet()) {
            if (!commandMap.register(entry.getKey().toLowerCase(), plugin, entry.getValue())) {
                allSuccess = false;
            }
        }

        return allSuccess;
    }
}
