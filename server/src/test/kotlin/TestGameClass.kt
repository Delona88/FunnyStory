import funnystoryserver.model.Game
import org.junit.Before
import org.junit.Test

class TestGameClass {
    lateinit var game: Game
    var list0 = mutableListOf<String>("00", "01", "02", "03")
    var list1 = mutableListOf<String>("10", "11", "12", "13")
    var list2 = mutableListOf<String>("20", "21", "22", "23")
    var list3 = mutableListOf<String>("30", "31", "32", "33")
    var list4 = mutableListOf<String>("40", "41", "42", "43")
    var list5 = mutableListOf<String>("50", "51", "52", "53")
    var hostId = 0
    var userId1: Int = 0
    var userId2: Int = 0
    var userId3: Int = 0
    var userId4: Int = 0
    var userId5: Int = 0

    @Before
    fun setUp() {
        game = Game().apply {
            userId1 = getNewUserId()
            userId2 = getNewUserId()
            userId3 = getNewUserId()
            userId4 = getNewUserId()
            userId5 = getNewUserId()
            setSentenceByUserId(hostId, list0)
            setSentenceByUserId(userId1, list1)
            setSentenceByUserId(userId2, list2)
            setSentenceByUserId(userId3, list3)
            setSentenceByUserId(userId4, list4)
            setSentenceByUserId(userId5, list5)
        }
    }

    @Test
    fun testMixSentences() {
        println(game.getNewSentenceByUserId(hostId))
        println(game.getNewSentenceByUserId(userId1))
        println(game.getNewSentenceByUserId(userId2))
        println(game.getNewSentenceByUserId(userId3))
        println(game.getNewSentenceByUserId(userId4))
        println(game.getNewSentenceByUserId(userId5))
    }

/*    @Test
    fun testHM() {
        val hm = mutableMapOf(
            "a" to mutableListOf<Int>(1, 2, 3),
            "b" to mutableListOf<Int>(1, 2, 3),
            "c" to mutableListOf<Int>(1, 2, 3)
        )
        val hm2 = HashMap(hm)

        //hm.replace("b", mutableListOf<Int>(4, 5, 6))
        val list = hm["b"]
        list?.set(0, 4)
        list?.set(1, 5)
        list?.set(2, 6)
            //hm.put("d", mutableListOf<Int>(4, 5, 6))
        println(hm)
        println(hm2)
    }*/


}