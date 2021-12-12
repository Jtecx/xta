package xta.game.text

import xta.game.PlayerCharacter
import xta.game.creature.Gender
import xta.text.displayInches
import xta.utils.decapitalized
import xta.utils.joinToSentence
import xta.utils.prependIfNotEmpty

/*
 * Created by aimozg on 05.12.2021.
 */
class PlayerAppearance(private val character:PlayerCharacter) {
	fun describe() = buildString {
		append("<h3>"+character.name+", Level "+character.level+" "+character.raceFullName()+"</h3>")
		append(describeRace())

		append("[pg]")
		append(describeSkin())

		append(" ")
		append(describeFace())

		append(" [Your] features are adorned by ")
		append(character.face.describeMF(true))
		append(".")
		append("[pg]")

		append(
			listOf(
				describeHair(),
				describeEars()
			).joinToSentence(pair = ", while ")
		)

		val horns = describeHorns()
		val antennae = describeAntennae()
		if (horns.isNotEmpty() || antennae.isNotEmpty()) {
			append(" Beyond that, ")
			append(
				listOf(horns, antennae).joinToSentence().decapitalized()
			)
		}

		append(
			listOf(
				describeEyes(),
				describeTongue()
			).joinToSentence()
		)

		val beard = describeBeard()
		val gills = describeGills()
		if (beard.isNotEmpty() || gills.isNotEmpty()) {
			append("Also, ")
			append(
				listOf(beard, gills).joinToSentence().decapitalized()
			)
		}

		append(describeVisage())

		append("[pg]")
		append(
			listOf(
				describeArms(),
				describeLowerBody()
			).joinToSentence()
		)

		append(describeWings().prependIfNotEmpty("[pg]"))

		append(describeRearBody().prependIfNotEmpty("[pg]"))

		append(describeTail().prependIfNotEmpty(" "))

		append("[pg]")
		append(describeBreasts())

		append(describeCrotch().prependIfNotEmpty(" "))

		if (character.hasCock()) {
			append("[pg]")
			append(describeCock())
			append("[pg]")
			append(describeBalls())
		}

		append(describePussy().prependIfNotEmpty("[pg]"))

		append("[pg]")
		if (character.gender == Gender.GENDERLESS) {
			append(
				listOf(
					"You have a curious lack of any sexual endowments.",
					describeAsshole()
				).joinToSentence()
			)
		} else {
			append(describeAsshole())
		}

		append(describePiercings().prependIfNotEmpty("[pg]"))

		append(describeSpecialCases().prependIfNotEmpty("[pg]"))

		append(describePregnancy().prependIfNotEmpty("[pg]"))

		append(describeEquipment().prependIfNotEmpty("[pg]"))
	}

	fun describeRace() = buildString {
		//Discuss race
		if (character.raceName() != character.startingRace) append("[You] began [your] journey as a " + character.startingRace + ", but gave that up as [he] explored the dangers of this realm. ")
		//Height and race.
		append("[He] [is] a ")
		append(character.tallness.displayInches())
		append(" tall [racefull], with [bodytype].")
		// TODO move to races
		// val genderlessRace:Array<String> = arrayOf("half cow-morph", "half cow-girl", "cow-girl", "cow-girl", "cow-morph", "minotaur", "half-minotaur", "alraune", "liliraune", "half unicorn", "unicorn", "unicornkin", "half alicorn", "alicorn", "alicornkin", "true alicorn", "half bicorn", "bicorn", "bicornkin", "half nightmare","nightmare", "nightmarekin", "true nightmare")
//		if (pcrace !in genderlessRace) {
//			append(" tall [malefemaleherm] [race], with [bodytype].");
//		} else {
//			append(" tall [race], with [bodytype].");
//		}
	}

	fun describeSkin() =
		listOf(
			character.skin.baseType.appearanceDescription(),
			character.skin.basePattern.appearanceDescription()
		).joinToSentence(pair = ", while ")

	fun describeFace() = character.face.appearanceDescription()

	fun describeHair() = character.hair.appearanceDescription()

	fun describeEars() = character.ears.appearanceDescription()

	fun describeHorns() = character.horns.appearanceDescription()

	fun describeAntennae() = character.antennae.appearanceDescription()

	fun describeEyes() = "" // TODO

	fun describeTongue() = "" // TODO

	fun describeBeard() = "" // TODO

	fun describeGills() = "" // TODO

	fun describeVisage() = "" // TODO

	fun describeArms() = "" // TODO

	fun describeLowerBody() = "" // TODO

	fun describeWings() = "" // TODO

	fun describeRearBody() = "" // TODO

	fun describeTail() = "" // TODO

	fun describeBreasts() = "" // TODO

	fun describeCrotch() = "" // TODO

	fun describeCock() = "" // TODO

	fun describeBalls() = "" // TODO

	fun describePussy() = "" // TODO

	fun describeAsshole() = "" // TODO

	fun describePiercings() = "" // TODO

	fun describeSpecialCases() = "" // TODO

	fun describePregnancy() = "" // TODO

	fun describeEquipment() = "" // TODO
}
