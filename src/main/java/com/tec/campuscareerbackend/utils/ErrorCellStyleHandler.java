package com.tec.campuscareerbackend.utils;

import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.BooleanUtils;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import org.apache.poi.ss.usermodel.*;

import java.util.List;
import java.util.Map;

public class ErrorCellStyleHandler implements CellWriteHandler, RowWriteHandler {

    private final List<Map<Integer, String>> errorDataList;

    public ErrorCellStyleHandler(List<Map<Integer, String>> errorDataList) {
        this.errorDataList = errorDataList;
    }

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        // 检查是否是数据行（非表头）
        if (Boolean.TRUE.equals(context.getHead())) {
            return;
        }

        // 获取当前行索引和列索引
        int rowIndex = context.getRowIndex();
        int columnIndex = context.getColumnIndex();

        // 检查当前单元格是否存在错误
        if (rowIndex > 0 && rowIndex - 1 < errorDataList.size()) { // rowIndex 是从 1 开始的
            Map<Integer, String> errorMap = errorDataList.get(rowIndex - 1);

            if (errorMap.containsKey(columnIndex)) {
                // 获取或创建样式
                WriteCellData<?> cellData = context.getFirstCellData();
                WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();
                writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
                writeCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());

                // 输出日志，便于调试
                System.out.println("标记错误: 行 " + (rowIndex + 1) + ", 列 " + (columnIndex + 1) + ", 错误信息: " + errorMap.get(columnIndex));
            }
        }
    }

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        // 如果是头部行，添加“错误信息”标题
        if (BooleanUtils.isTrue(context.getHead())) {
            int lastColumnIndex = context.getRow().getLastCellNum();
            Cell cell = context.getRow().createCell(lastColumnIndex, CellType.STRING);
            cell.setCellValue("错误信息");

            Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            cell.setCellStyle(headerStyle);
            return;
        }

        // 如果不是头部，处理错误信息列
        if (context.getRelativeRowIndex() != null) {
            int rowIndex = context.getRelativeRowIndex();

            if (rowIndex < errorDataList.size()) {
                Map<Integer, String> errorMap = errorDataList.get(rowIndex);

                if (errorMap != null && !errorMap.isEmpty()) {
                    int lastColumnIndex = context.getRow().getLastCellNum();
                    String errorMessage = String.join("; ", errorMap.values());
                    Cell cell = context.getRow().createCell(lastColumnIndex, CellType.STRING);
                    cell.setCellValue(errorMessage);

                    // 设置样式（如红色字体）
                    Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
                    CellStyle errorStyle = workbook.createCellStyle();
                    Font font = workbook.createFont();
                    font.setColor(IndexedColors.RED.getIndex());
                    errorStyle.setFont(font);
                    cell.setCellStyle(errorStyle);
                }
            }
        }
    }
}