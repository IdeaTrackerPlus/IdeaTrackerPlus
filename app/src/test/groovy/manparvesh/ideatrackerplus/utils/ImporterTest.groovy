package manparvesh.ideatrackerplus.utils

import manparvesh.ideatrackerplus.MainActivity
import manparvesh.ideatrackerplus.database.DatabaseHelper
import manparvesh.ideatrackerplus.models.Idea
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

class ImporterTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Shared
    def FILENAME = "file.txt"
    @Shared
    def idea = new Idea("Rule the world", "do it within 2 months", 1, MainActivity.IDEAS_TAB)

    def "Read file"() {
        given:
        def file = temporaryFolder.newFile(FILENAME)
        def databaseHelper = Mock(DatabaseHelper)
        def ideaFormatter = Mock(IdeaFormatter) {
            1 * deserialize(_) >> [idea]
        }
        def importer = new Importer(databaseHelper, ideaFormatter)
        when:
        importer.readFile(file.path)
        then:
        1 * databaseHelper.newEntries([idea])
    }
}
