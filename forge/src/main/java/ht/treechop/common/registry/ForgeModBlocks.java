package ht.treechop.common.registry;

import ht.treechop.TreeChop;
import ht.treechop.common.block.ForgeChoppedLogBlock;
import ht.treechop.common.loot.CountBlockChopsLootItemCondition;
import ht.treechop.common.loot.TreeFelledLootItemCondition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ForgeModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TreeChop.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TreeChop.MOD_ID);

    // Blocks
    public static final RegistryObject<Block> CHOPPED_LOG = BLOCKS.register("chopped_log",
            () -> new ForgeChoppedLogBlock(
                    Block.Properties.of()
                            .mapColor(blockState -> MapColor.WOOD)
                            .instrument(NoteBlockInstrument.BASS)
                            .strength(2.0F)
                            .sound(SoundType.WOOD)
                            .ignitedByLava()
            )
    );

    // Block entities
    public static final RegistryObject<BlockEntityType<ForgeChoppedLogBlock.MyEntity>> CHOPPED_LOG_ENTITY = ENTITIES.register("chopped_log",
            () -> BlockEntityType.Builder.of(ForgeChoppedLogBlock.MyEntity::new, CHOPPED_LOG.get()).build(null)
    );
}
