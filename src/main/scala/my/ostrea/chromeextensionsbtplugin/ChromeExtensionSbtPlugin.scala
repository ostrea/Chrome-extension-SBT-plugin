package my.ostrea.chromeextensionsbtplugin

import java.nio.file.Paths

import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt.Keys._
import sbt.{AutoPlugin, Compile, IO, Setting, taskKey}

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
    (ScalaJSPlugin.autoImport.fullOptJS in Compile).value

    createDirectoryForExtension
  }

  private def createDirectoryForExtension: Unit = {
    val pathToTheFile = (artifactPath in ScalaJSPlugin.autoImport.fullOptJS in Compile).value.toPath
    val targetDirectory = pathToTheFile.getParent.getParent
    val extensionDirectory = Paths.get(targetDirectory.toString, "unpacked_extension")
    IO.createDirectory(extensionDirectory.toFile)
  }
}
