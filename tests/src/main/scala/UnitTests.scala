package foo.zomdroid.tests

import junit.framework.Assert._
import _root_.android.test.AndroidTestCase

class UnitTests extends AndroidTestCase {
  def testPackageIsCorrect {
    assertEquals("foo.zomdroid", getContext.getPackageName)
  }
}