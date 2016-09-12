package ethanp.xml2json

/**
  * 9/11/16 12:55 AM
  */
object Main extends App {
    val tokens: Seq[XMLToken] = Tokenizer tokenize Test.exampleXML
    val tokenString = tokens mkString "\n"
    assert(tokenString equals Test.expectedTokenString, "tokenization")
    val parsed: ASTNode = Parser parse tokens
    val json: JsonObj = JsonGenerator generateFrom parsed
    val jsonString: String = JsonPrinter print json
    println(json)
}