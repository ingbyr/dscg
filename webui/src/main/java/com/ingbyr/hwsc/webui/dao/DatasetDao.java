package com.ingbyr.hwsc.webui.dao;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Param;
import com.ingbyr.hwsc.common.models.Qos;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DatasetDao {

    private final StringRedisTemplate redisTemplate;

    private static final String split = ",";
    private static final String SERVICE_KEY_PREFIX = "action:";
    private static final String SERVICE_INPUTS = "inputs";
    private static final String SERVICE_OUTPUTS = "outputs";
    private static final String SERVICE_QOS = "qos";
    private static final String CONCEPT_KEY_PREFIX = "atom:";
    private static final String CONCEPT_DIRECT_PARENT = "directParent";
    private static final String CONCEPT_PARENTS = "parents";
    private static final String CONCEPT_CHILDREN = "children";

    public DatasetDao(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Save service map to database
     *
     * @param serviceMap Service map
     */
    public void saveServiceMap(Map<String, com.ingbyr.hwsc.common.models.Service> serviceMap) {
        HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
        for (Map.Entry<String, com.ingbyr.hwsc.common.models.Service> entry : serviceMap.entrySet()) {
            String serviceName = SERVICE_KEY_PREFIX + entry.getKey();
            com.ingbyr.hwsc.common.models.Service service = entry.getValue();
            Map<String, String> serviceData = new HashMap<>();
            serviceData.put(SERVICE_INPUTS,
                    service.getInputParamSet().stream().map(Param::toString).collect(Collectors.joining(split)));
            serviceData.put(SERVICE_OUTPUTS,
                    service.getOutputParamSet().stream().map(Param::toString).collect(Collectors.joining(split)));
            Qos qos = service.getOriginQos();
            for (int qosType : Qos.TYPES) {
                serviceData.put(Qos.NAMES[qosType], Double.toString(qos.get(qosType)));
            }
            hashOp.putAll(serviceName, serviceData);
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
                    concept.getParentConceptsIndex().stream().map(Concept::toString).collect(Collectors.joining(split)));
            conceptData.put(CONCEPT_CHILDREN,
                    concept.getChildrenConceptsIndex().stream().map(Concept::toString).collect(Collectors.joining(split)));
            hashOp.putAll(conceptName, conceptData);
        }
    }
}
