import de.sciss.synth.Server

/**
  * Created by Pierre-Alexandre Adamski on 21/03/2016.
  */
class Room(player: Player, io: Int, glitches: List[Glitch]) {
  val p: Player = player
  val gs: List[Glitch] = glitches

  private def getAllGlitch: Int = {
     gs.size
  }

  def empty(): Boolean = {
    if (io==1) false
    else true
  }

  def run(code: Server => Unit): Unit = {
    val server = Server
    server.run(code)
  }
}
