import de.sciss.synth._
import de.sciss.synth.ugen._
import Ops._

val cfg = Server.Config()
cfg.program      = "C:/Program Files (x86)/SuperCollider-3.6.6/scsynth.exe"
cfg.deviceName   = Some("ASIO : Komplete Audio 6")
cfg.memorySize   = 65536
cfg.blockSize    = 128
cfg.maxNodes     = 2048
cfg.audioBuffers = 256

Server.run(cfg) { s =>
  // 8 channel ring
  val a = VBAPSetup(2, Seq(-60, 60, -120, 120))

  val b = Buffer.alloc(s, a.bufferData.size)
  b.setn(a.bufferData)

  val x = play {
    val azi = "azi".kr(0)
    val ele = "ele".kr(0)
    val spr = "spr".kr(0)
    VBAP.ar(4, PinkNoise.ar(0.2), b.id, LFSaw.ar(1).madd(180, 0), ele, spr)
  }

  // test them out
  x.set("azi" -> a.directions(1).azi)
  x.set("azi" -> a.directions(2).azi)
  x.set("azi" -> a.directions(3).azi)
  // ...
  x.set("azi" -> a.directions(7).azi)
  x.set("azi" -> a.directions(0).azi)

  // try the spread
  x.set("spr" -> 20)
  x.set("spr" -> 100) // all speakers

  x.release(10.0);
  b.free();
}

