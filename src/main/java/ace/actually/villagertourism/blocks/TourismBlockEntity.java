package ace.actually.villagertourism.blocks;

import ace.actually.villagertourism.VillagerTourism;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TourismBlockEntity extends BlockEntity {
    int stored = 0;
    int total = 0;


    public TourismBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(VillagerTourism.TOURISM_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    @Override
    protected void saveAdditional(CompoundTag p_187471_) {
        p_187471_.putInt("stored",stored);
        p_187471_.putInt("total",total);
        super.saveAdditional(p_187471_);
    }

    @Override
    public void load(CompoundTag p_155245_) {
        super.load(p_155245_);
        stored = p_155245_.getInt("stored");
        total = p_155245_.getInt("total");
    }

    public void setTotal(int total)
    {
        this.total = total;
        setChanged();
    }

    public int getTotal() {
        return total;
    }

    public void setStored(int stored) {
        this.stored = stored;
        setChanged();
    }

    public int getStored() {
        return stored;
    }

    public void increment(int inc)
    {
        this.stored+=inc;
        this.total+=inc;
        setChanged();
    }
}
