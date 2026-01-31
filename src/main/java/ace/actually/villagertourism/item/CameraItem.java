package ace.actually.villagertourism.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Set;

public class CameraItem extends Item {
    public CameraItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        if(p_41432_ instanceof ServerLevel sl && p_41434_==InteractionHand.MAIN_HAND)
        {
            Set<Structure> structures = sl.structureManager().getAllStructuresAt(p_41433_.getOnPos()).keySet();
            for(Structure structure: structures)
            {
                String s = sl.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(structure).toString();
                p_41433_.sendSystemMessage(Component.literal(s));
            }
        }
        return super.use(p_41432_, p_41433_, p_41434_);
    }
}
