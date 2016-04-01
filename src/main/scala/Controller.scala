import scalafx.scene.control.{MenuItem, RadioButton, SplitMenuButton}
import scalafxml.core.macros.sfxml

/**
  * Created by Pierre-Alexandre Adamski on 31/03/2016.
  */

@sfxml
class Controller(private var outputAudioSelector: SplitMenuButton,
                 private var something: SplitMenuButton,
                 private var serverStatus: RadioButton) {

  private def initialize(): Unit ={

    var item1 = new MenuItem()
  }

  def onServerOnButtonAction(): Unit ={
    serverStatus.selected_=(true)
  }

  def onServerOffButtonAction(): Unit ={
    serverStatus.selected_=(false)
  }

}
