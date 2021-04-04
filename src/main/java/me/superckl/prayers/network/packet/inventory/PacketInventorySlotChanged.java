package me.superckl.prayers.network.packet.inventory;

import java.util.function.Supplier;

import lombok.Getter;
import me.superckl.prayers.ClientHelper;
import me.superckl.prayers.block.entity.InteractableInventoryTileEntity;
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
		buffer.writeVarInt(this.slot);
		buffer.writeItem(this.stack);
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		//Only the server should be sending these packets
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			supplier.get().enqueueWork(() -> {
				if(!ClientHelper.getClientLevel().isAreaLoaded(this.pos, 0))
					return;
				final TileEntity te = ClientHelper.getClientLevel().getBlockEntity(this.pos);
				if(!(te instanceof InteractableInventoryTileEntity))
					return;
				final InteractableInventoryTileEntity iTE = (InteractableInventoryTileEntity) te;
				iTE.getInternalItemHandler().setStackInSlot(this.slot, this.stack);
			});
		supplier.get().setPacketHandled(true);
	}

	public static PacketInventorySlotChanged decode(final PacketBuffer buffer) {
		final BlockPos pos = buffer.readBlockPos();
		final int slot = buffer.readVarInt();
		return new PacketInventorySlotChanged(pos, slot, buffer.readItem());
	}

}
