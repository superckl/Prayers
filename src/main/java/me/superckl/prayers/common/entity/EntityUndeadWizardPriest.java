package me.superckl.prayers.common.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import me.superckl.prayers.common.item.ItemPotionPrayers;
import me.superckl.prayers.common.prayer.EnumPrayers;
import me.superckl.prayers.common.prayer.IPrayerUser;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.common.reference.ModPotions;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityUndeadWizardPriest extends EntityMob implements IPrayerUser, IRangedAttackMob{

	private int field_70846_g;
	@Getter
	private static final List[] prayers = new List[] {Arrays.asList(EnumPrayers.PROTECT_MAGIC), Arrays.asList(EnumPrayers.PROTECT_MAGIC, EnumPrayers.PROTECT_RANGE),
		Arrays.asList(EnumPrayers.PROTECT_MAGIC, EnumPrayers.PROTECT_RANGE, EnumPrayers.PROTECT_MELEE, EnumPrayers.ENCHANCE_DEFENCE_4),
		Arrays.asList(EnumPrayers.PROTECT_MAGIC, EnumPrayers.PROTECT_RANGE, EnumPrayers.PROTECT_MELEE, EnumPrayers.ENCHANCE_DEFENCE_4, EnumPrayers.ENHANCE_MAGIC_4)};

	public EntityUndeadWizardPriest(final World world, int level) {
		super(world);
		if(level <= 0){
			final int rand = this.getRNG().nextInt(1000);
			level = rand == 0 ? 4: rand < 100 ? 3: rand < 700 ? 2:1;
		}
		this.setLevel(level, true);
		this.setHealth(this.getMaxHealth());
		this.getNavigator().setCanSwim(true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityAIArrowAttack(this, 1.0D, 20, 60, 15.0F));
		this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		this.setSize(1F, 3F);
	}

	public EntityUndeadWizardPriest(final World world) {
		this(world, 0);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	protected void fall(final float p_70069_1_) {}

	@Override
	protected void updateFallState(final double p_70064_1_, final boolean p_70064_3_) {}

	@Override
	public boolean doesEntityNotTriggerPressurePlate()
	{
		return true;
	}

	@Override
	public void onLivingUpdate() {
		this.motionY *= 0.6000000238418579D;
		double d1;
		double d3;
		double d5;

		if (!this.worldObj.isRemote && (this.getAttackTarget() != null))
		{
			final Entity entity = this.getAttackTarget();

			if (entity != null)
			{
				if (this.posY < (entity.posY + 4.5D))
				{
					if (this.motionY < 0.0D)
						this.motionY = 0.0D;

					this.motionY += (0.5D - this.motionY) * 0.28000000417232513D;
				}

				final double d0 = entity.posX - this.posX;
				d1 = entity.posZ - this.posZ;
				d3 = (d0 * d0) + (d1 * d1);

				if (d3 > 12.0D)
				{
					d5 = MathHelper.sqrt_double(d3);
					this.motionX += (((d0 / d5) * 0.5D) - this.motionX) * 0.28000000417232513D;
					this.motionZ += (((d1 / d5) * 0.5D) - this.motionZ) * 0.28000000417232513D;
				}
			}
		}
		super.onLivingUpdate();
	}

	@Override
	public boolean isOnLadder()
	{
		return false;
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataWatcher().addObject(12, new Integer(1));
	}

	public int getLevel(){
		return this.getDataWatcher().getWatchableObjectInt(12);
	}

	public void setLevel(final int level, final boolean updateMaxHealth){
		this.getDataWatcher().updateObject(12, new Integer(level));
		if(updateMaxHealth)
			this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20D+(this.getLevel()*20D));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.28000000417232513D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20D+(this.getLevel()*20D));
	}

	@Override
	public int getTotalArmorValue()
	{
		final int i = super.getTotalArmorValue() + (this.getLevel()*3);

		return i;
	}

	@Override
	public void setInWeb() {}

	@Override
	protected boolean isAIEnabled()
	{
		return true;
	}

	@Override
	protected String getLivingSound()
	{
		return "mob.zombie.say"; //TODO
	}

	@Override
	protected String getHurtSound()
	{
		return "mob.zombie.hurt"; //TODO
	}

	@Override
	protected String getDeathSound()
	{
		return "mob.zombie.death"; //TODO
	}

	@Override
	protected void func_145780_a(final int p_145780_1_, final int p_145780_2_, final int p_145780_3_, final Block p_145780_4_)
	{
		this.playSound("mob.zombie.step", 0.15F, 1.0F); //TODO
	}

	@Override
	protected Item getDropItem()
	{
		return ModItems.basicBone;
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute()
	{
		return EnumCreatureAttribute.UNDEAD;
	}

	@Override
	protected void dropRareDrop(final int p_70600_1_)
	{
		//TODO
	}

	@Override
	protected void addRandomArmor()
	{
		super.addRandomArmor();

		//TODO
	}

	@Override
	public void writeEntityToNBT(final NBTTagCompound comp)
	{
		super.writeEntityToNBT(comp);
		comp.setInteger("priestLevel", this.getLevel());
	}

	@Override
	public void readEntityFromNBT(final NBTTagCompound comp)
	{
		super.readEntityFromNBT(comp);
		this.setLevel(comp.getInteger("priestLevel"), true);
	}

	@Override
	public List<EnumPrayers> getActivePrayers() {
		int index = this.getLevel() - 1;
		if(index < 0)
			return Collections.EMPTY_LIST;
		if(index > (EntityUndeadWizardPriest.prayers.length-1))
			index = EntityUndeadWizardPriest.prayers.length-1;
		return EntityUndeadWizardPriest.prayers[index];
	}

	@Override
	public void attackEntityWithRangedAttack(final EntityLivingBase target, final float uhh) {
		final double d0 = target.posX - this.posX;
		final double d1 = (target.boundingBox.minY + (target.height / 2.0F)) - (this.posY + (this.height / 2.0F));
		final double d2 = target.posZ - this.posZ;
		final float f1 = MathHelper.sqrt_float(uhh) * 0.5F;
		//TODO effect
		final EntityWizardSpell spell = new EntityWizardSpell(this.worldObj, this, d0 + (this.rand.nextGaussian() * f1), d1, d2 + (this.rand.nextGaussian() * f1), 4F + (this.getLevel()*4F));
		spell.posY = this.posY + (this.height / 2.0F) + 0.5D;
		this.worldObj.spawnEntityInWorld(spell);

	}

	@Override
	protected void dropFewItems(final boolean recentHit, final int looting){
		final ItemStack exquisite = new ItemStack(ModItems.basicBone, looting + 2, 3);
		this.entityDropItem(exquisite, 0F);
		if(this.rand.nextInt(5) == 0)
			this.entityDropItem(ItemPotionPrayers.withEffects(new ItemStack(ModItems.potion),
					this.rand.nextBoolean() ? new PotionEffect(ModPotions.prayerMaxPointsRaise.id, 0, 1):new PotionEffect(ModPotions.prayerMaxPointsRaise.id, 0, 2)), 0F);
	}

	@Override
	protected int getExperiencePoints(final EntityPlayer p_70693_1_) {
		return 10+(this.getLevel()*12);
	}

	@Override
	public void mountEntity(final Entity p_70078_1_)
	{
		this.ridingEntity = null;
	}



}
