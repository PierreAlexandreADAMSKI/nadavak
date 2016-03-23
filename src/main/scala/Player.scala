import PositionUtil.Position

/**&
  * Created by Pierre-Alexandre Adamski on 21/03/2016.
  */
class Player(positionType: Int, interactionType: Int, position: Position) {

  val posType: Int = positionType
  val interactType: Int = interactionType


  def interacts: Boolean = {
    if (interactType != 0) false
    else true
  }

}
