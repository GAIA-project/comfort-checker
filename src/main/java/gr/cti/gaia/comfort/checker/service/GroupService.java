package gr.cti.gaia.comfort.checker.service;

import net.sparkworks.cargo.client.GroupClient;
import net.sparkworks.cargo.common.dto.GroupDTO;
import net.sparkworks.cargo.common.dto.ResourceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class GroupService {
    
    private static final String TEMPERATURE_UUID = "905107a7-ca00-476f-b923-bf9720ed5c80";
    private static final String HUMIDITY_UUID = "795a7633-dd75-44eb-8ecb-11ecbf5a986b";
    private static final String LUMINOSITY_UUID = "484f9e6e-c3a5-4865-a03c-9ea5137a73ee";
    
    @Autowired
    GroupClient groupClient;
    
    public Collection<GroupDTO> listAll() {
        return groupClient.listAll();
    }
    
    public ResourceDTO getTemperatureResource(GroupDTO groupDTO) {
        return getResourceByPhenomenonUuid(groupDTO, TEMPERATURE_UUID);
    }
    
    public ResourceDTO getRelativeHumidityResource(GroupDTO groupDTO) {
        return getResourceByPhenomenonUuid(groupDTO, HUMIDITY_UUID);
    }
    
    public ResourceDTO getLuminosityResource(GroupDTO groupDTO) {
        return getResourceByPhenomenonUuid(groupDTO, LUMINOSITY_UUID);
    }
    
    public ResourceDTO getResourceByPhenomenonUuid(GroupDTO groupDTO, final String phenomenonUUID) {
        return groupClient.getGroupResources(groupDTO.getUuid()).stream().filter(resourceDTO -> resourceDTO.getGroupUuid().equals(groupDTO.getUuid()) && resourceDTO.getPhenomenonUuid().toString().equals(phenomenonUUID)).distinct().collect(Collectors.toList()).iterator().next();
    }
}
