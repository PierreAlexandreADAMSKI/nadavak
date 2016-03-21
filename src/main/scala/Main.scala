import de.sciss.synth.ugen.VBAPSetup.Polar
import de.sciss.synth.ugen._
import de.sciss.{synth, osc}
import de.sciss.osc._
import de.sciss.synth._
import Ops._

/**
  * Created by Pierre-Alexandre Adamski on 16/03/2016.
  */
object Main {

  val server = synth.Server
  val udp = osc.UDP

  def main(args: Array[String]) {

    //var socket = new InetSocketAddress(InetAddress.getLocalHost, 57120)

    val conf = udp.Config()
    conf.localPort = 57120
    conf.codec = PacketCodec().doublesAsFloats().booleansAsInts()

    val receiver = udp.Receiver(conf)
    receiver.connect()

    val vbap = VBAP
    val speakersSetup = VBAPSetup

    /* idea
     * si on change le position des speaker au lieu de celle du joueur
     * ca devrait permettre de spatialiser sa position dans la cave
     * à voir si on peut calculer ca sur une autre thread mais ca devrait le faire
     *
     * je sais pas si minDist c'est le rayon sinon il manque la notion de rayon quelque part
     * et c'est très embêtant!
     *
     * NICO???
     */
    speakersSetup.apply(3, Seq(new Polar(45, 45), Polar(135, 45), Polar(-135, 45), Polar(-45, 45)), 2)

    /** SERVER **/
    val cfg = server.Config()
    cfg.program = "/Applications/SuperCollider/SuperCollider.app/Contents/Resources/scsynth"
    cfg.deviceName = Some("ASIO : Komplete Audio 6")
    cfg.memorySize = 65536
    cfg.blockSize = 128
    cfg.maxNodes = 2048
    cfg.audioBuffers = 256

    server.run(cfg) { serv =>

      receiver.action = {
        // match against a particular message
        case (m@osc.Message("/test", value: Int), s) =>
          println(s"Received: $m")
          val a = play {
            SinOsc.ar(value) * Decay2.kr(Impulse.kr(2))
          }
        //a.release(2)
        //a.free()

        case (m@osc.Message("/salle", id: Int, x: Int, y: Int, z: Int), s) =>

        case (m@osc.Message("/objet", id: Int, isActive: Boolean, io: Int), s) =>

        //TODO create synths for depending of the room
        case (m@osc.Message("/joueur", typ: Boolean, interaction: Int, x: Int, y: Int, z: Int), s) =>
          var sType = "relative"
          if (typ) sType = "absolue"
          println("position" + sType + "du joueur")

        case (m@osc.Message("/quit", c: Int), s) =>
          println("closing port")
          receiver.close()
          serv.quit()

        case (m@osc.Message("/reset", c: Int), s) =>
          println("reconnecting port")
          if (!receiver.isConnected) receiver.connect()

        case _ =>
          println("Unsupported OSC Message")
      }
    }
  }
}
