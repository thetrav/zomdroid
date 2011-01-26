import sbt._

class Zomdroid(info: ProjectInfo) extends AndroidProject(info) {
  override def androidPlatformName = "android-9"
}
