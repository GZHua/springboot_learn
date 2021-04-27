package com.ggunlics.demo.test;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelToHtmlUtil
 */
public class ExcelToHtmlUtils2 {
    /**
     * 图片缓存目录
     */
    private static final String IMG_PATH = "D:/test";
    /**
     * 边框四边
     */
    static String[] bordesr = {"border-top:", "border-right:", "border-bottom:", "border-left:"};
    /**
     * css 边框类型和大小  {@see BorderStyle}
     */
    static String[] borderStyles = {"0px", "1px solid", "3px solid", "1px dashed", "1px dotted", "5px solid",
            "double", "1px solid", "3px dashed", "1px dashed", "3px dashed", "1px dashed", "3px dashed", "1px solid"};
    /**
     * 合并单元格相关的参数
     * <p>0: 保存合并单元格的对应起始和截止单元格 (左上坐标, 右下坐标)</p>
     * <p>1: 保存被合并的那些单元格</p>
     * <p>2: 记录被隐藏的单元格个数</p>
     * <p>3: 记录合并了单元格，但是合并的首行被隐藏的情况</p>
     */
    private static Map<String, Object>[] map;

    /**
     * 程序入口方法（读取指定位置的excel，将其转换成html形式的字符串，并保存成同名的html文件在相同的目录下，默认带样式）
     *
     * @param sourcePath 文件路径
     * @return <table>...</table> 字符串
     */
    public static String excelWriteToHtml(String sourcePath) {
        File sourceFile = new File(sourcePath);
        try {
            InputStream fis = new FileInputStream(sourceFile);
            return ExcelToHtmlUtils2.readExcelToHtml(fis, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 程序入口方法（将指定路径的excel文件读取成字符串）
     *
     * @param filePath    文件的路径
     * @param isWithStyle 是否需要表格样式 包含 字体 颜色 边框 对齐方式
     * @return <table>...</table> 字符串
     */
    public static String readExcelToHtml(String filePath, boolean isWithStyle) {
        File sourceFile = new File(filePath);
        try (InputStream is = new FileInputStream(sourceFile)) {
            return ExcelToHtmlUtils2.readExcelToHtml(is, isWithStyle);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 程序入口方法（将指定路径的excel文件读取成字符串）
     *
     * @param is          excel转换成的输入流
     * @param isWithStyle 是否需要表格样式 包含 字体 颜色 边框 对齐方式
     * @return <table>...</table> 字符串
     */
    public static String readExcelToHtml(InputStream is, boolean isWithStyle) {
        ZipSecureFile.setMinInflateRatio(-1.0d);
        String htmlExcel = null;
        try {
            Workbook wb = WorkbookFactory.create(is);
            htmlExcel = readWorkbook(wb, isWithStyle);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return htmlExcel;
    }

    /**
     * 根据excel的版本分配不同的读取方法进行处理
     *
     * @param wb          excle
     * @param isWithStyle 带样式
     * @return html
     */
    private static String readWorkbook(Workbook wb, boolean isWithStyle) {
        String htmlExcel = "";
        if (wb instanceof XSSFWorkbook) {
            XSSFWorkbook xWb = (XSSFWorkbook) wb;
            htmlExcel = getExcelInfo(xWb, isWithStyle);
        } else if (wb instanceof HSSFWorkbook) {
            HSSFWorkbook hWb = (HSSFWorkbook) wb;
            htmlExcel = getExcelInfo(hWb, isWithStyle);
        }
        return htmlExcel;
    }

    /**
     * 读取excel成string
     *
     * @param wb          excel
     * @param isWithStyle 带样式
     * @return html
     */
    public static String getExcelInfo(Workbook wb, boolean isWithStyle) {

        StringBuilder sb = new StringBuilder();
        Sheet sheet = wb.getSheetAt(0);//获取第一个Sheet的内容
        // map等待存储excel图片
        Map<String, PictureData> sheetIndexPicMap = getSheetPictrues(0, sheet, wb);
        //临时保存位置，正式环境根据部署环境存放其他位置
        try {
            if (sheetIndexPicMap != null) {
                printImg(sheetIndexPicMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //读取excel拼装html
        int lastRowNum = sheet.getLastRowNum();
        map = getRowSpanColSpanMap(sheet);
        sb.append("<table style='border-collapse:collapse;width:100%;'>");
        Row row = null;        //兼容
        Cell cell = null;    //兼容

        // 以行处理
        for (int rowNum = sheet.getFirstRowNum(); rowNum <= lastRowNum; rowNum++) {
            if (rowNum > 1000) {
                break;
            }
            row = sheet.getRow(rowNum);

            int lastColNum = ExcelToHtmlUtils2.getColsOfTable(sheet)[0];
            int rowHeight = ExcelToHtmlUtils2.getColsOfTable(sheet)[1];

            if (null != row) {
                lastColNum = row.getLastCellNum();
                rowHeight = row.getHeight();
            }

            // 空行处理
            if (null == row) {
                sb.append("<tr><td >  </td></tr>");
                continue;
            } else if (row.getZeroHeight()) {
                continue;
            } else if (0 == rowHeight) {
                continue;     //针对jxl的隐藏行（此类隐藏行只是把高度设置为0，单getZeroHeight无法识别）
            }

            // 有值单元格
            sb.append("<tr>");
            for (int colNum = 0; colNum < lastColNum; colNum++) {
                if (sheet.isColumnHidden(colNum)) {
                    continue;
                }
                String imageRowNum = "0_" + rowNum + "_" + colNum;
                String imageHtml = "";
                cell = row.getCell(colNum);
                //特殊情况 空白的单元格会返回null
                // 判断该单元格是否包含图片，为空时也可能包含图片
                if ((sheetIndexPicMap == null || !sheetIndexPicMap.containsKey(imageRowNum)) && cell == null) {
                    sb.append("<td>  </td>");
                    continue;
                }
                // 图片填充 地址形式
                if (sheetIndexPicMap != null && sheetIndexPicMap.containsKey(imageRowNum)) {
                    //待修改路径
                    String imagePath = IMG_PATH + imageRowNum + ".jpeg";

                    imageHtml = "<img src='" + imagePath + "' style='height:" + rowHeight / 20 + "px;'>";
                }

                // 单元格值
                String stringValue = getCellValue(cell);

                // 合并单元格处理
                if (map[0].containsKey(rowNum + "," + colNum)) {
                    String pointString = (String) map[0].get(rowNum + "," + colNum);
                    int bottomeRow = Integer.parseInt(pointString.split(",")[0]);
                    int bottomeCol = Integer.parseInt(pointString.split(",")[1]);
                    int rowSpan = bottomeRow - rowNum + 1;
                    int colSpan = bottomeCol - colNum + 1;
                    if (map[2].containsKey(rowNum + "," + colNum)) {
                        rowSpan = rowSpan - (Integer) map[2].get(rowNum + "," + colNum);
                    }
                    sb.append("<td rowspan= '").append(rowSpan).append("' colspan= '").append(colSpan).append("' ");
                    if (map.length > 3 && map[3].containsKey(rowNum + "," + colNum)) {
                        //此类数据首行被隐藏，value为空，需使用其他方式获取值
                        stringValue = getMergedRegionValue(sheet, rowNum, colNum);
                    }
                } else if (map[1].containsKey(rowNum + "," + colNum)) {
                    map[1].remove(rowNum + "," + colNum);
                    continue;
                } else {
                    sb.append("<td ");
                }

                //判断是否需要样式
                if (isWithStyle) {
                    dealExcelStyle(wb, sheet, cell, sb);//处理单元格样式
                }

                sb.append(">");

                // 单元格填入图形
                if (sheetIndexPicMap != null && sheetIndexPicMap.containsKey(imageRowNum)) {
                    sb.append(imageHtml);
                }

                // 处理空值格
                if (stringValue == null || "".equals(stringValue.trim())) {
                    sb.append("   ");
                } else {
                    // 将ascii码为160的空格转换为html下的空格（ ）
                    sb.append(stringValue.replace(String.valueOf((char) 160), " "));
                }

                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    /**
     * 分析excel表格，记录合并单元格相关的参数，用于之后html页面元素的合并操作
     *
     * @param sheet sheet
     * @return map[]
     */
    private static Map<String, Object>[] getRowSpanColSpanMap(Sheet sheet) {
        //保存合并单元格的对应起始和截止单元格
        Map<String, String> map0 = new HashMap<>();
        //保存被合并的那些单元格
        Map<String, String> map1 = new HashMap<>();
        //记录被隐藏的单元格个数
        Map<String, Integer> map2 = new HashMap<>();
        //记录合并了单元格，但是合并的首行被隐藏的情况
        Map<String, String> map3 = new HashMap<>();

        int mergedNum = sheet.getNumMergedRegions();
        // 合并单元格地址范围
        CellRangeAddress range;
        Row row = null;
        for (int i = 0; i < mergedNum; i++) {
            range = sheet.getMergedRegion(i);
            // 合并单元格地址首部行列号
            int topRow = range.getFirstRow();
            int topCol = range.getFirstColumn();
            // 合并单元格地址尾部行列号
            int bottomRow = range.getLastRow();
            int bottomCol = range.getLastColumn();
            /**
             * 此类数据为合并了单元格的数据
             * 1.处理隐藏（只处理行隐藏，列隐藏poi已经处理）
             */
            if (topRow != bottomRow) {
                int zeroRoleNum = 0;
                int tempRow = topRow;
                for (int j = topRow; j <= bottomRow; j++) {
                    row = sheet.getRow(j);
                    if (row.getZeroHeight() || row.getHeight() == 0) {
                        if (j == tempRow) {
                            // 首行就进行隐藏，将rowTop向后移
                            // 由于top下移，后面计算rowSpan时会扣除移走的列，所以不必增加zeroRoleNum;
                            tempRow++;
                            continue;
                        }
                        zeroRoleNum++;
                    }
                }
                if (tempRow != topRow) {
                    map3.put(tempRow + "," + topCol, topRow + "," + topCol);
                    topRow = tempRow;
                }
                if (zeroRoleNum != 0) {
                    map2.put(topRow + "," + topCol, zeroRoleNum);
                }
            }
            map0.put(topRow + "," + topCol, bottomRow + "," + bottomCol);
            int tempRow = topRow;
            while (tempRow <= bottomRow) {
                int tempCol = topCol;
                while (tempCol <= bottomCol) {
                    map1.put(tempRow + "," + tempCol, topRow + "," + topCol);
                    tempCol++;
                }
                tempRow++;
            }
            map1.remove(topRow + "," + topCol);
        }
        return new Map[]{map0, map1, map2, map3};
    }

    /**
     * 获取合并单元格的值
     *
     * @param sheet  sheet
     * @param row    row
     * @param column col
     * @return value
     */
    public static String getMergedRegionValue(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if (row >= firstRow && row <= lastRow && column >= firstColumn && column <= lastColumn) {
                Row fRow = sheet.getRow(firstRow);
                Cell fCell = fRow.getCell(firstColumn);
                return getCellValue(fCell);
            }
        }
        return null;
    }

    /**
     * 获取表格单元格Cell内容
     *
     * @param cell cell
     * @return String
     */
    private static String getCellValue(Cell cell) {
        String result;
        switch (cell.getCellType()) {
            case NUMERIC:// 数字类型
                if (DateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                    SimpleDateFormat sdf;
                    if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                        sdf = new SimpleDateFormat("HH:mm");
                    } else {// 日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                    }
                    Date date = cell.getDateCellValue();
                    result = sdf.format(date);
                } else if (cell.getCellStyle().getDataFormat() == 58) {
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    double value = cell.getNumericCellValue();
                    Date date = DateUtil
                            .getJavaDate(value);
                    result = sdf.format(date);
                } else {
                    double value = cell.getNumericCellValue();
                    CellStyle style = cell.getCellStyle();
                    DecimalFormat format = new DecimalFormat();
                    String temp = style.getDataFormatString();
                    // 单元格设置成常规
                    if (temp.equals("General")) {
                        format.applyPattern("#");
                    }
                    result = format.format(value);
                }
                break;
            case STRING:// String类型
                result = cell.getRichStringCellValue().toString();
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    /**
     * 处理表格样式
     *
     * @param wb    excel
     * @param sheet sheet
     * @param cell  cell
     * @param sb    html
     */
    private static void dealExcelStyle(Workbook wb, Sheet sheet, Cell cell, StringBuilder sb) {
        CellStyle cellStyle = cell.getCellStyle();
        if (cellStyle != null) {
            // 对齐方式
            HorizontalAlignment alignment = cellStyle.getAlignment();
            sb.append("align='" + convertAlignToHtml(alignment) + "' ");//单元格内容的水平对齐方式
            VerticalAlignment verticalAlignment = cellStyle.getVerticalAlignment();
            sb.append("valign='" + convertVerticalAlignToHtml(verticalAlignment) + "' ");//单元格中内容的垂直排列方式

            if (wb instanceof XSSFWorkbook) {
                // 字体
                XSSFFont xf = ((XSSFCellStyle) cellStyle).getFont();
                String boldWeight = xf.getBold() ? "bolder" : "normal";
                sb.append("style='");
                sb.append("font-weight:").append(boldWeight).append(";"); // 字体加粗
                sb.append("font-size: ").append(xf.getFontHeight() / 2).append("%;"); // 字体大小

                int topRow = cell.getRowIndex();
                int topColumn = cell.getColumnIndex();
                //该单元格为合并单元格，宽度需要获取所有单元格宽度后合并
                if (map[0].containsKey(topRow + "," + topColumn)) {
                    String value = (String) map[0].get(topRow + "," + topColumn);
                    String[] ary = value.split(",");
                    int bottomColumn = Integer.parseInt(ary[1]);
                    //合并列，需要计算相应宽度
                    if (topColumn != bottomColumn) {
                        int columnWidth = 0;
                        for (int i = topColumn; i <= bottomColumn; i++) {
                            columnWidth += sheet.getColumnWidth(i);
                        }
                        sb.append("width:" + columnWidth / 256 * xf.getFontHeight() / 20 + "pt;");
                    } else {
                        int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
                        sb.append("width:" + columnWidth / 256 * xf.getFontHeight() / 20 + "pt;");
                    }
                } else {
                    int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
                    sb.append("width:" + columnWidth / 256 * xf.getFontHeight() / 20 + "pt;");
                }

                XSSFColor xc = xf.getXSSFColor();
                if (xc != null && !"".equals(xc.toString())) {
                    sb.append("color:#" + xc.getARGBHex().substring(2) + ";"); // 字体颜色
                }

                XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
                if (bgColor != null && !"".equals(bgColor.toString())) {
                    sb.append("background-color:#" + bgColor.getARGBHex().substring(2) + ";"); // 背景颜色
                }

                // 边框处理
                sb.append(getBorderStyle(0, cellStyle.getBorderTop().getCode(), ((XSSFCellStyle) cellStyle).getTopBorderXSSFColor()));
                sb.append(getBorderStyle(1, cellStyle.getBorderRight().getCode(), ((XSSFCellStyle) cellStyle).getRightBorderXSSFColor()));
                sb.append(getBorderStyle(2, cellStyle.getBorderBottom().getCode(), ((XSSFCellStyle) cellStyle).getBottomBorderXSSFColor()));
                sb.append(getBorderStyle(3, cellStyle.getBorderLeft().getCode(), ((XSSFCellStyle) cellStyle).getLeftBorderXSSFColor()));
            } else if (wb instanceof HSSFWorkbook) {
                HSSFFont hf = ((HSSFCellStyle) cellStyle).getFont(wb);
                String boldWeight = hf.getBold() ? "bolder" : "normal";
                short fontColor = hf.getColor();
                sb.append("style='");

                HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette(); // 类HSSFPalette用于求的颜色的国际标准形式
                HSSFColor hc = palette.getColor(fontColor);
                sb.append("font-weight:" + boldWeight + ";"); // 字体加粗
                sb.append("font-size: " + hf.getFontHeight() / 2 + "%;"); // 字体大小
                String fontColorStr = convertToStardColor(hc);
                if (fontColorStr != null && !"".equals(fontColorStr.trim())) {
                    sb.append("color:" + fontColorStr + ";"); // 字体颜色
                }

                int topRow = cell.getRowIndex();
                int topColumn = cell.getColumnIndex();
                if (map[0].containsKey(topRow + "," + topColumn)) {//该单元格为合并单元格，宽度需要获取所有单元格宽度后合并
                    String value = (String) map[0].get(topRow + "," + topColumn);
                    String[] ary = value.split(",");
                    int bottomColumn = Integer.parseInt(ary[1]);
                    if (topColumn != bottomColumn) {//合并列，需要计算相应宽度
                        int columnWidth = 0;
                        for (int i = topColumn; i <= bottomColumn; i++) {
                            columnWidth += sheet.getColumnWidth(i);
                        }
                        sb.append("width:" + columnWidth / 256 * hf.getFontHeight() / 20 + "pt;");
                    } else {
                        int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
                        sb.append("width:" + columnWidth / 256 * hf.getFontHeight() / 20 + "pt;");
                    }
                } else {
                    int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
                    sb.append("width:" + columnWidth / 256 * hf.getFontHeight() / 20 + "pt;");
                }

                short bgColor = cellStyle.getFillForegroundColor();
                hc = palette.getColor(bgColor);
                String bgColorStr = convertToStardColor(hc);
                if (bgColorStr != null && !"".equals(bgColorStr.trim())) {
                    sb.append("background-color:" + bgColorStr + ";");        // 背景颜色
                }

                // 边框处理
                sb.append(getBorderStyle(palette, 0, cellStyle.getBorderTop().getCode(), cellStyle.getTopBorderColor()));
                sb.append(getBorderStyle(palette, 1, cellStyle.getBorderRight().getCode(), cellStyle.getRightBorderColor()));
                sb.append(getBorderStyle(palette, 2, cellStyle.getBorderBottom().getCode(), cellStyle.getBottomBorderColor()));
                sb.append(getBorderStyle(palette, 3, cellStyle.getBorderLeft().getCode(), cellStyle.getLeftBorderColor()));
            }
            sb.append("' ");
        }
    }

    /**
     * 单元格内容的水平对齐方式
     *
     * @param alignment 水平对齐方式
     * @return css
     */
    private static String convertAlignToHtml(HorizontalAlignment alignment) {
        String align = "left";
        switch (alignment) {
            case LEFT:
                align = "left";
                break;
            case CENTER:
                align = "center";
                break;
            case RIGHT:
                align = "right";
                break;
            default:
                break;
        }
        return align;
    }

    /**
     * 单元格中内容的垂直排列方式
     *
     * @param verticalAlignment 垂直对齐方式
     * @return css
     */
    private static String convertVerticalAlignToHtml(VerticalAlignment verticalAlignment) {
        String valign = "middle";
        switch (verticalAlignment) {
            case BOTTOM:
                valign = "bottom";
                break;
            case CENTER:
                valign = "center";
                break;
            case TOP:
                valign = "top";
                break;
            default:
                break;
        }
        return valign;
    }

    private static String convertToStardColor(HSSFColor hc) {
        StringBuilder sb = new StringBuilder();
        if (hc != null) {
            if (HSSFColor.HSSFColorPredefined.AUTOMATIC.getIndex() == hc.getIndex()) {
                return null;
            }
            sb.append("#");
            for (int i = 0; i < hc.getTriplet().length; i++) {
                sb.append(fillWithZero(Integer.toHexString(hc.getTriplet()[i])));
            }
        }
        return sb.toString();
    }

    private static String fillWithZero(String str) {
        if (str != null && str.length() < 2) {
            return "0" + str;
        }
        return str;
    }

    /**
     * xlx 边框转换
     *
     * @param palette 调色板
     * @param b       边框边
     * @param s       边框类型
     * @param t       边框颜色
     * @return css
     */
    private static String getBorderStyle(HSSFPalette palette, int b, short s, short t) {
        if (s == 0) {
            return bordesr[b] + borderStyles[s] + ";";
        }
        String borderColorStr = convertToStardColor(palette.getColor(t));
        borderColorStr = borderColorStr == null || borderColorStr.length() < 1 ? "#000000" : borderColorStr;
        return bordesr[b] + borderStyles[s] + " " + borderColorStr + ";";
    }

    /**
     * xlsx 边框转换
     *
     * @param b  边框
     * @param s  样式
     * @param xc 颜色
     * @return css
     */
    private static String getBorderStyle(int b, short s, XSSFColor xc) {
        if (s == 0) {
            return bordesr[b] + borderStyles[s] + ";";
        }
        if (xc != null) {
            String borderColorStr = xc.getARGBHex();
            borderColorStr = borderColorStr == null || borderColorStr.length() < 1 ? "#000000" : borderColorStr.substring(2);
            return bordesr[b] + borderStyles[s] + " " + borderColorStr + ";";
        }
        return bordesr[b] + borderStyles[s] + " #000000;";
    }

    @SuppressWarnings("unused")
    private static void writeFile(String content, String path) {
        File file = new File(path);
        try (OutputStream os = new FileOutputStream(file);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8.name()))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取Excel图片公共方法
     *
     * @param sheetNum 当前sheet编号
     * @param sheet    当前sheet对象
     * @param workbook 工作簿对象
     * @return Map key:图片单元格索引（0_1_1）String，value:图片流PictureData
     */
    public static Map<String, PictureData> getSheetPictrues(int sheetNum, Sheet sheet, Workbook workbook) {
        if (workbook instanceof HSSFWorkbook) {
            return getSheetPictures03(sheetNum, (HSSFSheet) sheet, (HSSFWorkbook) workbook);
        } else if (workbook instanceof XSSFWorkbook) {
            return getSheetPictures07(sheetNum, (XSSFSheet) sheet, (XSSFWorkbook) workbook);
        } else {
            return null;
        }
    }

    /**
     * 获取Excel2003图片
     *
     * @param sheetNum 当前sheet编号
     * @param sheet    当前sheet对象
     * @param workbook 工作簿对象
     * @return Map key:图片单元格索引（0_1_1）String，value:图片流PictureData
     */
    private static Map<String, PictureData> getSheetPictures03(int sheetNum,
                                                               HSSFSheet sheet, HSSFWorkbook workbook) {

        Map<String, PictureData> sheetIndexPicMap = new HashMap<>();
        List<HSSFPictureData> pictures = workbook.getAllPictures();
        if (pictures.isEmpty()) {
            for (HSSFShape shape : sheet.getDrawingPatriarch().getChildren()) {
                HSSFClientAnchor anchor = (HSSFClientAnchor) shape.getAnchor();
                shape.getLineWidth();
                if (shape instanceof HSSFPicture) {
                    HSSFPicture pic = (HSSFPicture) shape;
                    int pictureIndex = pic.getPictureIndex() - 1;
                    HSSFPictureData picData = pictures.get(pictureIndex);
                    String picIndex = sheetNum + "_" + anchor.getRow1() + "_" + anchor.getCol1();
                    sheetIndexPicMap.put(picIndex, picData);
                }
            }
            return sheetIndexPicMap;
        } else {
            return null;
        }
    }

    /**
     * 获取Excel2007图片
     *
     * @param sheetNum 当前sheet编号
     * @param sheet    当前sheet对象
     * @param workbook 工作簿对象
     * @return Map key:图片单元格索引（0_1_1）String，value:图片流PictureData
     */
    private static Map<String, PictureData> getSheetPictures07(int sheetNum,
                                                               XSSFSheet sheet, XSSFWorkbook workbook) {
        Map<String, PictureData> sheetIndexPicMap = new HashMap<>();

        for (POIXMLDocumentPart dr : sheet.getRelations()) {
            if (dr instanceof XSSFDrawing) {
                XSSFDrawing drawing = (XSSFDrawing) dr;
                List<XSSFShape> shapes = drawing.getShapes();
                for (XSSFShape shape : shapes) {
                    XSSFPicture pic = (XSSFPicture) shape;
                    XSSFClientAnchor anchor = pic.getPreferredSize();
                    CTMarker ctMarker = anchor.getFrom();
                    String picIndex = sheetNum + "_"
                            + ctMarker.getRow() + "_" + ctMarker.getCol();
                    sheetIndexPicMap.put(picIndex, pic.getPictureData());
                }
            }
        }

        return sheetIndexPicMap;
    }

    public static void printImg(List<Map<String, PictureData>> sheetList) throws IOException {
        for (Map<String, PictureData> map : sheetList) {
            printImg(map);
        }
    }

    public static void printImg(Map<String, PictureData> map) throws IOException {
        String[] key = map.keySet().toArray(new String[0]);
        for (int i = 0; i < map.size(); i++) {
            // 获取图片流
            PictureData pic = map.get(key[i]);
            // 获取图片索引
            String picName = key[i];
            // 获取图片格式
            String ext = pic.suggestFileExtension();

            byte[] data = pic.getData();

            try (FileOutputStream out = new FileOutputStream(IMG_PATH + picName + "." + ext)) {
                out.write(data);
                out.flush();
            }
        }
    }

    private static int[] getColsOfTable(Sheet sheet) {

        int[] data = {0, 0};
        for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++) {
            if (null != sheet.getRow(i)) {
                data[0] = sheet.getRow(i).getLastCellNum();
                data[1] = sheet.getRow(i).getHeight();
            }
        }
        return data;
    }

    public static void main(String[] args) throws IOException {
        String out = ExcelToHtmlUtils2.readExcelToHtml("D:\\sxDown\\check\\国网福建电力福州中心库（福清）20210301物资盘点汇总表.xlsx", true);

        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setCharset(StandardCharsets.UTF_8.displayName());

        // 中文  先加先应用
        FontProvider fontProvider = new FontProvider();
        // 宋体 resources 需要有这个ttf文件
        fontProvider.addFont("font/SimSun.ttf");
        PdfFont font1 = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", false);
        fontProvider.addFont(font1.getFontProgram(), "UniGB-UCS2-H");
        fontProvider.addStandardPdfFonts();
        fontProvider.addSystemFonts();

        converterProperties.setFontProvider(fontProvider);


        FileOutputStream outputStream = new FileOutputStream(new File("D:\\sxDown\\check\\物资盘点汇总表B.pdf"));
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
        pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
        HtmlConverter.convertToPdf(out, pdfDocument, converterProperties);
    }
}
