package ugr.fjgg.indexer

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.en.EnglishAnalyzer
import org.apache.lucene.document._
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.{Directory, FSDirectory}

import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import scala.io.Source

object indexer extends App {
  val usage = "scala indexer-0.1.jar -index INDEX_PATH -docs DOCS_PATH"
  var indexPath = "index";
  var docsPath = "";
  args.foreach(arg => {
    if (arg.equals("-index")) indexPath = args(args.indexOf(arg) + 1)
    if (arg.equals("-docs")) docsPath = args(args.indexOf(arg) + 1)
  })
  if (docsPath.isEmpty) {
    println(s"Usage: $usage")
    System.exit(1)
  }
  val docDir = Paths.get(docsPath)
  val dir: Directory = FSDirectory.open(Paths.get(indexPath))
  val analyzer: Analyzer = new EnglishAnalyzer()
  val iwc: IndexWriterConfig = new IndexWriterConfig(analyzer)
  iwc.setOpenMode(OpenMode.CREATE)

  val writer: IndexWriter = new IndexWriter(dir, iwc)


  indexDocs(writer, docDir)

  writer.close()

  def indexDocs(writer: IndexWriter, docDir: Path): Unit = {
    if (Files.isDirectory(docDir)) {
      Files.walkFileTree(docDir, new SimpleFileVisitor[Path]() {
        override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
          indexDoc(writer, file, attrs.lastModifiedTime().toMillis)
          FileVisitResult.CONTINUE
        }
      })
    } else indexDoc(writer, docDir, Files.getLastModifiedTime(docDir).toMillis)
  }

  def indexDoc(writer: IndexWriter, file: Path, lastModified: Long): Unit = {
    val source = Source.fromFile(file.toFile)
    val rawDoc = source.getLines().mkString
    val regex = "<[^>]*>".r
    val cleanText = regex.replaceAllIn(rawDoc, "")
    val doc: Document = new Document

    val pathField: Field = new StringField("path", file.toString, Field.Store.YES)
    doc.add(pathField)
    doc.add(new LongPoint("modified", lastModified))
    doc.add(new TextField("contents", cleanText, Field.Store.NO))

    //  println("indexing file " + file)
    writer.addDocument(doc)
    source.close
  }
}
