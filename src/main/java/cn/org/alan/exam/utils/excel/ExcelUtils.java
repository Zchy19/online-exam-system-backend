package cn.org.alan.exam.utils.excel;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;


@SuppressWarnings("unused")
public class ExcelUtils {

    private static final String XLSX = ".xlsx";
    private static final String XLS = ".xls";
    public static final String ROW_MERGE = "row_merge";
    public static final String COLUMN_MERGE = "column_merge";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String ROW_NUM = "rowNum";
    private static final String ROW_DATA = "rowData";
    private static final String ROW_TIPS = "rowTips";
    private static final int CELL_OTHER = 0;
    private static final int CELL_ROW_MERGE = 1;
    private static final int CELL_COLUMN_MERGE = 2;
    private static final int IMG_HEIGHT = 30;
    private static final int IMG_WIDTH = 30;
    private static final char LEAN_LINE = '/';
    private static final int BYTES_DEFAULT_LENGTH = 10240;
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();


    public static boolean isExcel(String filename) {
        String lastName = filename.substring(filename.indexOf(".") + 1);
        return lastName.equalsIgnoreCase("xls") || lastName.equalsIgnoreCase("xlsx");
    }

    public static <T> List<T> readFile(File file, Class<T> clazz) throws Exception {
        JSONArray array = readFile(file);
        return getBeanList(array, clazz);
    }

    public static <T> List<T> readMultipartFile(MultipartFile mFile, Class<T> clazz) throws Exception {
        JSONArray array = readMultipartFile(mFile);
        return getBeanList(array, clazz);
    }

    public static JSONArray readFile(File file) throws Exception {
        return readExcel(null, file);
    }

    public static JSONArray readMultipartFile(MultipartFile mFile) throws Exception {
        return readExcel(mFile, null);
    }

    public static Map<String, JSONArray> readFileManySheet(File file) throws Exception {
        return readExcelManySheet(null, file);
    }

    public static Map<String, JSONArray> readFileManySheet(MultipartFile file) throws Exception {
        return readExcelManySheet(file, null);
    }

