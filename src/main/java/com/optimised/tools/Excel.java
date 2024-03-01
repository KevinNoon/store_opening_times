package com.optimised.tools;

import com.optimised.model.CoreTimes;
import com.optimised.model.ExceptionTime;
import com.optimised.security.AuthenticatedUser;
import com.optimised.services.CoreTimesService;
import com.optimised.services.ExceptionTimeService;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;

@Service
public class Excel {
  private static final Logger log = LogManager.getLogger(Excel.class);
  final CoreTimesService coreTimesService;
  final ExceptionTimeService exceptionTimeService;

  public Excel(CoreTimesService coreTimesService, ExceptionTimeService exceptionTimeService) {
    this.coreTimesService = coreTimesService;
    this.exceptionTimeService = exceptionTimeService;
  }

  public Upload setCoreTimes(Span errorField, Span updateCompleteField) {
    MemoryBuffer memoryBuffer = new MemoryBuffer();
    Upload singleFileUpload = new Upload(memoryBuffer);

    singleFileUpload.addSucceededListener(event -> {
          // Get information about the uploaded file
          InputStream fileData = memoryBuffer.getInputStream();
          String fileName = event.getFileName();
          long contentLength = event.getContentLength();
          String mimeType = event.getMIMEType();
          XSSFWorkbook workbook = null;
          try {
            workbook = new XSSFWorkbook(fileData);
            Boolean sheetFound = false;
            Iterator<Sheet> sheetIterator = workbook.iterator();
            while (sheetIterator.hasNext()) {
              Sheet sheet = sheetIterator.next();
              System.out.println(sheet.getSheetName());
              if (sheet.getSheetName().contains("CoreHours")) {
                sheetFound = true;
                //Iterate through each rows one by one
                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                  CoreTimes coreTimes = new CoreTimes();
                  Row row = rowIterator.next();
                  if (row.getCell(0) != null) {
                    if (row.getCell(0).getCellType() == CellType.NUMERIC) {
                      coreTimes.setStoreNo((int) row.getCell(0).getNumericCellValue());
                      coreTimes.setStoreName(row.getCell(1).getStringCellValue());
                      coreTimes.setSunOpen(cellToLocalTime(row, 2));
                      coreTimes.setSunClose(cellToLocalTime(row, 3));
                      coreTimes.setMonOpen(cellToLocalTime(row, 4));
                      coreTimes.setMonClose(cellToLocalTime(row, 5));
                      coreTimes.setTueOpen(cellToLocalTime(row, 6));
                      coreTimes.setTueClose(cellToLocalTime(row, 7));
                      coreTimes.setWedOpen(cellToLocalTime(row, 8));
                      coreTimes.setWedClose(cellToLocalTime(row, 9));
                      coreTimes.setThuOpen(cellToLocalTime(row, 10));
                      coreTimes.setThuClose(cellToLocalTime(row, 11));
                      coreTimes.setFriOpen(cellToLocalTime(row, 12));
                      coreTimes.setFriClose(cellToLocalTime(row, 13));
                      coreTimes.setSatOpen(cellToLocalTime(row, 14));
                      coreTimes.setSatClose(cellToLocalTime(row, 15));
                      coreTimesService.save(coreTimes);
                    }
                  }
                }
              }
            }
            if (!sheetFound) {
              showErrorMessage(errorField, "Error in Excel sheet");
            }
            fileData.close();
            if (!errorField.isVisible()) showUpdateCompleteField(updateCompleteField, "Update Complete Please Close");
          } catch (IOException e) {
            showErrorMessage(errorField, e.getMessage());
            log.error("Setting core time error " + e.getMessage());
          }
        }
    );
    singleFileUpload.setAcceptedFileTypes(".xlsx");
    singleFileUpload.addFailedListener(e -> showErrorMessage(errorField, e.getReason().getMessage()));
    singleFileUpload.addFileRejectedListener(e -> showErrorMessage(errorField, e.getErrorMessage()));
    singleFileUpload.addFinishedListener(e -> {
    });

