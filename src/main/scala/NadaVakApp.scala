import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.{RadioButton, SplitMenuButton}
import scalafxml.core.{NoDependencyResolver, DependenciesByType, FXMLView}

/**
  * Created by Pierre-Alexandre Adamski on 31/03/2016.
  */
object NadaVakApp extends JFXApp{
  val root = FXMLView(getClass.getResource("test.fxml"), NoDependencyResolver)

  stage = new JFXApp.PrimaryStage() {
    title = "Unit conversion"
    //scene = new Scene(root)
  }
}
