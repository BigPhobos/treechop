package ht.treechop.compat;

import ht.treechop.TreeChop;
import ht.treechop.common.block.ChoppedLogBlock;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.elements.ElementHorizontal;
import mcjty.theoneprobe.apiimpl.elements.ElementItemLabel;
import mcjty.theoneprobe.apiimpl.elements.ElementText;
import mcjty.theoneprobe.apiimpl.elements.ElementVertical;
import mcjty.theoneprobe.apiimpl.styles.TextStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

class TheOneProbeInfoProvider implements IProbeInfoProvider {
    /** Identifies both {@link TheOneProbeInfoProvider} and {@link TheOneProbeInfoProvider.DeferredTreeDataElement} **/
    public static final ResourceLocation ID = TreeChop.resource("tree_info");

    private static final boolean SHOW_TREE_BLOCKS = true;
    private static final boolean SHOW_NUM_CHOPS_REMAINING = true;

    public static Void register(ITheOneProbe probe) {
        probe.registerProvider(new TheOneProbeInfoProvider());
        IStyleManager styleManager = probe.getStyleManager();

        probe.registerElementFactory(new IElementFactory() {
            public IElement createElement(RegistryFriendlyByteBuf buffer) {
                return TheOneProbeInfoProvider.DeferredTreeDataElement.decode(buffer, styleManager);
            }

            public ResourceLocation getId() {
                return ID;
            }
        });

        return null;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo builder, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        changeBlockName(builder, level, iProbeHitData.getPos());

        BlockPos blockPos = iProbeHitData.getPos();
        if (WailaUtil.playerWantsTreeInfo(level, blockPos, SHOW_TREE_BLOCKS, SHOW_NUM_CHOPS_REMAINING)) {
            builder.element(new DeferredTreeDataElement(iProbeHitData.getPos()));
        }
    }

    private void changeBlockName(IProbeInfo builder, Level level, BlockPos pos) {
        try {
            if (level.getBlockEntity(pos) instanceof ChoppedLogBlock.MyEntity choppedEntity) {
                if (builder.getElements().get(0) instanceof ElementHorizontal iconAndBlockInfo) {
                    if (iconAndBlockInfo.getElements().get(1) instanceof ElementVertical blockNameAndMod) {
                        if (blockNameAndMod.getElements().get(0) instanceof ElementItemLabel label) {
                            blockNameAndMod.getElements().set(0, new ElementText(WailaUtil.getPrefixedBlockName(choppedEntity), new TextStyle().height(label.getHeight())));
                        }
                    }
                }
            }
        } catch (NoSuchMethodError | NoClassDefFoundError | NullPointerException ignored) {
        }
    }

    private static void addTreeDataElements(IProbeInfo builder, Level level, BlockPos blockPos) {
        IItemStyle itemStyle = builder.defaultItemStyle();
        Optional<IProbeInfo> tiles = Optional.empty();
        WailaUtil.addTreeInfo(
                level,
                blockPos,
                SHOW_TREE_BLOCKS,
                SHOW_NUM_CHOPS_REMAINING,
                builder::text,
                stack -> tiles.orElseGet(builder::horizontal).item(stack, itemStyle)
        );
    }

    private record DeferredTreeDataElement(BlockPos pos) implements IElement {
        @Override
        public void toBytes(RegistryFriendlyByteBuf buffer) {
            buffer.writeBlockPos(pos);
        }

        public static IElement decode(FriendlyByteBuf buffer, IStyleManager styleManager) {
            ElementVertical elements = new ElementVertical(styleManager.layoutStylePadded(2));
            BlockPos pos = buffer.readBlockPos();
            addTreeDataElements(elements, Minecraft.getInstance().level, pos);
            return elements;
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }

        @Override
        public void render(GuiGraphics gui, int i, int i1) {
        }

        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public int getHeight() {
            return 0;
        }
    }
}
