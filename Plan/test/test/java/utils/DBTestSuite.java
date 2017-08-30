/* 
 * Licence is provided in the jar as license.yml also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/license.yml
 */
package test.java.utils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import main.java.com.djrapitops.plan.database.DatabaseCommitTest;
import main.java.com.djrapitops.plan.database.DatabaseTest;

import java.io.File;
import java.io.IOException;

/**
 * @author Fuzzlemann
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({DatabaseCommitTest.class, DatabaseTest.class})
public class DBTestSuite {
    @BeforeClass
    public static void setUp() throws IOException {
        clean();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        clean();
    }

    private static void clean() {
        File testFolder = TestInit.getTestFolder();

        if (!testFolder.exists() || !testFolder.isDirectory()) {
            return;
        }

        File[] files = testFolder.listFiles();

        if (files == null) {
            return;
        }

        for (File f : files) {
            if (!f.delete()) {
                f.deleteOnExit();
            }
        }
    }
}
