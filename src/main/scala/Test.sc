import de.sciss.osc

val m = osc.Message("/test", "2.2", "2.2", "2.2")
val c = osc.PacketCodec().doublesAsFloats().booleansAsInts().build
c.encodedMessageSize(m) //size : 28