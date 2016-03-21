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
    conf.codec     = PacketCodec().doublesAsFloats().booleansAsInts()

    val receiver = udp.Receiver(conf)
    receiver.connect()

    /** SERVER **/
    val cfg = server.Config()
    cfg.program      = "C:/Program Files (x86)/SuperCollider-3.6.6/scsynth.exe"
    cfg.deviceName   = Some("ASIO : Komplete Audio 6")
    cfg.memorySize   = 65536
    cfg.blockSize    = 128
    cfg.maxNodes     = 2048
    cfg.audioBuffers = 256

    /** SYNTHS **/
    val ssa = SynthDef("scape-a"){
      //______________args____
      val wet = "wet".kr(0.8);  val imp = "imp".kr(0.5); val dft = "dft".kr(0.1)
      val nfl = "nfl".kr(0.02); val blp = "blp".kr(0.1); val vol = "vol".kr(4)
      //______________local__
      val np = 16                                                             //number of partials
      val ex = Dust.ar(Seq(1*imp, 1*imp, 1*imp, 1*imp)) * 0.004               //exciter. Seq(4 elements) => each elem outputs to != channels
      val cf = LFNoise2.ar(LFNoise2.ar(0.1).madd(0.5, 0.75)).madd(400, 600)   //cutoff frequency modulator
      val no = RLPF.ar(Clip.ar(BrownNoise.ar(1).tanh, -0.5, 0.5), cf, 0.7)    //brownian noise through lpf
      val ks = KlangSpec.fill(np){                                            //Klank specifications
          ( Seq(174, 207, 261, 350),                                          //frequency.  4elems for 4ch.
            Seq(  5,   5,   4,   4),                                          //amplitude.  4elems for 4ch.
            Seq(  8,   6,   4,   2))}                                         //ring times. 4elems for 4ch.
      val dr = LFNoise2.ar(1).madd((dft-0.01)/2, dft)                         //comb delay decay time modulator
      val dl = CombC.ar(Klank.ar(ks, ex), 2, dr, LFTri.kr(2) * 0.5)           //Klank through comb delay w/linear interpolation
      val env = EnvGen.ar(Env.perc(0.6, 6), ex, 2)                            //AR env
      //_______________output__                                               //WrapOut adds a control "gate" to be released and an control "out" bus
      WrapOut(FreeVerb.ar(env * ( dl + no * nfl ), wet) * vol)                //env applied to noise + wet_klank through reverb
    }

    /** GO **/
    server.run(cfg) { serv =>

      /**
        * Setup rig angles and radius
        * The data loaded in the buffer is used for VBAP
        */
      val a = VBAPSetup(2, Seq(-60, 60, 120, -120))
      val b = Buffer.alloc(serv, a.bufferData.size)
      b.setn(a.bufferData)

      val hit = SynthDef("KarStrong") {
        val clk = "clk".kr(1);    val atk = "atk".kr(0.02);  val dec = "dec".kr(0.05);  val del = "del".kr(2)
        val wet = "wet".kr(0.7);  val rmz = "rmz".kr(0.8);   val dmp = "dmp".kr(0.05);  val amp = "amp".kr(2)

        val ex = Impulse.kr(1*clk)                                                                  //trigger
        val in  = WhiteNoise.ar(Decay2.kr(ex, attack = atk, release = dec))                           //excitation
        val fltrd = RLPF.ar(in, LFNoise0.kr(0.5).madd(400, 1000), LFNoise2.ar(0.5).madd(0.1, 0.8))    //filter
        val sig = CombN.ar(fltrd, 2, LFTri.ar(1).madd(del/2, del))                                    //Comb filter delay w/ cubic interpolation

        WrapOut(FreeVerb.ar(VBAP.ar(4, sig, b.id, LFPulse.kr(1).abs.madd(180, 0)), wet, rmz, dmp)*amp)
      }

      var ss = Synth()
      ssa.recv(serv)
      var ht = Synth()
      hit.recv(serv)

      /**
        * Upon reception, find relevant case.
        * NOTE : the OSC msg must contain the same
        *        header, number of arguments & type
        */
      receiver.action = {
        case (m@osc.Message("/test", value: Int), s) =>
          println(s"Received: $m")
          if(value == 1) ss = Synth.play(ssa.name)
          if(value == 0) ss.release(3.0)
          if(value == 2) ht = Synth.play(hit.name)
          if(value == 3) ht.release(2.0)

        case (m@osc.Message("/salle", id: Int, x: Int, y: Int, z: Int), s) =>
          println(s"Received: $m")
          ss.set( "imp" -> id )

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
