package ugr.fjgg.searcher

import org.apache.lucene.analysis.en.EnglishAnalyzer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.FSDirectory

import java.nio.file.Paths

object searcher {
  def main(args: Array[String]): Unit = {
    val usage = "scala searcher-0.1.jar -search [SEARCH] -index [INDEX_DIR]"
    val field = "contents"
    var indexDir = "./index"
    var queryString = ""
    args.foreach(arg => {
      if (arg.equals("-index")) indexDir = args(args.indexOf(arg) + 1)
      if (arg.equals("-search")) queryString = args(args.indexOf(arg) + 1)
    })
    if (queryString.isEmpty) {
      println(s"Usage: $usage")
      System.exit(1)
    }
    val reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)))
    val searcher = new IndexSearcher(reader)
    val analyzer = new EnglishAnalyzer()
    val parser = new QueryParser(field, analyzer)
    val query = parser.parse(queryString)
    println(s"Buscando = ${query.toString(field)}")
    val search = searcher.search(query, 100)
    val results = search.scoreDocs
    println(s"results.size = ${results.size}")
    results.foreach(hit => {
      val hitDoc = searcher.doc(hit.doc)
      println(s"Doc: ${hitDoc.get("path")}")
    })
    println(s"Total hits: ${search.totalHits.value}")
    reader.close()
  }
}
