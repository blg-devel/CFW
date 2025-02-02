/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.varoplugin.cfw.player.hook;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public abstract class AbstractPlayerHook<T extends HookListener<?>> extends AbstractPluginRegistrable implements PlayerHook {

    private final Collection<T> listeners;
    private final Collection<HookSubscriber<? extends AbstractHookEvent<?, ?>>> subscriber;
    private final boolean cancelEvent;

    public AbstractPlayerHook(boolean cancelEvent, Collection<HookSubscriber<? extends AbstractHookEvent<?, ?>>> subscriber, T listener) {
        this.listeners = new LinkedList<>(Collections.singletonList(listener));
        this.subscriber = new LinkedList<>(subscriber);
        this.cancelEvent = cancelEvent;
    }

    @SuppressWarnings("unchecked")
    private <E extends AbstractHookEvent<?, ?>> Collection<HookSubscriber<E>> getSubscriber(Class<? extends Event> eventClass) {
        return this.subscriber.stream().filter(sub -> sub.getKey().equals(eventClass)).map(t -> (HookSubscriber<E>) t).collect(Collectors.toList());
    }

    private void registerBukkitListener(T listener) {
        this.registerListener(listener);
        this.getPlugin().getServer().getPluginManager().registerEvents(listener, this.getPlugin());
    }

    private void unregisterListener(HookListener<?> listener) {
        HandlerList.unregisterAll(listener);
    }

    protected abstract void registerListener(T listener);

    @Override
    protected void onRegister(Player player, Plugin plugin) {
        super.onRegister(player, plugin);
        this.listeners.forEach(this::registerBukkitListener);
    }

    @Override
    protected void onUnregister() {
        this.listeners.forEach(this::unregisterListener);
        super.onUnregister();
    }

    @Override
    public <E extends AbstractHookEvent<?, ?>> void eventFired(E event) {
        if (!this.isRegistered())
            throw new IllegalStateException("Cannot fire event on unregistered hook");
        if (this.cancelEvent)
            event.setCancelled(true);
        this.getPlugin().getServer().getPluginManager().callEvent(event);
        this.getSubscriber(event.getClass()).forEach(s -> s.getValue().accept(event));
    }

    public static class HookSubscriber<T extends AbstractHookEvent<?, ?>> extends AbstractMap.SimpleEntry<Class<T>, Consumer<T>> {

        public HookSubscriber(Class<T> tClass, Consumer<T> consumer) {
            super(tClass, consumer);
        }
    }
}
