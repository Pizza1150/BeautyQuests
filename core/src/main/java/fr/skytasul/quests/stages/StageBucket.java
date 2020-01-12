package fr.skytasul.quests.stages;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketFillEvent;

import fr.skytasul.quests.QuestsConfiguration;
import fr.skytasul.quests.api.stages.AbstractStage;
import fr.skytasul.quests.players.PlayerAccount;
import fr.skytasul.quests.players.PlayersManager;
import fr.skytasul.quests.players.PlayersManagerYAML;
import fr.skytasul.quests.structure.QuestBranch;
import fr.skytasul.quests.structure.QuestBranch.Source;
import fr.skytasul.quests.utils.Lang;
import fr.skytasul.quests.utils.Utils;
import fr.skytasul.quests.utils.XMaterial;

public class StageBucket extends AbstractStage {

	private BucketType bucket;
	private int amount;
	
	public StageBucket(QuestBranch branch, BucketType bucket, int amount){
		super(branch);
		this.bucket = bucket;
		this.amount = amount;
	}
	
	public BucketType getBucketType(){
		return bucket;
	}
	
	public int getBucketAmount(){
		return amount;
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onBucketFill(PlayerBucketFillEvent e){
		Player p = e.getPlayer();
		PlayerAccount acc = PlayersManager.getPlayerAccount(p);
		if (branch.hasStageLaunched(acc, this)){
			if (BucketType.fromMaterial(XMaterial.fromMaterial(e.getItemStack().getType())) == bucket){
				int amount = (int) getData(acc, "amount");
				if (amount <= 1) {
					finishStage(p);
				}else {
					updateObjective(acc, p, "amount", --amount);
				}
			}
		}
	}

	protected void initPlayerDatas(PlayerAccount acc, Map<String, Object> datas) {
		datas.put("amount", amount);
	}
	
	protected String descriptionLine(PlayerAccount acc, Source source){
		return Lang.SCOREBOARD_BUCKET.format(Utils.getStringFromNameAndAmount(bucket.getName(), QuestsConfiguration.getItemAmountColor(), (int) getData(acc, "amount"), false));
	}

	protected Object[] descriptionFormat(PlayerAccount acc, Source source){
		return new Object[] { Utils.getStringFromNameAndAmount(bucket.getName(), QuestsConfiguration.getItemAmountColor(), (int) getData(acc, "amount"), false) };
	}
	
	protected void serialize(Map<String, Object> map){
		map.put("bucket", bucket.name());
		map.put("amount", amount);
	}
	
	public static AbstractStage deserialize(Map<String, Object> map, QuestBranch branch){
		StageBucket stage = new StageBucket(branch, BucketType.valueOf((String) map.get("bucket")), (int) map.get("amount"));

		if (map.containsKey("players")) {
			PlayersManagerYAML migration = PlayersManagerYAML.getMigrationYAML();
			((Map<String, Object>) map.get("players")).forEach((acc, amount) -> stage.setData(migration.getByIndex(acc), "amount", (int) amount));
		}

		return stage;
	}

	public static enum BucketType{
		WATER(Lang.BucketWater, XMaterial.WATER_BUCKET), LAVA(Lang.BucketLava, XMaterial.LAVA_BUCKET), MILK(Lang.BucketMilk, XMaterial.MILK_BUCKET);

		private Lang name;
		private XMaterial type;
		
		private BucketType(Lang name, XMaterial type){
			this.name = name;
			this.type = type;
		}
		
		public String getName(){
			return name.toString();
		}
		
		public XMaterial getMaterial(){
			return type;
		}
		
		public static BucketType fromMaterial(XMaterial type){
			if (type == XMaterial.WATER_BUCKET) return WATER;
			if (type == XMaterial.LAVA_BUCKET) return LAVA;
			if (type == XMaterial.MILK_BUCKET) return MILK;
			throw new IllegalArgumentException(type.name() + " does not correspond to any bucket type");
		}
	}
	
}
