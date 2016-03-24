import de.sciss.synth.ugen.VBAPSetup.Polar
import de.sciss.synth.ugen._
import de.sciss.{synth, osc}
import de.sciss.osc._
import de.sciss.synth._
import Ops._


/**
  * Created by Pierre-Alexandre Adamski on 23/03/2016.
  */
object NadaVak extends App {

  val server = synth.Server
  val udp = osc.UDP

  /** OSC SETUP **/
  val conf = udp.Config()
  conf.localPort = 57120
  conf.codec = PacketCodec().doublesAsFloats().booleansAsInts()

  val receiver = udp.Receiver(conf)
  receiver.connect()

  /** SERVER SETUP **/
  val cfg = server.Config()
  cfg.program = "/Applications/SuperCollider/SuperCollider.app/Contents/Resources/scsynth"
  cfg.deviceName = Some("Komplete Audio 6")
  cfg.memorySize = 65536
  cfg.blockSize = 128
  cfg.maxNodes = 2048
  cfg.audioBuffers = 256

  server.run(cfg) { serv =>

    /**
      * Setup rig angles and radius
      * The data loaded in the buffer is used for VBAP
      */

    val a = VBAPSetup(2, Seq(Polar(-60, 0), Polar(60, 0), Polar(110, 0), Polar(-110, 0), 3.35))
    val b = Buffer.alloc(serv, a.bufferData.size)
    b setn a.bufferData

    MySynths.save()
    //SynthDef.load(path = "src/synths/soundscape-1.scsyndef", serv)
    //load seams to work but we still have an error
    //SynthDef.load(path = "src/synths/KarStrong.scsyndef", serv)

    var ss = Synth()
    val ssa = MySynths.getByName("soundscape-1")
    ssa recv serv
    var ht = Synth()
    val hit = MySynths.getByName("KarStrong")
    hit recv serv
    /**
      * Upon reception, find relevant case.
      * NOTE : the OSC msg must contain the same
      * header, number of arguments & type
      */
    receiver.action = {
      // match against a particular message
      case (m@osc.Message("/test", value: Int), s) =>
        //println(s"Received: $m")
        if (value == 1) {
          ss = Synth.play(ssa.name)
          println("got 1")
        }
        else if (value == 0) {
          ss release 3.0
          println("got 0")
        }
        else if (value == 2) ht = Synth.play(hit.name, args = Seq("buf" -> b.id))
        else if (value == 3) ht release 2.0

      case (m@osc.Message("/salle", id: Int, x: Int, y: Int, z: Int), s) =>
      //println(s"Received: $m")
      //ss.set( "imp" -> id )

      case (m@osc.Message("/objet", id: Int, isActive: Boolean, io: Int), s) =>

      //TODO create synths depending of the room
      case (m@osc.Message("/joueur", typ: Boolean, interaction: Int, x: Int, y: Int, z: Int), s) =>
        var sType = "relative"
        if (typ) sType = "absolue"
      //println("position" + sType + "du joueur")

      case (m@osc.Message("/quit", c: Int), s) =>
        //println("closing port")
        receiver.close()
        serv.quit()

      case (m@osc.Message("/reset", c: Int), s) =>
        //println("reconnecting port")
        if (!receiver.isConnected) receiver.connect()

      case _ =>
      //println("Unsupported OSC Message")
    }
  }
}
