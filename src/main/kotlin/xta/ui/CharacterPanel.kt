package xta.ui

import kotlinx.dom.addClass
import kotlinx.dom.clear
import kotlinx.dom.removeClass
import org.w3c.dom.HTMLElement
import xta.charview.CharViewImage
import xta.game.PlayerCharacter
import xta.game.settings.GameSettings
import xta.game.stats.BuffableStat
import xta.game.stats.PrimaryStat
import xta.text.toNiceString
import xta.utils.toggleClass
import xta.utils.wrapIfNotEmpty
import kotlin.math.roundToInt

/*
 * Created by aimozg on 02.12.2021.
 */
class CharacterPanel : UiTemplate("char-panel") {
	private val container = fragment.ref("container")
	private val titleDiv = fragment.ref("title")
	private val subtitleDiv = fragment.ref("subtitle")
	private val xpBar = fragment.ref("xpbar")

	private val tabBtnMain = fragment.ref("seltab-main")
	private val tabBtnCombat = fragment.ref("seltab-combat")
	private val tabContentMain = fragment.ref("tab-main")
	private val tabContentCombat = fragment.ref("tab-combat")

	private val strVal = fragment.ref("stat-str")
	private val touVal = fragment.ref("stat-tou")
	private val speVal = fragment.ref("stat-spe")
	private val intVal = fragment.ref("stat-int")
	private val wisVal = fragment.ref("stat-wis")
	private val libVal = fragment.ref("stat-lib")
	private val senVal = fragment.ref("stat-sen")
	private val corVal = fragment.ref("stat-cor")
	private val hpBar = BarGauge().also { it.insertTo(fragment.ref("bar-hp")) }
	private val lustBar = BarGauge().also { it.insertTo(fragment.ref("bar-lust")) }
	private val wrathBar = BarGauge().also { it.insertTo(fragment.ref("bar-wrath")) }
	private val staminaBar = BarGauge().also { it.insertTo(fragment.ref("bar-stamina")) }
	private val manaBar = BarGauge().also { it.insertTo(fragment.ref("bar-mana")) }
	private val sfBar = BarGauge().also { it.insertTo(fragment.ref("bar-sf")) }
	private val gemsValue = fragment.ref("gems")
	private val ssValue = fragment.ref("soulstones")
	private val btnRenderZoom = fragment.ref("render-zoombtn")
	private val btnRenderShow = fragment.ref("render-showbtn")
	private val renderDiv = fragment.ref("render")

	private val combatStatDiv = fragment.ref("combat-stats")
	private val csDodge = CombatStat("Dodge").also { it.insertTo(combatStatDiv) }
	private val csDodgeMelee = CombatStat("Melee dodge bonus").also { it.insertTo(combatStatDiv) }
	private val csAimMelee = CombatStat("Melee aim").also { it.insertTo(combatStatDiv) }
	private val csDmgMelee = CombatStat("Melee damage bonus").also { it.insertTo(combatStatDiv) }
	private val csDmgRangedMult = CombatStat("Ranged damage").also { it.insertTo(combatStatDiv) }
	private val csResistPhys = CombatStat("Resist physical").also { it.insertTo(combatStatDiv) }
	private val csResistMag = CombatStat("Resist magical").also { it.insertTo(combatStatDiv) }
	private val csResistLust = CombatStat("Resist lust").also { it.insertTo(combatStatDiv) }
	private val csSpellPower = CombatStat("Spell power").also { it.insertTo(combatStatDiv) }
	private val csSoulskillPower = CombatStat("Soulskill power").also { it.insertTo(combatStatDiv) }
	private val csSoulskillCost = CombatStat("Soulskill cost").also { it.insertTo(combatStatDiv) }

