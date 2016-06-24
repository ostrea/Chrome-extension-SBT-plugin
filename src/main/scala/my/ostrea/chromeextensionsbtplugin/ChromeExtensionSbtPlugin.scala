package my.ostrea.chromeextensionsbtplugin

import sbt.{AutoPlugin, Keys, Setting, taskKey, Compile}
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin

object ChromeExtensionSbtPlugin extends AutoPlugin {

  object autoImport {
    val createUnpackedExtension = taskKey[Unit]("Create unpacked extension.")
  }

  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    createUnpackedExtensionSetting
  )

  def createUnpackedExtensionSetting: Setting[_] = createUnpackedExtension := {
    val log = streams.value.log
    log.info("JS generated.")
    val result = (ScalaJSPlugin.autoImport.fullOptJS in Compile).value
  }
}
