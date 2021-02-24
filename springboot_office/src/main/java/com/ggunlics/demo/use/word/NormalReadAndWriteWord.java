package com.ggunlics.demo.use.word;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 普通读写word文档
 *
 * @author ggunlics
 * @date 2020/11/16 0:38
 **/
@Slf4j
public class NormalReadAndWriteWord {
    public static String FILE_PATH = "D:\\\\office_demo\\\\word\\\\复杂.doc";

    public static void main(String[] args) {
//        simpleTextRead();
        specificTextRead();
    }

    //region doc

    //region read doc

    /**
     * 读取 - 基本文本
     */
    public static void simpleTextRead() {
        try (FileInputStream file = new FileInputStream(new File(FILE_PATH))) {
            WordExtractor wordExtractor = new WordExtractor(file);
            // 标注
            String[] commentsText = wordExtractor.getCommentsText();
            // 较详细的文本
            String[] paragraphText = wordExtractor.getParagraphText();
            // 尾注
            String[] endnoteText = wordExtractor.getEndnoteText();
            // 脚注
            String[] footnoteText = wordExtractor.getFootnoteText();
            // 文本框
            String[] mainTextboxText = wordExtractor.getMainTextboxText();
            // 含其他内容的文本
            String textFromPieces = wordExtractor.getTextFromPieces();
            // 纯文本
            String text = wordExtractor.getText();
            DocumentSummaryInformation docSummaryInformation = wordExtractor.getDocSummaryInformation();
            log.info("文件版本: {}", docSummaryInformation.getDocumentVersion());
            log.info("字节数: {}", docSummaryInformation.getByteCount());
            log.info("版本号: {}", docSummaryInformation.getApplicationVersion());
            log.info("字数: {}", docSummaryInformation.getCharCountWithSpaces());
            log.info("纯文本内容: {}", text);
            log.info("paragraphText: {}", Arrays.toString(paragraphText));
            log.info("标注: {}", Arrays.toString(commentsText));
            log.info("尾注: {}", Arrays.toString(endnoteText));
            log.info("脚注: {}", Arrays.toString(footnoteText));
            log.info("文本框: {}", Arrays.toString(mainTextboxText));
            log.info("含其他内容的文本: {}", textFromPieces);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取 - 特定文本
     */
    public static void specificTextRead() {
        try (FileInputStream file = new FileInputStream(new File(FILE_PATH))) {
            HWPFDocument document = new HWPFDocument(file);
            log.info("");

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    //endregion

    //region write doc

    public static void writeTitle(HWPFDocument doc) {

    }

    public static void writeText(HWPFDocument doc) {}

    public static void writeFormation(HWPFDocument doc) {}

    public static void writeList(HWPFDocument doc) {}

    public static void writeOrderList(HWPFDocument doc) {}

    public static void writeOrderNestList(HWPFDocument doc) {}

    public static void writeImage(HWPFDocument doc) {}

    public static void writeShape(HWPFDocument doc) {}

    public static void writeTextBox(HWPFDocument doc) {}

    public static void writeTable(HWPFDocument doc) {}

    public static void writeNestTable(HWPFDocument doc) {}

    public static void writeCatalog(HWPFDocument doc) {}

    public static void writeEndNote(HWPFDocument doc) {}

    public static void writeHeader(HWPFDocument doc) {}

    public static void writeFootNote(HWPFDocument doc) {}

    public static void writeNestFile(HWPFDocument doc) {}

    //endregion

    //endregion

    //region docx

    //region read docx


    //endregion

    //region write docx


    //endregion

    //endregion

}
