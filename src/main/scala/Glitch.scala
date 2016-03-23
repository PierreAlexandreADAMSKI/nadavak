import PositionUtil.Position
import de.sciss.synth
import de.sciss.synth.{Ops, SynthDef, Synth}
import Ops._

/**
  * Created by Pierre-Alexandre Adamski on 21/03/2016.
  */
class Glitch(synthDef: SynthDef, id: Int, isActive: Int, position: Position) {

  var synth1 = Synth()

  def isActivated: Boolean = {
    if (isActive==1) true
    else false
  }

  def play(is: Boolean = isActivated) = {
    //TODO VBAP
    if (is) {
      synthDef.recv(synth.Server.default)
      this.synth1 = Synth.play(synthDef.name)
    }
    else this.synth1.release(2.5)
  }
}
