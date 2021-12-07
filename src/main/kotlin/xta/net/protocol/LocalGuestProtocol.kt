package xta.net.protocol

import org.khronos.webgl.Uint8Array
import xta.Game
import xta.Player
import xta.ScreenManager
import xta.game.PlayerCharacter
import xta.game.combat.Combat
import xta.logging.LogManager
import xta.net.transport.AbstractGuestConnection
import xta.utils.decodeToJson
import xta.utils.stringify
import kotlin.js.Json

class LocalGuestProtocol(
	override val player: Player,
	val connection: AbstractGuestConnection?
): GuestProtocol() {
	override val isConnected: Boolean
		get() = connection?.isConnected != false
	override val identity: String
		get() = connection?.identity?:"[LocalHost]"

	override fun toLogString() = connection?.toLogString()?:"[LocalHost]"

	override fun onMessage(message: MessageToGuest) {
		logger.ifdebug(this) { "< ${message.stringify()}" }
		message.chat?.let {
			ScreenManager.displayChatMessage(it)
			return
		}
		message.charAccepted?.let { msg ->
			Game.setMyPlayerId(msg.yourId)
			Game.hostProtocol.sendStatusRequest(screen=true)
			return
		}
		message.charRejected?.let {
			Game.localErrorMessage("Your character was rejected by server: ${it.message}")
			return
		}
		message.statusUpdate?.let { msg ->
			msg.char?.let {
				Game.me.char = PlayerCharacter().apply { deserializeFromJson(it) }
				ScreenManager.updateCharacter()
			}
			msg.screen?.let {
				Game.me.screen = it
				ScreenManager.displayScreen()
			}
			return
		}
		message.sceneTransition?.let { msg ->
			Game.me.screen = msg.screen
			ScreenManager.displayScreen()
			return
		}
		message.combatUpdate?.let { cum -> /* yes, and?*/
			if (cum.inCombat == true) {
				for (id in ((cum.partyA?: emptyArray()) + (cum.partyB?: emptyArray()))) {
					@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
					Game.requireKnownPlayer(id, cum.playerData?.get(id) as Json?)
				}
				// TODO consider party change
				if (!Game.me.inCombat) {
					Game.me.combat = Combat(
						Combat.Party(
							(cum.partyA?: emptyArray()).map {
								Game.requireKnownPlayer(it)
							}
						),
						Combat.Party(
							(cum.partyB?: emptyArray()).map {
								Game.requireKnownPlayer(it)
							}
						)
					)
					ScreenManager.transitionToCombat()
				} else {
					ScreenManager.updateCombatScreen(canChangeScreen=true)
				}
			} else {
				if (Game.me.inCombat) {
					ScreenManager.transitionOutOfCombat()
					Game.me.combat?.ongoing = false
				}
				Game.me.updateScreen()
			}
			return
		}
		logger.error(this,"Received bad message "+message.stringify())
	}

	fun onRawMessage(message: Uint8Array) {
		try {
			@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
			val json = message.decodeToJson() as MessageToGuest
			onMessage(json)
		} catch (e:Throwable) {
			logger.warn(this,"Malformed message from host",e)
		}
	}

	companion object {
		private val logger by lazy {
			LogManager.getLogger("xta.net.protocol.LocalRequestHandler")
		}
	}
}
