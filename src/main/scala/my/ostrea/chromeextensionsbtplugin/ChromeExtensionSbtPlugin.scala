package my.ostrea.chromeextensionsbtplugin

import java.nio.file.{DirectoryStream, Files, Path, Paths}
import java.io.{File, IOException}

import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt.Keys._
import sbt.{AutoPlugin, Compile, IO, Logger, Setting, taskKey}

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

    val pathToTheFile = (artifactPath in ScalaJSPlugin.autoImport.fullOptJS in Compile).value.toPath
    val compilerOutputDirectory = pathToTheFile.getParent.getParent

    val extensionDirectory = createDirectoryForExtension(compilerOutputDirectory.toString)

    copyResources(log, extensionDirectory, targetDirectory = compilerOutputDirectory.getParent.toString)

    copyJsFiles(log, extensionDirectory.toString, directoryWithGeneratedJs = pathToTheFile.getParent)
  }

  private def createDirectoryForExtension(targetDirectory: String): Path = {
    val extensionDirectory = Paths.get(targetDirectory, "unpacked_extension")
    IO.createDirectory(extensionDirectory.toFile)
    extensionDirectory
  }

  private def copyResources(log: Logger, extensionDirectory: Path, targetDirectory: String): Unit = {
    val resourcesDirectory = Paths.get(targetDirectory, "src", "main", "resources")
    IO.copyDirectory(resourcesDirectory.toFile, extensionDirectory.toFile)
    log.info("Resources copied.")
  }

  private def copyJsFiles(log: Logger, extensionDirectory: String, directoryWithGeneratedJs: Path): Unit = {
    var stream: DirectoryStream[Path] = null
    var jsFiles: Traversable[(File, File)] = null
    try {
      stream = Files.newDirectoryStream(directoryWithGeneratedJs, "*.js")
      jsFiles = stream.map(file => (file.toFile,
        Paths.get(extensionDirectory, file.getFileName.toString).toFile))
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
