package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.ItemClick;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AdvancedListInventory<T> extends AdvancedInventory {

    private final int size;
    private final Supplier<List<T>> list;

    public AdvancedListInventory(AdvancedInventoryManager manager, Player player, int size, Supplier<List<T>> list) {
        super(manager, player);

        this.size = size;
        this.list = list;
    }

    public AdvancedListInventory(AdvancedInventoryManager manager, Player player, int size, List<T> list) {
        this(manager, player, size, () -> list);
    }

    @Override
    public int getSize() {
        return this.size;
    }

    protected void addListItem(int index, T item) {
        this.addItem(index, this.getItemStack(item), this.getClick(item));
    }

    protected abstract ItemStack getItemStack(T item);

    protected abstract ItemClick getClick(T item);

    @Override
    public int getMaxPage() {
        List<T> original = this.getList();
        if (original.size() == 0)
            return 0;

        return (int) Math.ceil((float) this.getList().size() / (float) this.getUsableSize());
    }

    @Override
    protected void refreshContent() {
        List<T> original = this.getList();
        if (original == null)
            return;

        int start = this.getUsableSize() * (this.getPage() - 1);
        List<T> list = new ArrayList<>(original);
        for (int i = 0; (start + i) < list.size() && i < this.getUsableSize(); i++) {
            T item = list.get(i + start);
            if (item == null)
                continue;

            this.addListItem(i, item);
        }
    }

    protected List<T> getList() {
        return list.get();
    }
}
