import de.sciss.synth.ugen.VBAPSetup.Polar
import scala.math.{atan, sqrt, pow}

/**
  * Created by Pierre-Alexandre Adamski on 22/03/2016.
  */
object PositionUtil {
  class Position(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0){
    def toPolar: Polar = {
      val azi = atan(y/x)
      val ele = atan(sqrt(pow(x,2) + pow(y,2)) / z)
      Polar(azi, ele)
    }
  }
}
