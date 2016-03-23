import java.util

import de.sciss.synth
import de.sciss.synth.SynthDef
import java.util.List
import de.sciss.synth._

import de.sciss.synth.ugen._

/**
  * Created by Pierre-Alexandre Adamski on 23/03/2016.
  */
object MySynths {

  def load(buffer: Buffer): Seq[SynthDef] = {
    Seq(SynthDef("soudscape-1") {
      //______________args____
      val wet = "wet".kr(0.8) //theses values
      val imp = "imp".kr(0.5) //can be modified
      val dft = "dft".kr(0.1) //real-time
      val nfl = "nfl".kr(0.02) //using the
      val blp = "blp".kr(0.1) //<synth_name>.set( "val_name" -> 0.0 )
      val amp = "amp".kr(2.0)                                                  //command
      //______________local__
      val np = 8 //number of partials
      val ex = Dust.ar(Seq(1 * imp, 1 * imp, 1 * imp, 1 * imp)) * 0.004 //exciter. Seq(4 elements) => each elem outputs to != channels
      val cf = LFNoise2.ar(LFNoise2.ar(0.1).madd(0.5, 0.75)).madd(400, 600) //cutoff frequency modulator
      val no = RLPF.ar(Clip.ar(BrownNoise.ar(1), -0.5, 0.5), cf, 0.7) * nfl //brownian noise through lpf
      val ks = KlangSpec.fill(np) {
          //Klank specifications
          (Seq(174, 207, 261, 350), //frequency.  4elems for 4ch.
            Seq(5, 5, 4, 4), //amplitude.  4elems for 4ch.
            Seq(8, 6, 4, 2))
        } //ring times. 4elems for 4ch.
      val dr = LFNoise2.ar(1).madd((dft - 0.01) / 2, dft) //comb delay decay time modulator
      val dl = CombL.ar(Klank.ar(ks, ex), 2, dr, LFTri.kr(2) * 0.5) //Klank through comb delay w/linear interpolation
      val env = EnvGen.ar(Env.perc(0.6, 6), ex, 2) //AR env
      //_______________output__
      WrapOut(FreeVerb.ar(env * (dl + no), 0.8) * amp) //env applied to noise + wet_klank through reverb
    }, SynthDef("glitch-1") {
      val gate = "gate".kr(0.0)
      val detune = "detune".kr(64.0)
      val dshft = "dshft".kr(10.0)
      val wet = "wet".kr(0.1)
      val trg = Dust.kr(2) * 0.1
      val frq = Duty.kr(Drand(Seq(0.5, 0.01, 8.0), inf), Dseq(Seq(40, 30), inf))
      val fmd = LFPulse.ar(64).madd(detune, dshft) * Decay2.kr(gate, 0.1, 2) * 0.05
      val cmd = LFNoise2.kr(4)
      val sig = RLPF.ar(LFSaw.ar(frq + fmd + LFTri.kr(0.2, 270).madd(2, 0)) , cmd.madd(300, 700)).tanh
      WrapOut(FreeVerb.ar(VBAP.ar(4, sig, buffer.id), wet) * 0.2)
    }, SynthDef("KarStrong") {
      val clk = "clk".kr(2.0);    val atk = "atk".kr(0.02);  val dec = "dec".kr(0.2);  val del = "del".kr(0.05)
      val wet = "wet".kr(0.2);  val rmz = "rmz".kr(0.9);   val dmp = "dmp".kr(0.05);  val amp = "amp".kr(3.0)

      val ex = Impulse.kr(1*clk)                                                                  //trigger
      val in  = WhiteNoise.ar(Decay2.kr(ex, attack = atk, release = dec))                           //excitation
      val fltrd = RLPF.ar(in, LFNoise0.kr(0.5).madd(400, 1000), LFNoise2.ar(0.5).madd(0.1, 0.8))    //filter
      val sig = CombN.ar(fltrd, 2, LFTri.ar(1).madd(del/2, del))                                    //Comb filter delay w/ cubic interpolation

      WrapOut(FreeVerb.ar(VBAP.ar(4, sig, buffer.id, LFSaw.kr(0.1).madd(90, 90)), wet, rmz, dmp)*amp)
    })

  }

}