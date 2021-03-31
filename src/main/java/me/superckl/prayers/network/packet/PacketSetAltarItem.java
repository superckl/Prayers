package me.superckl.prayers.network.packet;

import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.block.AltarTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

@RequiredArgsConstructor
public class PacketSetAltarItem {

	private final BlockPos pos;
	private final ItemStack stack;
	private final Direction dir;

	public void encode(final PacketBuffer buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeItem(this.stack);
		if(!this.stack.isEmpty())
			buffer.writeEnum(this.dir);
	}

	public static PacketSetAltarItem decode(final PacketBuffer buffer) {
		final BlockPos pos = buffer.readBlockPos();
		final ItemStack stack = buffer.readItem();
		if(stack.isEmpty())
			return new PacketSetAltarItem(pos, stack, null);
		final Direction dir = buffer.readEnum(Direction.class);
		return new PacketSetAltarItem(pos, stack, dir);
	}

	@SuppressWarnings("resource")
	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			supplier.get().enqueueWork(() -> {
				if(!Minecraft.getInstance().level.isAreaLoaded(this.pos, 0))
					return;
				final TileEntity te = Minecraft.getInstance().level.getBlockEntity(this.pos);
				if(!(te instanceof AltarTileEntity))
					return;
				final AltarTileEntity aTE = (AltarTileEntity) te;
				if(this.stack.isEmpty())
					aTE.clearItem();
				else
					aTE.setItem(this.stack, null, this.dir);
			});
		supplier.get().setPacketHandled(true);
	}

}
