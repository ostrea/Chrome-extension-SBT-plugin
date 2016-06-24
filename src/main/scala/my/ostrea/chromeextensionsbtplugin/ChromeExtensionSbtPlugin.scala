package my.ostrea.chromeextensionsbtplugin

import java.nio.file.{DirectoryStream, Files, Path, Paths}
import java.io.{File, IOException}

import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt.Keys._
import sbt.{AutoPlugin, Compile, IO, Setting, taskKey}

import scala.collection.JavaConversions._

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

    (ScalaJSPlugin.autoImport.fullOptJS in Compile).value
    log.info("JS generated.")

//    createDirectoryForExtension()

    val pathToTheFile = (artifactPath in ScalaJSPlugin.autoImport.fullOptJS in Compile).value.toPath
    val targetDirectory = pathToTheFile.getParent.getParent
    val extensionDirectory = Paths.get(targetDirectory.toString, "unpacked_extension")
    IO.createDirectory(extensionDirectory.toFile)

    val resourcesDirectory = Paths.get(targetDirectory.getParent.toString, "src", "main", "resources")
    IO.copyDirectory(resourcesDirectory.toFile, extensionDirectory.toFile)
    log.info("Resources copied.")

    val directoryWithGeneratedJs = pathToTheFile.getParent
    var stream: DirectoryStream[Path] = null
    var jsFiles: Traversable[(File, File)] = null
    try {
      stream = Files.newDirectoryStream(directoryWithGeneratedJs, "*.js")
      jsFiles = stream.map(file => (file.toFile,
                                    Paths.get(extensionDirectory.toString, file.getFileName.toString).toFile))
    } catch {
      case ioException: IOException =>
        log.error(s"Can't get stream for the $directoryWithGeneratedJs.")
    } finally {
      if (stream != null) {
        stream.close()
      }
    }
    IO.copy(jsFiles)
    log.info("JS files copied.")
  }
}