    return singleFileUpload;
  }

  public Upload setExceptionTimes(Span errorField, Span updateCompleteField) {
    MemoryBuffer memoryBuffer = new MemoryBuffer();
    Upload singleFileUpload = new Upload(memoryBuffer);

    singleFileUpload.addSucceededListener(event -> {
          // Get information about the uploaded file
          InputStream fileData = memoryBuffer.getInputStream();
          String fileName = event.getFileName();
          long contentLength = event.getContentLength();
          String mimeType = event.getMIMEType();
          XSSFWorkbook workbook = null;
          try {
            workbook = new XSSFWorkbook(fileData);

            //Iterate through each rows one by one
            //Get first/desired sheet from the workbook
            Iterator<Sheet> sheetIterator = workbook.iterator();
            while (sheetIterator.hasNext()) {
              Sheet sheet = sheetIterator.next();
              if (sheet.getSheetName().contains("EX")) {

                if (sheet.getRow(1).getCell(2).getCellType().equals(CellType.NUMERIC)) {
                  LocalDate weekStartSunday = sheet.getRow(1).getCell(2).getLocalDateTimeCellValue().toLocalDate();
                  Iterator<Row> rowIterator = sheet.iterator();
                  while (rowIterator.hasNext()) {

                    Row row = rowIterator.next();
                    if (row.getCell(0) != null) {
                      if (row.getCell(0).getCellType() == CellType.NUMERIC) {

                        for (int day = 0; day < 7; day++) {
                          if (weekStartSunday.isAfter(LocalDate.now().plusDays(day))) {
                            ExceptionTime exceptionTime = new ExceptionTime();
                            exceptionTime.setStoreNo((int) row.getCell(0).getNumericCellValue());
                            exceptionTime.setStoreName(row.getCell(1).getStringCellValue());
                            exceptionTime.setChangeDate(weekStartSunday.plusDays(day));
                            exceptionTime.setOpen(cellToLocalTime(row, day * 2 + 2));
                            exceptionTime.setClose(cellToLocalTime(row, day * 2 + 3));
                            CoreTimes coreTimes = coreTimesService.findByStoreNo(exceptionTime.getStoreNo());

                            switch (day) {
                              case 0:
                                if (coreTimes != null && (exceptionTime.getOpen() != coreTimes.getSunOpen() ||
                                    exceptionTime.getClose() != coreTimes.getSunClose())) {
                                  exceptionTimeService.save(exceptionTime);
                                }
                                break;
                              case 1:
                                if (coreTimes != null && (exceptionTime.getOpen() != coreTimes.getMonOpen() ||
                                    exceptionTime.getClose() != coreTimes.getMonClose())) {
                                  exceptionTimeService.save(exceptionTime);
                                }
                                break;
                              case 2:
                                if (coreTimes != null && (exceptionTime.getOpen() != coreTimes.getTueOpen() ||
                                    exceptionTime.getClose() != coreTimes.getTueClose())) {
                                  exceptionTimeService.save(exceptionTime);
                                }
                                break;
                              case 3:
                                if (coreTimes != null && (exceptionTime.getOpen() != coreTimes.getWedOpen() ||
                                    exceptionTime.getClose() != coreTimes.getWedClose())) {
                                  exceptionTimeService.save(exceptionTime);
                                }
                                break;
                              case 4:
                                if (coreTimes != null && (exceptionTime.getOpen() != coreTimes.getThuOpen() ||
                                    exceptionTime.getClose() != coreTimes.getThuClose())) {
                                  exceptionTimeService.save(exceptionTime);
                                }
                                break;
                              case 5:
                                if (coreTimes != null && (exceptionTime.getOpen() != coreTimes.getFriOpen() ||
                                    exceptionTime.getClose() != coreTimes.getFriClose())) {
                                  exceptionTimeService.save(exceptionTime);
                                }
                                break;
                              case 6:
                                if (coreTimes != null && (exceptionTime.getOpen() != coreTimes.getSatOpen() ||
                                    exceptionTime.getClose() != coreTimes.getSatClose())) {
                                  exceptionTimeService.save(exceptionTime);
                                }
                                break;
                            }
                          }
                        }
                      }
                    }
                  }
                } else {
                  showErrorMessage(errorField, "Error in Excel sheet");
                  log.error("Error setting exception times " + "Error in Excel sheet");
                }
              }
            }
            fileData.close();
            if (!errorField.isVisible()) {
              showUpdateCompleteField(updateCompleteField, "Update Complete Please Close");
              log.error("Setting exception times success ");
            }


          } catch (IOException e) {
            showErrorMessage(errorField, e.getMessage());
            log.error("Error setting exception times " + e.getMessage());
          }
        }
    );
    singleFileUpload.setAcceptedFileTypes(".xlsx");
    singleFileUpload.addFailedListener(e -> showErrorMessage(errorField, e.getReason().getMessage()));
    singleFileUpload.addFileRejectedListener(e -> showErrorMessage(errorField, e.getErrorMessage()));
    singleFileUpload.addFinishedListener(e -> {
    });
    return singleFileUpload;
  }

  static private LocalTime cellToLocalTime(Row row, int i) {
    LocalTime localTime;
    String time = "00:00";
    if (row.getCell(i).getCellType().equals(CellType.NUMERIC)) {
      localTime = row.getCell(i).getLocalDateTimeCellValue().toLocalTime();
    } else {

      if (!row.getCell(i).getStringCellValue().isEmpty()) {
        time = row.getCell(i).getStringCellValue();
      }
      localTime = LocalTime.parse(time);
    }
    return localTime;
  }

  private void showErrorMessage(Span errorField, String message) {
    log.error("Error setting core/exception times " + message);
    errorField.setVisible(true);
    errorField.setText(message);
  }

  private void showUpdateCompleteField(Span updateCompleteField, String message) {
    updateCompleteField.setVisible(true);
    updateCompleteField.setText(message);
    log.info("Setting exception times " + message);
  }

  public static String createCsv(List<ExceptionTime> changedTimes){
   // List<ExceptionTime> changedTimes = exceptionTimeService.findByChanged();
    String timeStamp = LocalDateTime.now().toString().replace("-","").replace(":","");
    String fileName = "Exception" + timeStamp.substring(0,timeStamp.indexOf(".")) + ".csv";
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
      writer.write("StoreName,Event,occupancy,Start hr,Start Min,End hr,End Min,Day Start,Month Start,	Year Start\n"
      );
      for (ExceptionTime et : changedTimes
      ) {
        boolean occpancy = et.getOpen() != et.getClose();
        writer.write(et.getStoreName() + "," +
            "Ev" + et.getChangeDate().toString().replace("-", "") + et.getStoreNo()
            + "," +
            occpancy + "," + et.getOpen().getHour() + "," + et.getOpen().getMinute()
            + "," + et.getClose().getHour() + "," + et.getClose().getMinute()
            + "," + et.getChangeDate().getDayOfMonth() + "," + et.getChangeDate().getMonthValue() + "," + et.getChangeDate().getYear() +"\n"
        );
      }
      writer.close();
    }catch (IOException e){
      log.error("Failed to create CSV " + e.getMessage());
    }
    return fileName;
  }

}
