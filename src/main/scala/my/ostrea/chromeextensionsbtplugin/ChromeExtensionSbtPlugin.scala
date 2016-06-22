package my.ostrea.chromeextensionsbtplugin

import sbt.{AutoPlugin, Keys, Setting, taskKey}
import Keys._

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
    log.info("I am sbt plugin, yahuu")
  }
}
