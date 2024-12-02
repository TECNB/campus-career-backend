package com.tec.campuscareerbackend.utils;


import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.StringUtils;

public class CustomIntegerConverter implements Converter<Integer> {

    String errorData = "";

    @Override
    public Integer convertToJavaData(ReadCellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String cellValue = cellData.getStringValue();

        // 如果是 null 或者空字符串，尝试读取为数字
        if (cellValue == null || cellValue.trim().isEmpty()) {
            // 尝试使用数字格式获取值
            if (cellData.getNumberValue() != null) {
                return cellData.getNumberValue().intValue();
            }
            return 0;  // 错误格式时返回 0
        }

        try {
            // 尝试将表格中的字符串转换为 Integer
            return Integer.parseInt(cellValue);
        } catch (NumberFormatException e) {
            // 如果字符串不能转换为整数，返回 0
            errorData = cellValue;
            return 0;  // 错误格式时返回 0
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        System.out.println("convertToExcelData-errorData: " + errorData);
        // 如果值为 null 或 0，则写入空字符串，避免写入错误数据
        if (value == null || value == 0) {
            return new WriteCellData<>(errorData); // 返回空字符串
        }
        // 正常转换 Integer 为字符串
        return new WriteCellData<>(String.valueOf(value));
    }
}