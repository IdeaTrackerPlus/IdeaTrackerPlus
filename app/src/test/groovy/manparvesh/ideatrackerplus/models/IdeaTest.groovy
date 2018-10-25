package manparvesh.ideatrackerplus.models

import manparvesh.ideatrackerplus.MainActivity
import spock.lang.Specification
import spock.lang.Unroll

class IdeaTest extends Specification {

    @Unroll
    def "Convert tab '#tab' to state '#state'"() {
        expect:
        Idea.tabToState(tab) == state
        where:
        tab                    || state
        MainActivity.DONE_TAB  || Idea.DONE
        MainActivity.IDEAS_TAB || Idea.IDEAS
        MainActivity.LATER_TAB || Idea.LATER
        15                     || Idea.IDEAS
    }

    @Unroll
    def "Convert state '#state' to tab '#tab'"() {
        expect:
        Idea.stateToTab(state) == tab
        where:
        state           || tab
        Idea.DONE       || MainActivity.DONE_TAB
        Idea.IDEAS      || MainActivity.IDEAS_TAB
        Idea.LATER      || MainActivity.LATER_TAB
        "unknown state" || MainActivity.IDEAS_TAB
    }
}
