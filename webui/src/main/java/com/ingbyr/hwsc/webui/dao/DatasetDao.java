package com.ingbyr.hwsc.webui.dao;

import com.ingbyr.hwsc.common.models.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DatasetDao {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String split = ",";
    private static final String SERVICE_KEY_PREFIX = "action:";
    private static final String SERVICE_INPUTS = "inputs";
    private static final String SERVICE_OUTPUTS = "outputs";
    private static final String SERVICE_QOS = "qos";
    private static final String CONCEPT_KEY_PREFIX = "atom:";
    private static final String CONCEPT_DIRECT_PARENT = "directParent";
    private static final String CONCEPT_PARENTS = "parents";
    private static final String CONCEPT_CHILDREN = "children";

    @Autowired
    public DatasetDao(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Save service map to database
     *
     * @param serviceMap Service map
     */
    public void saveServiceMap(Map<String, com.ingbyr.hwsc.common.models.Service> serviceMap) {
        HashOperations<String, String, Object> hashOp = redisTemplate.opsForHash();
        for (Map.Entry<String, com.ingbyr.hwsc.common.models.Service> entry : serviceMap.entrySet()) {
            String serviceName = SERVICE_KEY_PREFIX + entry.getKey();
            com.ingbyr.hwsc.common.models.Service service = entry.getValue();
            hashOp.put(serviceName, SERVICE_INPUTS, service.getInputParamSet());
            hashOp.put(serviceName, SERVICE_OUTPUTS, service.getOutputParamSet());
            hashOp.put(serviceName, SERVICE_QOS, service.getQos());
        }
    }

    public void saveConceptMap(Map<String, Concept> conceptMap) {
        HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
        for (Map.Entry<String, Concept> entry : conceptMap.entrySet()) {
            String conceptName = CONCEPT_KEY_PREFIX + entry.getKey();
            Concept concept = entry.getValue();
            Map<String, String> conceptData = new HashMap<>();
            conceptData.put(CONCEPT_DIRECT_PARENT, concept.getDirectParentName());
            conceptData.put(CONCEPT_PARENTS,
                    concept.getParentConcepts().stream().map(Concept::toString).collect(Collectors.joining(split)));
            conceptData.put(CONCEPT_CHILDREN,
                    concept.getChildrenConceptsIndex().stream().map(Concept::toString).collect(Collectors.joining(split)));
            hashOp.putAll(conceptName, conceptData);
        }
    }
}
