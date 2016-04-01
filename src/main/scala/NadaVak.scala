import java.util.Date

import com.illposed.osc.{OSCMessage, OSCListener, OSCPortIn}
import de.sciss.synth
import de.sciss.synth._


/**
  * Created by Pierre-Alexandre Adamski on 23/03/2016.
  */
object NadaVak extends App {

  //debug method for malformed packet analysis
  /*
    val debug = new Thread {
      val buf = java.nio.ByteBuffer.allocate(1024)
      val channel = java.nio.channels.DatagramChannel.open()
      channel.bind(localhost -> 57120)

      override def run(): Unit = {
        buf.clear()
        println("Waiting...")
        channel.receive(buf)
        println("Received!")
        buf.flip()
        osc.Packet.printHexOn(buf, System.out)
      }
    }
  */

  /*
    /** OSC SETUP **/
    val udp = osc.UDP
    val conf = udp.Config()
    conf.localPort = 57120
    conf.codec = PacketCodec().doublesAsFloats().booleansAsInts()
    val receiver = udp.Receiver(conf)
    receiver.connect()

  */
  /** SERVER SETUP **/
  val server = synth.Server
  val cfg = server.Config()
  cfg.program = "/Applications/SuperCollider/SuperCollider.app/Contents/Resources/scsynth"
  cfg.deviceName = Some("Komplete Audio 6")
  cfg.memorySize = 65536
  cfg.blockSize = 128
  cfg.maxNodes = 2048
  cfg.audioBuffers = 256

  server.run(cfg) { serv =>

    /*
      * start debug on parallel thread
    debug.start()
    */

    /**
      * Setup rig angles and radius
      * The data loaded in the buffer is used for VBAP
      */

    MySynths.load()

    var ss = Synth()
    var ht = Synth()

    val port = new OSCPortIn(57120)
    val moveListener = new OSCListener {
      override def acceptMessage(date: Date, oscMessage: OSCMessage): Unit = {
        println("TEST | addr : " + oscMessage.getAddress + " | args : " + oscMessage.getArguments.toString)
      }
    }
    val roomListener = new OSCListener {
      override def acceptMessage(date: Date, oscMessage: OSCMessage): Unit = {
        println("ROOM | addr : " + oscMessage.getAddress + " | args : " + oscMessage.getArguments.toString)
      }
    }

    val quitListener = new OSCListener {
      override def acceptMessage(date: Date, oscMessage: OSCMessage): Unit = {
        println("quit")
        port.stopListening()
        serv.quit()
      }
    }
    port.addListener("/test", moveListener)
    port.addListener("/salle", roomListener)
    port.addListener("/quit", quitListener)
    port.startListening()

    /*
        /**
          * Upon reception, find relevant case.
          * NOTE : the OSC msg must contain the same
          * header, number of arguments & type
          */

        receiver.dump(osc.Dump.Text)
        receiver.action = {
          /**
            * BufferOverflow :

            * case (m@osc.Bundle(Timetag.now, osc.Message("/test", x: Float, y: Float, z: Float)), s) =>
            * case (m@osc.Message("/test", x: Float, y: Float, z: Float), s) =>
            *
            * works but ugly =>

            * case (m@osc.Bundle(Timetag.now, osc.Message("/x", x: Float), osc.Message("/y", y: Float), osc.Message("/z", z: Float)), s) =>
            *
            */

          case (m@osc.Bundle(Timetag.now, osc.Message("/x", x: Float), osc.Message("/y", y: Float), osc.Message("/z", z: Float)), s) =>
            println("received :\nX : " + x + " Y : " + y + " Z : " + z)

          //        //println(s"Received: $m")
          //        if (x == 1) {
          //          ss = Synth.play("soundscape-1")
          //          println("got 1")
          //        }
          //        else if (x == 0) {
          //          ss release 3.0
          //          println("got 0")
          //        }
          //        else if (x == 2) ht = Synth.play("KarStrong", args = Seq("buf" -> b.id))
          //        else if (x == 3) ht release 2.0

          case (m@osc.Message("/salle", id: Int, x: Int, y: Int, z: Int), s) =>
            println(s"Received: $m")
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
            println("Unsupported OSC Message")

        }
        */
  }
}