    private static <T> List<T> getBeanList(JSONArray array, Class<T> clazz) throws Exception {
        List<T> list = new ArrayList<>();
        Map<Integer, String> uniqueMap = new HashMap<>(16);
        for (int i = 0; i < array.size(); i++) {
            list.add(getBean(clazz, array.getJSONObject(i), uniqueMap));
        }
        return list;
    }

    
    private static <T> T getBean(Class<T> c, JSONObject obj, Map<Integer, String> uniqueMap) throws Exception {
        T t = c.newInstance();
        Field[] fields = c.getDeclaredFields();
        List<String> errMsgList = new ArrayList<>();
        boolean hasRowTipsField = false;
        StringBuilder uniqueBuilder = new StringBuilder();
        int rowNum = 0;
        for (Field field : fields) {
            
            if (field.getName().equals(ROW_NUM)) {
                rowNum = obj.getInteger(ROW_NUM);
                field.setAccessible(true);
                field.set(t, rowNum);
                continue;
            }
            
            if (field.getName().equals(ROW_TIPS)) {
                hasRowTipsField = true;
                continue;
            }
            
            if (field.getName().equals(ROW_DATA)) {
                field.setAccessible(true);
                field.set(t, obj.toString());
                continue;
            }
            
            setFieldValue(t, field, obj, uniqueBuilder, errMsgList);
        }
        
        if (uniqueBuilder.length() > 0) {
            if (uniqueMap.containsValue(uniqueBuilder.toString())) {
                Set<Integer> rowNumKeys = uniqueMap.keySet();
                for (Integer num : rowNumKeys) {
                    if (uniqueMap.get(num).equals(uniqueBuilder.toString())) {
                        errMsgList.add(String.format("数据唯一性校验失败,(%s)与第%s行重复)", uniqueBuilder, num));
                    }
                }
            } else {
                uniqueMap.put(rowNum, uniqueBuilder.toString());
            }
        }
        
        if (errMsgList.isEmpty() && !hasRowTipsField) {
            return t;
        }
        StringBuilder sb = new StringBuilder();
        int size = errMsgList.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                sb.append(errMsgList.get(i));
            } else {
                sb.append(errMsgList.get(i)).append(";");
            }
        }
        
        for (Field field : fields) {
            if (field.getName().equals(ROW_TIPS)) {
                field.setAccessible(true);
                field.set(t, sb.toString());
            }
        }
        return t;
    }

    private static <T> void setFieldValue(T t, Field field, JSONObject obj, StringBuilder uniqueBuilder, List<String> errMsgList) {
        
        ExcelImport annotation = field.getAnnotation(ExcelImport.class);
        if (annotation == null) {
            return;
        }
        String cname = annotation.value();
        if (cname.trim().length() == 0) {
            return;
        }
        
        String val = null;
        if (obj.containsKey(cname)) {
            val = getString(obj.getString(cname));
        }
        if (val == null) {
            return;
        }
        field.setAccessible(true);
        
        boolean require = annotation.required();
        if (require && val.isEmpty()) {
            errMsgList.add(String.format("[%s]不能为空", cname));
            return;
        }
        
        boolean unique = annotation.unique();
        if (unique) {
            if (uniqueBuilder.length() > 0) {
                uniqueBuilder.append("--").append(val);
            } else {
                uniqueBuilder.append(val);
            }
        }
        
        int maxLength = annotation.maxLength();
        if (maxLength > 0 && val.length() > maxLength) {
            errMsgList.add(String.format("[%s]长度不能超过%s个字符(当前%s个字符)", cname, maxLength, val.length()));
        }
        
        LinkedHashMap<String, String> kvMap = getKvMap(annotation.kv());
        if (!kvMap.isEmpty()) {
            boolean isMatch = false;
            for (String key : kvMap.keySet()) {
                if (kvMap.get(key).equals(val)) {
                    val = key;
                    isMatch = true;
                    break;
                }
            }
            if (!isMatch) {
                errMsgList.add(String.format("[%s]的值不正确(当前值为%s)", cname, val));
                return;
            }
        }
        
        String fieldClassName = field.getType().getSimpleName();
        try {
            if ("String".equalsIgnoreCase(fieldClassName)) {
                field.set(t, val);
            } else if ("boolean".equalsIgnoreCase(fieldClassName)) {
                field.set(t, Boolean.valueOf(val));
            } else if ("int".equalsIgnoreCase(fieldClassName) || "Integer".equals(fieldClassName)) {
                try {
                    field.set(t, Integer.valueOf(val));
                } catch (NumberFormatException e) {
                    errMsgList.add(String.format("[%s]的值格式不正确(当前值为%s)", cname, val));
                }
            } else if ("double".equalsIgnoreCase(fieldClassName)) {
                field.set(t, Double.valueOf(val));
            } else if ("long".equalsIgnoreCase(fieldClassName)) {
                field.set(t, Long.valueOf(val));
            } else if ("BigDecimal".equalsIgnoreCase(fieldClassName)) {
                field.set(t, new BigDecimal(val));
            } else if ("Date".equalsIgnoreCase(fieldClassName)) {
                try {
                    field.set(t, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(val));
                } catch (Exception e) {
                    field.set(t, new SimpleDateFormat("yyyy-MM-dd").parse(val));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, JSONArray> readExcelManySheet(MultipartFile mFile, File file) throws IOException {
        Workbook book = getWorkbook(mFile, file);
        if (book == null) {
            return Collections.emptyMap();
        }
        Map<String, JSONArray> map = new LinkedHashMap<>();
        for (int i = 0; i < book.getNumberOfSheets(); i++) {
            Sheet sheet = book.getSheetAt(i);
            JSONArray arr = readSheet(sheet);
            map.put(sheet.getSheetName(), arr);
        }
        book.close();
        return map;
    }

    private static JSONArray readExcel(MultipartFile mFile, File file) throws IOException {
        Workbook book = getWorkbook(mFile, file);
        if (book == null) {
            return new JSONArray();
        }
        JSONArray array = readSheet(book.getSheetAt(0));
        book.close();
        return array;
    }

    private static Workbook getWorkbook(MultipartFile mFile, File file) throws IOException {
        boolean fileNotExist = (file == null || !file.exists());
        if (mFile == null && fileNotExist) {
            return null;
        }
        
        InputStream in;
        String fileName;
        if (mFile != null) {
            
            in = mFile.getInputStream();
            fileName = getString(mFile.getOriginalFilename()).toLowerCase();
        } else {
            
            in = new FileInputStream(file);
            fileName = file.getName().toLowerCase();
        }
        Workbook book;
        if (fileName.endsWith(XLSX)) {
            book = new XSSFWorkbook(in);
        } else if (fileName.endsWith(XLS)) {
            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(in);
            book = new HSSFWorkbook(poifsFileSystem);
        } else {
            return null;
        }
        in.close();
        return book;
    }

    private static JSONArray readSheet(Sheet sheet) {
        
        int rowStart = sheet.getFirstRowNum() + 1;
        
        int rowEnd = sheet.getLastRowNum();
        
        Row headRow = sheet.getRow(rowStart);
        if (headRow == null) {
            return new JSONArray();
        }
        int cellStart = headRow.getFirstCellNum();
        int cellEnd = headRow.getLastCellNum();
        Map<Integer, String> keyMap = new HashMap<>();
        for (int j = cellStart; j < cellEnd; j++) {
            
            String val = getCellValue(headRow.getCell(j));
            if (val != null && !val.trim().isEmpty()) {
                keyMap.put(j, val);
            }
        }
        
        if (keyMap.isEmpty()) {
            return (JSONArray) Collections.emptyList();
        }
        
        JSONArray array = new JSONArray();
        
        if (rowStart == rowEnd) {
            JSONObject obj = new JSONObject();
            
            obj.put(ROW_NUM, 1);
            for (int i : keyMap.keySet()) {
                obj.put(keyMap.get(i), "");
            }
            array.add(obj);
            return array;
        }
        for (int i = rowStart + 1; i <= rowEnd; i++) {
            Row eachRow = sheet.getRow(i);
            JSONObject obj = new JSONObject();
            
            obj.put(ROW_NUM, i + 1);
            StringBuilder sb = new StringBuilder();
            for (int k = cellStart; k < cellEnd; k++) {
                if (eachRow != null) {
                    String val = getCellValue(eachRow.getCell(k));
                    
                    sb.append(val);
                    obj.put(keyMap.get(k), val);
                }
            }
            if (sb.length() > 0) {
                array.add(obj);
            }
        }
        return array;
    }

    private static String getCellValue(Cell cell) {
        
        if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
            return "";
        }
        
        if (cell.getCellTypeEnum() == CellType.STRING) {
            String val = cell.getStringCellValue();
            if (val == null || val.trim().length() == 0) {
                return "";
            }
            return val.trim();
        }
        
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            String s = cell.getNumericCellValue() + "";
            
            if (Pattern.matches(".*\\.0*", s)) {
                return s.split("\\.")[0];
            } else {
                return s;
            }
        }
        
        if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue() + "";
        }
        
        return cell.getCellFormula();
    }

    public static <T> void exportTemplate(HttpServletResponse response, String fileName, Class<T> clazz) {
        exportTemplate(response, fileName, fileName, clazz, false);
    }

    public static <T> void exportTemplate(HttpServletResponse response, String fileName, String sheetName,
                                          Class<T> clazz) {
        exportTemplate(response, fileName, sheetName, clazz, false);
    }

    public static <T> void exportTemplate(HttpServletResponse response, String fileName, Class<T> clazz,
                                          boolean isContainExample) {
        exportTemplate(response, fileName, fileName, clazz, isContainExample);
    }

    public static <T> void exportTemplate(HttpServletResponse response, String fileName, String sheetName,
                                          Class<T> clazz, boolean isContainExample) {
        
        List<ExcelClassField> headFieldList = getExcelClassFieldList(clazz);
        
        List<List<Object>> sheetDataList = new ArrayList<>();
        List<Object> headList = new ArrayList<>();
        List<Object> exampleList = new ArrayList<>();
        Map<Integer, List<String>> selectMap = new LinkedHashMap<>();
        for (int i = 0; i < headFieldList.size(); i++) {
            ExcelClassField each = headFieldList.get(i);
            headList.add(each.getName());
            exampleList.add(each.getExample());
            LinkedHashMap<String, String> kvMap = each.getKvMap();
            if (kvMap != null && kvMap.size() > 0) {
                selectMap.put(i, new ArrayList<>(kvMap.values()));
            }
        }
        sheetDataList.add(headList);
        if (isContainExample) {
            sheetDataList.add(exampleList);
        }
        
        export(response, fileName, sheetName, sheetDataList, selectMap);
    }

    private static <T> List<ExcelClassField> getExcelClassFieldList(Class<T> clazz) {
        
        Field[] fields = clazz.getDeclaredFields();
        boolean hasExportAnnotation = false;
        Map<Integer, List<ExcelClassField>> map = new LinkedHashMap<>();
        List<Integer> sortList = new ArrayList<>();
        for (Field field : fields) {
            ExcelClassField cf = getExcelClassField(field);
            if (cf.getHasAnnotation() == 1) {
                hasExportAnnotation = true;
            }
            int sort = cf.getSort();
            if (map.containsKey(sort)) {
                map.get(sort).add(cf);
            } else {
                List<ExcelClassField> list = new ArrayList<>();
                list.add(cf);
                sortList.add(sort);
                map.put(sort, list);
            }
        }
        Collections.sort(sortList);
        
        List<ExcelClassField> headFieldList = new ArrayList<>();
        if (hasExportAnnotation) {
            for (Integer sort : sortList) {
                for (ExcelClassField cf : map.get(sort)) {
                    if (cf.getHasAnnotation() == 1) {
                        headFieldList.add(cf);
                    }
                }
            }
        } else {
            headFieldList.addAll(map.get(0));
        }
        return headFieldList;
    }

    private static ExcelClassField getExcelClassField(Field field) {
        ExcelClassField cf = new ExcelClassField();
        String fieldName = field.getName();
        cf.setFieldName(fieldName);
        ExcelExport annotation = field.getAnnotation(ExcelExport.class);
        
        if (annotation == null) {
            cf.setHasAnnotation(0);
            cf.setName(fieldName);
            cf.setSort(0);
            return cf;
        }
        
        cf.setHasAnnotation(1);
        cf.setName(annotation.value());
        String example = getString(annotation.example());
        if (!example.isEmpty()) {
            if (isNumeric(example) && example.length() < 8) {
                cf.setExample(Double.valueOf(example));
            } else {
                cf.setExample(example);
            }
        } else {
            cf.setExample("");
        }
        cf.setSort(annotation.sort());
        
        String kv = getString(annotation.kv());
        cf.setKvMap(getKvMap(kv));
        return cf;
    }

    private static LinkedHashMap<String, String> getKvMap(String kv) {
        LinkedHashMap<String, String> kvMap = new LinkedHashMap<>();
        if (kv.isEmpty()) {
            return kvMap;
        }
        String[] kvs = kv.split(";");
        if (kvs.length == 0) {
            return kvMap;
        }
        for (String each : kvs) {
            String[] eachKv = getString(each).split("-");
            if (eachKv.length != 2) {
                continue;
            }
            String k = eachKv[0];
            String v = eachKv[1];
            if (k.isEmpty() || v.isEmpty()) {
                continue;
            }
            kvMap.put(k, v);
        }
        return kvMap;
    }

    
    public static void exportFile(File file, List<List<Object>> sheetData) {
        if (file == null) {
            System.out.println("文件创建失败");
            return;
        }
        if (sheetData == null) {
            sheetData = new ArrayList<>();
        }
        Map<String, List<List<Object>>> map = new HashMap<>();
        map.put(file.getName(), sheetData);
        export(null, file, file.getName(), map, null);
    }

    
    public static <T> File exportFile(String filePath, String fileName, List<T> list) throws IOException {
        File file = getFile(filePath, fileName);
        List<List<Object>> sheetData = getSheetData(list);
        exportFile(file, sheetData);
        return file;
    }

    
    private static File getFile(String filePath, String fileName) throws IOException {
        String dirPath = getString(filePath);
        String fileFullPath;
        if (dirPath.isEmpty()) {
            fileFullPath = fileName;
        } else {
            
            File dirFile = new File(dirPath);
            if (!dirFile.exists()) {
                boolean mkdirs = dirFile.mkdirs();
                if (!mkdirs) {
                    return null;
                }
            }
            
            if (dirPath.endsWith(String.valueOf(LEAN_LINE))) {
                fileFullPath = dirPath + fileName + XLSX;
            } else {
                fileFullPath = dirPath + LEAN_LINE + fileName + XLSX;
            }
        }
        System.out.println(fileFullPath);
        File file = new File(fileFullPath);
        if (!file.exists()) {
            boolean result = file.createNewFile();
            if (!result) {
                return null;
            }
        }
        return file;
    }

    private static <T> List<List<Object>> getSheetData(List<T> list) {
        
        List<ExcelClassField> excelClassFieldList = getExcelClassFieldList(list.get(0).getClass());
        List<String> headFieldList = new ArrayList<>();
        List<Object> headList = new ArrayList<>();
        Map<String, ExcelClassField> headFieldMap = new HashMap<>();
        for (ExcelClassField each : excelClassFieldList) {
            String fieldName = each.getFieldName();
            headFieldList.add(fieldName);
            headFieldMap.put(fieldName, each);
            headList.add(each.getName());
        }
        
        List<List<Object>> sheetDataList = new ArrayList<>();
        sheetDataList.add(headList);
        
        for (T t : list) {
            Map<String, Object> fieldDataMap = getFieldDataMap(t);
            Set<String> fieldDataKeys = fieldDataMap.keySet();
            List<Object> rowList = new ArrayList<>();
            for (String headField : headFieldList) {
                if (!fieldDataKeys.contains(headField)) {
                    continue;
                }
                Object data = fieldDataMap.get(headField);
                if (data == null) {
                    rowList.add("");
                    continue;
                }
                ExcelClassField cf = headFieldMap.get(headField);
                
                LinkedHashMap<String, String> kvMap = cf.getKvMap();
                if (kvMap == null || kvMap.isEmpty()) {
                    rowList.add(data);
                    continue;
                }
                String val = kvMap.get(data.toString());
                if (isNumeric(val)) {
                    rowList.add(Double.valueOf(val));
                } else {
                    rowList.add(val);
                }
            }
            sheetDataList.add(rowList);
        }
        return sheetDataList;
    }

    private static <T> Map<String, Object> getFieldDataMap(T t) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = t.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                String fieldName = field.getName();
                field.setAccessible(true);
                Object object = field.get(t);
                map.put(fieldName, object);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void exportEmpty(HttpServletResponse response, String fileName) {
        List<List<Object>> sheetDataList = new ArrayList<>();
        List<Object> headList = new ArrayList<>();
        headList.add("导出无数据");
        sheetDataList.add(headList);
        export(response, fileName, sheetDataList);
    }

    public static void export(HttpServletResponse response, String fileName, List<List<Object>> sheetDataList) {
        export(response, fileName, fileName, sheetDataList, null);
    }

    public static void exportManySheet(HttpServletResponse response, String fileName, Map<String, List<List<Object>>> sheetMap) {
        export(response, null, fileName, sheetMap, null);
    }


    public static void export(HttpServletResponse response, String fileName, String sheetName,
                              List<List<Object>> sheetDataList) {
        export(response, fileName, sheetName, sheetDataList, null);
    }

    public static void export(HttpServletResponse response, String fileName, String sheetName,
                              List<List<Object>> sheetDataList, Map<Integer, List<String>> selectMap) {

        Map<String, List<List<Object>>> map = new HashMap<>();
        map.put(sheetName, sheetDataList);
        export(response, null, fileName, map, selectMap);
    }

    public static <T, K> void export(HttpServletResponse response, String fileName, List<T> list, Class<K> template) {
        
        boolean lisIsEmpty = list == null || list.isEmpty();
        
        if (template == null && lisIsEmpty) {
            exportEmpty(response, fileName);
            return;
        }
        
        if (lisIsEmpty) {
            exportTemplate(response, fileName, template);
            return;
        }
        
        List<List<Object>> sheetDataList = getSheetData(list);
        export(response, fileName, sheetDataList);
    }

    public static void export(HttpServletResponse response, String fileName, List<List<Object>> sheetDataList, Map<Integer, List<String>> selectMap) {
        export(response, fileName, fileName, sheetDataList, selectMap);
    }

    private static void export(HttpServletResponse response, File file, String fileName,
                               Map<String, List<List<Object>>> sheetMap, Map<Integer, List<String>> selectMap) {
        
        SXSSFWorkbook book = new SXSSFWorkbook();
        
        Set<Entry<String, List<List<Object>>>> entries = sheetMap.entrySet();
        for (Entry<String, List<List<Object>>> entry : entries) {
            List<List<Object>> sheetDataList = entry.getValue();
            Sheet sheet = book.createSheet(entry.getKey());
            Drawing<?> patriarch = sheet.createDrawingPatriarch();
            
            CellStyle headStyle = book.createCellStyle();
            headStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.index);
            headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headStyle.setAlignment(HorizontalAlignment.CENTER);
            headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            
            CellStyle rowStyle = book.createCellStyle();
            rowStyle.setAlignment(HorizontalAlignment.CENTER);
            rowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            sheet.setDefaultColumnWidth(15);
            
            int rowLength = sheetDataList.size();
            int columnLength = sheetDataList.get(0).size();
            int[][] mergeArray = new int[rowLength][columnLength];
            for (int i = 0; i < sheetDataList.size(); i++) {
                
                Row row = sheet.createRow(i);
                List<Object> rowList = sheetDataList.get(i);
                for (int j = 0; j < rowList.size(); j++) {
                    
                    Object o = rowList.get(j);
                    int v = 0;
                    if (o instanceof URL) {
                        
                        setCellPicture(book, row, patriarch, i, j, (URL) o);
                    } else {
                        Cell cell = row.createCell(j);
                        if (i == 0) {
                            
                            v = setCellValue(cell, o, headStyle);
                        } else {
                            
                            v = setCellValue(cell, o, rowStyle);
                        }
                    }
                    mergeArray[i][j] = v;
                }
            }
            
            mergeCells(sheet, mergeArray);
            
            setSelect(sheet, selectMap);
        }
        
        if (response != null) {
            
            try {
                write(response, book, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                ByteArrayOutputStream ops = new ByteArrayOutputStream();
                book.write(ops);
                fos.write(ops.toByteArray());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    private static void mergeCells(Sheet sheet, int[][] mergeArray) {
        
        for (int x = 0; x < mergeArray.length; x++) {
            int[] arr = mergeArray[x];
            boolean merge = false;
            int y1 = 0;
            int y2 = 0;
            for (int y = 0; y < arr.length; y++) {
                int value = arr[y];
                if (value == CELL_COLUMN_MERGE) {
                    if (!merge) {
                        y1 = y;
                    }
                    y2 = y;
                    merge = true;
                } else {
                    merge = false;
                    if (y1 > 0) {
                        sheet.addMergedRegion(new CellRangeAddress(x, x, (y1 - 1), y2));
                    }
                    y1 = 0;
                    y2 = 0;
                }
            }
            if (y1 > 0) {
                sheet.addMergedRegion(new CellRangeAddress(x, x, (y1 - 1), y2));
            }
        }
        
        int xLen = mergeArray.length;
        int yLen = mergeArray[0].length;
        for (int y = 0; y < yLen; y++) {
            boolean merge = false;
            int x1 = 0;
            int x2 = 0;
            for (int x = 0; x < xLen; x++) {
                int value = mergeArray[x][y];
                if (value == CELL_ROW_MERGE) {
                    if (!merge) {
                        x1 = x;
                    }
                    x2 = x;
                    merge = true;
                } else {
                    merge = false;
                    if (x1 > 0) {
                        sheet.addMergedRegion(new CellRangeAddress((x1 - 1), x2, y, y));
                    }
                    x1 = 0;
                    x2 = 0;
                }
            }
            if (x1 > 0) {
                sheet.addMergedRegion(new CellRangeAddress((x1 - 1), x2, y, y));
            }
        }
    }

    private static void write(HttpServletResponse response, SXSSFWorkbook book, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String name = URLEncoder.encode(fileName, "UTF-8")+ XLSX ;
        response.addHeader("Content-Disposition", "attachment;filename=" + name);
        ServletOutputStream out = response.getOutputStream();
        book.write(out);
        out.flush();
        out.close();
    }

    private static int setCellValue(Cell cell, Object o, CellStyle style) {
        
        cell.setCellStyle(style);
        
        if (o == null) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");
            return CELL_OTHER;
        }
        
        if (o instanceof String) {
            String s = o.toString();
            
            if (isNumeric(s) && s.length() < 8) {
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(Double.parseDouble(s));
                return CELL_OTHER;
            } else {
                cell.setCellType(CellType.STRING);
                cell.setCellValue(s);
            }
            if (s.equals(ROW_MERGE)) {
                return CELL_ROW_MERGE;
            } else if (s.equals(COLUMN_MERGE)) {
                return CELL_COLUMN_MERGE;
            } else {
                return CELL_OTHER;
            }
        }
        
        if (o instanceof Integer || o instanceof Long || o instanceof Double || o instanceof Float) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(Double.parseDouble(o.toString()));
            return CELL_OTHER;
        }
        
        if (o instanceof Boolean) {
            cell.setCellType(CellType.BOOLEAN);
            cell.setCellValue((Boolean) o);
            return CELL_OTHER;
        }
        
        if (o instanceof BigDecimal) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(((BigDecimal) o).setScale(3, RoundingMode.HALF_UP).doubleValue());
            return CELL_OTHER;
        }
        
        if (o instanceof Date) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(formatDate((Date) o));
            return CELL_OTHER;
        }
        
        cell.setCellType(CellType.STRING);
        cell.setCellValue(o.toString());
        return CELL_OTHER;
    }

    private static void setCellPicture(SXSSFWorkbook wb, Row sr, Drawing<?> patriarch, int x, int y, URL url) {
        
        sr.setHeight((short) (IMG_WIDTH * IMG_HEIGHT));
        
        try (InputStream is = url.openStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buff = new byte[BYTES_DEFAULT_LENGTH];
            int rc;
            while ((rc = is.read(buff, 0, BYTES_DEFAULT_LENGTH)) > 0) {
                outputStream.write(buff, 0, rc);
            }
            
            XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, y, x, y + 1, x + 1);
            
            anchor.setAnchorType(AnchorType.MOVE_AND_RESIZE);
            patriarch.createPicture(anchor, wb.addPicture(outputStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(date);
    }

    private static void setSelect(Sheet sheet, Map<Integer, List<String>> selectMap) {
        if (selectMap == null || selectMap.isEmpty()) {
            return;
        }
        Set<Entry<Integer, List<String>>> entrySet = selectMap.entrySet();
        for (Entry<Integer, List<String>> entry : entrySet) {
            int y = entry.getKey();
            List<String> list = entry.getValue();
            if (list == null || list.isEmpty()) {
                continue;
            }
            String[] arr = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                arr[i] = list.get(i);
            }
            DataValidationHelper helper = sheet.getDataValidationHelper();
            CellRangeAddressList addressList = new CellRangeAddressList(1, 65000, y, y);
            DataValidationConstraint dvc = helper.createExplicitListConstraint(arr);
            DataValidation dv = helper.createValidation(dvc, addressList);
            if (dv instanceof HSSFDataValidation) {
                dv.setSuppressDropDownArrow(false);
            } else {
                dv.setSuppressDropDownArrow(true);
                dv.setShowErrorBox(true);
            }
            sheet.addValidationData(dv);
        }
    }

    private static boolean isNumeric(String str) {
        if (Objects.nonNull(str) && "0.0".equals(str)) {
            return true;
        }
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static String getString(String s) {
        if (s == null) {
            return "";
        }
        if (s.isEmpty()) {
            return s;
        }
        return s.trim();
    }

}
