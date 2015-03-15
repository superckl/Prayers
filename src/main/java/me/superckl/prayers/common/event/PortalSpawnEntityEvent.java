package me.superckl.prayers.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.superckl.prayers.common.entity.EntityMonsterPortal;
import net.minecraft.entity.Entity;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
@AllArgsConstructor
@Getter
@Setter
public class PortalSpawnEntityEvent extends Event{

	private final EntityMonsterPortal portal;
	private Entity entityToSpawn;

}
