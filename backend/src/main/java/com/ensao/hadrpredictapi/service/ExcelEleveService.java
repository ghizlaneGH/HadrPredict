package com.ensao.hadrpredictapi.service;

import com.ensao.hadrpredictapi.entity.Eleve;
import com.ensao.hadrpredictapi.entity.Ecole;
import com.ensao.hadrpredictapi.repository.EleveRepository;
import com.ensao.hadrpredictapi.repository.EcoleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;
import java.util.*;

@Service
public class ExcelEleveService {

    private final EleveRepository eleveRepository;
    private final EcoleRepository ecoleRepository;
    private final EcoleService ecoleService;

    public ExcelEleveService(EleveRepository eleveRepository, EcoleRepository ecoleRepository, EcoleService ecoleService) {
        this.eleveRepository = eleveRepository;
        this.ecoleRepository = ecoleRepository;
        this.ecoleService = ecoleService;
    }

    public void importer(MultipartFile file) throws Exception {
        List<Eleve> eleves = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int firstDataRowIndex = sheet.getFirstRowNum() + 1;

            for (int i = firstDataRowIndex; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Long idEleve = getLongValue(row.getCell(0));
                if (idEleve == null) {
                    System.out.println("❌ Ligne " + (i + 1) + " ignorée : idEleve manquant");
                    continue;
                }

                if (eleveRepository.findByIdEleve(idEleve).isPresent()) {
                    System.out.println("⚠️ Ligne " + (i + 1) + " ignorée : doublon idEleve " + idEleve);
                    continue;
                }

                OffsetDateTime odt = getOffsetDateTimeValue(row.getCell(1));
                if (odt == null) {
                    System.out.println("❌ Ligne " + (i + 1) + " ignorée : dateDeNaissance invalide");
                    continue;
                }

                String situation = getStringCellValue(row.getCell(2));
                String genre = getStringCellValue(row.getCell(3));
                if (genre == null || genre.isBlank()) {
                    System.out.println("❌ Ligne " + (i + 1) + " ignorée : genre manquant");
                    continue;
                }

                String classe = getStringCellValue(row.getCell(4));
                String cycle = getStringCellValue(row.getCell(5));
                Double resultat = getDoubleValue(row.getCell(6));
                Integer absence = getIntegerValue(row.getCell(7));

                String nomEcole = getStringCellValue(row.getCell(8));
                String commune = getStringCellValue(row.getCell(9));
                String province = getStringCellValue(row.getCell(10));
                String typeSchool = getStringCellValue(row.getCell(11));
                String milieu = getStringCellValue(row.getCell(12));

                if (nomEcole == null || commune == null) {
                    System.out.println("❌ Ligne " + (i + 1) + " ignorée : informations école incomplètes");
                    continue;
                }

                if (milieu == null || milieu.isBlank()) {
                    System.out.println("❌ Ligne " + (i + 1) + " ignorée : milieu manquant");
                    continue;
                }

                Optional<Ecole> existingEcole = ecoleRepository.findByNomAndCommune(nomEcole, commune);
                Ecole ecole = existingEcole.orElseGet(() -> {
                    Ecole newEcole = new Ecole();
                    newEcole.setNom(nomEcole);
                    newEcole.setCommune(commune);
                    newEcole.setProvince(province);
                    newEcole.setTypeSchool(typeSchool);
                    newEcole.setMilieu(milieu);
                    return ecoleService.saveSchool(newEcole);
                });

                Eleve eleve = new Eleve();
                eleve.setIdEleve(idEleve);
                eleve.setDateDeNaissance(odt.toLocalDate());
                eleve.setSituation(situation);
                eleve.setGenre(genre);
                eleve.setClasse(classe);
                eleve.setCycle(cycle);
                eleve.setAbsence(absence);
                eleve.setResultat(resultat);
                eleve.setEcole(ecole);

                setPredictionFromMLModel(eleve);
                eleves.add(eleve);
                System.out.println("✅ Élève prêt à insérer : " + idEleve);
            }
        }

        eleveRepository.saveAll(eleves);
        System.out.println("✅ Total élèves enregistrés dans la base : " + eleves.size());

        updatePredictionsForExistingStudents();
    }

    public void updatePredictionsForExistingStudents() {
        List<Eleve> eleves = eleveRepository.findAll();
        for (Eleve eleve : eleves) {
            setPredictionFromMLModel(eleve);
        }
        eleveRepository.saveAll(eleves);
    }

    private void setPredictionFromMLModel(Eleve eleve) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> payload = new HashMap<>();
            payload.put("Resultat", eleve.getResultat());
            payload.put("Absence", eleve.getAbsence());
            payload.put("Genre", eleve.getGenre());
            payload.put("Milieu", eleve.getEcole().getMilieu());
            payload.put("Cycle", eleve.getCycle());
            payload.put("Type_etablissement", eleve.getEcole().getTypeSchool());
            payload.put("age", calculateAge(eleve.getDateDeNaissance()));

            payload.put("Situation", eleve.getSituation() != null ? eleve.getSituation() : "");

            String requestBody = mapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8000/predict"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode json = mapper.readTree(response.body());
                eleve.setPrediction(json.get("prediction").asInt());
                eleve.setProbabilityAbandon(json.get("probability_abandon").asDouble());
            } else {
                System.err.println("Erreur ML API : " + response.statusCode() + " " + response.body());
            }

        } catch (Exception e) {
            System.err.println("Erreur appel modèle ML : " + e.getMessage());
        }
    }

    private int calculateAge(LocalDate dateDeNaissance) {
        return Period.between(dateDeNaissance, LocalDate.now()).getYears();
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private Long getLongValue(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (long) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                return Long.parseLong(cell.getStringCellValue().trim());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private Integer getIntegerValue(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                return Integer.parseInt(cell.getStringCellValue().trim());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private Double getDoubleValue(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                return Double.parseDouble(cell.getStringCellValue().trim());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private OffsetDateTime getOffsetDateTimeValue(Cell cell) {
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Instant instant = cell.getDateCellValue().toInstant();
                return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
            } else {
                String text = getStringCellValue(cell);
                if (text == null) return null;
                return OffsetDateTime.parse(text);
            }
        } catch (Exception e) {
            return null;
        }
    }
}