package my.ostrea.chromeextensionsbtplugin

import sbt.{AutoPlugin, Keys, Setting, inputKey}
import Keys._

object ChromeExtensionSbtPlugin extends AutoPlugin {

  object autoImport {
    val testPluginCommand = inputKey[Unit]("Test plugin command")
  }

  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    testPluginCommandSetting
  )

  def testPluginCommandSetting: Setting[_] = testPluginCommand := {
    val log = streams.value.log
    log.info("I am sbt plugin, yahuu")
  }
}
