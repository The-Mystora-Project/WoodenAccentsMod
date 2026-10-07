package com.github.mystery2099.woodenAccentsMod.block.custom;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ThinBookshelfBlockTest {
    private static final BlockPos POS = new BlockPos(-38, -60, 9);
    // Use the back face so vanilla returns before insertion/removal. Both entrypoints must still repair.
    private static final BlockHitResult HIT = new BlockHitResult(Vec3.atCenterOf(POS), Direction.SOUTH, POS, false);

    @BeforeClass
    public static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void repairsEveryStaleSlotPatternThroughBothInteractions() {
        for (boolean withItem : new boolean[]{false, true}) {
            for (int mask = 0; mask < 64; mask++) {
                BlockState state = stateWithSlots(mask);
                Level level = mock(Level.class);
                ChiseledBookShelfBlockEntity bookshelf = spy(new ChiseledBookShelfBlockEntity(POS, state));
                when(level.getBlockEntity(POS)).thenReturn(bookshelf);
                ThinBookshelfBlock block = block();

                interact(block, state, level, withItem);
                BlockState repaired = stateWithSlots(0);
                if (mask == 0) {
                    verify(level, never()).setBlock(any(), any(), anyInt());
                    verify(bookshelf, never()).setChanged();
                } else {
                    verify(level).setBlock(POS, repaired, Block.UPDATE_ALL);
                    verify(bookshelf).setChanged();
                }
                clearInvocations(level, bookshelf);
                interact(block, repaired, level, withItem);
                verify(level, never()).setBlock(any(), any(), anyInt());
                verify(bookshelf, never()).setChanged();
            }
        }
    }

    @Test
    public void preservesStoredBooksAndTheirComponents() {
        BlockState state = stateWithSlots(1);
        Level level = mock(Level.class);
        ChiseledBookShelfBlockEntity bookshelf = new ChiseledBookShelfBlockEntity(POS, state);
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        book.set(DataComponents.CUSTOM_NAME, Component.literal("Keep this book"));
        NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
        items.set(4, book);
        CompoundTag saved = new CompoundTag();
        // Load through vanilla's persistence API, avoiding insertion's automatic flag reconciliation.
        ContainerHelper.saveAllItems(saved, items, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        bookshelf.loadWithComponents(saved, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        ItemStack stored = bookshelf.getItem(4);
        ItemStack before = stored.copy();
        when(level.getBlockEntity(POS)).thenReturn(bookshelf);

        interact(block(), state, level, true);

        verify(level).setBlock(POS, stateWithSlots(1 << 4), Block.UPDATE_ALL);
        assertSame(stored, bookshelf.getItem(4));
        assertTrue(ItemStack.isSameItemSameComponents(before, stored));
        assertEquals(before.getCount(), stored.getCount());
        assertEquals(1, bookshelf.count());
    }

    @Test
    public void leavesClientAndMissingEntityStatesAlone() throws ReflectiveOperationException {
        BlockState state = stateWithSlots(63);
        for (boolean withItem : new boolean[]{false, true}) {
            Level client = mock(Level.class);
            // Minecraft stores this side flag in a final field, so a mock's default is server-side.
            var side = Level.class.getDeclaredField("isClientSide");
            side.setAccessible(true);
            side.setBoolean(client, true);
            interact(block(), state, client, withItem);
            verify(client, never()).setBlock(any(), any(), anyInt());

            Level missingEntity = mock(Level.class);
            interact(block(), state, missingEntity, withItem);
            verify(missingEntity, never()).setBlock(any(), any(), anyInt());
        }
    }

    // Bootstrap freezes registries before loader registration. Skip construction while executing real methods.
    private static ThinBookshelfBlock block() {
        return mock(ThinBookshelfBlock.class, CALLS_REAL_METHODS);
    }

    private static void interact(ThinBookshelfBlock block, BlockState state, Level level, boolean withItem) {
        if (withItem) {
            block.useItemOn(ItemStack.EMPTY, state, level, POS, mock(Player.class), InteractionHand.MAIN_HAND, HIT);
        } else {
            block.useWithoutItem(state, level, POS, mock(Player.class), HIT);
        }
    }

    private static BlockState stateWithSlots(int mask) {
        BlockState state = Blocks.CHISELED_BOOKSHELF.defaultBlockState();
        for (int slot = 0; slot < 6; slot++) {
            state = state.setValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(slot), (mask & (1 << slot)) != 0);
        }
        return state;
    }
}
