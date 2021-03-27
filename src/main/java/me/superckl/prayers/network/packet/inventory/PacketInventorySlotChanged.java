package me.superckl.prayers.network.packet.inventory;

import java.util.function.Supplier;

import lombok.Getter;
import me.superckl.prayers.inventory.InteractableInventoryTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

@Getter
public class PacketInventorySlotChanged {

	private final BlockPos pos;
	private final int slot;
	private final ItemStack stack;

	public PacketInventorySlotChanged(final BlockPos pos, final int slot, final ItemStack stack) {
		this.pos = pos;
		this.slot = slot;
		this.stack = stack;
	}

	public void encode(final PacketBuffer buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeInt(this.slot);
		buffer.writeItem(this.stack);
	}

	@SuppressWarnings("resource")
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			supplier.get().enqueueWork(() -> {
				if(!Minecraft.getInstance().level.isAreaLoaded(this.pos, 0))
					return;
				final TileEntity te = Minecraft.getInstance().level.getBlockEntity(this.pos);
				if(!(te instanceof InteractableInventoryTileEntity))
					return;
				final InteractableInventoryTileEntity iTE = (InteractableInventoryTileEntity) te;
				iTE.setItem(this.slot, this.stack);
			});
		supplier.get().setPacketHandled(true);
	}

	public static PacketInventorySlotChanged decode(final PacketBuffer buffer) {
		final BlockPos pos = buffer.readBlockPos();
		final int slot = buffer.readInt();
		return new PacketInventorySlotChanged(pos, slot, buffer.readItem());
	}

}
