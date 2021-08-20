package org.king.excool;

import java.util.function.Supplier;

public interface ExcelOperatorFactory extends Supplier<ExcelOperator> {

    default ExcelOperator get() {
        return newInstance();
    }

    ExcelOperator newInstance();
}
