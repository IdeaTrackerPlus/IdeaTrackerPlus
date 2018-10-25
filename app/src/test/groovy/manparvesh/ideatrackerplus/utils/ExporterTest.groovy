package manparvesh.ideatrackerplus.utils

import manparvesh.ideatrackerplus.MainActivity
import manparvesh.ideatrackerplus.database.DatabaseHelper
import manparvesh.ideatrackerplus.models.Idea
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

class ExporterTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Shared
    def FILENAME = "file.txt"
    @Shared
    def idea = new Idea("Rule the world", "do it within 2 months", 1, MainActivity.IDEAS_TAB)

    def "Save to File"() {
        given:
        def file = temporaryFolder.newFile(FILENAME)
        def databaseHelper = Mock(DatabaseHelper) {
            1 * getAllIdeas() >> [idea]
        }
        def ideaFormatter = Mock(IdeaFormatter)
        def exporter = new Exporter(databaseHelper, ideaFormatter)
        when:
        exporter.saveFile(file.path)
        then:
        1 * ideaFormatter.serialize([idea], _)
    }
}
