package manparvesh.ideatrackerplus.utils

import manparvesh.ideatrackerplus.MainActivity
import manparvesh.ideatrackerplus.models.Idea
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.text.ParseException

class YamlIdeaFormatterTest extends Specification {

    @Shared
    def NEW_YAML_DOC = "---\n"
    @Shared
    def IDEAS_HEADER = "ideas:\n"
    @Shared
    def IDEA_TITLE_1 = "rule the world"
    @Shared
    def IDEA_NOTE_1 = "do it fast"
    @Shared
    def IDEA_PRIORITY_1 = 1
    @Shared
    def IDEA_YAML_1 =
            "- title: " + IDEA_TITLE_1 + "\n" +
                    "  note: " + IDEA_NOTE_1 + "\n" +
                    "  priority: " + IDEA_PRIORITY_1 + "\n" +
                    "  state: Ideas\n"
    @Shared
    def IDEA_TITLE_2 = "create an idea app"
    @Shared
    def IDEA_NOTE_2 = "better than Idea Tracker Plus"
    @Shared
    def IDEA_PRIORITY_2 = 2
    @Shared
    def IDEA_YAML_2 =
            "- title: " + IDEA_TITLE_2 + "\n" +
                    "  note: " + IDEA_NOTE_2 + "\n" +
                    "  priority: " + IDEA_PRIORITY_2 + "\n" +
                    "  state: Ideas\n"

    YamlIdeaFormatter formatter

    def setup() {
        formatter = new YamlIdeaFormatter()
    }

    @Unroll
    def "deserialization of a yaml to a list of Ideas"() {
        given:
        def reader = new StringReader(yaml)
        expect:
        def deserialize = formatter.deserialize(reader)
        deserialize == expectedIdeas
        where:
        yaml                                                    || expectedIdeas
        NEW_YAML_DOC + IDEAS_HEADER + IDEA_YAML_1               || [firstIdea()]
        NEW_YAML_DOC + IDEAS_HEADER + IDEA_YAML_1 + IDEA_YAML_2 || [firstIdea(), secondIdea()]
    }

    def "deserialization of an invalid yaml causes an exception"() {
        given:
        def reader = new StringReader("- illegal yaml")
        when:
        formatter.deserialize(reader)
        then:
        thrown ParseException
    }

    @Unroll
    def "serialize a list of Ideas to yaml"() {
        given:
        StringWriter writer = new StringWriter()
        expect:
        formatter.serialize(ideas, writer)
        writer.toString() == expectedYaml
        where:
        ideas                       || expectedYaml
        [firstIdea()]               || IDEAS_HEADER + IDEA_YAML_1
        [firstIdea(), secondIdea()] || IDEAS_HEADER + IDEA_YAML_1 + IDEA_YAML_2
    }

    @Unroll
    def "end to end serialization and deserialization"() {
        given:
        StringWriter writer = new StringWriter()
        expect:
        formatter.serialize(ideas, writer)
        def reader = new StringReader(writer.toString())
        formatter.deserialize(reader) == ideas
        where:
        ideas << [[firstIdea()], [firstIdea(), secondIdea()]]
    }

    ///////////////////
    // Helper methods
    ///////////////////

    @Newify(Idea)
    def firstIdea() {
        Idea(IDEA_TITLE_1, IDEA_NOTE_1, IDEA_PRIORITY_1, MainActivity.IDEAS_TAB)
    }

    @Newify(Idea)
    def secondIdea() {
        Idea(IDEA_TITLE_2, IDEA_NOTE_2, IDEA_PRIORITY_2, MainActivity.IDEAS_TAB)
    }
}
