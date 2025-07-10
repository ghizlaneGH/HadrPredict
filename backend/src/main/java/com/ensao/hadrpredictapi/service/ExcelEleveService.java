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

    private int calculateAge(LocalDate dateDeNaissance) {
        return Period.between(dateDeNaissance, LocalDate.now()).getYears();
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
                System.err.println("Erreur de prédiction : statut " + response.statusCode());
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel au modèle ML : " + e.getMessage());
        }
    }

    public void importer(MultipartFile file) throws Exception {
        List<Eleve> eleves = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int firstDataRowIndex = sheet.getFirstRowNum() + 1; // Skip header row

            for (int i = firstDataRowIndex; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Long idEleve = getLongValue(row.getCell(0));
                if (idEleve == null) {
                    System.out.println("Ligne " + (i + 1) + " ignorée : idEleve manquant");
                    continue;
                }

                OffsetDateTime odt = getOffsetDateTimeValue(row.getCell(1));
                if (odt == null) {
                    System.out.println("Ligne " + (i + 1) + " ignorée : dateDeNaissance invalide");
                    continue;
                }

                String genre = getStringCellValue(row.getCell(2));
                if (genre == null || genre.isBlank()) {
                    System.out.println("Ligne " + (i + 1) + " ignorée : genre manquant");
                    continue;
                }

                String classe = getStringCellValue(row.getCell(3));
                String cycle = getStringCellValue(row.getCell(4));
                Integer absence = getIntegerValue(row.getCell(5));
                Double resultat = getDoubleValue(row.getCell(6));
                String situation = getStringCellValue(row.getCell(12)); // Optionnelle

                String nomEcole = getStringCellValue(row.getCell(7));
                String commune = getStringCellValue(row.getCell(8));
                if (nomEcole == null || commune == null) {
                    System.out.println("Ligne " + (i + 1) + " ignorée : informations école incomplètes");
                    continue;
                }

                Optional<Ecole> existingEcole = ecoleRepository.findByNomAndCommune(nomEcole, commune);
                Ecole ecole = existingEcole.orElseGet(() -> {
                    Ecole newEcole = new Ecole();
                    newEcole.setNom(nomEcole);
                    newEcole.setCommune(commune);
                    newEcole.setProvince(getStringCellValue(row.getCell(9)));
                    newEcole.setTypeSchool(getStringCellValue(row.getCell(10)));
                    newEcole.setMilieu(getStringCellValue(row.getCell(11)));
                    return ecoleService.saveSchool(newEcole);
                });

                Optional<Eleve> existingEleveOpt = eleveRepository.findByIdEleve(idEleve);
                Eleve eleve;
                boolean needsPrediction = false;

                if (existingEleveOpt.isPresent()) {
                    eleve = existingEleveOpt.get();

                    if (!Objects.equals(eleve.getDateDeNaissance(), odt.toLocalDate()) ||
                            !Objects.equals(eleve.getGenre(), genre) ||
                            !Objects.equals(eleve.getClasse(), classe) ||
                            !Objects.equals(eleve.getCycle(), cycle) ||
                            !Objects.equals(eleve.getAbsence(), absence) ||
                            !Objects.equals(eleve.getResultat(), resultat) ||
                            !Objects.equals(eleve.getSituation(), situation) ||
                            !Objects.equals(eleve.getEcole(), ecole)) {

                        eleve.setDateDeNaissance(odt.toLocalDate());
                        eleve.setGenre(genre);
                        eleve.setClasse(classe);
                        eleve.setCycle(cycle);
                        eleve.setAbsence(absence);
                        eleve.setResultat(resultat);
                        eleve.setSituation(situation);
                        eleve.setEcole(ecole);
                        needsPrediction = true;
                    }

                } else {
                    eleve = new Eleve();
                    eleve.setIdEleve(idEleve);
                    eleve.setDateDeNaissance(odt.toLocalDate());
                    eleve.setGenre(genre);
                    eleve.setClasse(classe);
                    eleve.setCycle(cycle);
                    eleve.setAbsence(absence);
                    eleve.setResultat(resultat);
                    eleve.setSituation(situation);
                    eleve.setEcole(ecole);
                    needsPrediction = true;
                }

                if (needsPrediction) {
                    setPredictionFromMLModel(eleve);
                }

                eleves.add(eleve);
            }
        }

        eleveRepository.saveAll(eleves);
        System.out.println(eleves.size() + " élèves traités (créés ou mis à jour) avec succès.");
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