	private var lastCharacter: PlayerCharacter? = null
	init {
		btnRenderZoom.onclick = {
			GameSettings.data.renderX2 = GameSettings.data.renderX2?.not()?:true
			GameSettings.save()
			refresh()
		}
		btnRenderShow.onclick = {
			GameSettings.data.render = GameSettings.data.render?.not()?:true
			GameSettings.save()
			refresh()
		}
		setupTabList(
			tabBtnMain to tabContentMain,
			tabBtnCombat to tabContentCombat,
		)
	}
	fun hide() {
		container.style.display = "none"
	}
	fun show() {
		container.style.display = ""
	}
	fun refresh() {
		showCharacter(lastCharacter)
	}
	fun showCharacter(char: PlayerCharacter?,
	                  render: Boolean = GameSettings.data.render?:false,
	                  renderX2: Boolean = GameSettings.data.renderX2?:false
	) {
		lastCharacter = char
		btnRenderZoom.textContent = if (renderX2) "zoom_out" else "zoom_in"
		btnRenderShow.textContent = if (render) "visibility_off" else "visibility"
		hideTooltip()
		if (char == null) {
			hide()
			return
		}
		show()
		// TODO tooltips!
		titleDiv.textContent = char.name
		subtitleDiv.textContent = "Level ${char.level} ${char.raceFullName()}"
		val xpp = char.xp.toDouble() / char.xpToLevelUp()
		xpBar.style.width = "" + (xpp * 100).coerceIn(0.0, 100.0) + "%"
		if (xpp >= 1.0) {
			xpBar.addClass("-levelup")
		} else {
			xpBar.removeClass("-levelup")
		}
		// TODO mark buffed/debuffed stats with text-positive/text-negative classses
		val stats = listOf(
			strVal to char.strStat,
			touVal to char.touStat,
			speVal to char.speStat,
			intVal to char.intStat,
			wisVal to char.wisStat,
			libVal to char.libStat,
		)
		for ((div, stat) in stats) {
			div.textContent = stat.value.roundToInt().toString()
			addBuffTooltip(div,stat)
			div.toggleClass("-buffed", stat.hasPositiveBuffs())
			div.toggleClass("-debuffed", stat.hasNegativeBuffs())
		}
		senVal.textContent = char.sens.roundToInt().toString()
		corVal.textContent = char.cor.toString()
		hpBar.displayValue(
			value = char.hp,
			max = char.maxHp(),
			text = char.hp.toString()
		)
		addResourceTooltip(
			hpBar.container,
			"HP",
			char.hp,
			char.maxHp(),
			char.maxHpPerLevelStat,
			char.maxHpBaseStat,
			char.maxHpMultStat,
		)
		// TODO display white/gray/black magic thresholds
		lustBar.displayValue(
			value = char.lust,
			extra = char.minLust(),
			max = char.maxLust(),
			text = char.lust.toString()
		)
		addResourceTooltip(
			lustBar.container,
			"Lust",
			char.lust,
			char.maxLust(),
			char.maxLustPerLevelStat,
			char.maxLustBaseStat,
			char.maxLustMultStat,
		)
		wrathBar.displayValue(
			value = char.wrath,
			max = char.maxWrath(),
			text = char.wrath.toString()
		)
		addResourceTooltip(
			wrathBar.container,
			"Wrath",
			char.wrath,
			char.maxWrath(),
			char.maxWrathPerLevelStat,
			char.maxWrathBaseStat,
			char.maxWrathMultStat,
		)
		staminaBar.displayValue(
			value = char.stamina,
			max = char.maxFatigue(),
			text = char.stamina.toString()
		)
		addResourceTooltip(
			staminaBar.container,
			"Stamina",
			char.stamina,
			char.maxFatigue(),
			char.maxFatiguePerLevelStat,
			char.maxFatigueBaseStat,
			char.maxFatigueMultStat,
		)
		manaBar.displayValue(
			value = char.mana,
			max = char.maxMana(),
			text = char.mana.toString()
		)
		addResourceTooltip(
			manaBar.container,
			"Mana",
			char.mana,
			char.maxMana(),
			char.maxManaPerLevelStat,
			char.maxManaBaseStat,
			char.maxManaMultStat,
		)
		sfBar.displayValue(
			value = char.soulforce,
			max = char.maxSoulforce(),
			text = char.soulforce.toString()
		)
		addResourceTooltip(
			sfBar.container,
			"Soulforce",
			char.soulforce,
			char.maxSoulforce(),
			char.maxSfPerLevelStat,
			char.maxSfBaseStat,
			char.maxSfMultStat,
		)
		gemsValue.textContent = char.gems.toString()
		ssValue.textContent = "0" // TODO soulstones
		// TODO status effects (the cool kind)

		csDodge.showForStat(true, char.dodgeStat)
		csDodgeMelee.showForStat(true, char.meleeDodgeStat)
		csAimMelee.showForStat(true, char.meleeAimStat)
		csDmgMelee.showForStat(false, char.meleeDamageStat)
		csDmgRangedMult.showForStat(true, char.rangedDamageMultStat)
		csResistPhys.showForStat(true, char.resistPhysStat)
		csResistMag.showForStat(true, char.resistMagStat)
		csResistLust.showForStat(true, char.resistLustStat)
		csSpellPower.showForStat(true, char.spellPowerStat)
		csSoulskillPower.showForStat(true, char.soulskillPowerStat)
		csSoulskillCost.showForStat(true, char.soulskillCostStat)

		renderDiv.clear()
		if (render) {
			renderDiv.append(CharViewImage.INSTANCE.renderCharacter(char, renderX2).canvas)
		}
	}

	private fun addResourceTooltip(
		container: HTMLElement,
		name: String,
		value: Double,
		maxValue: Double,
		perLevelStat: BuffableStat,
		baseStat: BuffableStat,
		multStat: BuffableStat
	) {
		container.addTooltip(buildString {
			append(name+": "+value.toInt()+"/"+maxValue.toInt()+"\n")
//			append("(${char.hpRatio.times(100).roundToInt()}%)")
			append("<div><b>Per level</b>: <span class='buff-value'>")
			append(perLevelStat.value.toInt())
			append("</span></div>")
			append(perLevelStat.explainBuffs(false))
			append("<div class='stat-buffs -hp'>")
			append("<div><b>Bonus</b>: <span class='buff-value'>")
			append(baseStat.value.toInt())
			append("</span></div>")
			append(baseStat.explainBuffs(false))
			append("<div><b>Multiplier</b>: <span class='buff-value'>")
			append(multStat.value.times(100).toInt())
			append("%</span></div>")
			append(multStat.explainBuffs(true))
		})
	}

	private class CombatStat(
		statName: String
	):UiTemplate("char-combat-stat") {
		private val divValue = fragment.ref("stat-value")
		init {
			fragment.ref("stat-name").textContent = statName
		}

		fun showForStat(asPercentage:Boolean, stat:BuffableStat) {
			divValue.toggleClass("-buffed", stat.hasPositiveBuffs())
			divValue.toggleClass("-debuffed", stat.hasNegativeBuffs())
			divValue.textContent =
				if (asPercentage) (stat.value*100).roundToInt().toString()+"%"
				else stat.value.toNiceString(1)
			divValue.addTooltip(
				stat.explainBuffs(asPercentage = asPercentage).wrapIfNotEmpty(
					"<div class='stat-buffs -"+stat.statName+"'>",
					"</div>"
				)
			)
		}
	}

	private fun addBuffTooltip(
		element: HTMLElement,
		stat: PrimaryStat
	) {
		element.addTooltip(
			"<div class='stat-buffs -"+stat.statName+"'>" +
			stat.explainBuffs() +
			"</div>"
		)
	}
}
