package net.sf.opendse.encoding.mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.Inject;

import net.sf.opendse.encoding.MappingEncoding;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ProcessPropertyService;
import net.sf.opendse.model.properties.ProcessPropertyService.MappingModes;
import net.sf.opendse.model.properties.TaskPropertyService;

public class DefaultMappingEncoding implements MappingEncoding {
	
	protected final MappingConstraintGeneratorManager generatorManager;
	
	@Inject
	public DefaultMappingEncoding(MappingConstraintGeneratorManager generatorManager) {
		this.generatorManager = generatorManager;
	}

	@Override
	public Set<MappingVariable> toConstraints(Mappings<Task, Resource> mappings,
			Set<ApplicationVariable> applicationVariables, Set<Constraint> constraints) {
		// gather the task variables
		Set<T> processVariables = new HashSet<T>();
		for (ApplicationVariable applVar : applicationVariables) {
			if (applVar instanceof T) {
				T tVar = (T) applVar;
				if (TaskPropertyService.isProcess(tVar.getTask()))
					processVariables.add(tVar);
			}
		}
		Map<MappingModes, Set<T>> mappingModeMap = filterProcessVariables(processVariables);
		Set<MappingVariable> result = new HashSet<MappingVariable>();
		for (Entry<MappingModes, Set<T>> entry : mappingModeMap.entrySet()) {
			MappingModes mappingMode = entry.getKey();
			Set<T> processVars = entry.getValue();
			MappingConstraintGenerator constraintGenerator = generatorManager.getMappingConstraintGenerator(mappingMode);
			result.addAll(constraintGenerator.toConstraints(processVars, mappings, constraints));
		}
		return result;
	}

	/**
	 * Sorts the process variables into a map according to their mapping mode.
	 * 
	 * @param processVariables
	 * @return map mapping the different mapping modes onto sets of process
	 *         variables
	 */
	protected Map<MappingModes, Set<T>> filterProcessVariables(Set<T> processVariables) {
		Map<MappingModes, Set<T>> result = new HashMap<MappingModes, Set<T>>();
		for (T processVar : processVariables) {
			MappingModes mappingMode = ProcessPropertyService.getMappingMode(processVar.getTask());
			if (!result.containsKey(mappingMode)) {
				result.put(mappingMode, new HashSet<T>());
			}
			result.get(mappingMode).add(processVar);
		}
		return result;
	}
}
