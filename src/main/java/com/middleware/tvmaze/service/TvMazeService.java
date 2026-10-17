package com.middleware.tvmaze.service;

import com.middleware.tvmaze.model.*;
import com.middleware.tvmaze.repository.CommentRepository;
import com.middleware.tvmaze.repository.ShowCacheRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;

import java.util.*;

@Service
public class TvMazeService {

    private final ShowCacheRepository showCacheRepository;
    private final CommentRepository commentRepository;
    private final RestTemplate restTemplate;
    
    // URL base oficial de la API de TV Maze
    private final String BASE_URL = "https://api.tvmaze.com";

    // Inyección por constructor estricta (Resuelve advertencias JAVA_CONSTRUCTOR_PARAMETER_INJECTION)
    public TvMazeService(ShowCacheRepository showCacheRepository, CommentRepository commentRepository, RestTemplate restTemplate) {
        this.showCacheRepository = showCacheRepository;
        this.commentRepository = commentRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Endpoint A: Realiza la búsqueda filtrada de shows y acopla sus comentarios de MongoDB Atlas.
     */
    public List<ShowSearchDTO> searchShows(String query) {
        // Formateo y codificación básica del parámetro de búsqueda
            // Concatenación dinámica obligatoria usando BASE_URL
    String url = BASE_URL + "/search/shows?q=" + query.trim().replace(" ", "%20");
    System.out.println("Enviando petición real a: " + url);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Descarga genérica basada en clases nativas de Spring para evitar conflictos locales de dependencias
            @SuppressWarnings("rawtypes")
            ResponseEntity<List> responseEntity = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                List.class
            );

            @SuppressWarnings("unchecked")
            List<Object> responses = responseEntity.getBody();
            if (responses == null || responses.isEmpty()) return Collections.emptyList();

            List<ShowSearchDTO> dtos = new ArrayList<>();

            for (Object obj : responses) {
                if (!(obj instanceof Map)) continue;
                
                @SuppressWarnings("unchecked")
                Map<String, Object> item = (Map<String, Object>) obj;
                
                @SuppressWarnings("unchecked")
                Map<String, Object> showMap = (Map<String, Object>) item.get("show");
                if (showMap == null) continue;

                ShowSearchDTO dto = new ShowSearchDTO();
                
                Object idObj = showMap.get("id");
                if (idObj != null) {
                    dto.setId(Long.valueOf(idObj.toString()));
                }
                
                dto.setName((String) showMap.get("name"));
                dto.setSummary((String) showMap.get("summary"));
                
                if (showMap.get("genres") instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> genresList = (List<String>) showMap.get("genres");
                    dto.setGenres(genresList);
                }
                
                String channelName = null;
                if (showMap.get("network") != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> network = (Map<String, Object>) showMap.get("network");
                    channelName = (String) network.get("name");
                } else if (showMap.get("webChannel") != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> webChannel = (Map<String, Object>) showMap.get("webChannel");
                    channelName = (String) webChannel.get("name");
                }
                dto.setChannel(channelName);

                // Requerimiento A Modificado: Inyección de comentarios en tiempo real desde la BD
                if (dto.getId() != null) {
                    dto.setComments(commentRepository.findByShowId(dto.getId()));
                } else {
                    dto.setComments(new ArrayList<>());
                }

                dtos.add(dto);
            }

            return dtos;

        } catch (Exception e) {
            System.out.println("Error procesando los datos de TV Maze (Search): " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Endpoint B: Obtiene la información completa de un show validando previamente el Caché en MongoDB Atlas.
     */
    public Map<String, Object> getShowById(Long showId) {
        // 1. Validar el Caché persistido en MongoDB
        Optional<ShowCacheDocument> cachedShow = showCacheRepository.findById(showId);
        if (cachedShow.isPresent()) {
            return cachedShow.get().getFullShowData();
        }

        String url = BASE_URL + "/shows/" + showId;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            Map<String, Object> externalShowData = responseEntity.getBody();

            // 2. Persistir en la base de datos antes de retornar si no existía el registro
            if (externalShowData != null) {
                ShowCacheDocument cacheDocument = new ShowCacheDocument();
                cacheDocument.setId(showId);
                cacheDocument.setFullShowData(externalShowData);
                showCacheRepository.save(cacheDocument);
                return externalShowData;
            }
        } catch (Exception e) {
            System.out.println("Error al obtener show por ID de la API externa: " + e.getMessage());
            return null;
        }
        return null;
    }

    /**
     * Endpoint C: Almacena una calificación y una opinión vinculada a un Show ID dentro de MongoDB Atlas.
     */
    public Map<String, String> saveComment(Long showId, String comment, Integer rating) {
        Map<String, String> status = new HashMap<>();
        if (rating < 0 || rating > 5) {
            status.put("status", "Error: El rating debe estar estrictamente en un rango de 0 a 5");
            return status;
        }

        CommentDocument commentDoc = new CommentDocument();
        commentDoc.setShowId(showId);
        commentDoc.setComment(comment);
        commentDoc.setRating(rating);
        commentRepository.save(commentDoc);
        
        status.put("status", "Comentario guardado exitosamente");
        return status;
    }
}
