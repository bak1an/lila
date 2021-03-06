package lila.bot

import scala.concurrent.Promise

import chess.format.Uci

import lila.game.{ Game, Pov }
import lila.hub.actorApi.map.Tell
import lila.hub.actorApi.round.BotPlay

final class BotPlayer(roundMap: akka.actor.ActorSelection) {

  def apply(pov: Pov, uciStr: String): Funit =
    Uci(uciStr).fold(fufail[Unit](s"Invalid UCI: $uciStr")) { uci =>
      if (!pov.isMyTurn) fufail("Not your turn, or game already over")
      else {
        val promise = Promise[Unit]
        roundMap ! Tell(pov.gameId, BotPlay(pov.playerId, uci, promise.some))
        promise.future
      }
    }
}
