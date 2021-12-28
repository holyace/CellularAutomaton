package com.michael.demo.celllife

import com.michael.demo.celllife.controller.IController
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import javafx.stage.StageStyle

class Launcher: Application() {

    override fun start(primaryStage: Stage?) {
        try {
            val loader = FXMLLoader(Launcher::class.java.getResource("/main.fxml"))
            val root = loader.load<Pane>()
            primaryStage?.apply {
                scene = Scene(root)
                isResizable = true
                title = "Life Cell"
                initStyle(StageStyle.DECORATED)
                onCloseRequest = EventHandler {
                    loader.getController<IController>()?.onExit()
                }
                show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}