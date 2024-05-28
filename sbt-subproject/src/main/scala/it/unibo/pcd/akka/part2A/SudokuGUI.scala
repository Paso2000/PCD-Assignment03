package it.unibo.pcd.akka.part2A

import scala.swing._
import scala.swing.event._

object SudokuGUI extends SimpleSwingApplication {
  def top: MainFrame = new MainFrame {
    title = "Simple GUI"

    // Creiamo una Label e un Button
    val label = new Label {
      text = "Premi il pulsante!"
    }

    val button = new Button {
      text = "Premi me"
    }

    // Aggiungiamo un'azione al Button
    listenTo(button)
    reactions += {
      case ButtonClicked(_) =>
        label.text = "Hai premuto il pulsante!"
    }

    // Layout del contenitore principale
    contents = new BoxPanel(Orientation.Vertical) {
      contents += label
      contents += button
      border = Swing.EmptyBorder(30, 30, 10, 30)
    }
  }
}

