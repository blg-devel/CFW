package de.cuuky.cfw;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.clientadapter.ClientAdapterManager;
import de.cuuky.cfw.configuration.language.LanguageManager;
import de.cuuky.cfw.configuration.placeholder.MessagePlaceholderManager;
import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;
import de.cuuky.cfw.menu.SuperInventoryManager;
import de.cuuky.cfw.serialize.VaroSerializeHandler;

public class CuukyFrameWork {

	/*
	 * CFW - A Bukkit framework
	 * 
	 * VERSION: 0.1.6 (2020) AUTHOR: Cuuky CONTACT: { website: "varoplugin.de",
	 * discord: 'Cuuky#2783', mail: 'just.cookie.jc@gmail.com' }
	 */

	private JavaPlugin ownerInstance;
	private String consolePrefix;
	private HashMap<FrameworkManagerType, FrameworkManager> manager;

	public CuukyFrameWork(JavaPlugin pluginInstance) {
		this(pluginInstance, new ArrayList<>());
	}

	public CuukyFrameWork(JavaPlugin pluginInstance, ArrayList<FrameworkManager> manager) {
		this.ownerInstance = pluginInstance;
		this.manager = new HashMap<>();
		this.consolePrefix = "[" + pluginInstance.getName() + "] ";

		manager.forEach(manage -> this.manager.put(manage.getType(), manage));
		for (FrameworkManagerType type : FrameworkManagerType.values()) {
			if (this.manager.containsKey(type))
				continue;

			try {
				this.manager.put(type, (FrameworkManager) type.getManager().getDeclaredConstructor(CuukyFrameWork.class).newInstance(this));
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
				e.printStackTrace();
				throw new IllegalStateException("Failed to initialize type " + type.toString() + "!");
			}
		}
	}

	public JavaPlugin getPluginInstance() {
		return this.ownerInstance;
	}
	
	public String getConsolePrefix() {
		return consolePrefix;
	}

	public ClientAdapterManager getClientAdapterManager() {
		return (ClientAdapterManager) this.manager.get(FrameworkManagerType.CLIENT_ADAPTER);
	}

	public HookManager getHookManager() {
		return (HookManager) this.manager.get(FrameworkManagerType.HOOKING);
	}

	public SuperInventoryManager getInventoryManager() {
		return (SuperInventoryManager) this.manager.get(FrameworkManagerType.INVENTORY);
	}

	public LanguageManager getLanguageManager() {
		return (LanguageManager) this.manager.get(FrameworkManagerType.LANGUAGE);
	}

	public MessagePlaceholderManager getPlaceholderManager() {
		return (MessagePlaceholderManager) this.manager.get(FrameworkManagerType.PLACEHOLDER);
	}

	public VaroSerializeHandler getSerializeManager() {
		return (VaroSerializeHandler) this.manager.get(FrameworkManagerType.SERIALIZE);
	}
}