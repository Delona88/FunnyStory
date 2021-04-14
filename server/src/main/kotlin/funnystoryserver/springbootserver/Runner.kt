package funnystoryserver.springbootserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class Runner

fun main(args: Array<String>) {
    runApplication<Runner>(*args)
}


