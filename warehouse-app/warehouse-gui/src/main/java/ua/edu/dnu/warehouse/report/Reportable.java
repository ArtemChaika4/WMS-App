package ua.edu.dnu.warehouse.report;


import java.io.File;


public interface Reportable {
    void createReport(File directory) throws Exception;
}
