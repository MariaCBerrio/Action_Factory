package com.IntegrativeProject.ActionFactory.util;

import com.IntegrativeProject.ActionFactory.model.Device;
import com.IntegrativeProject.ActionFactory.model.Employee;
import com.IntegrativeProject.ActionFactory.model.Supplier;
import com.IntegrativeProject.ActionFactory.repository.EmployeeRepository;
import com.IntegrativeProject.ActionFactory.repository.SupplierRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvUtility {

    private final EmployeeRepository employeeRepository;
    private final SupplierRepository supplierRepository;

    public CsvUtility(EmployeeRepository employeeRepository, SupplierRepository supplierRepository) {
        this.employeeRepository = employeeRepository;
        this.supplierRepository = supplierRepository;
    }

//  Definición del tipo MIME(Multipurpose Internet Mail Extensions) para archivos CSV
    public static String type = "text/csv";

//  Encabezados de los atributos del archivo
    static String[] headers = {"imei", "status", "score", "validation_status", "validation_date", "supplier_id", "employee_id"};

//  Método para verificar el formato CSV
    public static boolean hasCsvFormat(MultipartFile file) {
        return file != null && file.getContentType().equals("text/csv");
    }

//  Método para convertir un archivo CSV en una lista de objetos Device
    public List<Device> csvToDeviceList(InputStream is) {
        try (
//              Lectura del InputStream de archivo CSV mediante BufferedReader
                BufferedReader bReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//                                                                                      wFRAH()                 wIHC()            wT()
                CSVParser csvParser = new CSVParser(bReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());      //Parsea el archivo CSV usando CSVParser
        ) {                                                                                                                                       //wFRAH() -> indica que la primera fila del archivo CSV son los encabezados de las columnas
            List<Device> deviceList = new ArrayList<>();                                                                                          //wIHC() ->  ignora las diferencias de mayúsculas/minúsculas al comparar los nombres de las columanas con los encabezados predeterminados
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();                                                                              //wT() -> indica que se deben eliminar los espacios en blanco alrededor de los valores de las celdas del CSV durante el proceso de parsing
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss");

            for (CSVRecord csvRecord : csvRecords) {
                try {
                    Device device = new Device();
                    device.setImei(Long.parseLong(csvRecord.get("imei")));
                    device.setStatus(csvRecord.get("status"));
                    device.setScore(Integer.parseInt(csvRecord.get("score")));
                    device.setValidationStatus(csvRecord.get("validation_status"));
                    device.setValidationDate(LocalDateTime.parse(csvRecord.get("validation_date"), formatter));

                    Long supplierId = Long.parseLong(csvRecord.get("supplier_id"));
                    Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(() -> new IllegalStateException("Supplier not found: " + supplierId));
                    device.setSupplier(supplier);

                    Long employeeId = Long.parseLong(csvRecord.get("employee_id"));
                    Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new IllegalStateException("Employee not found: " + employeeId));
                    device.setEmployee(employee);

                    deviceList.add(device);
                } catch (Exception e) {
                    System.out.println("Error parsing record: " + e.getMessage());
                }
            }
            return deviceList;
        } catch (IOException e) {
            throw new RuntimeException("CSV data is failed to parse: " + e.getMessage());
        }
    }
}
