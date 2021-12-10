package xta.game.creature.body

import xta.flash.CocId
import xta.flash.CocIdLookup

enum class ArmType(override val cocID: Int): CocId {
	HUMAN(0),
	HARPY(1),
	SPIDER(2),
	MANTIS(3),
	BEE(4),
	SALAMANDER(5),
	PHOENIX(6),
	PLANT(7),
	SHARK(8),
	GARGOYLE(9),
	WOLF(10),
	LION(11),
	KITSUNE(12),
	FOX(13),
	LIZARD(14),
	DRACONIC(15),
	YETI(16),
	ORCA(17),
	PLANT2(18),
	DEVIL(19),
	ONI(20),
	ELF(21),
	RAIJU(22),
	RED_PANDA(23),
	GARGOYLE_2(24),
	CAT(25),
	AVIAN(26),
	GRYPHON(27),
	SPHINX(28),
	PIG(29),
	BOAR(30),
	ORC(31),
	DISPLACER(32),
	CAVE_WYRM(33),
	HINEZUMI(34),
	BEAR(35),
	GOO(36),
	HYDRA(37),
	GHOST(38),
	JIANGSHI(39),
	RAIJU_PAWS(40),
	YUKI_ONNA(41),
	MELKIE(42),
	CENTIPEDE(43),
	KRAKEN(44),
	FROSTWYRM(45),
	CANCER(46),
	USHI_ONI(47),
	KAMAITACHI(48),
	GAZER(49),
	RACCOON(50),
	WEASEL(51),
	SQUIRREL(52),
	WENDIGO(53),
	BAT(54),
	SEA_DRAGON(55),
	MINDBREAKER(56),
	;
	companion object: CocIdLookup<ArmType>(values())
}