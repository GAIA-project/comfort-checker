package gr.cti.gaia.comfort.checker.service;

import lombok.extern.slf4j.Slf4j;
import net.sparkworks.cargo.client.GroupClient;
import net.sparkworks.cargo.common.dto.GroupDTO;
import net.sparkworks.cargo.common.dto.ResourceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupService {
    
    private static final String TEMPERATURE_UUID = "905107a7-ca00-476f-b923-bf9720ed5c80";
    private static final String HUMIDITY_UUID = "795a7633-dd75-44eb-8ecb-11ecbf5a986b";
    private static final String LUMINOSITY_UUID = "484f9e6e-c3a5-4865-a03c-9ea5137a73ee";
    private static final String NOISE_UUID = "e9405ed3-01be-44f6-be39-e8415ff6b78b";
    private static final String MOTION_UUID = "16dc9b05-16e7-4854-93c2-aa31cb63cc8c";
    private static final String POWER_CONSUMPTION_UUID = "b749b7f2-9124-4a60-aa93-4771f69d9b9b";
    private static final String PM1 = "d3b17c2d-570a-40df-a8e9-28a09b761f8b";
    private static final String PM2 = "d94d238d-9887-4c5a-9f2d-8e821c5154a8";
    private static final String PM10 = "1563abc9-00f9-4fbd-bd0f-2469dc52972a";
    
    @Autowired
    GroupClient groupClient;
    
    public Collection<GroupDTO> listAll() {
        return groupClient.listAll();
    }
    
    public ResourceDTO getTemperatureResource(GroupDTO groupDTO, Collection<ResourceDTO> resources) {
        return getResourceByPhenomenonUuid(groupDTO, TEMPERATURE_UUID, resources);
    }
    
    public ResourceDTO getRelativeHumidityResource(GroupDTO groupDTO, Collection<ResourceDTO> resources) {
        return getResourceByPhenomenonUuid(groupDTO, HUMIDITY_UUID, resources);
    }
    
    public ResourceDTO getLuminosityResource(GroupDTO groupDTO, Collection<ResourceDTO> resources) {
        return getResourceByPhenomenonUuid(groupDTO, LUMINOSITY_UUID, resources);
    }
    public ResourceDTO getNoiseResource(GroupDTO groupDTO, Collection<ResourceDTO> resources) {
        return getResourceByPhenomenonUuid(groupDTO, NOISE_UUID, resources);
    }
    public ResourceDTO getMotionResource(GroupDTO groupDTO, Collection<ResourceDTO> resources) {
        return getResourceByPhenomenonUuid(groupDTO, MOTION_UUID, resources);
    }
    public ResourceDTO getPM1Resource(GroupDTO groupDTO, Collection<ResourceDTO> resources) {
        return getResourceByPhenomenonUuid(groupDTO, PM1, resources);
    }
    public ResourceDTO getPM25Resource(GroupDTO groupDTO, Collection<ResourceDTO> resources) {
        return getResourceByPhenomenonUuid(groupDTO, PM2, resources);
    }
    public ResourceDTO getPM10Resource(GroupDTO groupDTO, Collection<ResourceDTO> resources) {
        return getResourceByPhenomenonUuid(groupDTO, PM10, resources);
    }
    
    public ResourceDTO getPowerConsumptionResource(GroupDTO groupDTO, Collection<ResourceDTO> resources) {
        return getResourceByPhenomenonUuid(groupDTO, POWER_CONSUMPTION_UUID, resources);
    }
    
    public ResourceDTO getResourceByPhenomenonUuid(GroupDTO groupDTO, final String phenomenonUUID, Collection<ResourceDTO> resources) {
        return resources.stream().filter(resourceDTO -> resourceDTO.getPhenomenonUuid() != null)
                .filter(resourceDTO -> resourceDTO.getSystemName().startsWith("site-"))
                .filter(resourceDTO -> resourceDTO.getGroupUuid().equals(groupDTO.getUuid())
                        && resourceDTO.getPhenomenonUuid().toString().equals(phenomenonUUID)).findFirst().orElse(null);
    }
    
    public Collection<ResourceDTO> getResources(GroupDTO groupDTO) {
        return groupClient.getGroupResources(groupDTO.getUuid());
    }
}
